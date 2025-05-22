<template>
  <view class="error-page">
    <view class="error-content">
      <image src="@/static/images/404.png" mode="widthFix" class="error-image"></image>
      <text class="error-title">页面不存在</text>
      <text class="error-desc">抱歉，您访问的页面不存在或没有权限访问</text>
      <button type="primary" @click="goHome" class="error-btn">返回首页</button>
    </view>
  </view>
</template>

<script>
export default {
  name: 'Error404',
  methods: {
    goHome() {
      uni.switchTab({
        url: '/pages/index'
      });
    }
  }
}
</script>

<style lang="scss">
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f8f8f8;
  
  .error-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px;
  }
  
  .error-image {
    width: 200px;
    margin-bottom: 20px;
  }
  
  .error-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 10px;
  }
  
  .error-desc {
    font-size: 14px;
    color: #666;
    text-align: center;
    margin-bottom: 30px;
  }
  
  .error-btn {
    width: 200px;
  }
}
</style> 