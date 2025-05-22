<template>
	<view class="attendance-container">
		<!-- 顶部信息卡片（只显示时间） -->
		<view class="info-card">
			<view class="time-display">
				<view class="date">{{ currentDate }}</view>
				<view class="time">{{ currentTime }}</view>
			</view>
		</view>
		
		<!-- 考勤规则信息卡片 -->
		<view class="rule-card" v-if="attendanceConfig">
			<view class="rule-content">
				<view class="rule-item">
					<text class="rule-label">上班时间：</text>
					<text class="rule-value">{{ attendanceConfig.attendance_time || "08:30" }}</text>
				</view>
				<view class="rule-item">
					<text class="rule-label">下班时间：</text>
					<text class="rule-value">{{ attendanceConfig.closing_time || "18:30" }}</text>
				</view>
			</view>
		</view>
		
		<!-- 今日打卡流程 -->
		<view class="checkin-status-card">
			<view class="status-header">
				<text class="status-title">今日打卡状态</text>
			</view>
			
			<view class="status-content">
				<!-- 使用卡片式布局替代圆形图标 -->
				<view class="status-cards">
					<!-- 签到卡片 -->
					<view class="status-card" :class="{'completed': todayInfo.hasCheckedIn}">
						<view class="card-header">
							<text class="card-title">签到</text>
							<text class="card-icon" v-if="todayInfo.hasCheckedIn">✓</text>
						</view>
						<view class="card-content" v-if="todayInfo.hasCheckedIn">
							<view class="card-time">{{ formatDateTime(todayInfo.checkInTime) }}</view>
							<view class="card-status-tag" :class="todayInfo.checkInStatusClass">
								{{ getStatusText(todayInfo.checkInStatus) }}
							</view>
						</view>
						<view class="card-content" v-else>
							<view class="card-placeholder">未签到</view>
						</view>
					</view>
					
					<!-- 中间连接器 -->
					<view class="card-connector" :class="{
						'active': todayInfo.hasCheckedIn && !todayInfo.hasCheckedOut, 
						'complete': todayInfo.hasCheckedOut
					}"></view>
					
					<!-- 签退卡片 -->
					<view class="status-card" :class="{
						'completed': todayInfo.hasCheckedOut,
						'waiting': todayInfo.hasCheckedIn && !todayInfo.hasCheckedOut
					}">
						<view class="card-header">
							<text class="card-title">签退</text>
							<text class="card-icon" v-if="todayInfo.hasCheckedOut">✓</text>
						</view>
						<view class="card-content" v-if="todayInfo.hasCheckedOut">
							<view class="card-time">{{ formatDateTime(todayInfo.checkOutTime) }}</view>
							<view class="card-status-tag" :class="todayInfo.checkOutStatusClass">
								{{ getStatusText(todayInfo.checkOutStatus) }}
							</view>
						</view>
						<view class="card-content" v-else>
							<view class="card-placeholder">未签退</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 打卡按钮 -->
		<view class="btn-container">
			<button class="check-btn" :disabled="loading || !canCheckIn" @click="handleCheckIn" 
                :class="{'check-in': !isCheckedIn, 'check-out': isCheckedIn, 'disabled': !canCheckIn}">
				{{ isCheckedIn ? '下班打卡' : '上班打卡' }}
				<view class="btn-loading" v-if="loading"></view>
			</button>
			
			<!-- 显示不能打卡的原因 -->
			<view class="check-tip" v-if="!canCheckIn">
				{{ checkInMessage }}
			</view>
		</view>
		
		<!-- 地图组件 -->
		<view class="map-container">
			<map 
				id="locationMap" 
				class="location-map" 
				:latitude="latitude" 
				:longitude="longitude"
				:markers="markers"
				scale="16"
				show-location
			></map>
			
			<!-- 位置刷新按钮 -->
			<view class="map-controls">
				<view class="location-refresh" @click="refreshLocation">
					<text class="iconfont icon-refresh"></text>
					<text class="refresh-text">刷新位置</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getAttendanceConfig } from '@/api/attendance';

