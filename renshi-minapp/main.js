import Vue from 'vue'
import App from './App'
import store from './store' // store
import plugins from './plugins' // plugins
import config from './config' // 全局配置
// import './permission' // 移除旧的权限控制文件
import { setupPermissionDirectives } from './utils/directive'
import { setupRouteGuards } from './utils/permission-guard'

Vue.use(plugins)

Vue.config.productionTip = false
Vue.prototype.$store = store
Vue.prototype.$config = config // 将配置添加到Vue原型，所有组件可通过this.$config访问

// 注册权限指令
setupPermissionDirectives(Vue)

// 设置路由守卫
setupRouteGuards()

App.mpType = 'app'

const app = new Vue({
  ...App,
  store
})

app.$mount()
