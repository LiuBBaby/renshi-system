<template>
  <view class="user-info-container">
    <view class="info-card">
      <uni-list class="info-list">
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'person-filled', color: '#3c9cff'}" title="昵称" :rightText="user.nickName" />
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'phone-filled', color: '#3c9cff'}" title="手机号码" :rightText="user.phonenumber" />
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'email-filled', color: '#3c9cff'}" title="邮箱" :rightText="user.email" />
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'gender-filled', color: '#3c9cff'}" title="性别" :rightText="getSexText(user.sex)" />
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'auth-filled', color: '#3c9cff'}" title="岗位" :rightText="postGroup || '无'" />
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'staff-filled', color: '#3c9cff'}" title="角色" :rightText="roleGroup || '无'" />
        <uni-list-item showExtraIcon="true" :extraIcon="{type: 'calendar-filled', color: '#3c9cff'}" title="创建日期" :rightText="formatDate(user.createTime)" />
      </uni-list>
      
      <view class="btn-container">
        <button class="edit-btn" type="primary" @click="goEdit">编辑</button>
      </view>
    </view>
  </view>
</template>

<script>
  import { getUserProfile } from "@/api/system/user"

  export default {
    data() {
      return {
        user: {},
        roleGroup: "",
        postGroup: "",
        loading: false
      }
    },
    onLoad() {
      this.getUser()
    },
    onShow() {
      // 每次显示页面时刷新用户数据，以便在编辑后能看到最新数据
      this.getUser()
    },
    methods: {
      getUser() {
        this.loading = true
        uni.showLoading({
          title: '加载中...'
        })
        
        getUserProfile().then(response => {
          if (response.code === 200) {
            this.user = response.data
            this.roleGroup = response.roleGroup
            this.postGroup = response.postGroup
          } else {
            uni.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            })
          }
        }).catch(error => {
          console.error('获取用户信息失败:', error)
          uni.showToast({
            title: '网络请求失败，请稍后重试',
            icon: 'none'
          })
        }).finally(() => {
          this.loading = false
          uni.hideLoading()
        })
      },
      
      // 跳转到编辑页
      goEdit() {
        uni.navigateTo({
          url: '/pages/mine/info/edit'
        })
      },
      
      // 性别显示转换
      getSexText(sex) {
        if (sex === '0') return '男'
        if (sex === '1') return '女'
        return '未设置'
      },
      
      // 格式化日期
      formatDate(dateString) {
        if (!dateString) return '未知'
        
        try {
          const date = new Date(dateString)
          const year = date.getFullYear()
          const month = (date.getMonth() + 1).toString().padStart(2, '0')
          const day = date.getDate().toString().padStart(2, '0')
          return `${year}-${month}-${day}`
        } catch (e) {
          return dateString
        }
      }
    }
  }
</script>

<style lang="scss">
  page {
    background-color: #f5f7fa;
  }
  
  .user-info-container {
    padding: 20rpx;
    min-height: 100vh;
  }
  
  .info-card {
    background-color: #fff;
    border-radius: 12rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
    
    .info-list {
      .uni-list-item {
        padding: 24rpx 0;
      }
      
      .uni-list--border:after {
        background-color: #f0f0f0;
      }
    }
    
    .btn-container {
      display: flex;
      justify-content: center;
      margin-top: 40rpx;
    }
    
    .edit-btn {
      background-color: #3c9cff;
      color: #fff;
      width: 80%;
      height: 80rpx;
      line-height: 80rpx;
      font-size: 30rpx;
      border-radius: 40rpx;
      border: none;
    }
  }
</style>
