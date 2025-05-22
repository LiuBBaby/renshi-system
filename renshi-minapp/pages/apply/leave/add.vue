<template>
  <view class="apply-container">
    
    
    <view class="form-card">
      <!-- 请假类型选择 -->
      <view class="form-item">
        <view class="label">请假类型</view>
        <view class="leave-types">
          <view 
            v-for="(type, index) in leaveTypes" 
            :key="index"
            class="type-item"
            :class="{ active: formData.leaveType === index }"
            @click="selectLeaveType(index)">
            {{ type }}
          </view>
        </view>
      </view>
      
      <!-- 开始时间 -->
      <view class="form-item">
        <view class="label">开始时间</view>
        <view class="date-picker" @click="showStartDatePicker">
          <text>{{ formData.startDate || '请选择开始日期' }}</text>
          <uni-icons type="calendar" size="20" color="#666"></uni-icons>
        </view>
      </view>
      
      <!-- 结束时间 -->
      <view class="form-item">
        <view class="label">结束时间</view>
        <view class="date-picker" @click="showEndDatePicker">
          <text>{{ formData.endDate || '请选择结束日期' }}</text>
          <uni-icons type="calendar" size="20" color="#666"></uni-icons>
        </view>
      </view>
      
      <!-- 请假天数 -->
      <view class="form-item" v-if="formData.startDate && formData.endDate">
        <view class="label">请假天数</view>
        <view class="days">{{ formData.days }}天</view>
      </view>
      
      <!-- 请假原因 -->
      <view class="form-item">
        <view class="label">请假原因</view>
        <textarea 
          class="reason-input" 
          v-model="formData.reason" 
          placeholder="请输入请假原因（必填）" 
          maxlength="200">
        </textarea>
        <view class="char-count">{{ formData.reason.length }}/200</view>
      </view>
    </view>
    
    <!-- 提交按钮 -->
    <view class="submit-btn" @click="submitLeave">提交申请</view>
    
    <!-- 日期选择器 -->
    <view class="date-picker-wrapper" v-if="showDatePicker">
      <view class="mask" @click="closeDatePicker"></view>
      <view class="picker-content">
        <view class="picker-header">
          <view class="cancel" @click="closeDatePicker">取消</view>
          <view class="title">选择日期</view>
          <view class="confirm" @click="confirmDateSelect">确定</view>
        </view>
        <picker-view 
          class="picker-view" 
          :value="datePickerValue" 
          @change="onDatePickerChange">
          <picker-view-column>
            <view class="item" v-for="(year, index) in years" :key="index">{{ year }}年</view>
          </picker-view-column>
          <picker-view-column>
            <view class="item" v-for="(month, index) in months" :key="index">{{ month }}月</view>
          </picker-view-column>
          <picker-view-column>
            <view class="item" v-for="(day, index) in days" :key="index">{{ day }}日</view>
          </picker-view-column>
        </picker-view>
      </view>
    </view>
  </view>
</template>

<script>
import { applyLeave } from '@/api/leave'

