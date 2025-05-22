const getters = {
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions,
  isAdmin: state => {
    const permissions = state.user.permissions || []
    return permissions.includes('*:*:*')
  },
  // 路由相关
  routes: state => state.permission.routes,
  sidebarRouters: state => state.permission.sidebarRouters
}
export default getters
