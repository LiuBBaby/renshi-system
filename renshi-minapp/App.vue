<script>
  import config from './config'
  import store from '@/store'
  import { getToken } from '@/utils/auth'
  import { setTabBarByPermission } from '@/utils/tab-permission'
  import { isAdmin } from '@/utils/permission'

  export default {
    globalData: {
      config: {},
      userInfo: null
    },
    onLaunch: function() {
      this.initApp()
    },
    onShow: function() {
      // 应用显示时，检查是否已登录，如果已登录则设置TabBar
      if (getToken()) {
        // 获取用户信息
        store.dispatch('GetInfo').then(res => {
          // 获取动态路由
          this.generateRoutes()
        }).catch(() => {
          // 如果获取信息失败，则可能是token过期，跳转到登录页
          this.$tab.reLaunch('/pages/login')
        })
      }
    },
    methods: {
      // 初始化应用
      initApp() {
        // 初始化应用配置
        this.initConfig()
        // 检查用户登录状态
        //#ifdef H5
        this.checkLogin()
        //#endif
      },
      initConfig() {
        // config.js 加载时已包含正确的 baseUrl
        this.globalData.config = config
        console.log('App.vue initConfig - globalData.config.baseUrl:', this.globalData.config.baseUrl);
      },
      checkLogin() {
        if (!getToken()) {
          this.$tab.reLaunch('/pages/login') 
        }
      },
      // 根据权限生成路由
      generateRoutes() {
        store.dispatch('GenerateRoutes').then(() => {
          // 判断是否为管理员
          if (isAdmin()) {
            console.log('当前用户是管理员，拥有所有权限')
          }
          // 根据权限设置TabBar
          this.setupTabBar()
        }).catch(err => {
          console.error('获取路由失败:', err)
          this.setupTabBar() // 即使获取路由失败，也尝试设置TabBar
        })
      },
      // 设置TabBar
      setupTabBar() {
        // 根据用户权限设置TabBar
        setTimeout(() => {
          setTabBarByPermission()
        }, 300)
      }
    }
  }
</script>

<style lang="scss">
  @import '@/static/scss/index.scss'
</style>
