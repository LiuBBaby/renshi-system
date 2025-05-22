import tab from './tab'
import auth from './auth'
import modal from './modal'
import request from '@/utils/request'

export default {
  install(Vue) {
    // 页签操作
    Vue.prototype.$tab = tab
    // 认证对象
    Vue.prototype.$auth = auth
    // 模态框对象
    Vue.prototype.$modal = modal
    // HTTP请求方法
    Vue.prototype.$http = {
      get: (url, params) => {
        return request({
          url,
          method: 'get',
          params
        })
      },
      post: (url, data) => {
        return request({
          url,
          method: 'post',
          data
        })
      },
      put: (url, data) => {
        return request({
          url,
          method: 'put',
          data
        })
      },
      delete: (url, data) => {
        return request({
          url,
          method: 'delete',
          data
        })
      }
    }
  }
}
