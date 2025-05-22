<template>
  <view class="mine-container" :style="{height: `${windowHeight}px`}">
    <!--顶部个人信息栏-->
    <view class="header-section">
      <view class="flex padding justify-between">
        <view class="flex align-center">
          <view v-if="!currentAvatar" class="cu-avatar xl round bg-white">
            <view class="iconfont icon-people text-gray icon"></view>
          </view>
          <image v-if="currentAvatar" @click="handleToAvatar" :src="currentAvatar" @error="handleAvatarError" class="cu-avatar xl round" mode="widthFix">
          </image>
          <view v-if="!nickname" @click="handleToLogin" class="login-tip">
            点击登录
          </view>
          <view v-if="nickname" @click="handleToInfo" class="user-info">
            <view class="u_title">
              昵称：{{ nickname }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="content-section">
      <!-- 移除快捷入口 -->
      <!-- 
      <view class="mine-actions grid col-4 text-center">
        ...
      </view> 
      -->

      <view class="menu-list">
        <!-- 个人信息入口 -->
        <view class="custom-list-cell" @click="handleToInfo">
          <text class="cell-text">个人信息</text>
          <view class="right-arrow"></view>
        </view>
        
        <!-- 修改密码入口 -->
        <view class="custom-list-cell" @click="handleToPwd">
          <text class="cell-text">修改密码</text>
          <view class="right-arrow"></view>
        </view>
        
      </view>
      
      <!-- 退出登录按钮 -->
      <view class="logout-button" v-if="nickname">
        <button class="btn-logout" @click="handleLogout">退出登录</button>
      </view>

    </view>
  </view>
</template>

<script>
  import storage from '@/utils/storage'
  // 导入权限检查方法 (假设存在此方法)
  import { checkPermi } from '@/utils/permission' 

  export default {
    data() {
      return {
        nickname: this.$store.state.user.nickname || this.$store.state.user.name, // 优先使用昵称，没有则使用用户名
        version: getApp().globalData.config.appInfo.version,
        currentAvatar: null,
        defaultAvatar: require('@/static/images/profile.jpg')
      }
    },
    computed: {
      avatar() {
        return this.$store.state.user.avatar
      },
      windowHeight() {
        return uni.getSystemInfoSync().windowHeight - 50
      }
    },
    watch: {
      avatar: {
        immediate: true,
        handler(newVal) {
          this.currentAvatar = newVal;
        }
      },
      // 监听store中nickname的变化
      '$store.state.user.nickname': {
        immediate: true,
        handler(newVal) {
          if (newVal) {
            this.nickname = newVal;
          } else {
            // 昵称为空则使用用户名
            this.nickname = this.$store.state.user.name;
          }
        }
      }
    },
    methods: {
      handleToInfo() {
        this.$tab.navigateTo('/pages/mine/info/index')
      },
      handleToSetting() {
        this.$tab.navigateTo('/pages/mine/setting/index')
      },
      handleToLogin() {
        this.$tab.reLaunch('/pages/login')
      },
      handleToAvatar() {
        this.$tab.navigateTo('/pages/mine/avatar/index')
      },
      handleToPwd() {
        this.$tab.navigateTo('/pages/mine/pwd/index')
      },
      handleLogout() {
        this.$modal.confirm('确定注销并退出系统吗？').then(() => {
          uni.showLoading({
            title: '退出登录中...',
            mask: true
          });
          
          this.$store.dispatch('LogOut').then(() => {
            uni.hideLoading();
            this.$tab.reLaunch('/pages/login');
          }).catch(error => {
            uni.hideLoading();
            console.error('退出登录失败:', error);
            this.$tab.reLaunch('/pages/login');
          });
        }).catch(() => {
          // 用户取消了退出操作
        });
      },
      // 处理头像加载错误
      handleAvatarError() {
        console.log('头像加载失败，使用默认头像')
        this.currentAvatar = this.defaultAvatar
      }
    }
  }
</script>

<style lang="scss" scoped>
  /* 样式可以保持不变，或者根据需要调整 */
  page {
    background-color: #f5f6f7;
  }

  .mine-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    background-color: #f5f6f7; /* 确保容器背景色正确 */

    .header-section {
      /* 修改样式使其成为flex容器，垂直居中 */
      display: flex;
      align-items: center;
      height: 240rpx; /* 增加高度使蓝色区域更高 */
      background-color: #3c96f3;
      color: white;
      padding: 0 30rpx; /* 左右内边距 */
      
      .flex {
        width: 100%;
      }

      .login-tip {
        font-size: 18px;
        margin-left: 10px;
      }

      .cu-avatar {
        border: 2px solid #eaeaea;
        flex-shrink: 0; /* 防止头像被压缩 */

        .icon {
          font-size: 40px;
        }
      }

      .user-info {
        margin-left: 15px;

        .u_title {
          font-size: 18px;
          line-height: 30px;
        }
      }
    }

    .content-section {
      /* 正常流布局，不使用绝对定位 */
      margin-top: 20rpx;
      padding-bottom: 20rpx; /* 底部填充，避免内容过于贴近底部 */
      flex: 1;
      display: flex;
      flex-direction: column;
      background-color: #f5f6f7;
      justify-content: space-between; /* 主要内容在顶部，按钮在底部 */

      .menu-list {
        margin: 0 15px;
        background-color: white;
        border-radius: 8px;
        overflow: hidden; // 确保圆角生效
      }
      
      /* 全新的自定义列表项样式 */
      .custom-list-cell {
        position: relative;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px;
        background-color: #fff;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .cell-text {
          font-size: 15px;
          color: #333;
        }
        
        .right-arrow {
          width: 8px;
          height: 8px;
          border-top: 2px solid #c8c8c8;
          border-right: 2px solid #c8c8c8;
          transform: rotate(45deg);
        }
      }
    }
    
    /* 退出登录按钮样式 */
    .logout-button {
      padding: 0 30rpx;
      margin-top: auto; /* 将按钮推到底部 */
      margin-bottom: 20rpx; /* 与底部导航栏保持一定距离 */
      width: 100%;
      display: flex;
      justify-content: center; /* 水平居中 */
    }
    
    .btn-logout {
      background-color: #ffffff;
      color: #E54D42;
      border: 1px solid #f0f0f0;
      border-radius: 8px;
      font-size: 16px;
      font-weight: normal;
      padding: 10px 0;
      width: 80%; /* 按钮宽度调整为80%实现居中效果 */
      text-align: center;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1); /* 添加阴影使按钮更明显 */
    }
  }
</style>
