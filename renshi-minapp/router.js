// 静态路由，这些是基础路由，所有用户都可以访问
export const constantRoutes = [
  {
    path: '/pages/login',
    component: '/pages/login',
    hidden: true
  },
  {
    path: '/pages/register',
    component: '/pages/register',
    hidden: true
  },
  {
    path: '/pages/common/webview/index',
    component: '/pages/common/webview/index',
    hidden: true
  },
  {
    path: '/pages/common/textview/index',
    component: '/pages/common/textview/index',
    hidden: true
  },
  {
    path: '/pages/error/404',
    component: '/pages/error/404',
    hidden: true
  }
]

// 动态路由，根据后端返回的菜单和权限动态生成
export const dynamicRoutes = [] 