import { constantRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import { checkPermi, isAdmin } from '@/utils/permission'

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_SIDEBAR_ROUTERS: (state, routers) => {
      state.sidebarRouters = routers
    }
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit }) {
      return new Promise(resolve => {
        // 向后端请求路由数据
        getRouters().then(res => {
          const { data } = res
          const sdata = JSON.parse(JSON.stringify(data))
          
          // 根据用户权限过滤路由
          let accessedRoutes = filterAsyncRouter(data)
          let sidebarRouters = filterAsyncRouter(sdata)
          
          commit('SET_ROUTES', accessedRoutes)
          commit('SET_SIDEBAR_ROUTERS', sidebarRouters)
          resolve(accessedRoutes)
        }).catch(error => {
          console.error('获取路由数据失败', error)
          // 即使失败也返回空数组，确保应用不会崩溃
          commit('SET_ROUTES', [])
          commit('SET_SIDEBAR_ROUTERS', [])
          resolve([])
        })
      })
    }
  }
}

// 遍历过滤有权限的路由
function filterAsyncRouter(asyncRouters) {
  if (!asyncRouters || !Array.isArray(asyncRouters)) {
    return [];
  }
  
  return asyncRouters.filter(route => {
    // 管理员拥有所有路由权限
    if (isAdmin()) {
      return true
    }
    
    // 检查路由权限
    if (route.permissions) {
      if (!checkPermi(route.permissions)) {
        return false
      }
    }
    
    // 处理子路由
    if (route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children)
    }
    
    return true
  })
}

export default permission 