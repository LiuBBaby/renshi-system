/**
 * 权限配置相关常量
 */

// 不需要登录的白名单页面
export const whiteList = [
  '/pages/login',
  '/pages/register',
  '/pages/error/404',
  '/pages/common/webview/index',
  '/pages/common/textview/index'
]

// 页面权限映射 - 路径和所需权限的对应关系
export const pagePermissionMap = {
  // 考勤模块
  '/pages/checkin/index': ['wxapp:attendance:punch'],
  '/pages/attendance/index': ['wxapp:attendance:list'],
  '/pages/attendance/monthly': ['wxapp:attendance:monthly'],
  '/pages/attendance/evaluate': ['wxapp:attendance:evaluate'],
  
  // 申请模块
  '/pages/apply/index': ['wxapp:leave:apply', 'wxapp:business:apply'],
  '/pages/apply/leave/add': ['wxapp:leave:apply'],
  '/pages/apply/leave/detail': ['wxapp:leave:detail'],
  '/pages/apply/business/add': ['wxapp:business:apply'],
  '/pages/apply/business/detail': ['wxapp:business:detail'],
  
  // 个人中心模块
  '/pages/mine/info/index': ['wxapp:mine:info'],
  '/pages/mine/info/edit': ['wxapp:mine:edit'],
  '/pages/mine/avatar/index': ['wxapp:mine:avatar'],
  '/pages/mine/setting/index': ['wxapp:mine:setting'],
  '/pages/mine/pwd/index': ['wxapp:mine:password'],
  '/pages/mine/help/index': ['wxapp:mine:help'],
  '/pages/mine/about/index': ['wxapp:mine:about']
}

// TabBar项权限配置
export const tabBarPermissionMap = {
  'checkin': ['wxapp:attendance:punch'],
  'apply': ['wxapp:leave:apply', 'wxapp:business:apply'],
  'attendance': ['wxapp:attendance:list'],
  'mine': [] // 我的页面不需要特殊权限
}

// 临时调试开关，设置为true时禁用权限检查，所有页面自由访问
export const DISABLE_PERMISSION_CHECK = false; 