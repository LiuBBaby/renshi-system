<template>
  <view class="container">
    <view class="user-info-edit">
      <uni-forms ref="form" :model="user" labelWidth="80px">
        <uni-forms-item label="用户昵称" name="nickName">
          <uni-easyinput v-model="user.nickName" placeholder="请输入昵称" />
        </uni-forms-item>
        <uni-forms-item label="手机号码" name="phonenumber">
          <uni-easyinput v-model="user.phonenumber" placeholder="请输入手机号码" />
        </uni-forms-item>
        <uni-forms-item label="邮箱" name="email">
          <uni-easyinput v-model="user.email" placeholder="请输入邮箱" />
        </uni-forms-item>
        <uni-forms-item label="性别" name="sex" required>
          <uni-data-checkbox v-model="user.sex" :localdata="sexs" />
        </uni-forms-item>
      </uni-forms>
      <button class="submit-btn" type="primary" @click="submit" :disabled="submitting">{{ submitting ? '提交中...' : '保存修改' }}</button>
    </view>
  </view>
</template>

<script>
  import { getUserProfile, updateUserProfile } from "@/api/system/user"

  export default {
    data() {
      return {
        user: {
          nickName: "",
          phonenumber: "",
          email: "",
          sex: ""
        },
        sexs: [{
          text: '男',
          value: "0"
        }, {
          text: '女',
          value: "1"
        }],
        rules: {
          nickName: {
            rules: [{
              required: true,
              errorMessage: '用户昵称不能为空'
            }]
          },
          phonenumber: {
            rules: [{
              required: true,
              errorMessage: '手机号码不能为空'
            }, {
              pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
              errorMessage: '请输入正确的手机号码'
            }]
          },
          email: {
            rules: [{
              required: true,
              errorMessage: '邮箱地址不能为空'
            }, {
              format: 'email',
              errorMessage: '请输入正确的邮箱地址'
            }]
          }
        },
        submitting: false,
        originalUser: null // 保存原始用户数据用于对比
      }
    },
    onLoad() {
      this.getUser()
    },
    onReady() {
      this.$refs.form.setRules(this.rules)
    },
    methods: {
      getUser() {
        uni.showLoading({
          title: '加载中...'
        });
        
        getUserProfile().then(response => {
          if (response.code === 200) {
            this.user = response.data;
            // 保存原始数据的副本
            this.originalUser = JSON.parse(JSON.stringify(response.data));
          } else {
            uni.showToast({
              title: '获取用户信息失败',
              icon: 'none'
            });
          }
        }).catch(error => {
          console.error('获取用户信息失败:', error);
          uni.showToast({
            title: '网络请求失败，请稍后重试',
            icon: 'none'
          });
        }).finally(() => {
          uni.hideLoading();
        });
      },
      
      submit() {
        this.$refs.form.validate().then(res => {
          // 检查是否有修改
          if (JSON.stringify(this.user) === JSON.stringify(this.originalUser)) {
            uni.showToast({
              title: '未修改任何信息',
              icon: 'none'
            });
            return;
          }
          
          this.submitting = true;
          uni.showLoading({
            title: '保存中...'
          });
          
          updateUserProfile(this.user).then(response => {
            if (response.code === 200) {
              uni.showToast({
                title: '个人信息修改成功',
                icon: 'success'
              });
              
              // 更新原始数据
              this.originalUser = JSON.parse(JSON.stringify(this.user));
              
              // 延迟返回上一页
              setTimeout(() => {
                uni.navigateBack();
              }, 1500);
            } else {
              uni.showToast({
                title: response.msg || '修改失败',
                icon: 'none'
              });
            }
          }).catch(error => {
            console.error('更新用户信息失败:', error);
            uni.showToast({
              title: '网络请求失败，请稍后重试',
              icon: 'none'
            });
          }).finally(() => {
            this.submitting = false;
            uni.hideLoading();
          });
        }).catch(errors => {
          console.error('表单验证错误:', errors);
        });
      }
    }
  }
</script>

<style lang="scss">
  page {
    background-color: #f5f7fa;
  }

  .container {
    min-height: 100vh;
  }

  .user-info-edit {
    padding: 30rpx;
    background-color: #fff;
    border-radius: 12rpx;
    margin: 20rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
    
    .submit-btn {
      margin-top: 50rpx;
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      font-size: 32rpx;
      background-color: #07C160;
      border-radius: 8rpx;
      
      &[disabled] {
        background-color: #a0cfb4;
        color: #eee;
      }
    }
  }
</style>