export default {
  data() {
    const currentDate = new Date()
    const currentYear = currentDate.getFullYear()
    
    return {
      leaveTypes: ['事假', '病假', '年假', '婚假', '产假', '丧假'],
      formData: {
        leaveType: 0, // 默认事假
        startDate: '',
        endDate: '',
        days: 0,
        reason: ''
      },
      // 日期选择器相关数据
      showDatePicker: false,
      currentPickingType: '', // 'start' 或 'end'
      datePickerValue: [0, 0, 0],
      years: Array.from({length: 5}, (_, i) => currentYear + i),
      months: Array.from({length: 12}, (_, i) => i + 1),
      days: Array.from({length: 31}, (_, i) => i + 1),
      tempDate: null
    }
  },
  methods: {
    // 返回上一页
    goBack() {
      uni.navigateBack()
    },
    
    // 选择请假类型
    selectLeaveType(index) {
      this.formData.leaveType = index
    },
    
    // 显示开始日期选择器
    showStartDatePicker() {
      this.currentPickingType = 'start'
      this.initDatePicker(this.formData.startDate)
      this.showDatePicker = true
    },
    
    // 显示结束日期选择器
    showEndDatePicker() {
      this.currentPickingType = 'end'
      this.initDatePicker(this.formData.endDate)
      this.showDatePicker = true
    },
    
    // 初始化日期选择器值
    initDatePicker(dateStr) {
      const date = dateStr ? new Date(dateStr) : new Date()
      const yearIndex = this.years.findIndex(y => y === date.getFullYear())
      const monthIndex = date.getMonth()
      const dayIndex = date.getDate() - 1
      
      this.datePickerValue = [
        yearIndex > -1 ? yearIndex : 0,
        monthIndex,
        dayIndex
      ]
      
      // 保存临时日期
      this.tempDate = date
    },
    
    // 日期选择器变化
    onDatePickerChange(e) {
      const values = e.detail.value
      const year = this.years[values[0]]
      const month = this.months[values[1]]
      const day = this.days[values[2]]
      
      // 创建日期对象
      const date = new Date(year, month - 1, day)
      this.tempDate = date
    },
    
    // 确认日期选择
    confirmDateSelect() {
      if (!this.tempDate) return
      
      const year = this.tempDate.getFullYear()
      const month = (this.tempDate.getMonth() + 1).toString().padStart(2, '0')
      const day = this.tempDate.getDate().toString().padStart(2, '0')
      const formattedDate = `${year}-${month}-${day}`
      
      if (this.currentPickingType === 'start') {
        this.formData.startDate = formattedDate
      } else {
        this.formData.endDate = formattedDate
      }
      
      this.calculateDays()
      this.closeDatePicker()
    },
    
    // 关闭日期选择器
    closeDatePicker() {
      this.showDatePicker = false
    },
    
    // 计算请假天数
    calculateDays() {
      if (this.formData.startDate && this.formData.endDate) {
        const start = new Date(this.formData.startDate)
        const end = new Date(this.formData.endDate)
        
        // 检查日期顺序是否合理
        if (start > end) {
          uni.showToast({
            title: '结束日期不能早于开始日期',
            icon: 'none'
          })
          this.formData.endDate = ''
          this.formData.days = 0
          return
        }
        
        // 计算天数差（包含首尾两天）
        const diffTime = Math.abs(end - start)
        this.formData.days = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
      }
    },
    
    // 提交请假申请
    submitLeave() {
      // 表单验证
      if (!this.formData.startDate) {
        return this.$modal.showToast('请选择开始日期')
      }
      
      if (!this.formData.endDate) {
        return this.$modal.showToast('请选择结束日期')
      }
      
      if (!this.formData.reason) {
        return this.$modal.showToast('请输入请假原因')
      }
      
      // 构建请求数据
      const leaveData = {
        examineInfoDateBegin: this.formData.startDate,
        examineInfoDateEnd: this.formData.endDate,
        examineInfoReason: this.leaveTypes[this.formData.leaveType] + ': ' + this.formData.reason
      }
      
      // 显示加载提示
      uni.showLoading({
        title: '提交中...'
      })
      
      // 调用API
      applyLeave(leaveData).then(res => {
        if (res.code === 200) {
          uni.showToast({
            title: '申请提交成功',
            icon: 'success'
          })
          
          // 延迟返回上一页，防止过快看不到提示
          setTimeout(() => {
            // 返回到申请主页
            uni.navigateBack()
          }, 1500)
        } else {
          this.$modal.showToast(res.msg || '提交失败，请稍后再试')
        }
      }).catch(err => {
        console.error('提交请假申请错误:', err)
        this.$modal.showToast('提交失败，请稍后再试')
      }).finally(() => {
        uni.hideLoading()
      })
    }
  }
}
</script>

<style lang="scss">
.apply-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 30rpx;
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100rpx;
  padding: 0 30rpx;
  background-color: #fff;
  position: relative;
}

.back, .placeholder {
  width: 60rpx;
}

.nav-bar .title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.form-card {
  margin: 20rpx;
  padding: 30rpx;
  background-color: #fff;
  border-radius: 12rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.leave-types {
  display: flex;
  flex-wrap: wrap;
}

.type-item {
  padding: 16rpx 30rpx;
  background-color: #f5f5f5;
  border-radius: 50rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  color: #666;
}

.type-item.active {
  background-color: #e6f7ff;
  color: #007aff;
  border: 1rpx solid #007aff;
}

.date-picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
}

.days {
  font-size: 32rpx;
  font-weight: bold;
  color: #007aff;
}

.reason-input {
  width: 100%;
  height: 240rpx;
  padding: 20rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.char-count {
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.submit-btn {
  margin: 60rpx 20rpx 0;
  height: 90rpx;
  line-height: 90rpx;
  text-align: center;
  background-color: #007aff;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 45rpx;
}

/* 日期选择器样式 */
.date-picker-wrapper {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  z-index: 999;
}

.mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.picker-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100rpx;
  padding: 0 30rpx;
  border-bottom: 1rpx solid #eee;
}

.cancel {
  color: #999;
  font-size: 28rpx;
}

.confirm {
  color: #007aff;
  font-size: 28rpx;
  font-weight: bold;
}

.picker-header .title {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
}

.picker-view {
  height: 480rpx;
  width: 100%;
}

.item {
  line-height: 80rpx;
  text-align: center;
  font-size: 32rpx;
  color: #333;
}
</style> 