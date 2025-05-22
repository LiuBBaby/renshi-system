<template>
  <view class="apply-container">
    <u-navbar title="申请" :border="false" :placeholder="true"></u-navbar>
    
    <!-- Tab选项卡 -->
    <view class="tab-header">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 0 }" 
        @click="switchTab(0)"
      >请假</view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 1 }" 
        @click="switchTab(1)"
      >出差</view>
    </view>
    
    <!-- 请假Tab内容 -->
    <view class="tab-content" v-if="currentTab === 0">
      <!-- 请假申请按钮 -->
      <view class="apply-btn" @click="navigateTo('/pages/apply/leave/add')">
        <text class="btn-icon">+</text>
        <text>新建请假申请</text>
      </view>
      
      <!-- 请假记录列表 -->
      <view class="record-list">
        <view class="section-header">
          <view class="section-title">请假记录</view>
        </view>
        
        <!-- 筛选菜单 - 更美观的设计 -->
        <view class="filter-wrapper">
          <!-- 状态筛选 -->
          <scroll-view scroll-x class="filter-scroll">
            <view class="filter-section">
              <view class="filter-item" :class="{ active: leaveFilterStatus === 'mine' }" @click="changeLeaveFilter('mine')">我的申请</view>
              <view class="filter-item" :class="{ active: leaveFilterStatus === 'approval' }" @click="changeLeaveFilter('approval')" v-if="isGroupOrAbove()">待我审批</view>
              <view class="filter-item" :class="{ active: leaveFilterStatus === 'pending' }" @click="changeLeaveFilter('pending')">审批中</view>
              <view class="filter-item" :class="{ active: leaveFilterStatus === 'approved' }" @click="changeLeaveFilter('approved')">已通过</view>
              <view class="filter-item" :class="{ active: leaveFilterStatus === 'rejected' }" @click="changeLeaveFilter('rejected')">已驳回</view>
            </view>
          </scroll-view>
        </view>
        
        <!-- 无记录显示 -->
        <view class="empty-tip" v-if="filteredLeaveList.length === 0">
          <image src="/static/images/empty.png" mode="aspectFit" class="empty-img"></image>
          <text class="empty-text">暂无请假记录</text>
        </view>
        
        <!-- 记录列表 -->
        <view class="list-content">
          <view 
            class="record-item" 
            v-for="(item, index) in filteredLeaveList" 
            :key="index" 
            @click="viewLeaveDetail(item)" 
            :class="{
              'my-application': isOwnApplication(item),
              'needs-my-approval': needsMyApproval(item)
            }">
            <!-- 待审批标签 -->
            <view class="approval-tag" v-if="needsMyApproval(item) && !isApproved(item)">待审批</view>
            <view class="approval-tag approved-tag" v-if="needsMyApproval(item, true) && isApproved(item)">已审批</view>
            
            <view class="record-header">
              <view class="record-type leave-type">{{ getRecordTypeName(item) }}</view>
              <view class="record-status" :class="[getStatusClass(item.examineInfoResult)]" v-if="!needsMyApproval(item) || leaveFilterStatus !== 'approval'">
                {{ getCustomStatusText(item.examineInfoResult, isOwnApplication(item)) }}
              </view>
            </view>
            
            <view class="record-info-row">
              <text class="info-label">申请人:</text>
              <text class="info-value">{{ item.examineInfoName }}</text>
              <text class="info-label">部门:</text>
              <text class="info-value">{{ item.examineInfoDept || '未知部门' }}</text>
            </view>
            
            <view class="record-time">
              <view class="time-label">请假时间:</view>
              <view class="time-value">
                {{ formatDate(item.examineInfoDateBegin) }} 至 {{ formatDate(item.examineInfoDateEnd) }}
                <text class="record-days">{{ calculateDays(item.examineInfoDateBegin, item.examineInfoDateEnd) }}天</text>
              </view>
            </view>
            
            <view class="record-reason">
              <text class="reason-label">请假事由:</text>
              <text class="reason-content">{{ item.examineInfoReason || '无理由' }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 出差Tab内容 -->
    <view class="tab-content" v-if="currentTab === 1">
      <!-- 出差申请按钮 -->
      <view class="apply-btn" @click="navigateTo('/pages/apply/business/add')">
        <text class="btn-icon">+</text>
        <text>新建出差申请</text>
      </view>
      
      <!-- 出差记录列表 -->
      <view class="record-list">
        <view class="section-header">
          <view class="section-title">出差记录</view>
        </view>
        
        <!-- 筛选菜单 - 更美观的设计 -->
        <view class="filter-wrapper">
          <!-- 状态筛选 -->
          <scroll-view scroll-x class="filter-scroll">
            <view class="filter-section">
              <view class="filter-item" :class="{ active: businessFilterStatus === 'mine' }" @click="changeBusinessFilter('mine')">我的申请</view>
              <view class="filter-item" :class="{ active: businessFilterStatus === 'approval' }" @click="changeBusinessFilter('approval')" v-if="isGroupOrAbove()">待我审批</view>
              <view class="filter-item" :class="{ active: businessFilterStatus === 'pending' }" @click="changeBusinessFilter('pending')">审批中</view>
              <view class="filter-item" :class="{ active: businessFilterStatus === 'approved' }" @click="changeBusinessFilter('approved')">已通过</view>
              <view class="filter-item" :class="{ active: businessFilterStatus === 'rejected' }" @click="changeBusinessFilter('rejected')">未通过</view>
            </view>
          </scroll-view>
        </view>
        
        <!-- 无记录显示 -->
        <view class="empty-tip" v-if="filteredBusinessList.length === 0">
          <image src="/static/images/empty.png" mode="aspectFit" class="empty-img"></image>
          <text class="empty-text">暂无出差记录</text>
        </view>
        
        <!-- 记录列表 -->
        <view class="list-content">
          <view 
            class="record-item" 
            v-for="(item, index) in filteredBusinessList" 
            :key="index" 
            @click="viewBusinessDetail(item)"
            :class="{
              'my-application': isOwnApplication(item),
              'needs-my-approval': needsMyApproval(item)
            }">
            <!-- 待审批标签 -->
            <view class="approval-tag" v-if="needsMyApproval(item) && !isApproved(item)">待审批</view>
            <view class="approval-tag approved-tag" v-if="needsMyApproval(item, true) && isApproved(item)">已审批</view>
            
            <view class="record-header">
              <view class="record-type business-type">出差</view>
              <view class="record-status" :class="[getStatusClass(item.tripInfoResult)]" v-if="!needsMyApproval(item) || businessFilterStatus !== 'approval'">
                {{ getCustomStatusText(item.tripInfoResult, isOwnApplication(item)) }}
              </view>
            </view>
            
            <view class="record-info-row">
              <text class="info-label">申请人:</text>
              <text class="info-value">{{ item.tripInfoName }}</text>
              <text class="info-label">部门:</text>
              <text class="info-value">{{ item.tripInfoDept || '未知部门' }}</text>
            </view>
            
            <view class="record-info-row">
              <text class="info-label">目的地:</text>
              <text class="info-value">{{ item.tripInfoDestination || '未填写' }}</text>
            </view>
            
            <view class="record-time">
              <view class="time-label">出差时间:</view>
              <view class="time-value">
                {{ formatDate(item.tripInfoDateBegin) }} 至 {{ formatDate(item.tripInfoDateEnd) }}
                <text class="record-days">{{ calculateDays(item.tripInfoDateBegin, item.tripInfoDateEnd) }}天</text>
              </view>
            </view>
            
            <view class="record-reason">
              <text class="reason-label">出差事由:</text>
              <text class="reason-content">{{ item.tripInfoReason || '无理由' }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 下拉刷新提示 -->
    <view class="loading-more" v-if="isLoading">
      <u-loading size="24" color="#007aff"></u-loading>
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script>
import { getLeaveList } from '@/api/leave'
import { getBusinessList } from '@/api/business'
import { isOwnApplication, needsMyApproval, getCustomStatusText, isGroupOrAbove, isManagerOrAbove } from '@/utils/role-utils'

export default {
  data() {
    return {
      currentTab: 0, // 当前选中的tab：0-请假，1-出差
      leaveTypes: ['事假', '病假', '年假', '婚假', '产假', '丧假'],
      leaveList: [], // 请假记录列表
      businessList: [], // 出差记录列表
      isLoading: false,
      page: 1,
      size: 20,  // 增加每页显示的记录数
      hasMore: true,
      leaveFilterStatus: 'mine', // 请假过滤状态: mine, approval, pending, approved, rejected
      businessFilterStatus: 'mine' // 出差过滤状态: mine, approval, pending, approved, rejected
    };
  },
  
  computed: {
    // 根据过滤条件筛选请假列表
    filteredLeaveList() {
      if (this.leaveFilterStatus === 'mine') {
        // 我的申请，显示自己发起的申请
        return this.leaveList.filter(item => this.isOwnApplication(item));
      } else if (this.leaveFilterStatus === 'approval') {
        // 待我审批，简化处理：只要不是自己提交的申请且未撤销，就显示
        // 添加排序：状态0和1的排在前面，状态2和3的排在后面
        const filtered = this.leaveList.filter(item => 
          !this.isOwnApplication(item) && 
          item.examineInfoResult !== 4 // 排除已撤销的申请
        );
        
        // 对结果进行排序
        return filtered.sort((a, b) => {
          const statusA = a.examineInfoResult;
          const statusB = b.examineInfoResult;
          
          // 待审批的状态(0或1)排在前面
          if ((statusA === 0 || statusA === 1) && (statusB === 2 || statusB === 3)) {
            return -1; // a排在b前面
          }
          // 已审批的状态(2或3)排在后面
          if ((statusA === 2 || statusA === 3) && (statusB === 0 || statusB === 1)) {
            return 1; // a排在b后面
          }
          
          // 如果状态类型相同，则按ID排序(新的在前)
          return b.id - a.id;
        });
      } else if (this.leaveFilterStatus === 'pending') {
        // 审批中，只显示自己发起的且状态为审批中的申请
        return this.leaveList.filter(item => 
          this.isOwnApplication(item) && 
          (item.examineInfoResult === 0 || item.examineInfoResult === 1)
        );
      } else if (this.leaveFilterStatus === 'approved') {
        // 已通过，只显示自己发起的申请
        return this.leaveList.filter(item => 
          this.isOwnApplication(item) && item.examineInfoResult === 2
        );
      } else if (this.leaveFilterStatus === 'rejected') {
        // 已驳回，只显示自己发起的申请
        return this.leaveList.filter(item => 
          this.isOwnApplication(item) && item.examineInfoResult === 3
        );
      }
      return this.leaveList.filter(item => this.isOwnApplication(item));
    },
    
    // 根据过滤条件筛选出差列表
    filteredBusinessList() {
      if (this.businessFilterStatus === 'mine') {
        // 我的申请，显示自己发起的申请
        return this.businessList.filter(item => this.isOwnApplication(item));
      } else if (this.businessFilterStatus === 'approval') {
        // 待我审批，简化处理：只要不是自己提交的申请且未撤销，就显示
        // 添加排序：状态0和1的排在前面，状态2和3的排在后面
        const filtered = this.businessList.filter(item => 
          !this.isOwnApplication(item) && 
          item.tripInfoResult !== 4 // 排除已撤销的申请
        );
        
        // 对结果进行排序
        return filtered.sort((a, b) => {
          const statusA = a.tripInfoResult;
          const statusB = b.tripInfoResult;
          
          // 待审批的状态(0或1)排在前面
          if ((statusA === 0 || statusA === 1) && (statusB === 2 || statusB === 3)) {
            return -1; // a排在b前面
          }
          // 已审批的状态(2或3)排在后面
          if ((statusA === 2 || statusA === 3) && (statusB === 0 || statusB === 1)) {
            return 1; // a排在b后面
          }
          
          // 如果状态类型相同，则按ID排序(新的在前)
          return b.id - a.id;
        });
      } else if (this.businessFilterStatus === 'pending') {
        // 审批中，只显示自己发起的且状态为审批中的申请
        return this.businessList.filter(item => 
          this.isOwnApplication(item) && 
          (item.tripInfoResult === 0 || item.tripInfoResult === 1)
        );
      } else if (this.businessFilterStatus === 'approved') {
        // 已通过，只显示自己发起的申请
        return this.businessList.filter(item => 
          this.isOwnApplication(item) && item.tripInfoResult === 2
        );
      } else if (this.businessFilterStatus === 'rejected') {
        // 已驳回，只显示自己发起的申请
        return this.businessList.filter(item => 
          this.isOwnApplication(item) && item.tripInfoResult === 3
        );
      }
      return this.businessList.filter(item => this.isOwnApplication(item));
    }
  },
  
  onLoad() {
    // 确保非组长以上角色不会看到审批相关内容
    if (!this.isGroupOrAbove()) {
      this.leaveFilterStatus = 'mine';
      this.businessFilterStatus = 'mine';
    }
    
    // 加载请假记录
    this.loadLeaveList();
  },
  onShow() {
    // 每次显示页面都重新加载数据，确保数据是最新的
    this.refreshList();
  },
  onPullDownRefresh() {
    // 下拉刷新
    this.refreshList();
  },
  onReachBottom() {
    // 上拉加载更多
    this.loadMore();
  },
  methods: {
    // 切换Tab
    switchTab(index) {
      if (this.currentTab === index) return;
      this.currentTab = index;
      this.page = 1;
      this.hasMore = true;
      
      // 根据当前Tab加载对应数据
      if (index === 0) {
        this.loadLeaveList();
      } else {
        this.loadBusinessList();
      }
    },
    
    // 更改请假过滤状态
    changeLeaveFilter(status) {
      // 如果是普通用户尝试选择待审批，则忽略
      if (status === 'approval' && !this.isGroupOrAbove()) {
        return;
      }
      this.leaveFilterStatus = status;
    },
    
    // 更改出差过滤状态
    changeBusinessFilter(status) {
      // 如果是普通用户尝试选择待审批，则忽略
      if (status === 'approval' && !this.isGroupOrAbove()) {
        return;
      }
      this.businessFilterStatus = status;
    },
    
    // 页面导航
    navigateTo(url) {
      uni.navigateTo({
        url: url
      });
    },
    
    // 加载请假记录
    loadLeaveList() {
      if (!this.hasMore || this.isLoading) return;
      
      this.isLoading = true;
      uni.showLoading({
        title: '加载中...',
        mask: true
      });
      
      getLeaveList({
        page: this.page,
        size: this.size
      }).then(res => {
        uni.hideLoading();
        console.log('请假记录响应数据:', res);
        
        if (res.code === 200) {
          // 优先使用res.rows，如果不存在则尝试使用res.data
          const list = res.rows || res.data || [];
          if (this.page === 1) {
            this.leaveList = list;
          } else {
            this.leaveList = [...this.leaveList, ...list];
          }
          this.hasMore = list.length === this.size;
          this.page++;
        } else {
          this.$modal.showToast(res.msg || '获取请假记录失败');
        }
        this.isLoading = false;
        uni.stopPullDownRefresh();
      }).catch(err => {
        uni.hideLoading();
        this.isLoading = false;
        uni.stopPullDownRefresh();
        this.$modal.showToast('获取请假记录失败');
        console.error('获取请假记录失败:', err);
      });
    },
    
    // 加载出差记录
    loadBusinessList() {
      if (!this.hasMore || this.isLoading) return;
      
      this.isLoading = true;
      uni.showLoading({
        title: '加载中...',
        mask: true
      });
      
      getBusinessList({
        page: this.page,
        size: this.size
      }).then(res => {
        uni.hideLoading();
        console.log('出差记录响应数据:', res);
        
        if (res.code === 200) {
          // 优先使用res.rows，如果不存在则尝试使用res.data
          const list = res.rows || res.data || [];
          if (this.page === 1) {
            this.businessList = list;
          } else {
            this.businessList = [...this.businessList, ...list];
          }
          this.hasMore = list.length === this.size;
          this.page++;
        } else {
          this.$modal.showToast(res.msg || '获取出差记录失败');
        }
        this.isLoading = false;
        uni.stopPullDownRefresh();
      }).catch(err => {
        uni.hideLoading();
        this.isLoading = false;
        uni.stopPullDownRefresh();
        this.$modal.showToast('获取出差记录失败');
        console.error('获取出差记录失败:', err);
      });
    },
    
    // 刷新列表
    refreshList() {
      this.page = 1;
      this.hasMore = true;
      
      if (this.currentTab === 0) {
        this.loadLeaveList();
      } else {
        this.loadBusinessList();
      }
    },
    
    // 加载更多
    loadMore() {
      if (this.currentTab === 0) {
        this.loadLeaveList();
      } else {
        this.loadBusinessList();
      }
    },
    
    // 查看请假详情
    viewLeaveDetail(item) {
      uni.navigateTo({
        url: `/pages/apply/leave/detail?id=${item.id || item.examineInfoId}`
      });
    },
    
    // 查看出差详情
    viewBusinessDetail(item) {
      uni.navigateTo({
        url: `/pages/apply/business/detail?id=${item.id || item.examineInfoId}`
      });
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 计算请假天数
    calculateDays(startDate, endDate) {
      if (!startDate || !endDate) return 0;
      const start = new Date(startDate);
      const end = new Date(endDate);
      const diffTime = Math.abs(end - start);
      // 计算天数差（包含首尾两天）
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '待组长审批',
        1: '待经理审批',
        2: '已通过',
        3: '未通过',
        4: '已撤销'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取状态样式类
    getStatusClass(status) {
      const classMap = {
        0: 'status-pending',    // 待组长审批
        1: 'status-pending',    // 待经理审批
        2: 'status-approved',   // 已通过
        3: 'status-rejected',   // 未通过
        4: 'status-cancelled'   // 已撤销
      };
      return classMap[status] || '';
    },
    
    // 获取记录类型名称
    getRecordTypeName(item) {
      // 优先使用leaveType字段
      if (item.leaveType !== undefined && this.leaveTypes[item.leaveType]) {
        return this.leaveTypes[item.leaveType];
      }
      
      // 如果有请假类型字段
      if (item.examineInfoType !== undefined) {
        const typeIndex = parseInt(item.examineInfoType);
        if (!isNaN(typeIndex) && this.leaveTypes[typeIndex]) {
          return this.leaveTypes[typeIndex];
        }
      }
      
      // 从请假理由中分析
      if (item.examineInfoReason) {
        const reason = item.examineInfoReason;
        for (let i = 0; i < this.leaveTypes.length; i++) {
          if (reason.includes(this.leaveTypes[i])) {
            return this.leaveTypes[i];
          }
        }
      }
      
      // 默认返回类型
      return '请假';
    },
    
    // 判断是否为用户的申请
    isOwnApplication(item) {
      return isOwnApplication(item);
    },
    
    // 判断是否需要我的审批
    needsMyApproval(item, showApproved = false) {
      return needsMyApproval(item, showApproved);
    },
    
    // 获取自定义状态文本
    getCustomStatusText(status, isOwnRecord) {
      return getCustomStatusText(status, isOwnRecord);
    },
    
    // 判断是否组长或以上角色
    isGroupOrAbove() {
      return isGroupOrAbove();
    },
    
    // 判断是否经理或以上角色
    isManagerOrAbove() {
      return isManagerOrAbove();
    },
    
    // 判断是否已审批
    isApproved(item) {
      if (item.examineInfoResult !== undefined) {
        return item.examineInfoResult === 2 || item.examineInfoResult === 3;
      } else if (item.tripInfoResult !== undefined) {
        return item.tripInfoResult === 2 || item.tripInfoResult === 3;
      }
      return false;
    }
  }
};
</script>

<style lang="scss">
.apply-container {
  min-height: 100vh;
  background-color: #f8f8f8;
}

.tab-header {
  display: flex;
  background-color: #fff;
  padding: 0 30rpx;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.tab-item {
  flex: 1;
  text-align: center;
  height: 88rpx;
  line-height: 88rpx;
  font-size: 32rpx;
  color: #666;
  position: relative;
  transition: all 0.3s;
}

.tab-item.active {
  font-weight: bold;
  color: #007aff;
}

.tab-item.active:after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background-color: #007aff;
  border-radius: 2rpx;
}

.tab-content {
  padding: 24rpx;
}

.apply-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 90rpx;
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
}

.apply-btn:active {
  background-color: #f5f5f5;
  transform: scale(0.98);
}

.btn-icon {
  font-size: 36rpx;
  margin-right: 10rpx;
  color: #007aff;
}

.record-list {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  position: relative;
  padding-left: 20rpx;
}

.section-title:before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6rpx;
  height: 32rpx;
  background-color: #007aff;
  border-radius: 3rpx;
}

/* 横向滚动筛选菜单 */
.filter-wrapper {
  margin-bottom: 20rpx;
}

.filter-scroll {
  white-space: nowrap;
  width: 100%;
}

.filter-section {
  display: inline-flex;
  flex-wrap: nowrap;
  padding: 10rpx 0;
}

.filter-item {
  display: inline-block;
  padding: 8rpx 24rpx;
  margin-right: 16rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
  background-color: #f5f5f5;
  transition: all 0.3s;
}

.filter-item.active {
  background-color: #e6f7ff;
  color: #007aff;
  font-weight: bold;
}

.list-content {
  margin-top: 10rpx;
}

.record-item {
  padding: 24rpx;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
  background-color: #fff;
  position: relative;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  border: 1rpx solid #eee;
}

.record-item:last-child {
  margin-bottom: 0;
}

.record-item:active {
  background-color: #f9f9f9;
}

/* 自己的申请样式 */
.my-application {
  border-left: 6rpx solid #409EFF;
  background-color: rgba(64, 158, 255, 0.03);
}

/* 需要审批的申请样式 */
.needs-my-approval {
  border-left: 6rpx solid #E6A23C;
  background-color: rgba(230, 162, 60, 0.03);
}

/* 审批标签 */
.approval-tag {
  position: absolute;
  right: 20rpx;
  top: 20rpx;
  background-color: #ff4d4f;
  color: #fff;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  z-index: 1;
}

.approved-tag {
  background-color: #52c41a;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.record-type {
  padding: 8rpx 20rpx;
  border-radius: 6rpx;
  font-size: 24rpx;
  font-weight: bold;
}

.leave-type {
  background-color: rgba(79, 125, 247, 0.1);
  color: #4f7df7;
}

.business-type {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.record-status {
  font-size: 24rpx;
  font-weight: bold;
}

.status-pending {
  color: #e6a23c;
}

.status-approved {
  color: #67c23a;
}

.status-rejected {
  color: #f56c6c;
}

.status-cancelled {
  color: #909399;
}

.record-info-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 10rpx;
  font-size: 26rpx;
}

.info-label {
  color: #999;
  margin-right: 10rpx;
}

.info-value {
  color: #333;
  margin-right: 20rpx;
}

.record-time {
  display: flex;
  margin-bottom: 10rpx;
  font-size: 26rpx;
  flex-direction: column;
}

.time-label {
  color: #999;
  margin-bottom: 6rpx;
}

.time-value {
  color: #333;
}

.record-days {
  margin-left: 10rpx;
  color: #909399;
  background-color: #f5f5f5;
  padding: 2rpx 10rpx;
  border-radius: 4rpx;
  font-size: 24rpx;
}

.record-reason {
  font-size: 26rpx;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx dashed #eee;
}

.reason-label {
  color: #999;
  display: block;
  margin-bottom: 8rpx;
}

.reason-content {
  color: #666;
  word-break: break-all;
  line-height: 1.5;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.empty-img {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx 0;
}

.loading-text {
  font-size: 24rpx;
  color: #999;
  margin-left: 10rpx;
}
</style> 