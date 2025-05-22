import auth from '@/utils/auth'
import store from '@/store'

/**
 * 检查是否是管理员
 * @returns {Boolean}
 */
export function isAdmin() {
  const permissions = store.getters.permissions || []
  console.log("[权限检查] 当前权限:", permissions);
  return permissions.includes('*:*:*')
}

/**
 * 检查用户是否具有某个权限
 * 如果用户是管理员，则拥有所有权限
 * @param {String|Array} permission 权限字符串或数组
 * @returns {Boolean}
 */
export function checkPermi(permission) {
  // 管理员直接放行
  if (isAdmin()) {
    console.log("[权限检查] 当前用户是管理员，自动拥有所有权限");
    return true
  }
  
  const all_permission = "*:*:*";
  const permissions = store.getters.permissions || []
  
  // 如果检查的是超级权限
  if (all_permission === permission) {
    const result = permissions.includes(all_permission);
    console.log(`[权限检查] 检查超级权限 "*:*:*", 结果: ${result}`);
    return result;
  }
  
  // 判断是否是数组权限，只要拥有其中一个权限即可通过
  if (Array.isArray(permission)) {
    console.log(`[权限检查] 检查数组权限:`, permission);
    const result = permission.some(perm => {
      const has = permissions.includes(perm);
      console.log(`[权限检查] 检查权限 "${perm}", 结果: ${has}`);
      return has;
    });
    console.log(`[权限检查] 最终结果: ${result}`);
    return result;
  }
  
  // 字符串判断
  const result = permissions.includes(permission);
  console.log(`[权限检查] 检查单个权限 "${permission}", 结果: ${result}`);
  return result;
}

/**
 * 角色权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export function checkRole(value) {
  if (value && value instanceof Array && value.length > 0) {
    const roles = store.getters && store.getters.roles
    const permissionRoles = value
    const super_admin = "admin"

    const hasRole = roles.some(role => {
      return super_admin === role || permissionRoles.includes(role)
    })

    if (!hasRole) {
      return false
    }
    return true
  } else {
    console.error(`need roles! Like checkRole="['admin','editor']"`)
    return false
  }
}