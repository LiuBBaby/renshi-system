import config from '@/config'
import storage from '@/utils/storage'
import constant from '@/utils/constant'
import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { saveUserRole } from '@/utils/role-utils'

const baseUrl = config.baseUrl

const user = {
  state: {
    token: getToken(),
    name: '',
    nickname: '',
    avatar: '',
    roles: [],
    permissions: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_NICKNAME: (state, nickname) => {
      state.nickname = nickname
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid).then(res => {
          setToken(res.token)
          commit('SET_TOKEN', res.token)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo(state.token).then(res => {
          const user = res.user
          
          // 设置用户基本信息
          // 如果用户头像为空，使用默认头像
          const defaultAvatar = require("@/static/images/default-avatar.jpg")
          let avatar = user.avatar == "" ? defaultAvatar : user.avatar
          
          // 设置头像，并处理远程头像可能加载失败的情况
          if (avatar && avatar.startsWith('http')) {
            // 对于远程头像，预先检查图片是否可访问
            uni.getImageInfo({
              src: avatar,
              fail: function() {
                console.log('远程头像加载失败，使用默认头像')
                avatar = defaultAvatar
                commit('SET_AVATAR', avatar)
              }
            })
          }
          
          commit('SET_NAME', user.userName)
          commit('SET_NICKNAME', user.nickName || user.userName)
          commit('SET_AVATAR', avatar)
          
          // 设置角色
          const roles = res.roles || []
          commit('SET_ROLES', roles)
          
          // 保存用户主要角色
          saveUserRole(roles)
          
          // 始终保存用户信息，至少包含userId
          const userInfo = {
            userId: user.userId,
            userName: user.userName,
            deptId: user.deptId || null
          };
          
          // 关联员工ID
          if (user.empId) {
            uni.setStorageSync('empId', user.empId)
            userInfo.empId = user.empId;
          }
          
          // 保存用户信息
          uni.setStorageSync('userInfo', userInfo)
          
          // 设置权限数组
          commit('SET_PERMISSIONS', res.permissions)
          
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        try {
          // 无论后端请求是否成功，都清除前端状态
          logout(state.token).then(() => {
            commit('SET_TOKEN', '')
            commit('SET_ROLES', [])
            commit('SET_PERMISSIONS', [])
            removeToken()
            storage.clean()
            // 清除角色信息
            uni.removeStorageSync('userRole')
            uni.removeStorageSync('empId')
            uni.removeStorageSync('userInfo')
            resolve()
          }).catch(error => {
            console.error('退出登录请求失败:', error)
            // 即使API调用失败，也清除前端的登录状态
            commit('SET_TOKEN', '')
            commit('SET_ROLES', [])
            commit('SET_PERMISSIONS', [])
            removeToken()
            storage.clean()
            // 清除角色信息
            uni.removeStorageSync('userRole')
            uni.removeStorageSync('empId')
            uni.removeStorageSync('userInfo')
            resolve() // 这里仍然resolve而不是reject，确保前端登出流程继续
          })
        } catch (error) {
          console.error('退出登录过程发生异常:', error)
          // 确保即使出现异常，也清理登录状态
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          removeToken()
          storage.clean()
          // 清除角色信息
          uni.removeStorageSync('userRole')
          uni.removeStorageSync('empId')
          uni.removeStorageSync('userInfo')
          resolve() // 确保前端登出流程继续
        }
      })
    }
  }
}

export default user
