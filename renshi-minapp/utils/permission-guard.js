import { getToken } from '@/utils/auth'
import { checkPermi, isAdmin } from '@/utils/permission'
import { whiteList, pagePermissionMap, DISABLE_PERMISSION_CHECK } from '@/utils/permission-config'

/**
 * 规范化路径格式
 * @param {String} url 原始路径
 * @returns {String} 规范化后的路径
 */
function normalizePath(url) {
  if (!url) return '';
  
  // 移除查询参数
  let path = url.split('?')[0];
  
  // 确保路径以/开头
  if (!path.startsWith('/')) {
    path = '/' + path;
  }
  
  // 针对特殊情况处理
  // 微信小程序可能会有多种路径格式
  // 例如: 
  // - /pages/checkin/index
  // - pages/checkin/index
  
  return path;
}

/**
 * 检查页面访问权限
 * @param {String} url 页面路径
 * @returns {Boolean} 是否有权限访问
 */
export function checkUrlPermission(url) {
  // 如果禁用权限检查，直接返回true
  if (DISABLE_PERMISSION_CHECK) {
    console.log(`[权限检查] 权限检查已禁用，允许访问所有页面`);
    return true;
  }

  // 处理url参数，规范化路径
  const originalPath = url;
  const pagePath = normalizePath(url);
  console.log(`[权限检查] 检查页面权限，原始路径: ${originalPath}, 处理后路径: ${pagePath}`);
  
  // 白名单页面不需要验证权限
  if (whiteList.some(path => pagePath.includes(path))) {
    console.log(`[权限检查] 白名单页面，允许访问:`, pagePath);
    return true;
  }
  
  // 检查是否登录
  if (!getToken()) {
    console.log(`[权限检查] 未登录，重定向到登录页`);
    uni.redirectTo({ url: '/pages/login' });
    return false;
  }
  
  // 管理员拥有所有权限
  if (isAdmin()) {
    console.log(`[权限检查] 管理员用户，拥有所有权限`);
    return true;
  }
  
  // 特殊情况处理: 如果是考勤打卡页面
  if (pagePath.includes('checkin/index')) {
    console.log('[权限检查] 匹配到考勤打卡页面，检查权限: wxapp:attendance:punch');
    const hasPunchPermi = checkPermi(['wxapp:attendance:punch']);
    console.log(`[权限检查] 考勤打卡权限检查结果: ${hasPunchPermi}`);
    return hasPunchPermi;
  }
  
  // 查找页面对应的权限
  let hasPermission = false;
  let requiredPermissions = [];
  
  // 精确匹配
  if (pagePermissionMap[pagePath]) {
    requiredPermissions = pagePermissionMap[pagePath];
    hasPermission = checkPermi(requiredPermissions);
    console.log(`[权限检查] 精确匹配页面 ${pagePath} 所需权限:`, requiredPermissions);
    console.log(`[权限检查] 权限检查结果:`, hasPermission);
    return hasPermission;
  }
  
  // 模糊匹配
  for (const path in pagePermissionMap) {
    if (pagePath.includes(path)) {
      requiredPermissions = pagePermissionMap[path];
      hasPermission = checkPermi(requiredPermissions);
      console.log(`[权限检查] 模糊匹配页面 ${pagePath} 所需权限:`, requiredPermissions);
      console.log(`[权限检查] 权限检查结果:`, hasPermission);
      return hasPermission;
    }
  }
  
  // 如果页面未配置权限要求，默认放行
  console.log(`[权限检查] 页面 ${pagePath} 未配置权限要求，默认放行`);
  return true;
}

/**
 * 设置路由拦截器
 */
export function setupRouteGuards() {
  console.log(`[权限系统] 初始化路由守卫`);
  
  // 页面跳转拦截方法
  const interceptMethod = (method) => {
    uni.addInterceptor(method, {
      invoke(params) {
        try {
          console.log(`[权限拦截] 拦截到 ${method} 跳转请求:`, params.url);
          
          // 如果没有URL参数，直接放行
          if (!params || !params.url) {
            console.log(`[权限拦截] 无URL参数，放行`);
            return params;
          }
          
          // 检查权限
          if (checkUrlPermission(params.url)) {
            console.log(`[权限拦截] 允许访问:`, params.url);
            return params;
          } else {
            // 无权限时提示并拦截
            console.log(`[权限拦截] 拒绝访问:`, params.url);
            
            setTimeout(() => {
              uni.showToast({
                title: '您没有访问该页面的权限',
                icon: 'none',
                duration: 2000
              });
            }, 100);
            
            return false;
          }
        } catch (error) {
          console.error(`[权限拦截] 检查权限时发生错误:`, error);
          // 发生错误时默认放行
          return params;
        }
      },
      fail(err) {
        console.error('[权限拦截] 路由拦截器错误:', err);
        // 错误时不做额外处理，保持uni默认行为
      }
    });
  };
  
  // 对所有导航方法应用拦截器
  ['navigateTo', 'redirectTo', 'reLaunch', 'switchTab'].forEach(method => {
    interceptMethod(method);
  });
  
  console.log(`[权限系统] 路由守卫初始化完成`);
}
