<template>
  <view class="page-wrapper">
    <view class="detail-container">
      <u-navbar title="请假详情" :border="false" :placeholder="true" leftIcon="arrow-left" @leftClick="goBack"></u-navbar>
      
      <view class="detail-card">
        <!-- 请假信息 -->
        <view class="section">
          <view class="section-title">请假信息</view>
          
          <view class="info-item">
            <text class="item-label">请假类型</text>
            <text class="item-value">{{ leaveDetail.leaveType || '未知类型' }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">开始日期</text>
            <text class="item-value">{{ leaveDetail.examineInfoDateBegin || leaveDetail.startDate }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">结束日期</text>
            <text class="item-value">{{ leaveDetail.examineInfoDateEnd || leaveDetail.endDate }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">请假天数</text>
            <text class="item-value">{{ leaveDetail.days }}天</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">请假原因</text>
            <text class="item-value reason-text">{{ leaveDetail.examineInfoReason || leaveDetail.reason }}</text>
          </view>
        </view>
        
        <!-- 申请信息 -->
        <view class="section">
          <view class="section-title">申请信息</view>
          
          <view class="info-item">
            <text class="item-label">申请人</text>
            <text class="item-value">{{ leaveDetail.examineInfoName || leaveDetail.name }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">所属部门</text>
            <text class="item-value">{{ leaveDetail.examineInfoDept || leaveDetail.department }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">申请时间</text>
            <text class="item-value">{{ leaveDetail.createTime || leaveDetail.applyTime }}</text>
          </view>
        </view>
        
        <!-- 审批信息 -->
        <view class="section" v-if="leaveDetail.examineInfoResult !== 0">
          <view class="section-title">审批信息</view>
          
          <view class="info-item">
            <text class="item-label">审批人</text>
            <text class="item-value">{{ leaveDetail.examineInfoPeople || leaveDetail.approver }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">审批时间</text>
            <text class="item-value">{{ leaveDetail.approvalTime }}</text>
          </view>
          
          <view class="info-item" v-if="leaveDetail.remark">
            <text class="item-label">审批意见</text>
            <text class="item-value reason-text">{{ leaveDetail.remark }}</text>
          </view>
        </view>
        
        <!-- 操作按钮 -->
        <view class="action-btns" v-if="leaveDetail.examineInfoResult === 0 || leaveDetail.examineInfoResult === 1">
          <!-- 审批按钮：使用canApprove计算属性来控制显示 -->
          <button 
            v-if="canApprove" 
            class="approve-btn" 
            @click="openApprovalModal">审批</button>
          <!-- 申请人显示撤销按钮（只在审批中状态时） -->
          <button v-else-if="isApplicant" class="cancel-btn" @click="cancelLeave">撤销申请</button>
        </view>
      </view>
      
      <!-- 流程图 -->
      <view class="process-section">
        <view class="process-title">审批流程</view>
        <view class="process-steps">
          <!-- 第一步：提交申请 -->
          <view class="process-step">
            <view class="step-icon completed">
              <text class="step-num">1</text>
            </view>
            <view class="step-info">
              <text class="step-name">提交申请</text>
              <text class="step-time">{{ leaveDetail.applyTime }}</text>
            </view>
          </view>
          
          <view class="step-line" :class="{ 'line-active': leaveDetail.examineInfoResult !== 0 }"></view>
          
          <!-- 对于组长用户，如果是自己的申请则隐藏一级审批步骤 -->
          <template v-if="!(isApplicant && isCurrentUserGroup())">
            <!-- 第二步：一级审批（针对用户） -->
            <view class="process-step">
              <view class="step-icon" :class="{ 'completed': leaveDetail.examineInfoResult > 0 }">
                <text class="step-num">2</text>
              </view>
              <view class="step-info">
                <text class="step-name">
                  <template v-if="leaveDetail.examineInfoResult === 3">
                    一级审批驳回
                  </template>
                  <template v-else>
                    {{ leaveDetail.examineInfoResult > 0 ? '一级审批通过' : '等待一级审批' }}
                  </template>
                </text>
                <text class="step-time" v-if="leaveDetail.examineInfoResult > 0">{{ leaveDetail.firstApprovalTime || leaveDetail.approvalTime }}</text>
              </view>
            </view>
            
            <view class="step-line" :class="{ 'line-active': leaveDetail.examineInfoResult > 1 || leaveDetail.examineInfoResult === 1 }"></view>
          </template>
          
          <!-- 最后一步：二级审批或最终审批 -->
          <view class="process-step">
            <view class="step-icon" :class="{ 'completed': leaveDetail.examineInfoResult > 1 || leaveDetail.examineInfoResult === 3, 'in-progress': leaveDetail.examineInfoResult === 1 }">
              <text class="step-num">{{ isApplicant && isCurrentUserGroup() ? '2' : '3' }}</text>
            </view>
            <view class="step-info">
              <text class="step-name">
                <template v-if="leaveDetail.examineInfoResult === 3">
                  审批驳回
                </template>
                <template v-else-if="isCurrentUserManager() && isApplicant">
                  {{ leaveDetail.examineInfoResult === 2 ? '审批通过' : '等待审批' }}
                </template>
                <template v-else-if="isApplicant && isCurrentUserGroup()">
                  {{ leaveDetail.examineInfoResult === 1 ? '等待经理审批' : (leaveDetail.examineInfoResult === 2 ? '经理审批通过' : '等待经理审批') }}
                </template>
                <template v-else>
                  {{ leaveDetail.examineInfoResult === 1 ? '等待二级审批' : (leaveDetail.examineInfoResult === 2 ? '二级审批通过' : '等待二级审批') }}
                </template>
              </text>
              <text class="step-time" v-if="leaveDetail.examineInfoResult > 1 || leaveDetail.examineInfoResult === 3">{{ leaveDetail.approvalTime }}</text>
            </view>
          </view>
        </view>
      </view>
    
      <!-- 审批弹窗 -->
      <view class="approval-modal" v-if="showApprovalModal">
        <view class="modal-mask" @click="closeApprovalModal"></view>
        <view class="modal-content">
          <view class="modal-title">请假审批</view>
          
          <view class="approval-options">
            <view class="option-item" 
                  :class="{ 'option-selected': approvalForm.result === 1 }"
                  @click="toggleApprovalResult(1)">
              <text class="option-icon">✓</text>
              <text class="option-text">同意</text>
            </view>
            
            <view class="option-item" 
                  :class="{ 'option-selected': approvalForm.result === 2 }"
                  @click="toggleApprovalResult(2)">
              <text class="option-icon">✕</text>
              <text class="option-text">拒绝</text>
            </view>
          </view>
          
          <view class="approval-form">
            <textarea class="remark-input" 
                     v-model="approvalForm.remark" 
                     placeholder="请输入审批意见（拒绝时必填）" 
                     :placeholder-style="{ color: '#999' }">
            </textarea>
          </view>
          
          <view class="modal-btns">
            <button class="btn-cancel" @click="closeApprovalModal">取消</button>
            <button class="btn-submit" @click="submitApproval">确认</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import storage from '@/utils/storage'
// 导入权限检查方法
import { checkPermi } from '@/utils/permission' 
// 导入请假相关API
import { getLeaveDetail, approveLeave, cancelLeave } from '@/api/leave'
// 导入角色工具函数
import { getCurrentRole, isOwnApplication, getCustomStatusText, isGroupOrAbove, isManagerOrAbove } from '@/utils/role-utils'

export default {
  data() {
    return {
      leaveId: null,
      leaveTypes: ['事假', '病假', '年假', '婚假', '产假', '丧假'],
      // 添加用户角色和权限状态
      hasApprovalAuth: false,
      hasApprovalPermission: false,
      isApplicant: false,
      userInfo: {},
      // 请假详情数据对象
      leaveDetail: {
        id: null,
        leaveType: 0,
        startDate: '',
        endDate: '',
        days: 0,
        reason: '',
        name: '',
        department: '',
        status: 0, // 0-审批中，1-已通过，2-未通过，3-已撤销
        applyTime: '',
        approver: '',
        approvalTime: '',
        remark: ''
      },
      showApprovalModal: false,
      approvalForm: {
        result: 1, // 默认同意
        remark: ''
      }
    };
  },
  created() {
    // 初始化 - 不在这里获取id，而是使用onLoad函数
    
    // 先获取用户基本信息
    this.userInfo = this.$store.state.user || {};
    console.log('初始化用户信息:', JSON.stringify(this.userInfo));
  },
  onLoad(options) {
    console.log('请假详情页面加载', options);
    
    // 初始化用户信息
    this.userInfo = this.$store.state.user || {};
    
    if (options.id) {
      this.leaveId = options.id;
      // 先获取详情数据，成功后会调用权限检查
      this.getLeaveDetail();
    } else {
      uni.showToast({
        title: '缺少请假ID参数',
        icon: 'none'
      });
    }
  },
  
  methods: {
    // 返回上一页
    goBack() {
      // 检查来源页面，优先返回到申请主页
      let pages = getCurrentPages()
      let prevPage = pages[pages.length - 2]
      
      // 如果上一页不是申请主页，则尝试返回到申请主页
      if (prevPage && !prevPage.route.includes('pages/apply/index')) {
        // 查找申请主页的位置
        let applyPageIndex = -1
        for (let i = 0; i < pages.length; i++) {
          if (pages[i].route && pages[i].route.includes('pages/apply/index')) {
            applyPageIndex = i
            break
          }
        }
        
        if (applyPageIndex >= 0) {
          // 返回到申请主页
          uni.navigateBack({
            delta: pages.length - 1 - applyPageIndex
          })
          return
        }
      }
      
      // 默认返回上一页
      uni.navigateBack()
    },
    
    // 检查用户权限
    checkUserPermission() {
      console.log('===== 开始权限检查 =====');
      
      // 从存储或状态管理中获取当前用户信息
      const userData = this.$store.state.user || {};
      const userRoles = this.$store.getters.roles || [];
      
      // 正确构建用户信息对象
      this.userInfo = {
        ...userData,
        roles: userRoles
      };
      
      console.log('用户原始信息:', JSON.stringify(userData));
      console.log('获取到的角色信息:', JSON.stringify(userRoles));
      
      // 判断用户角色类型 - 直接检查角色数组
      const isAdmin = userRoles.includes('admin');
      const isManager = userRoles.includes('manager');
      const isGroupLeader = userRoles.includes('group');
      
      console.log('角色检测结果:');
      console.log('- 是否管理员:', isAdmin);
      console.log('- 是否经理:', isManager);
      console.log('- 是否组长:', isGroupLeader);
      
      // 输出最终结果日志
      console.log('最终权限判断结果:');
      console.log('- 是否为申请人:', this.isApplicant);
      console.log('- 请假当前状态:', this.leaveDetail.status);
      console.log('===== 权限检查结束 =====');
    },
    
    // 获取请假详情
    async getLeaveDetail() {
      try {
        uni.showLoading({
          title: '加载中...',
          mask: true
        });
        
        const res = await getLeaveDetail({ id: this.leaveId });
        console.log('请假详情接口响应:', res);
        
        if (res.code === 200 && res.data) {
          const data = res.data;
          console.log('获取详情成功，原始数据:', JSON.stringify(data));
          
          // 转换接口数据到页面显示格式（保留原始字段，添加格式化后的显示字段）
          this.leaveDetail = {
            ...data, // 保留所有原始字段
            // 添加格式化后的字段
            leaveType: this.leaveTypes[data.leaveType] || data.leaveType || '未知类型',
            startDate: this.formatDate(data.examineInfoDateBegin),
            endDate: this.formatDate(data.examineInfoDateEnd),
            days: this.calculateDays(data.examineInfoDateBegin, data.examineInfoDateEnd),
            reason: data.examineInfoReason || '无',
            name: data.examineInfoName || '未知',
            department: data.examineInfoDept || '未知',
            status: data.examineInfoResult, // 申请状态
            applyTime: this.formatDateTime(data.createTime),
            approver: data.examineInfoPeople || '无',
            approvalTime: this.formatDateTime(data.approvalTime),
            firstApprovalTime: this.formatDateTime(data.firstApprovalTime)
          };
          
          console.log('处理后的请假详情:', JSON.stringify(this.leaveDetail));
          
          // 获取用户信息，从本地存储中获取
          const userInfo = uni.getStorageSync('userInfo') || {};
          const currentUserId = userInfo.userId || '';
          const applicantUserId = data.userId || '';
          
          // 确保转换为字符串进行比较
          this.isApplicant = String(currentUserId) === String(applicantUserId) && currentUserId !== '';
          
          console.log('用户身份判断信息:');
          console.log('- 本地存储用户信息:', JSON.stringify(userInfo));
          console.log('- 当前用户ID:', currentUserId);
          console.log('- 申请人用户ID:', applicantUserId);
          console.log('- 是否为申请人:', this.isApplicant);
          
          // 更新权限状态
          this.checkUserPermission();
        } else {
          uni.showToast({
            title: res.msg || '获取详情失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取请假详情异常:', error);
        uni.showToast({
          title: '网络异常，请稍后重试',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 获取状态文本
    getStatusText(status) {
      // 使用角色工具函数中的getCustomStatusText
      const isOwn = this.isApplicant;
      return getCustomStatusText(status, isOwn);
    },
    
    // 撤销请假申请
    cancelLeave() {
      uni.showModal({
        title: '提示',
        content: '确定要撤销此请假申请吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '处理中...'
            });
            
            cancelLeave({ id: this.leaveDetail.id }).then(res => {
              uni.hideLoading();
              if (res.code === 200) {
                uni.showToast({
                  title: '撤销成功',
                  icon: 'success',
                  duration: 2000
                });
                
                // 更新本地状态
                this.leaveDetail.status = 4; // 修改为已撤销状态值
                
                // 延迟返回
                setTimeout(() => {
                  uni.navigateBack();
                }, 2000);
              } else {
                uni.showToast({
                  title: res.msg || '撤销失败',
                  icon: 'none'
                });
              }
            }).catch(err => {
              uni.hideLoading();
              uni.showToast({
                title: '网络异常，请稍后重试',
                icon: 'none'
              });
              console.error('撤销请假申请失败:', err);
            });
          }
        }
      });
    },
    
    // 打开审批弹窗
    openApprovalModal() {
      this.showApprovalModal = true;
    },
    
    // 关闭审批弹窗
    closeApprovalModal() {
      this.showApprovalModal = false;
    },
    
    // 切换审批结果
    toggleApprovalResult(result) {
      this.approvalForm.result = result;
    },
    
    // 提交审批
    submitApproval() {
      // 验证输入
      if (this.approvalForm.result === 2 && !this.approvalForm.remark.trim()) {
        uni.showToast({
          title: '拒绝申请时必须填写审批意见',
          icon: 'none'
        });
        return;
      }
      
      uni.showLoading({
        title: '处理中...'
      });
      
      // 准备提交数据
      let resultValue;
      
      // 获取当前用户角色
      const userRoles = this.$store.getters.roles || [];
      const isAdmin = userRoles.includes('admin');
      const isManager = userRoles.includes('manager');
      const isGroup = userRoles.includes('group');
      
      // 审批拒绝的情况：无论什么角色都设为3（未通过）
      if (this.approvalForm.result === 2) {
        resultValue = 3; // 拒绝申请，设为未通过
      } 
      // 审批通过的情况：根据角色和当前状态决定
      else {
        // 如果是管理员或经理，直接通过，设为2
        if (isAdmin || isManager) {
          resultValue = 2; // 管理员或经理审批通过，直接设为最终通过
        }
        // 如果是组长，且是一级审批中，设为1表示需要二级审批
        else if (isGroup && this.leaveDetail.examineInfoResult === 0) {
          resultValue = 1; // 组长同意，进入二级审批
        }
        // 其他情况（组长对二级审批进行审批）设为2通过
        else if (this.leaveDetail.examineInfoResult === 1) {
          resultValue = 2; // 二级审批通过
        }
      }

      console.log('审批状态:', this.leaveDetail.examineInfoResult, '选择结果:', this.approvalForm.result, '用户角色:', userRoles, '映射为:', resultValue);

      const approvalData = {
        id: this.leaveDetail.id,
        examineInfoResult: resultValue,
        remark: this.approvalForm.remark,
        examineInfoPeople: this.userInfo.name || '当前用户',
        // 审批阶段标识
        approvalStage: this.leaveDetail.examineInfoResult === 0 ? 1 : 2, // 1=一级审批，2=二级审批
        approvalStatus: this.leaveDetail.examineInfoResult
      };
      
      approveLeave(approvalData).then(res => {
        uni.hideLoading();
        
        if (res.code === 200) {
          // 关闭弹窗
          this.closeApprovalModal();
          
          // 提示用户
          uni.showToast({
            title: '审批操作成功',
            icon: 'success',
            duration: 2000
          });
          
          // 重要修改: 审批成功后立即重新获取请假详情
          // 等待一小段时间再请求，确保后端数据已更新
          setTimeout(() => {
            console.log('审批成功，重新获取请假详情...');
            this.getLeaveDetail();
          }, 500);
        } else {
          uni.showToast({
            title: res.msg || '审批操作失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        uni.showToast({
          title: '网络异常，请稍后重试',
          icon: 'none'
        });
        console.error('审批请假申请失败:', err);
      });
    },
    
    // 格式化日期 (yyyy-MM-dd)
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      // 如果是字符串日期，转换为Date对象
      const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      
      return `${year}-${month}-${day}`;
    },
    
    // 计算两个日期之间的天数
    calculateDays(startDate, endDate) {
      if (!startDate || !endDate) return 0;
      
      // 确保日期是Date对象
      const start = typeof startDate === 'string' ? new Date(startDate) : startDate;
      const end = typeof endDate === 'string' ? new Date(endDate) : endDate;
      
      // 计算天数差异
      const diffTime = Math.abs(end - start);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // 加1是因为包含起始日期
      
      return diffDays;
    },
    
    // 格式化日期时间
    formatDateTime(dateStr) {
      // 如果参数为空，返回空字符串
      if (!dateStr) {
        return '';
      }
      
      // 如果传入的是字符串，尝试转换为Date对象
      let date;
      if (typeof dateStr === 'string') {
        // 常见的后端返回的时间格式: 2025-04-28 00:32:06
        date = new Date(dateStr.replace(/-/g, '/'));
      } else {
        date = new Date(dateStr);
      }
      
      // 检查是否是有效的Date对象
      if (isNaN(date.getTime())) {
        console.warn('无效的日期格式:', dateStr);
        return dateStr; // 返回原始字符串
      }
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      const seconds = date.getSeconds().toString().padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    
    // 判断是否为组长用户
    isCurrentUserGroup() {
      const userRoles = this.$store.getters.roles || [];
      return userRoles.includes('group');
    },
    
    // 判断是否为经理用户
    isCurrentUserManager() {
      const userRoles = this.$store.getters.roles || [];
      return userRoles.includes('manager');
    }
  },
  computed: {
    // 判断当前用户是否可以审批该申请
    canApprove() {
      // 获取当前用户角色
      const userRoles = this.$store.getters.roles || [];
      
      // 普通用户没有审批权限
      if (userRoles.includes('user') && !userRoles.includes('group') && !userRoles.includes('manager') && !userRoles.includes('admin')) {
        return false;
      }
      
      // 不能审批自己的申请
      if (this.isApplicant) return false;
      
      // 判断当前申请状态是否需要审批
      const status = this.leaveDetail.examineInfoResult;
      
      // 状态为已通过、已拒绝或已撤销的不能再审批
      if (status === 2 || status === 3 || status === 4) return false;
      
      // 管理员可以审批所有在审批中的申请
      if (userRoles.includes('admin')) return status === 0 || status === 1;
      
      // 经理可以审批所有在审批中的申请（一级和二级）
      if (userRoles.includes('manager')) return status === 0 || status === 1;
      
      // 组长仅可以审批一级审批中(status=0)的申请，不能审批二级审批
      if (userRoles.includes('group')) return status === 0;
      
      return false;
    }
  }
};
</script>

<style lang="scss">
.detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 30rpx;
}

.detail-card {
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  position: relative;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.status-badge {
  position: absolute;
  top: 30rpx;
  right: 30rpx;
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-pending {
  background-color: rgba(247, 186, 85, 0.1);
  color: #f7ba55;
}

.status-approved {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.status-rejected {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.status-cancelled {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.section {
  margin-bottom: 30rpx;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20rpx;
}

.section:last-child {
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  margin-top: 10rpx;
}

.info-item {
  display: flex;
  padding: 10rpx 0;
}

.item-label {
  width: 150rpx;
  color: #999;
  font-size: 28rpx;
}

.item-value {
  flex: 1;
  color: #333;
  font-size: 28rpx;
}

.reason-text {
  word-break: break-all;
}

.action-btns {
  margin-top: 30rpx;
  display: flex;
  justify-content: center;
}

.approve-btn {
  width: 60%;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #4a90e2;
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 8rpx;
  border: none;
}

.cancel-btn {
  width: 60%;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #f5f5f5;
  color: #666;
  font-size: 28rpx;
  border-radius: 8rpx;
  border: 1px solid #ddd;
}

.process-section {
  margin: 20rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.process-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.process-steps {
  padding: 0 20rpx;
}

.process-step {
  display: flex;
  align-items: flex-start;
  position: relative;
}

.step-icon {
  width: 50rpx;
  height: 50rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  position: relative;
  z-index: 2;
}

.step-icon.completed {
  background-color: #4a90e2;
  border-color: #4a90e2;
}

.step-num {
  font-size: 24rpx;
  color: #999;
}

.step-icon.completed .step-num {
  color: #ffffff;
}

.step-info {
  flex: 1;
  padding-bottom: 40rpx;
}

.step-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 5rpx;
  display: block;
}

.step-time {
  font-size: 24rpx;
  color: #999;
  display: block;
}

.step-line {
  position: absolute;
  top: 50rpx;
  left: 25rpx;
  width: 2rpx;
  height: 40rpx;
  background-color: #ddd;
  z-index: 1;
}

.step-line.line-active {
  background-color: #4a90e2;
}

.approval-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.modal-content {
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  width: 80%;
  max-width: 600rpx;
  position: relative;
  z-index: 10000;
}

.modal-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  text-align: center;
}

.approval-options {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30rpx;
}

.option-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  width: 45%;
  height: 80rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  cursor: pointer;
}

.option-item.option-selected {
  background-color: #4a90e2;
}

.option-icon {
  margin-right: 10rpx;
  font-size: 28rpx;
  color: #333;
}

.option-text {
  font-size: 28rpx;
  color: #333;
}

.option-selected .option-icon,
.option-selected .option-text {
  color: #ffffff;
}

.approval-form {
  margin-bottom: 30rpx;
}

.remark-input {
  width: 100%;
  height: 180rpx;
  border: 1px solid #ddd;
  border-radius: 8rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
}

.modal-btns {
  display: flex;
  justify-content: space-between;
  margin-top: 30rpx;
}

.btn-cancel {
  width: 45%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #f5f5f5;
  color: #666;
  font-size: 28rpx;
  border-radius: 8rpx;
  border: 1px solid #ddd;
}

.btn-submit {
  width: 45%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #4a90e2;
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 8rpx;
  border: none;
}
</style>