export default {
	data() {
		return {
			currentDate: '',
			currentTime: '',
			timer: null,
			address: '',
			latitude: 0,
			longitude: 0,
			locationAccuracy: 0,
			checkInTime: '',
			checkOutTime: '',
			isCheckedIn: false,
			loading: false,
			markers: [],
			todayInfo: {
				hasCheckedIn: false,
				hasCheckedOut: false,
				checkInTime: null,
				checkOutTime: null,
				checkInStatus: null,
				checkOutStatus: null,
				checkInStatusClass: '',
				checkOutStatusClass: '',
				goAddress: null,
				outAddress: null,
				goLatitude: null,
				goLongitude: null,
				outLatitude: null,
				outLongitude: null
			},
			attendanceConfig: null,
			canCheckIn: false,
			checkInMessage: '',
			currentHour: 0,
			currentMinute: 0
		}
	},
	onLoad() {
		this.updateDateTime();
		this.timer = setInterval(() => {
			this.updateDateTime();
			this.checkCanCheckIn(); // 每秒检查是否可以打卡
		}, 1000);
		
		console.log('当前使用的后端API地址：', this.$config.baseUrl);
		
		this.getLocation();
		this.checkTodayStatus();
		this.getAttendanceConfig(); // 获取考勤配置
	},
	onUnload() {
		if (this.timer) {
			clearInterval(this.timer);
			this.timer = null;
		}
	},
	methods: {
		updateDateTime() {
			const now = new Date();
			const year = now.getFullYear();
			const month = (now.getMonth() + 1).toString().padStart(2, '0');
			const day = now.getDate().toString().padStart(2, '0');
			const hours = now.getHours().toString().padStart(2, '0');
			const minutes = now.getMinutes().toString().padStart(2, '0');
			const seconds = now.getSeconds().toString().padStart(2, '0');
			
			this.currentDate = `${year}年${month}月${day}日`;
			this.currentTime = `${hours}:${minutes}:${seconds}`;
			
			// 更新当前小时和分钟，用于检查是否可以打卡
			this.currentHour = now.getHours();
			this.currentMinute = now.getMinutes();
		},
		
		// 获取考勤配置信息
		getAttendanceConfig() {
			getAttendanceConfig().then(res => {
				if (res.code === 200 && res.data) {
					console.log('获取到考勤配置：', res.data);
					this.attendanceConfig = res.data;
					this.checkCanCheckIn(); // 获取配置后检查是否可以打卡
				} else {
					console.error('获取考勤配置失败:', res.msg);
					uni.showToast({
						title: '获取考勤配置失败',
						icon: 'none'
					});
				}
			}).catch(err => {
				console.error('获取考勤配置请求失败:', err);
				uni.showToast({
					title: '获取考勤配置请求失败',
					icon: 'none'
				});
			});
		},
		
		// 检查当前时间是否可以打卡
		checkCanCheckIn() {
			if (!this.attendanceConfig) {
				this.canCheckIn = false;
				this.checkInMessage = '正在加载考勤配置...';
				return;
			}
			
			// 判断是上班打卡还是下班打卡
			const isCheckinTime = !this.isCheckedIn;
			const currentTime = this.timeToMinutes(this.currentHour, this.currentMinute);
			
			if (isCheckinTime) {
				// 上班打卡时间判断
				const morningStartTime = this.parseTimeToMinutes(this.attendanceConfig.attendance_start_time || "06:00");
				const morningEndTime = this.parseTimeToMinutes(this.attendanceConfig.attendance_end_time || "09:30");
				
				if (currentTime >= morningStartTime && currentTime <= morningEndTime) {
					this.canCheckIn = true;
					this.checkInMessage = '';
				} else {
					this.canCheckIn = false;
					this.checkInMessage = `上班打卡时间为${this.attendanceConfig.attendance_start_time || "06:00"}到${this.attendanceConfig.attendance_end_time || "09:30"}`;
				}
			} else {
				// 下班打卡时间判断
				const afternoonStartTime = this.parseTimeToMinutes(this.attendanceConfig.closing_start_time || "16:30");
				const afternoonEndTime = this.parseTimeToMinutes(this.attendanceConfig.closing_end_time || "23:59");
				
				if (currentTime >= afternoonStartTime && currentTime <= afternoonEndTime) {
					this.canCheckIn = true;
					this.checkInMessage = '';
				} else {
					this.canCheckIn = false;
					this.checkInMessage = `下班打卡时间为${this.attendanceConfig.closing_start_time || "16:30"}到${this.attendanceConfig.closing_end_time || "23:59"}`;
				}
			}
		},
		
		// 将时间字符串转换为分钟数
		parseTimeToMinutes(timeStr) {
			if (!timeStr) return 0;
			const [hours, minutes] = timeStr.split(':').map(Number);
			return hours * 60 + minutes;
		},
		
		// 将小时和分钟转换为分钟数
		timeToMinutes(hours, minutes) {
			return hours * 60 + minutes;
		},
		
		// 判断打卡状态（正常、迟到、早退）
		getCheckInStatus(currentTime) {
			if (!this.attendanceConfig) return 0; // 默认正常
			
			// 是否为上班打卡
			if (!this.isCheckedIn) {
				const morningStartTime = this.parseTimeToMinutes(this.attendanceConfig.attendance_start_time || "06:00");
				const normalEndTime = this.parseTimeToMinutes(this.attendanceConfig.attendance_time || "08:30");
				const lateEndTime = this.parseTimeToMinutes(this.attendanceConfig.attendance_end_time || "09:30");
				
				if (currentTime <= normalEndTime) {
					return 0; // 正常
				} else if (currentTime <= lateEndTime) {
					return 1; // 迟到
				}
			} else {
				// 下班打卡
				const earlyStartTime = this.parseTimeToMinutes(this.attendanceConfig.closing_start_time || "16:30");
				const normalStartTime = this.parseTimeToMinutes(this.attendanceConfig.closing_time || "18:30");
				const normalEndTime = this.parseTimeToMinutes(this.attendanceConfig.closing_end_time || "23:59");
				
				if (currentTime >= normalStartTime) {
					return 0; // 正常
				} else if (currentTime >= earlyStartTime) {
					return 2; // 早退
				}
			}
			
			return 0; // 默认正常
		},
		
		refreshLocation() {
			uni.showLoading({
				title: '正在刷新位置'
			});
			this.startGetLocation(true);
		},
		
		getLocation() {
			uni.getSetting({
				success: (res) => {
					if (!res.authSetting['scope.userLocation']) {
						uni.authorize({
							scope: 'scope.userLocation',
							success: () => {
								this.startGetLocation();
							},
							fail: () => {
								uni.showModal({
									title: '提示',
									content: '需要获取位置权限才能进行打卡，是否前往设置页面开启权限？',
									success: (modalRes) => {
										if (modalRes.confirm) {
											uni.openSetting();
										} else {
											uni.showToast({
												title: '无法获取位置，打卡功能受限',
												icon: 'none'
											});
										}
									}
								});
							}
						});
					} else {
						this.startGetLocation();
					}
				},
				fail: (err) => {
					console.error('获取设置信息失败', err);
					uni.showToast({
						title: '获取设置信息失败',
						icon: 'none'
					});
				}
			});
		},
		
		startGetLocation(isRefresh = false) {
			uni.getLocation({
				type: 'gcj02',
				isHighAccuracy: true,
				highAccuracyExpireTime: 5000,
				success: res => {
					this.latitude = res.latitude;
					this.longitude = res.longitude;
					this.locationAccuracy = res.accuracy ? Math.round(res.accuracy) : 0;
					
					if (res.address) {
						this.address = res.address;
					} else {
						this.address = `位置坐标(${res.latitude.toFixed(6)}, ${res.longitude.toFixed(6)})`;
					}
					
					this.markers = [{
						id: 1,
						latitude: res.latitude,
						longitude: res.longitude,
						width: 32,
						height: 32,
						callout: {
							content: '当前位置',
							color: '#ffffff',
							fontSize: 12,
							bgColor: '#1296db',
							padding: 5,
							borderRadius: 3,
							display: 'ALWAYS'
						}
					}];
					
					if (isRefresh) {
						uni.hideLoading();
						uni.showToast({
							title: '位置已更新',
							icon: 'success'
						});
					}
				},
				fail: err => {
					console.error('获取位置失败', err);
					if (isRefresh) uni.hideLoading();
					uni.showToast({
						title: '获取位置失败，请检查GPS是否开启',
						icon: 'none'
					});
				}
			});
		},
		
		handleCheckIn() {
			// 检查是否正在加载中，防止重复提交
			if (this.loading) {
				return;
			}
			
			// 检查是否可以打卡
			if (!this.canCheckIn) {
				uni.showToast({
					title: this.checkInMessage,
					icon: 'none'
				});
				return;
			}
			
			// 先设置loading状态
			this.loading = true;
			
			// 确保已获取位置信息
			if (!this.latitude || !this.longitude) {
				uni.showToast({
					title: '正在获取位置，请稍候再试',
					icon: 'none'
				});
				this.loading = false;
				return;
			}
			
			// 首先检查是否有已批准的请假或出差记录
			uni.request({
				url: this.$config.baseUrl + '/attendance/checkLeaveOrBusinessTrip',
				method: 'GET',
				header: {
					'Authorization': uni.getStorageSync('App-Token')
				},
				success: (res) => {
					if (res.data.code === 200) {
						const result = res.data.data;
						
						// 检查是否有已批准的请假或出差
						if (result.hasApprovedLeave || result.hasApprovedBusinessTrip) {
							let message = '';
							
							if (result.hasApprovedLeave) {
								const leaveInfo = result.leaveInfo;
								const leaveType = leaveInfo.leaveType || '请假';
								message = `您当前有已批准的${leaveType}记录，不能进行打卡！`;
							} else if (result.hasApprovedBusinessTrip) {
								message = '您当前有已批准的出差记录，不能进行打卡！';
							}
							
							// 直接提示并拒绝打卡
							uni.showToast({
								title: message,
								icon: 'none',
								duration: 3000
							});
							
							// 取消加载状态
							this.loading = false;
						} else {
							// 没有已批准的请假或出差记录，直接打卡
							this.submitCheckIn();
						}
					} else {
						// API请求返回错误，继续打卡流程
						console.error('检查请假/出差记录失败:', res.data.msg);
						this.submitCheckIn();
					}
				},
				fail: (err) => {
					console.error('检查请假/出差记录请求失败:', err);
					// 请求失败，继续打卡流程
					this.submitCheckIn();
				}
			});
		},
		
		submitCheckIn() {
			// 获取当前时间
			const now = new Date();
			const hours = now.getHours().toString().padStart(2, '0');
			const minutes = now.getMinutes().toString().padStart(2, '0');
			const timeStr = `${hours}:${minutes}`;
			
			// 计算当前打卡状态（正常、迟到、早退）
			const currentMinutes = this.timeToMinutes(now.getHours(), now.getMinutes());
			const checkStatus = this.getCheckInStatus(currentMinutes);
			
			// 构建打卡数据
			const type = this.isCheckedIn ? 'checkout' : 'checkin';
			const checkInData = {
				type: type,
				longitude: this.longitude,
				latitude: this.latitude,
				address: this.address,
				status: checkStatus // 添加打卡状态
			};
			
			console.log('打卡数据:', checkInData);
			
			// 调用打卡接口
			uni.request({
				url: this.$config.baseUrl + '/attendance/checkin',
				method: 'POST',
				data: checkInData,
				header: {
					'Authorization': uni.getStorageSync('App-Token'),
					'Content-Type': 'application/json'
				},
				success: (res) => {
					console.log('打卡请求响应:', res);
					this.loading = false;
					
					// 这里处理所有响应，包括HTTP状态码不是200的情况
					const responseData = res.data;
					
					if (responseData.code === 200) {
						// 成功处理逻辑
						if (type === 'checkin') {
							this.checkInTime = timeStr;
							this.isCheckedIn = true;
							
							this.todayInfo.hasCheckedIn = true;
							this.todayInfo.checkInTime = now;
							this.todayInfo.checkInStatus = checkStatus; // 使用计算的状态
							this.todayInfo.checkInStatusClass = this.getStatusClassString(checkStatus);
							this.todayInfo.goAddress = this.address;
							this.todayInfo.goLatitude = this.latitude;
							this.todayInfo.goLongitude = this.longitude;
							
							uni.showToast({
								title: responseData.data || '上班打卡成功',
								icon: 'success'
							});
							
							setTimeout(() => {
								this.checkTodayStatus();
							}, 500);
						} else {
							this.checkOutTime = timeStr;
							this.isCheckedIn = false;
							
							this.todayInfo.hasCheckedOut = true;
							this.todayInfo.checkOutTime = now;
							this.todayInfo.checkOutStatus = checkStatus; // 使用计算的状态
							this.todayInfo.checkOutStatusClass = this.getStatusClassString(checkStatus);
							this.todayInfo.outAddress = this.address;
							this.todayInfo.outLatitude = this.latitude;
							this.todayInfo.outLongitude = this.longitude;
							
							uni.showToast({
								title: responseData.data || '下班打卡成功',
								icon: 'success'
							});
							
							setTimeout(() => {
								this.checkTodayStatus();
							}, 500);
						}
					} else {
						// 错误处理逻辑
						console.error('打卡失败，错误信息:', responseData.msg);
						uni.showToast({
							title: responseData.msg || '打卡失败',
							icon: 'none',
							duration: 2000
						});
					}
				},
				fail: (err) => {
					console.error('打卡请求网络错误:', err);
					this.loading = false;
					uni.showToast({
						title: '网络请求失败，请检查网络连接',
						icon: 'none',
						duration: 2000
					});
				}
			});
		},
		
		checkTodayStatus() {
			console.log('正在获取今日打卡状态...');
			this.$http.get('/attendance/today').then(res => {
				if (res.code === 200 && res.data) {
					console.log('获取到今日打卡状态：', res.data);
					
					// 根据返回数据的结构重新解析打卡状态
					const data = res.data;
					
					// 根据checkInTime是否存在判断是否已签到
					this.todayInfo.hasCheckedIn = !!data.checkInTime;
					// 根据checkOutTime是否存在判断是否已签退
					this.todayInfo.hasCheckedOut = !!data.checkOutTime;
					
					// 打印原始时间字符串用于调试
					console.log('原始签到时间:', data.checkInTime);
					console.log('原始签退时间:', data.checkOutTime);
					
					// 安全地解析时间字符串为日期对象
					try {
						// 检查是否包含"T"，若不包含，添加时区信息以确保正确解析
						if (data.checkInTime) {
							if (data.checkInTime.includes('T')) {
								this.todayInfo.checkInTime = new Date(data.checkInTime);
							} else {
								// 如果是形如"20:15:47"的时间字符串，则需要与日期组合
								const today = new Date();
								const dateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
								this.todayInfo.checkInTime = new Date(`${dateStr} ${data.checkInTime}`);
							}
							console.log('解析后的签到时间对象:', this.todayInfo.checkInTime);
						} else {
							this.todayInfo.checkInTime = null;
						}
						
						if (data.checkOutTime) {
							if (data.checkOutTime.includes('T')) {
								this.todayInfo.checkOutTime = new Date(data.checkOutTime);
							} else {
								// 如果是形如"20:15:49"的时间字符串，则需要与日期组合
								const today = new Date();
								const dateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
								this.todayInfo.checkOutTime = new Date(`${dateStr} ${data.checkOutTime}`);
							}
							console.log('解析后的签退时间对象:', this.todayInfo.checkOutTime);
						} else {
							this.todayInfo.checkOutTime = null;
						}
					} catch (e) {
						console.error('解析日期时间出错:', e);
						// 如果日期解析错误，尝试直接使用字符串
						this.checkInTime = data.checkInTime || '';
						this.checkOutTime = data.checkOutTime || '';
					}
					
					this.todayInfo.checkInStatus = data.checkInStatus;
					this.todayInfo.checkOutStatus = data.checkOutStatus;
					
					this.todayInfo.checkInStatusClass = this.getStatusClassString(data.checkInStatus);
					this.todayInfo.checkOutStatusClass = this.getStatusClassString(data.checkOutStatus);
					
					this.todayInfo.goAddress = data.goAddress;
					this.todayInfo.outAddress = data.outAddress;
					this.todayInfo.goLatitude = data.goLatitude;
					this.todayInfo.goLongitude = data.goLongitude;
					this.todayInfo.outLatitude = data.outLatitude;
					this.todayInfo.outLongitude = data.outLongitude;
					
					// 设置打卡状态
					if (this.todayInfo.hasCheckedIn) {
						try {
							if (this.todayInfo.checkInTime && !isNaN(this.todayInfo.checkInTime.getTime())) {
								const inTime = this.todayInfo.checkInTime;
								const inHours = inTime.getHours().toString().padStart(2, '0');
								const inMinutes = inTime.getMinutes().toString().padStart(2, '0');
								this.checkInTime = `${inHours}:${inMinutes}`;
								console.log('格式化后的签到时间:', this.checkInTime);
							} else {
								// 如果时间对象无效，直接使用原始字符串
								this.checkInTime = data.checkInTime || '';
							}
						} catch (e) {
							console.error('处理签到时间出错:', e);
							this.checkInTime = data.checkInTime || '';
						}
						
						// 已签到但未签退时，标记为已签到状态
						this.isCheckedIn = !this.todayInfo.hasCheckedOut;
						
						console.log('上班打卡状态:', data.checkInStatus);
					}
					
					if (this.todayInfo.hasCheckedOut) {
						try {
							if (this.todayInfo.checkOutTime && !isNaN(this.todayInfo.checkOutTime.getTime())) {
								const outTime = this.todayInfo.checkOutTime;
								const outHours = outTime.getHours().toString().padStart(2, '0');
								const outMinutes = outTime.getMinutes().toString().padStart(2, '0');
								this.checkOutTime = `${outHours}:${outMinutes}`;
								console.log('格式化后的签退时间:', this.checkOutTime);
							} else {
								// 如果时间对象无效，直接使用原始字符串
								this.checkOutTime = data.checkOutTime || '';
							}
						} catch (e) {
							console.error('处理签退时间出错:', e);
							this.checkOutTime = data.checkOutTime || '';
						}
						
						// 如果已签退，则不再处于签到状态
						this.isCheckedIn = false;
						
						console.log('下班打卡状态:', data.checkOutStatus);
					}
				} else {
					console.log('未获取到今日打卡状态或获取失败');
					this.resetTodayInfo();
				}
			}).catch(err => {
				console.error('获取今日打卡状态失败', err);
				this.resetTodayInfo();
			});
		},
		
		getStatusClassString(status) {
			const classMap = {
				0: 'status-normal',
				1: 'status-late',
				2: 'status-early',
				3: 'status-absent'
			};
			return classMap[status] || '';
		},
		
		resetTodayInfo() {
			this.todayInfo = {
				hasCheckedIn: false,
				hasCheckedOut: false,
				checkInTime: null,
				checkOutTime: null,
				checkInStatus: null,
				checkOutStatus: null,
				checkInStatusClass: '',
				checkOutStatusClass: '',
				goAddress: null,
				outAddress: null,
				goLatitude: null,
				goLongitude: null,
				outLatitude: null,
				outLongitude: null
			};
		},
		
		formatDateTime(dateTime) {
			if (!dateTime) return '';
			
			try {
				// 先检查是否为有效的Date对象
				if (dateTime instanceof Date && !isNaN(dateTime.getTime())) {
					const hours = dateTime.getHours().toString().padStart(2, '0');
					const minutes = dateTime.getMinutes().toString().padStart(2, '0');
					return `${hours}:${minutes}`;
				}
				
				// 如果不是Date对象，尝试创建一个
				const date = new Date(dateTime);
				if (!isNaN(date.getTime())) {
					const hours = date.getHours().toString().padStart(2, '0');
					const minutes = date.getMinutes().toString().padStart(2, '0');
					return `${hours}:${minutes}`;
				}
				
				// 如果是时间字符串（如"20:15:47"），直接返回前5个字符
				if (typeof dateTime === 'string' && dateTime.includes(':')) {
					return dateTime.substring(0, 5);
				}
				
				return '';
			} catch (e) {
				console.error('格式化时间出错:', e, dateTime);
				return '';
			}
		},
		
		getStatusText(status) {
			const statusMap = {
				0: '正常',
				1: '迟到',
				2: '早退',
				3: '缺勤'
			};
			return statusMap[status] || '未知';
		},
		
		// 计算工作时长
		calculateWorkDuration(checkInTime, checkOutTime) {
			if (!checkInTime || !checkOutTime) return '未完成';
			
			const inTime = new Date(checkInTime);
			const outTime = new Date(checkOutTime);
			const durationMs = outTime - inTime;
			
			if (durationMs < 0) return '时间错误';
			
			const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
			const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
			
			return `${durationHours}小时${durationMinutes}分钟`;
		},
	}
}
</script>

