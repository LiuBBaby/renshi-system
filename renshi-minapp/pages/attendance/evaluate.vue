<template>
	<view class="container">
		<view class="evaluation-container">

			<!-- 筛选区域 -->
			<view class="filters-wrap">
				<view class="filter-row">
					<!-- 部门选择 -->
					<view class="filter-item">
						<text class="filter-label">部门：</text>
						<view class="filter-content" @click="showDeptPopup">
							<text class="filter-text">{{ deptName || '全部' }}</text>
							<uni-icons type="right" size="16" color="#999"></uni-icons>
						</view>
					</view>
					
					<!-- 月份选择 -->
					<view class="filter-item">
						<text class="filter-label">月份：</text>
						<view class="filter-date">
							<picker mode="date" fields="month" :value="yearMonth" @change="onYearMonthChange" style="flex: 1;">
								<view class="date-trigger year-month-trigger">
									<text class="date-text">{{ yearMonth || '请选择' }}</text>
									<uni-icons type="right" size="16" color="#999"></uni-icons>
								</view>
							</picker>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 考勤列表 -->
			<view class="attendance-list" v-if="attendanceList.length > 0">
				<!-- 表头 -->
				<view class="list-header">
					<text class="header-item header-name">员工姓名</text>
					<text class="header-item header-date">考勤日期</text>
					<text class="header-item header-status">考勤状态</text>
					<text class="header-item header-evaluate">评价</text>
					<text class="header-item header-action">操作</text>
				</view>
				
				<!-- 列表内容 -->
				<view class="list-content">
					<view class="list-item" v-for="(item, index) in attendanceList" :key="index">
						<!-- 员工姓名 -->
						<text class="item-name">{{ item.attendanceInfoName || '-' }}</text>
						
						<!-- 考勤日期 -->
						<text class="item-date">{{ formatDate(item.attendanceInfoDate) }}</text>
						
						<!-- 考勤状态 -->
						<view class="item-status-container">
							<view class="status-item">
								<text class="status-label">上班：</text>
								<text :class="[
									item.checkInStatus === 0 ? 'status-normal' : '',
									item.checkInStatus === 1 ? 'status-late' : '',
									item.checkInStatus === 3 ? 'status-absent' : ''
								]">{{ getCheckInStatusText(item.checkInStatus) }}</text>
							</view>
							<view class="status-item">
								<text class="status-label">下班：</text>
								<text :class="[
									item.checkOutStatus === 0 ? 'status-normal' : '',
									item.checkOutStatus === 2 ? 'status-early' : '',
									item.checkOutStatus === 3 ? 'status-absent' : ''
								]">{{ getCheckOutStatusText(item.checkOutStatus) }}</text>
							</view>
						</view>
						
						<!-- 评价 -->
						<text class="item-evaluate" :class="{
							'evaluate-none': item.evaluate === null || item.evaluate === undefined,
							'evaluate-excellent': item.evaluate === 0,
							'evaluate-good': item.evaluate === 1,
							'evaluate-average': item.evaluate === 2,
							'evaluate-poor': item.evaluate === 3
						}">{{ getEvaluateText(item.evaluate) }}</text>
						
						<!-- 操作按钮 -->
						<view class="item-action">
							<button class="btn-evaluate" size="mini" type="primary" @click="handleEvaluate(item)" v-if="canEvaluate(item)">评价</button>
							<button class="btn-evaluate" size="mini" type="default" disabled v-else>已评价</button>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 加载更多/空数据 -->
			<view class="load-more" v-if="attendanceList.length > 0 && hasMoreData">
				<text>{{ loadingMore ? '加载中...' : '上拉加载更多' }}</text>
			</view>
			
			<view class="empty-data" v-if="attendanceList.length === 0 && !loading">
				<image src="/static/images/empty-data.png" mode="aspectFit" style="width: 240rpx; height: 240rpx;"></image>
				<text class="empty-text">暂无考勤数据</text>
			</view>
		</view>
		
		<!-- 部门选择弹窗 -->
		<uni-popup ref="deptPopup" type="bottom">
			<view class="popup-container">
				<view class="popup-header">
					<text class="popup-title">选择部门</text>
					<text class="popup-close" @click="closeDeptPopup">取消</text>
				</view>
				<scroll-view scroll-y style="max-height: 600rpx;">
					<view class="dept-list">
						<view class="dept-item" @click="selectDept('')">
							<text class="dept-name">全部部门</text>
							<uni-icons v-if="deptId === ''" type="checkmarkempty" size="18" color="#2979ff"></uni-icons>
						</view>
						<view class="dept-item" v-for="(dept, index) in deptList" :key="index" @click="selectDept(dept.deptId, dept.deptName)">
							<text class="dept-name">{{ dept.deptName }}</text>
							<uni-icons v-if="deptId === dept.deptId" type="checkmarkempty" size="18" color="#2979ff"></uni-icons>
						</view>
					</view>
				</scroll-view>
			</view>
		</uni-popup>
		
		<!-- 评价弹窗 -->
		<uni-popup ref="evaluatePopup" type="center">
			<view class="popup-content">
				<view class="popup-form">
					<view class="form-item">
						<text class="form-label">员工姓名</text>
						<text class="form-value">{{ currentAttendance.attendanceInfoName }}</text>
					</view>
					<view class="form-item">
						<text class="form-label">考勤日期</text>
						<text class="form-value">{{ formatDate(currentAttendance.attendanceInfoDate) }}</text>
					</view>
					<view class="form-item">
						<text class="form-label">评价等级</text>
						<view class="form-value">
							<uni-data-checkbox v-model="evaluateValue" :localdata="evaluateOptions" @change="handleEvaluateChange"></uni-data-checkbox>
						</view>
					</view>
				</view>
				<view class="popup-footer">
					<button class="btn-cancel" @click="closeEvaluatePopup">取消</button>
					<button class="btn-submit" @click="submitEvaluate">确定</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import { getAttendanceList, evaluateAttendance } from '@/api/business/attendance';
	import { getDeptChildren } from '@/api/system/dept';
	import { formatDate } from '@/utils/date';
	import { getDeptMonthlyDetail } from '@/api/business/attendance';
	
	export default {
		data() {
			// 获取当前日期
			const currentDate = new Date();
			const year = currentDate.getFullYear();
			const month = String(currentDate.getMonth() + 1).padStart(2, '0');
			const yearMonth = `${year}-${month}`;
			
			return {
				// 查询参数
				queryParams: {
					pageNum: 1,
					pageSize: 10,
					deptId: '',
					year: year,
					month: parseInt(month)
				},
				// 部门列表
				deptList: [],
				deptName: '',
				deptId: '',
				
				// 年月选择
				yearMonth: yearMonth,
				
				// 考勤列表
				attendanceList: [],
				total: 0,
				hasMoreData: true,
				loading: false,
				loadingMore: false,
				
				// 评价相关
				currentAttendance: {},
				evaluateValue: 0,
				evaluateOptions: [
					{ text: '优秀', value: 0 },
					{ text: '良好', value: 1 },
					{ text: '中等', value: 2 },
					{ text: '较差', value: 3 }
				]
			};
		},
		created() {

			this.loadDeptList();
			this.loadAttendanceList();

		}
		,
		onLoad() {
			console.log('页面onLoad被触发');
			this.loadDeptList();
			this.loadAttendanceList();
		},
		onPullDownRefresh() {
			this.resetList();
			this.loadAttendanceList();
			setTimeout(() => {
				uni.stopPullDownRefresh();
			}, 1000);
		},
		onReachBottom() {
			if (this.hasMoreData) {
				this.loadMoreData();
			}
		},
		methods: {
			// 加载部门列表
			async loadDeptList() {

				console.log("test部门数据被调用")

				try {
					const res = await getDeptChildren();

					console.log( "部门列表数据：" + res)
					this.deptList = res.data || [];
				} catch (error) {
					console.error('获取部门列表失败', error);
					uni.showToast({
						title: '获取部门列表失败',
						icon: 'none'
					});
				}
			},
			
			// 加载考勤列表
			loadAttendanceList() {
				if (this.loading) return;
				
				this.loading = true;
				
				// 使用queryParams中的年月构建参数
				const params = {
					deptId: this.deptId || '',
					year: this.queryParams.year,
					month: this.queryParams.month
				};
				
				console.log('查询参数:', params);
				
				// 调用接口获取数据
				getDeptMonthlyDetail(params.deptId, params.year, params.month).then(res => {
					console.log('考勤数据返回:', res);
					if (res.code === 200) {
						this.attendanceList = res.data || [];
						this.total = this.attendanceList.length;
						this.hasMoreData = false; // 一次性加载所有数据
					} else {
						this.attendanceList = [];
						this.total = 0;
						uni.showToast({
							title: res.msg || '获取考勤列表失败',
							icon: 'none'
						});
					}
				}).catch(err => {
					console.error('获取考勤数据失败', err);
					uni.showToast({
						title: '获取考勤数据失败',
						icon: 'none'
					});
				}).finally(() => {
					this.loading = false;
				});
			},
			
			// 加载更多数据
			async loadMoreData() {
				if (this.loadingMore) return;
				
				this.loadingMore = true;
				this.queryParams.pageNum += 1;
				
				try {
					const res = await getAttendanceList(this.queryParams);
					const { rows, total } = res.data;
					
					if (rows && rows.length > 0) {
						this.attendanceList = [...this.attendanceList, ...rows];
					}
					
					this.total = total || 0;
					this.hasMoreData = this.attendanceList.length < this.total;
				} catch (error) {
					console.error('加载更多数据失败', error);
					this.queryParams.pageNum -= 1;
				} finally {
					this.loadingMore = false;
				}
			},
			
			// 重置列表
			resetList() {
				this.queryParams.pageNum = 1;
				this.attendanceList = [];
				this.total = 0;
				this.hasMoreData = true;
			},
			
			// 部门选择相关
			showDeptPopup() {
				this.$refs.deptPopup.open();
			},
			
			closeDeptPopup() {
				this.$refs.deptPopup.close();
			},
			
			selectDept(deptId, deptName = '全部部门') {
				this.deptId = deptId;
				this.deptName = deptName;
				this.queryParams.deptId = deptId;
				this.closeDeptPopup();
				
				this.resetList();
				this.loadAttendanceList();
			},
			
			// 年月选择处理
			onYearMonthChange(e) {
				this.yearMonth = e.detail.value;
				// 解析年月
				const [year, month] = this.yearMonth.split('-');
				this.queryParams.year = parseInt(year);
				this.queryParams.month = parseInt(month);
				
				// 重置列表并加载数据
				this.resetList();
				this.loadAttendanceList();
			},
			
			// 评价相关
			handleEvaluate(item) {
				this.currentAttendance = item;
				this.evaluateValue = item.evaluate !== null && item.evaluate !== undefined ? item.evaluate : 0;
				this.$refs.evaluatePopup.open();
			},
			
			closeEvaluatePopup() {
				this.$refs.evaluatePopup.close();
			},
			
			handleEvaluateChange(e) {
				this.evaluateValue = e.detail.value;
			},
			
			async submitEvaluate() {
				try {
					const params = {
						attendanceInfoId: this.currentAttendance.attendanceInfoId,
						evaluate: this.evaluateValue
					};
					
					const res = await evaluateAttendance(params);
					if (res.code === 200) {
						uni.showToast({
							title: '评价成功',
							icon: 'success'
						});
						
						// 更新本地数据
						const index = this.attendanceList.findIndex(item => item.attendanceInfoId === this.currentAttendance.attendanceInfoId);
						if (index !== -1) {
							this.attendanceList[index].evaluate = this.evaluateValue;
						}
						
						this.closeEvaluatePopup();
					} else {
						uni.showToast({
							title: res.msg || '评价失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('提交评价失败', error);
					uni.showToast({
						title: '提交评价失败',
						icon: 'none'
					});
				}
			},
			
			// 工具方法
			formatDate,
			
			getCheckInStatusText(status) {
				if (status === null || status === undefined) return '未知';
				const statusMap = {
					0: '正常',
					1: '迟到',
					3: '缺勤'
				};
				return statusMap[status] || '未知';
			},
			
			getCheckInStatusClass(status) {
				if (status === null || status === undefined) return '';
				const classMap = {
					0: 'status-normal',
					1: 'status-late',
					3: 'status-absent'
				};
				return classMap[status] || '';
			},
			
			getCheckOutStatusText(status) {
				if (status === null || status === undefined) return '未知';
				const statusMap = {
					0: '正常',
					2: '早退',
					3: '缺勤'
				};
				return statusMap[status] || '未知';
			},
			
			getCheckOutStatusClass(status) {
				if (status === null || status === undefined) return '';
				const classMap = {
					0: 'status-normal',
					2: 'status-early',
					3: 'status-absent'
				};
				return classMap[status] || '';
			},
			
			getEvaluateText(evaluate) {
				if (evaluate === null || evaluate === undefined) return '未评价';
				const evaluateMap = {
					0: '优秀',
					1: '良好',
					2: '中等',
					3: '较差'
				};
				return evaluateMap[evaluate] || '未知';
			},
			
			getEvaluateClass(evaluate) {
				if (evaluate === null || evaluate === undefined) return 'evaluate-none';
				const classMap = {
					0: 'evaluate-excellent',
					1: 'evaluate-good',
					2: 'evaluate-average',
					3: 'evaluate-poor'
				};
				return classMap[evaluate] || '';
			},
			
			canEvaluate(item) {
				return item.evaluate === null || item.evaluate === undefined;
			}
		}
	};
</script>

<style scoped>
/* 容器样式 */
.container {
	background-color: #f5f7fa;
	padding-bottom: 30rpx;
	min-height: 100vh;
}

.evaluation-container {
	padding: 20rpx;
}

/* 页面标题 */
.page-header {
	margin-bottom: 20rpx;
}

.page-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

/* 筛选区域 */
.filters-wrap {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 20rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.filter-row {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
	align-items: center;
	flex-wrap: nowrap;
}

.filter-item {
	flex: 1;
	margin-right: 20rpx;
	display: flex;
	align-items: center;
}

.filter-item:last-child {
	margin-right: 0;
}

.filter-label {
	font-size: 28rpx;
	color: #666;
	margin-right: 10rpx;
	display: inline-block;
}

.filter-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 70rpx;
	padding: 0 20rpx;
	border: 1px solid #e0e0e0;
	border-radius: 8rpx;
	background-color: #f9f9f9;
	flex: 1;
}

.filter-text {
	font-size: 28rpx;
	color: #333;
	max-width: 160rpx;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

/* 日期选择器样式 */
.filter-date {
	flex: 1;
	display: flex;
}

.date-trigger {
	height: 70rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	background-color: #F8F8F8;
	border-radius: 8rpx;
	flex: 1;
}

.year-month-trigger {
	padding: 0 20rpx;
	border: 1px solid #e0e0e0;
}

.date-text {
	font-size: 28rpx;
	color: #333;
}

/* 考勤列表 */
.attendance-list {
	background-color: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.list-header {
	display: flex;
	background-color: #f5f7fa;
	padding: 20rpx 16rpx;
	border-bottom: 1px solid #eee;
}

.header-item {
	font-size: 26rpx;
	color: #666;
	font-weight: bold;
	text-align: center;
}

.header-name {
	flex: 1;
}

.header-date {
	flex: 1;
}

.header-status {
	flex: 1.5;
}

.header-evaluate {
	flex: 0.8;
}

.header-action {
	flex: 1;
}


.list-item {
	display: flex;
	padding: 20rpx 16rpx;
	border-bottom: 1px solid #f0f0f0;
	align-items: center;
}

.list-item:last-child {
	border-bottom: none;
}

.item-name {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	text-align: center;
}

.item-date {
	flex: 1;
	font-size: 26rpx;
	color: #666;
	text-align: center;
}

.item-status-container {
	flex: 1.5;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.status-item {
	display: flex;
	align-items: center;
	margin-bottom: 6rpx;
}

.status-item:last-child {
	margin-bottom: 0;
}

.status-label {
	font-size: 24rpx;
	color: #666;
	margin-right: 4rpx;
}

.status-normal {
	font-size: 24rpx;
	color: #52c41a;
}

.status-late, .status-early {
	font-size: 24rpx;
	color: #faad14;
}

.status-absent {
	font-size: 24rpx;
	color: #f5222d;
}

.item-evaluate {
	flex: 0.8;
	font-size: 26rpx;
	text-align: center;
}

.evaluate-none {
	color: #999;
}

.evaluate-excellent {
	color: #52c41a;
	font-weight: bold;
}

.evaluate-good {
	color: #1890ff;
}

.evaluate-average {
	color: #faad14;
}

.evaluate-poor {
	color: #f5222d;
}

.item-action {
	flex: 1;
	display: flex;
	justify-content: center;
}

.btn-evaluate {
	font-size: 24rpx;
	padding: 0 20rpx;
	height: 50rpx;
	line-height: 50rpx;
}

/* 弹窗 */
.popup-container {
	background-color: #fff;
	border-radius: 12rpx 12rpx 0 0;
	overflow: hidden;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1px solid #f0f0f0;
}

.popup-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.popup-close {
	font-size: 28rpx;
	color: #999;
}

.dept-list {
	padding: 0 20rpx;
}

.dept-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx 20rpx;
	border-bottom: 1px solid #f0f0f0;
}

.dept-item:last-child {
	border-bottom: none;
}

.dept-name {
	font-size: 28rpx;
	color: #333;
}

/* 评价弹窗 */
.popup-content {
	background-color: #fff;
	border-radius: 12rpx;
	width: 600rpx;
	overflow: hidden;
}

.popup-form {
	padding: 30rpx;
}

.form-item {
	margin-bottom: 20rpx;
}

.form-item:last-child {
	margin-bottom: 0;
}

.form-label {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 10rpx;
	display: block;
}

.form-value {
	font-size: 28rpx;
	color: #333;
}

.popup-footer {
	display: flex;
	border-top: 1px solid #f0f0f0;
}

.btn-cancel, .btn-submit {
	flex: 1;
	height: 80rpx;
	line-height: 80rpx;
	text-align: center;
	font-size: 28rpx;
	border-radius: 0;
}

.btn-cancel {
	background-color: #f5f5f5;
	color: #666;
}

.btn-submit {
	background-color: #2979ff;
	color: #fff;
}

/* 加载更多和空数据 */
.load-more {
	text-align: center;
	padding: 30rpx 0;
	color: #999;
	font-size: 26rpx;
}

.empty-data {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-text {
	margin-top: 20rpx;
	font-size: 28rpx;
	color: #999;
}
</style> 