<template>
  <view class="page-wrapper">
    <view class="detail-container">
      <u-navbar title="出差详情" :border="false" :placeholder="true" leftIcon="arrow-left" @leftClick="goBack"></u-navbar>
      
      <view class="detail-card">
        <!-- 出差信息 -->
        <view class="section">
          <view class="section-title">出差信息</view>
          
          <view class="info-item">
            <text class="item-label">出差目的地</text>
            <text class="item-value">{{ tripDetail.tripInfoDestination || tripDetail.destination }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">开始日期</text>
            <text class="item-value">{{ tripDetail.tripInfoDateBegin || tripDetail.startDate }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">结束日期</text>
            <text class="item-value">{{ tripDetail.tripInfoDateEnd || tripDetail.endDate }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">出差天数</text>
            <text class="item-value">{{ tripDetail.days }}天</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">出差事由</text>
            <text class="item-value reason-text">{{ tripDetail.tripInfoReason || tripDetail.reason }}</text>
          </view>
          
          <view class="info-item" v-if="tripDetail.remark">
            <text class="item-label">备注信息</text>
            <text class="item-value reason-text">{{ tripDetail.remark }}</text>
          </view>
        </view>
        
        <!-- 申请信息 -->
        <view class="section">
          <view class="section-title">申请信息</view>
          
          <view class="info-item">
            <text class="item-label">申请人</text>
            <text class="item-value">{{ tripDetail.tripInfoName || tripDetail.name }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">所属部门</text>
            <text class="item-value">{{ tripDetail.tripInfoDept || tripDetail.department }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">申请时间</text>
            <text class="item-value">{{ tripDetail.createTime || tripDetail.applyTime }}</text>
          </view>
        </view>
        
        <!-- 审批信息 -->
        <view class="section" v-if="tripDetail.tripInfoResult !== 0">
          <view class="section-title">审批信息</view>
          
          <view class="info-item">
            <text class="item-label">审批人</text>
            <text class="item-value">{{ tripDetail.approver }}</text>
          </view>
          
          <view class="info-item">
            <text class="item-label">审批时间</text>
            <text class="item-value">{{ tripDetail.approvalTime }}</text>
          </view>
          
          <view class="info-item" v-if="tripDetail.approvalRemark">
            <text class="item-label">审批意见</text>
            <text class="item-value reason-text">{{ tripDetail.approvalRemark }}</text>
          </view>
        </view>
        
        <!-- 操作按钮 -->
        <view class="action-btns" v-if="tripDetail.tripInfoResult === 0 || tripDetail.tripInfoResult === 1">
          <!-- 审批按钮：使用canApprove计算属性来控制显示 -->
          <button 
            v-if="canApprove" 
            class="approve-btn" 
            @click="showApproveModal">审批</button>
          <!-- 申请人显示撤销按钮（只在审批中状态时） -->
          <button v-else-if="isApplicant" class="cancel-btn" @click="cancelTrip">撤销申请</button>
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
              <text class="step-time">{{ tripDetail.applyTime }}</text>
            </view>
          </view>
          
          <view class="step-line" :class="{ 'line-active': tripDetail.tripInfoResult > 0 }"></view>
          
          <!-- 对于组长用户，如果是自己的申请则隐藏一级审批步骤 -->
          <template v-if="!(isApplicant && isCurrentUserGroup())">
            <!-- 第二步：一级审批（针对用户） -->
            <view class="process-step">
              <view class="step-icon" :class="{ 'completed': tripDetail.tripInfoResult > 0 }">
                <text class="step-num">2</text>
              </view>
              <view class="step-info">
                <text class="step-name">
                  <template v-if="tripDetail.tripInfoResult === 3">
                    一级审批驳回
                  </template>
                  <template v-else>
                    {{ tripDetail.tripInfoResult > 0 ? '一级审批通过' : '等待一级审批' }}
                  </template>
                </text>
                <text class="step-time" v-if="tripDetail.tripInfoResult > 0">{{ tripDetail.firstApprovalTime || tripDetail.approvalTime }}</text>
              </view>
            </view>
            
            <view class="step-line" :class="{ 'line-active': tripDetail.tripInfoResult > 1 || tripDetail.tripInfoResult === 1 }"></view>
          </template>
          
          <!-- 最后一步：二级审批或最终审批 -->
          <view class="process-step">
            <view class="step-icon" :class="{ 'completed': tripDetail.tripInfoResult > 1 || tripDetail.tripInfoResult === 3, 'in-progress': tripDetail.tripInfoResult === 1 }">
              <text class="step-num">{{ isApplicant && isCurrentUserGroup() ? '2' : '3' }}</text>
            </view>
            <view class="step-info">
              <text class="step-name">
                <template v-if="tripDetail.tripInfoResult === 3">
                  审批驳回
                </template>
                <template v-else-if="isCurrentUserManager() && isApplicant">
                  {{ tripDetail.tripInfoResult === 2 ? '审批通过' : '等待审批' }}
                </template>
                <template v-else-if="isApplicant && isCurrentUserGroup()">
                  {{ tripDetail.tripInfoResult === 1 ? '等待经理审批' : (tripDetail.tripInfoResult === 2 ? '经理审批通过' : '等待经理审批') }}
                </template>
                <template v-else>
                  {{ tripDetail.tripInfoResult === 1 ? '等待二级审批' : (tripDetail.tripInfoResult === 2 ? '二级审批通过' : '等待二级审批') }}
                </template>
              </text>
              <text class="step-time" v-if="tripDetail.tripInfoResult > 1 || tripDetail.tripInfoResult === 3">{{ tripDetail.approvalTime }}</text>
            </view>
          </view>
        </view>
      </view>
    
      <!-- 审批弹窗 -->
      <view class="approval-modal" v-if="showApprovalModal">
        <view class="modal-mask" @click="closeApproveModal"></view>
        <view class="modal-content">
          <view class="modal-title">出差审批</view>
          
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
            <button class="btn-cancel" @click="closeApproveModal">取消</button>
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
// 导入出差相关API
import { getBusinessDetail, approveBusiness, cancelBusiness } from '@/api/business'
// 导入角色工具函数
import { getCurrentRole, isOwnApplication, getCustomStatusText, isGroupOrAbove, isManagerOrAbove } from '@/utils/role-utils'

export default {
  data() {
    return {
      tripId: null,
      // 添加用户角色和权限状态
      hasApprovalAuth: false,
      hasBusinessApprovePermission: false,
      isApplicant: false,
      userInfo: {},
      // 添加审批表单和弹窗控制
      showApprovalModal: false,
      approvalForm: {
        result: 1, // 默认同意
        remark: ''
      },
      tripDetail: {
        id: 1,
        destination: '上海',
        startDate: '2025-05-20',
        endDate: '2025-05-22',
        days: 3,
        reason: '参加行业展会',
        remark: '需要准备演示材料',
        name: '张三',
        department: '市场部',
        tripInfoResult: 0, // 0-一级审批中，1-二级审批中，2-通过，3-未通过
        applyTime: '2025-05-15 14:30:25',
        approver: '李经理',
        firstApprovalTime: '',
        approvalTime: '',
        approvalRemark: '',
        userId: ''
      }
    };
  },
  onLoad(options) {
    // 初始化用户信息
    this.userInfo = this.$store.state.user || {};
    
    if (options.id) {
      this.tripId = options.id;
      // 先获取详情，再检查权限（在getTripDetail成功回调中调用）
      this.getTripDetail();
    } else {
      uni.showToast({
        title: '缺少出差ID参数',
        icon: 'none'
      });
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
      const status = this.tripDetail.tripInfoResult;
      
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
    
    // 获取出差详情
    getTripDetail() {
      console.log('获取出差 ID:', this.tripId);
      
      uni.showLoading({
        title: '加载中...'
      });
      
      getBusinessDetail({ id: this.tripId }).then(res => {
        uni.hideLoading();
        if (res.code === 200) {
          console.log('获取详情成功，原始数据:', JSON.stringify(res.data));
          // 将后端返回的数据映射到前端显示模型
          const data = res.data;
          
          // 直接使用后端返回的原始数据，并添加计算字段
          this.tripDetail = {
            ...data, // 保留所有原始字段
            // 计算出差天数
            days: this.calculateDays(data.tripInfoDateBegin, data.tripInfoDateEnd),
            // 格式化显示字段
            startDate: this.formatDate(data.tripInfoDateBegin),
            endDate: this.formatDate(data.tripInfoDateEnd),
            applyTime: this.formatDateTime(data.createTime),
            // 方便前端显示的映射字段
            destination: data.tripInfoDestination,
            reason: data.tripInfoReason,
            name: data.tripInfoName,
            department: data.tripInfoDept,
            // 审批相关格式化
            firstApprovalTime: data.firstApprovalTime ? this.formatDateTime(data.firstApprovalTime) : '',
            approvalTime: data.approvalTime ? this.formatDateTime(data.approvalTime) : '',
            approver: data.tripInfoPeople || ''
          };
          
          console.log('处理后的tripDetail:', JSON.stringify(this.tripDetail));
          
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
          
          // 在获取数据成功后检查权限
          this.checkUserPermission();
        } else {
          uni.showToast({
            title: res.msg || '获取详情失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        uni.hideLoading();
        uni.showToast({
          title: '网络异常，请稍后重试',
          icon: 'none'
        });
        console.error('获取出差详情失败:', err);
      });
    },
    
    // 格式化日期 YYYY-MM-DD
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 格式化日期时间 YYYY-MM-DD HH:mm:ss
    formatDateTime(dateTimeString) {
      if (!dateTimeString) return '';
      const date = new Date(dateTimeString);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      const seconds = date.getSeconds().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    
    // 计算天数
    calculateDays(startDateStr, endDateStr) {
        if (!startDateStr || !endDateStr) return 0;
        try {
            const start = new Date(startDateStr);
            const end = new Date(endDateStr);
            // 设置时间为中午12点，避免时区和夏令时问题
            start.setHours(12, 0, 0, 0);
            end.setHours(12, 0, 0, 0);
            const diffTime = Math.abs(end - start);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // 加1计算，包含起止日期
            return diffDays > 0 ? diffDays : 0;
        } catch (e) {
            console.error("Error calculating days:", e);
            return 0;
        }
    },
    
    // 获取状态文本
    getStatusText(status) {
      // 使用角色工具函数中的getCustomStatusText
      const isOwn = this.isApplicant;
      return getCustomStatusText(status, isOwn);
    },
    
    // 撤销出差申请
    cancelTrip() {
      uni.showModal({
        title: '提示',
        content: '确定要撤销此出差申请吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '处理中...'
            });
            
            cancelBusiness({ id: this.tripDetail.id }).then(res => {
              uni.hideLoading();
              if (res.code === 200) {
                uni.showToast({
                  title: '撤销成功',
                  icon: 'success',
                  duration: 2000
                });
                
                // 更新本地状态
                this.tripDetail.tripInfoResult = 4; // 使用正确的撤销状态码4
                
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
              console.error('撤销出差申请失败:', err);
            });
          }
        }
      });
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
      console.log('- 出差当前状态:', this.tripDetail.tripInfoResult);
      console.log('===== 权限检查结束 =====');
    },
    
    // 显示审批弹窗
    showApproveModal() {
        this.approvalForm.result = 1; // 重置默认值为同意
        this.approvalForm.remark = ''; // 清空备注
        this.showApprovalModal = true;
    },

    // 关闭审批弹窗
    closeApproveModal() {
        this.showApprovalModal = false;
    },
    
    // 切换审批结果 (同意/拒绝)
    toggleApprovalResult(result) {
        this.approvalForm.result = result;
    },
    
    // 格式化日期 (yyyy-MM-dd)
    formatDate(dateStr) {
      if (!dateStr) return '';
      
      let date;
      if (typeof dateStr === 'string') {
        // 处理字符串日期
        date = new Date(dateStr.replace(/-/g, '/'));
      } else {
        date = new Date(dateStr);
      }
      
      if (isNaN(date.getTime())) {
        return dateStr;
      }
      
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      
      return `${year}-${month}-${day}`;
    },
    
    // 计算两个日期之间的天数
    calculateDays(startDate, endDate) {
      if (!startDate || !endDate) return 0;
      
      // 将字符串日期转换为Date对象
      const start = typeof startDate === 'string' ? new Date(startDate.replace(/-/g, '/')) : new Date(startDate);
      const end = typeof endDate === 'string' ? new Date(endDate.replace(/-/g, '/')) : new Date(endDate);
      
      // 检查是否是有效的Date对象
      if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        return 0;
      }
      
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

    // 提交审批
    submitApproval() {
        if (this.approvalForm.result === 2 && !this.approvalForm.remark.trim()) {
            uni.showToast({
                title: '请填写驳回原因',
                icon: 'none'
            });
            return;
        }

        uni.showLoading({ title: '提交中...' });

        // 准备结果值
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
            else if (isGroup && this.tripDetail.tripInfoResult === 0) {
                resultValue = 1; // 组长同意，进入二级审批
            }
            // 其他情况（组长对二级审批进行审批）设为2通过
            else if (this.tripDetail.tripInfoResult === 1) {
                resultValue = 2; // 二级审批通过
            }
        }
        
        console.log('审批状态:', this.tripDetail.tripInfoResult, '选择结果:', this.approvalForm.result, '用户角色:', userRoles, '映射为:', resultValue);
        
        const approvalData = {
            id: this.tripDetail.id, // 使用tripDetail.id而不是tripId以确保一致性
            tripInfoResult: resultValue, // 用映射后的结果值
            remark: this.approvalForm.remark,
            tripInfoPeople: this.userInfo.name || '当前用户',
            // 审批阶段标识
            approvalStage: this.tripDetail.tripInfoResult === 0 ? 1 : 2, // 1=一级审批，2=二级审批
            approvalStatus: this.tripDetail.tripInfoResult
        };
        
        console.log('提交审批数据:', approvalData);

        approveBusiness(approvalData).then(res => {
            uni.hideLoading();
            if (res.code === 200) {
                // 关闭审批弹窗
                this.closeApproveModal();
                
                // 审批成功提示
                uni.showToast({
                    title: '审批操作成功',
                    icon: 'success',
                    duration: 2000
                });
                
                // 重要修改: 审批成功后重新获取出差详情
                // 等待一小段时间确保后端数据已更新
                setTimeout(() => {
                    console.log('审批成功，重新获取出差详情...');
                    this.getTripDetail();
                }, 500);
            } else {
                uni.showToast({
                    title: res.msg || '审批失败',
                    icon: 'none'
                });
            }
        }).catch(err => {
            uni.hideLoading();
            uni.showToast({
                title: '网络异常，请稍后重试',
                icon: 'none'
            });
            console.error('提交审批失败:', err);
        });
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

/* 审批按钮样式 */
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

/* 审批弹窗样式 */
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