<style>
.attendance-container {
	padding: 20rpx;
	background-color: #f5f7fa;
	min-height: 100vh;
}

/* 顶部卡片样式 */
.info-card {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.time-display {
	text-align: center;
}

.date {
	font-size: 28rpx;
	color: #666;
}

.time {
	font-size: 56rpx;
	font-weight: bold;
	margin-top: 6rpx;
	color: #333;
}

/* 考勤规则信息卡片 */
.rule-card {
	background-color: #fff;
	border-radius: 10rpx;
	padding: 12rpx 16rpx;
	margin-bottom: 16rpx;
	box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.05);
}

.rule-content {
	padding: 0;
}

.rule-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.rule-item {
	display: flex;
	justify-content: space-between;
	margin-bottom: 8rpx;
	line-height: 32rpx;
}

.rule-label {
	font-size: 26rpx;
	color: #666;
}

.rule-value {
	font-size: 26rpx;
	color: #333;
	text-align: right;
}

/* 打卡状态卡片 */
.checkin-status-card {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.status-header {
	margin-bottom: 24rpx;
	border-bottom: 1px solid #f0f0f0;
	padding-bottom: 12rpx;
}

.status-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.status-content {
	padding: 20rpx 0;
}

.status-cards {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 20rpx;
}

.status-card {
	width: 42%;
	background-color: #f9f9f9;
	border-radius: 12rpx;
	padding: 16rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	border: 1rpx solid #f0f0f0;
	transition: all 0.3s;
}

.status-card.completed {
	background-color: #f6ffed;
	border-color: #b7eb8f;
}

.status-card.waiting {
	background-color: #fff7e6;
	border-color: #ffd591;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12rpx;
	padding-bottom: 8rpx;
	border-bottom: 1px dashed #f0f0f0;
}

.card-title {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.card-icon {
	font-size: 28rpx;
	font-weight: bold;
	color: #52c41a;
}

.card-content {
	min-height: 80rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
}

.card-time {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.card-status-tag {
	display: inline-block;
	font-size: 24rpx;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

.card-placeholder {
	font-size: 28rpx;
	color: #999;
}

.card-connector {
	width: 10%;
	height: 4rpx;
	background-color: #f0f0f0;
}

.card-connector.active {
	background-color: #faad14;
}

.card-connector.complete {
	background-color: #52c41a;
}

/* 打卡按钮样式 */
.btn-container {
	margin: 30rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.check-btn {
	width: 280rpx;
	height: 280rpx;
	border-radius: 140rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 40rpx;
	font-weight: bold;
	color: #fff;
	position: relative;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.2);
}

.check-btn.disabled {
	background: #cccccc;
	opacity: 0.8;
}

.check-tip {
	margin-top: 20rpx;
	font-size: 26rpx;
	color: #ff6347;
	text-align: center;
	padding: 0 40rpx;
}

.btn-loading {
	position: absolute;
	width: 30rpx;
	height: 30rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.3);
	border-top: 4rpx solid #fff;
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin-top: 70rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.check-in {
	background: linear-gradient(135deg, #4a90e2, #6273FF);
}

.check-out {
	background: linear-gradient(135deg, #FF8C69, #FF6347);
}

/* 地图容器样式 */
.map-container {
	width: 100%;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	margin-bottom: 20rpx;
	position: relative;
}

.location-map {
	width: 100%;
	height: 320rpx;
}

.map-controls {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	background-color: rgba(255, 255, 255, 0.8);
	padding: 10rpx 15rpx;
	border-radius: 8rpx;
}

.location-refresh {
	display: flex;
	align-items: center;
	font-size: 24rpx;
	color: #4a90e2;
}

.refresh-text {
	margin-left: 4rpx;
}

/* 添加一些基础字体图标 */
@font-face {
  font-family: 'iconfont';
  src: url('data:application/x-font-woff2;charset=utf-8;base64,d09GMgABAAAAAALcAAsAAAAABqQAAAKPAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHEIGVgCDBgqBHIERATYCJAMMCwgABCAFhG0HNRsRBsgOJUHBwAAAAABEPHzt9/vmzpl5X7WkWdTs0iRNolkTDZFIEBKZToLESJYO7ZC81Sr0v0hPaU+WUDOrnMBKrL7VTJavLMtXUjYH+BxgenG+C16vdm9LNRgbDx04wOKAzrdJVCBBbhi7wCW8T6DZpDCyldW1LWCoxLYKxJ2hgQEMVaQSUqFC3TC2iCdQVRcP62cAj/7349eIGhmSOrNtb20rNZD7S3tRskf/jzzDAXzjGcDmI2MMkIgbjYkzcgzG5Jt1yYLHVAa+tOqFi7qw//ESWYW2G9gCLXHP16jw1eBJQFJU3QbgFkoSH6ddQaEyPbm3uWZ+dXFu4vzq8vLa/OTlZY25qbWFtcnxtemJ5ZUbFoVCwLGGb49QELlNnZiB79OhkEJ+2CUvFGLLpxfClb7mXPv8MHp1szk8//n/7b54xFKZDkx3v79QCvz0N9c3DkD+a/4v6dVbEP6vtuepdRQvwvs3mgnzE/yQqTZgGjQMrISJYC9JKVCXRH3H35raJKDZwGrswIGNxiP/lk6nnAwVI4Zo6jFGEjOGqopZVNWboNm40vjqFnIUReENKIcAQrszSJs9o9PuGUnMF6r6/aFqz39odgHDhVXrE1lT5GAZBZSgL2g6Vk3C0Aj5B8Z8F5SmeYLfMBr5QF6qySdOaRE7jHeQx32Mtoh1qiX00QTCtoyVBMnUJRXTdZQ314y3GrfRkmOJAigC+gIaHVZaXcMofv4BY7wuUI8NPPwNRoYRwsuqzNxxlJrRDly5C3WY9B6ahaiFykTDPhoB4WwxLEkgUbTKEiWj9Sjd3Fj5efP+JmCzLThUdHjKYyT+rDQAAAA=') format('woff2');
}

.iconfont {
  font-family: "iconfont" !important;
  font-size: 16px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-refresh:before {
  content: "\e650";
}

/* 添加新的状态标签样式 */
.status-badge {
	display: none;
}

/* 修改状态样式以更加突出 */
.status-normal {
	background-color: #f6ffed;
	color: #52c41a;
	border: 1rpx solid #b7eb8f;
	box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.status-late {
	background-color: #fff7e6;
	color: #faad14;
	border: 1rpx solid #ffd591;
	box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.status-early {
	background-color: #fff7e6;
	color: #faad14;
	border: 1rpx solid #ffd591;
	box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.status-absent {
	background-color: #fff1f0;
	color: #f5222d;
	border: 1rpx solid #ffa39e;
	box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

/* Update weekly status display */
.weekly-status-detail {
	flex: 1;
	display: flex;
	flex-direction: column;
}

/* New styles for single-line weekly records */
.weekly-status-row {
	flex: 1;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.status-part {
	display: flex;
	align-items: center;
	font-size: 24rpx;
	color: #666;
}

.status-part text:first-child {
	margin-right: 8rpx;
}

.checkin-part {
	/* styles specific to checkin part if needed */
}

.checkout-part {
	margin-left: 16rpx;
}

.checkin-info,
.checkout-info {
	/* styles specific to checkin and checkout parts if needed */
}
</style> 