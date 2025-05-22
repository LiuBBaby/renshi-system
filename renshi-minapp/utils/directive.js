import { checkPermi, isAdmin } from '@/utils/permission'

/**
 * 注册全局权限指令
 * @param {Object} Vue Vue实例
 */
export function setupPermissionDirectives(Vue) {
  // v-hasPermi 权限判断指令
  Vue.directive('hasPermi', {
    inserted: function(el, binding) {
      const { value } = binding
      const hasPermission = checkPermi(value)
      
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  })
}
