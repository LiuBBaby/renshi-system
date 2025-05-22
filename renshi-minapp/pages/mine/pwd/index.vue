<template>
  <view class="pwd-retrieve-container">
    <uni-forms ref="form" :value="formData" labelWidth="80px">
      <uni-forms-item name="oldPassword" label="旧密码">
        <uni-easyinput type="password" v-model="formData.oldPassword" placeholder="请输入旧密码" />
      </uni-forms-item>
      <uni-forms-item name="newPassword" label="新密码">
        <uni-easyinput type="password" v-model="formData.newPassword" placeholder="请输入新密码" />
      </uni-forms-item>
      <uni-forms-item name="confirmPassword" label="确认密码">
        <uni-easyinput type="password" v-model="formData.confirmPassword" placeholder="请确认新密码" />
      </uni-forms-item>
      <button class="submit-btn" type="primary" @click="submit" :disabled="submitting">{{ submitting ? '提交中...' : '提交' }}</button>
    </uni-forms>
  </view>
</template>

<script>
  import { updateUserPwd } from "@/api/system/user"

  export default {
    data() {
      return {
        formData: {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        },
        rules: {
          oldPassword: {
            rules: [{
              required: true,
              errorMessage: '旧密码不能为空'
            }]
          },
          newPassword: {
            rules: [{
                required: true,
                errorMessage: '新密码不能为空',
              },
              {
                minLength: 6,
                maxLength: 20,
                errorMessage: '密码长度在 6 到 20 个字符'
              }
            ]
          },
          confirmPassword: {
            rules: [{
                required: true,
                errorMessage: '确认密码不能为空'
              }, {
                validateFunction: (rule, value, data) => data.newPassword === value,
                errorMessage: '两次输入的密码不一致'
              }
            ]
          }
        },
        submitting: false
      }
    },
    onReady() {
      this.$refs.form.setRules(this.rules)
    },
    methods: {
      // 前端检查密码是否相同
      checkPasswordsDifferent() {
        if (this.formData.oldPassword === this.formData.newPassword) {
          uni.showToast({
            title: '新密码不能与旧密码相同',
            icon: 'none',
            duration: 2000
          });
          return false;
        }
        return true;
      },
      
      submit() {
        this.$refs.form.validate().then(res => {
          // 前端验证确认密码是否一致
          if (this.formData.newPassword !== this.formData.confirmPassword) {
            uni.showToast({
              title: '两次输入的密码不一致',
              icon: 'none'
            });
            return;
          }
          
          // 前端验证新旧密码是否相同
          if (!this.checkPasswordsDifferent()) {
            return;
          }
          
          this.submitting = true;
          uni.showLoading({
            title: '提交中...'
          });
          
          // 调用封装的API方法
          updateUserPwd(this.formData.oldPassword, this.formData.newPassword).then(response => {
            if (response.code === 200) {
              uni.showToast({
                title: '密码修改成功',
                icon: 'success'
              });
              
              // 清空表单
              this.formData.oldPassword = '';
              this.formData.newPassword = '';
              this.formData.confirmPassword = '';
              
              // 延迟返回
              setTimeout(() => {
                uni.navigateBack();
              }, 1500);
            } else {
              // 处理各种返回情况
              if (response.code === 500 && response.msg === '新密码不能与旧密码相同') {
                // 这是一个预期的业务提示，不是错误
                uni.showToast({
                  title: '新密码不能与旧密码相同',
                  icon: 'none',
                  duration: 2000
                });
              } else {
                // 其他API错误
                uni.showToast({
                  title: response.msg || '密码修改失败',
                  icon: 'error',
                  duration: 2000
                });
              }
            }
          }).catch(error => {
            console.error('密码修改失败:', error);
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
    background-color: #ffffff;
  }

  .pwd-retrieve-container {
    padding: 30rpx;
    
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
