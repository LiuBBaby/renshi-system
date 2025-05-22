<template>
	<view class="monthly-container">
		<view class="month-selector">
			<view class="arrow left-arrow" @click="prevMonth">
				<!-- 使用自定义CSS箭头代替图标字体 -->
				<view class="custom-arrow-left"></view>
			</view>
			<view class="selected-month">{{ year }}年{{ month }}月</view>
			<view class="arrow right-arrow" @click="nextMonth">
				<!-- 保留原有图标，但添加备用CSS样式 -->
				<text class="iconfont icon-right"></text>
				<view class="custom-arrow-right"></view>
			</view>
		</view>
		
		<view class="statistics-card">
			<view class="card-item">
				<text class="item-value">{{ statistics.normalDays }}</text>
				<text class="item-label">正常</text>
			</view>
			<view class="card-item">
				<text class="item-value">{{ statistics.lateDays }}</text>
				<text class="item-label">迟到</text>
			</view>
			<view class="card-item">
				<text class="item-value">{{ statistics.earlyDays }}</text>
				<text class="item-label">早退</text>
			</view>
			<view class="card-item">
				<text class="item-value">{{ statistics.absenceDays }}</text>
				<text class="item-label">缺勤</text>
			</view>
			<view class="card-item">
				<text class="item-value">{{ statistics.overtimeDays }}</text>
				<text class="item-label">加班</text>
			</view>
		</view>
		
		<view class="calendar-container">
			<view class="calendar-header">
				<view class="weekday" v-for="(day, index) in weekdays" :key="index">{{ day }}</view>
			</view>
			<view class="calendar-body">
				<view class="calendar-day" v-for="(day, index) in calendarDays" :key="index"
					:class="[day.isCurrentMonth ? '' : 'other-month', day.status ? `status-${day.status}` : '']"
					@click="viewDayDetail(day)">
					<text class="day-number">{{ day.day }}</text>
					<text class="day-status" v-if="day.statusText">{{ day.statusText }}</text>
				</view>
			</view>
		</view>
		
		<view class="attendance-list">
			<view class="list-title">考勤记录</view>
			<view class="empty-state" v-if="attendanceList.length === 0">
				<text class="empty-text">本月暂无考勤记录</text>
			</view>
			<view class="list-item" v-for="(record, index) in attendanceList" :key="index"
				@click="viewRecordDetail(record)">
				<view class="item-left">
					<view class="item-date">{{ record.date }}</view>
					<view class="item-time">{{ record.checkInTime }} - {{ record.checkOutTime }}</view>
					<view class="item-status-detail">
						<text class="status-label" :class="{'status-normal': record.checkInStatus === 0, 'status-late': record.checkInStatus === 1, 'status-absent': record.checkInStatus === 3}">
							{{ record.checkInStatus === 0 ? '上班正常' : record.checkInStatus === 1 ? '上班迟到' : record.checkInStatus === 3 ? '上班缺勤' : '未打卡' }}
						</text>
						<text class="status-separator">|</text>
						<text class="status-label" :class="{'status-normal': record.checkOutStatus === 0, 'status-early': record.checkOutStatus === 2, 'status-absent': record.checkOutStatus === 3}">
							{{ record.checkOutStatus === 0 ? '下班正常' : record.checkOutStatus === 2 ? '下班早退' : record.checkOutStatus === 3 ? '下班缺勤' : '未打卡' }}
						</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getMonthlyAttendance } from '@/api/business/attendance.js';
export default {
	data() {
		return {
			year: new Date().getFullYear(),
			month: new Date().getMonth() + 1,
			weekdays: ['日', '一', '二', '三', '四', '五', '六'],
			calendarDays: [],
			statistics: {
				normalDays: 0,
				lateDays: 0,
				earlyDays: 0,
				absenceDays: 0,
				overtimeDays: 0
			},
			attendanceList: []
		}
	},
	created() {
		// 确保年月初始值有效
		const now = new Date();
		if (!this.year || isNaN(this.year)) {
			this.year = now.getFullYear();
		}
		if (!this.month || isNaN(this.month)) {
			this.month = now.getMonth() + 1;
		}
		this.initCalendarData();
	},
	onLoad(options) {
		// 如果URL中传入了年月参数，则使用URL参数
		if (options.year && !isNaN(parseInt(options.year))) {
			this.year = parseInt(options.year);
		}
		if (options.month && !isNaN(parseInt(options.month))) {
			this.month = parseInt(options.month);
		}
		this.initCalendarData();
	},
	onShow() {
		if (this.calendarDays.length === 0) {
			this.initCalendarData();
		}
	},
	methods: {
		initCalendarData() {
			const now = new Date();
			// 确保年月有效值
			if (!this.year || isNaN(this.year)) {
				this.year = now.getFullYear();
			}
			if (!this.month || isNaN(this.month)) {
				this.month = now.getMonth() + 1;
			}
			
			// 先生成日历结构，不带状态
			this.generateCalendar();
			
			// 重置统计数据为0
			this.statistics = {
				normalDays: 0,
				lateDays: 0,
				earlyDays: 0,
				absenceDays: 0,
				overtimeDays: 0
			};
			
			// 清空考勤列表
			this.attendanceList = [];
			
			// 加载真实数据
			this.loadMonthlyData();
			
			console.log(`初始化日历: ${this.year}年${this.month}月`);
		},
		// 生成日历数据
		generateCalendar() {
			const year = this.year;
			const month = this.month;
			const firstDay = new Date(year, month - 1, 1);
			const lastDay = new Date(year, month, 0);
			const daysInMonth = lastDay.getDate();
			const firstDayOfWeek = firstDay.getDay();
			
			let days = [];
			
			// 格式化年月为两位数
			const formattedMonth = month.toString().padStart(2, '0');
			
			// 上个月的天数
			const prevMonthLastDate = new Date(year, month - 1, 0).getDate();
			const prevMonth = month === 1 ? 12 : month - 1;
			const prevYear = month === 1 ? year - 1 : year;
			const formattedPrevMonth = prevMonth.toString().padStart(2, '0');
			
			for (let i = 0; i < firstDayOfWeek; i++) {
				const day = prevMonthLastDate - firstDayOfWeek + i + 1;
				const formattedDay = day.toString().padStart(2, '0');
				days.push({
					day,
					isCurrentMonth: false,
					date: `${prevYear}-${formattedPrevMonth}-${formattedDay}`
				});
			}
			
			// 当前月的天数
			for (let i = 1; i <= daysInMonth; i++) {
				const formattedDay = i.toString().padStart(2, '0');
				days.push({
					day: i,
					isCurrentMonth: true,
					date: `${year}-${formattedMonth}-${formattedDay}`,
					// 默认不设置状态和文本，等待接口返回后再更新
					status: null,
					statusText: ""
				});
			}
			
			// 下个月的天数
			const nextMonth = month === 12 ? 1 : month + 1;
			const nextYear = month === 12 ? year + 1 : year;
			const formattedNextMonth = nextMonth.toString().padStart(2, '0');
			
			const remainingDays = 42 - days.length;
			for (let i = 1; i <= remainingDays; i++) {
				const formattedDay = i.toString().padStart(2, '0');
				days.push({
					day: i,
					isCurrentMonth: false,
					date: `${nextYear}-${formattedNextMonth}-${formattedDay}`
				});
			}
			
			this.calendarDays = days;
		},
		
		// 加载月度考勤数据
		loadMonthlyData() {
			// 显示加载中
			uni.showLoading({
				title: '加载中'
			});
			
			// 确保参数格式正确，如果为null或NaN则使用当前日期
			let yearParam = parseInt(this.year);
			let monthParam = parseInt(this.month);
			
			const currentDate = new Date();
			if (isNaN(yearParam) || yearParam === null) {
				yearParam = currentDate.getFullYear();
				this.year = yearParam;
			}
			
			if (isNaN(monthParam) || monthParam === null) {
				monthParam = currentDate.getMonth() + 1;
				this.month = monthParam;
			}
			
			// 确保参数是数字类型
			yearParam = Number(yearParam);
			monthParam = Number(monthParam);
			
			console.log(`准备加载月度考勤数据: ${yearParam}年${monthParam}月`);
			console.log(`参数类型检查 - yearParam类型: ${typeof yearParam}, monthParam类型: ${typeof monthParam}`);
			
			// 调用后端接口获取月度考勤数据
			getMonthlyAttendance(yearParam, monthParam).then(res => {
				uni.hideLoading();
				
				console.log(`接收到${yearParam}年${monthParam}月考勤数据:`, res);
				
				if (res.code === 200 && res.data) {
					// 数据检查
					if (res.data.calendarData && res.data.calendarData.length > 0) {
						console.log('返回的日历数据:', JSON.stringify(res.data.calendarData));
						
						// 检查日期是否匹配
						res.data.calendarData.forEach(day => {
							if (day.date) {
								const dateParts = day.date.split('-');
								if (dateParts.length === 3) {
									const dayYear = parseInt(dateParts[0]);
									const dayMonth = parseInt(dateParts[1]);
									if (dayYear !== yearParam || dayMonth !== monthParam) {
										console.warn(`发现不匹配的日期: ${day.date}, 期望的年月: ${yearParam}-${monthParam}`);
									}
								}
							}
						});
					}
					
					// 检查返回的年月是否与请求的一致
					const returnedYear = res.data.year;
					const returnedMonth = res.data.month;
					
					console.log(`返回的年月: ${returnedYear}年${returnedMonth}月`);
					
					// 检查返回的年月是否与请求的年月不匹配
					if (returnedYear !== yearParam || returnedMonth !== monthParam) {
						console.warn(`警告: 请求的年月(${yearParam}年${monthParam}月)与返回的年月(${returnedYear}年${returnedMonth}月)不一致`);
						
						// 显示错误提示
						uni.showToast({
							title: `服务器数据异常，返回了${returnedYear}年${returnedMonth}月而非请求的${yearParam}年${monthParam}月数据`,
							icon: 'none',
							duration: 3000
						});
						
						// 尝试联系后端开发人员
						console.log(`请联系后端开发人员检查getMonthlyAttendance方法的实现，确保正确处理year=${yearParam}&month=${monthParam}参数`);
						
						// 对考勤记录进行额外过滤，确保只显示请求月份的数据
						if (res.data.records && res.data.records.length > 0) {
							console.log('执行前端额外过滤，确保数据匹配请求月份');
							
							// 构建目标年月字符串 YYYY-MM 格式
							const targetYearMonth = `${yearParam}-${monthParam.toString().padStart(2, '0')}`;
							
							// 过滤出目标月份的考勤记录
							const filteredRecords = res.data.records.filter(record => {
								if (!record.attendanceInfoDate) return false;
								
								const recordDate = new Date(record.attendanceInfoDate);
								const recordYear = recordDate.getFullYear();
								const recordMonth = recordDate.getMonth() + 1;
								
								// 检查年月是否匹配
								return recordYear === yearParam && recordMonth === monthParam;
							});
							
							console.log(`前端过滤: 从${res.data.records.length}条记录中筛选出${filteredRecords.length}条${targetYearMonth}的记录`);
							
							// 使用过滤后的记录替换原记录
							res.data.records = filteredRecords;
							
							// 修正返回的年月
							res.data.year = yearParam;
							res.data.month = monthParam;
							
							// 重新计算统计数据
							if (filteredRecords.length > 0) {
								let normalDays = 0;
								let lateDays = 0;
								let earlyDays = 0;
								let absenceDays = 0;
								let overtimeDays = 0;
								
								filteredRecords.forEach(record => {
									const checkInStatus = record.checkInStatus;
									const checkOutStatus = record.checkOutStatus;
									
									const isNormal = (checkInStatus !== null && checkInStatus === 0) && 
													(checkOutStatus !== null && checkOutStatus === 0);
									const isLate = checkInStatus !== null && checkInStatus === 1;
									const isEarly = checkOutStatus !== null && checkOutStatus === 2;
									const isAbsent = (checkInStatus !== null && checkInStatus === 3) || 
												  (checkOutStatus !== null && checkOutStatus === 3);
									const isOvertime = false; // 加班信息暂不处理
									
									if (isNormal && !isLate && !isEarly && !isAbsent) {
										normalDays++;
									}
									if (isLate) {
										lateDays++;
									}
									if (isEarly) {
										earlyDays++;
									}
									if (isAbsent) {
										absenceDays++;
									}
									if (isOvertime) {
										overtimeDays++;
									}
								});
								
								// 更新统计数据
								res.data.statistics = {
									normalDays,
									lateDays,
									earlyDays,
									absenceDays,
									overtimeDays
								};
								
								console.log('重新计算的统计数据:', res.data.statistics);
							}
						}
					}
					
					// 更新统计数据
					if (res.data.statistics) {
						this.statistics = res.data.statistics;
						console.log('更新统计数据:', this.statistics);
					}
					
					// 更新日历数据
					if (res.data.calendarData && res.data.calendarData.length > 0) {
						// 用后端返回的数据更新当前月的日历天数
						const daysMap = new Map();
						
						// 过滤出当前月份的数据
						const filteredCalendarData = res.data.calendarData.filter(day => {
							// 验证日期格式并确保属于请求的年月
							if (!day.date) return false;
							
							const dateParts = day.date.split('-');
							if (dateParts.length !== 3) return false;
							
							const dayYear = parseInt(dateParts[0]);
							const dayMonth = parseInt(dateParts[1]);
							const dayDate = parseInt(dateParts[2]);
							
							// 检查日期是否属于请求的年月
							const isMatchingMonth = (dayYear === yearParam && dayMonth === monthParam);
							
							// 如果不匹配但被标记为当月，修正isCurrentMonth标志
							if (!isMatchingMonth && day.isCurrentMonth) {
								console.warn(`日期数据不一致: ${day.date} 被错误地标记为当月数据`);
								day.isCurrentMonth = false;
							}
							
							return isMatchingMonth;
						});
						
						console.log(`日历数据过滤: 从${res.data.calendarData.length}条记录中筛选出${filteredCalendarData.length}条${yearParam}年${monthParam}月的记录`);
						
						// 使用过滤后的记录创建映射
						filteredCalendarData.forEach(day => {
							// 从日期中提取日号
							const dayOfMonth = parseInt(day.date.split('-')[2]);
							daysMap.set(dayOfMonth, day);
						});
						
						// 更新日历显示
						this.calendarDays.forEach((day, index) => {
							if (day.isCurrentMonth) {
								const serverDay = daysMap.get(day.day);
								if (serverDay) {
									// 更新状态和文本
									this.calendarDays[index].status = serverDay.status;
									this.calendarDays[index].statusText = serverDay.statusText;
								}
							}
						});
					}
					
					// 从日历组件中统计缺勤数据，更新统计显示
					this.countAbsencesFromCalendar();
					
					// 更新考勤记录列表
					if (res.data.records) {
						this.attendanceList = res.data.records.map(record => {
							// 将后端数据转换为前端需要的格式
							const checkInTime = record.checkInTime ? 
								this.formatDateTime(new Date(record.checkInTime)) : '--:--';
							const checkOutTime = record.checkOutTime ? 
								this.formatDateTime(new Date(record.checkOutTime)) : '--:--';
							
							let status = 0; // 默认正常
							// 判断考勤状态
							if (record.checkInStatus === 1) {
								status = 1; // 迟到
							} else if (record.checkOutStatus === 2) {
								status = 2; // 早退
							} else if (record.checkInStatus === 3 || record.checkOutStatus === 3) {
								status = 3; // 缺勤
							}
							
							// 格式化日期为 YYYY-MM-DD
							const recordDate = new Date(record.attendanceInfoDate);
							const year = recordDate.getFullYear();
							const month = (recordDate.getMonth() + 1).toString().padStart(2, '0');
							const day = recordDate.getDate().toString().padStart(2, '0');
							const formattedDate = `${year}-${month}-${day}`;
							
							return {
								date: formattedDate,
								checkInTime,
								checkOutTime,
								status,
								rawData: record, // 保存原始数据以便详情查看
								checkInStatus: record.checkInStatus,
								checkOutStatus: record.checkOutStatus
							};
						});
					}
					
					console.log('月度考勤数据加载成功');
				} else {
					uni.showToast({
						title: res.msg || '获取考勤数据失败',
						icon: 'none'
					});
					
					// 如果获取数据失败，保留之前的模拟数据生成方法
					this.generateMockData();
				}
			}).catch(err => {
				uni.hideLoading();
				console.error('获取月度考勤数据出错', err);
				uni.showToast({
					title: '获取考勤数据失败，请检查网络',
					icon: 'none'
				});
				
				// 如果出错，保留之前的模拟数据生成方法
				this.generateMockData();
			});
		},
		
		// 从日历组件中统计缺勤数据
		countAbsencesFromCalendar() {
			// 获取当前月的工作日
			const workdayAbsences = this.calendarDays.filter(day => {
				// 只统计当月的日期
				if (!day.isCurrentMonth) return false;
				
				// 判断是否为工作日（周一至周五）
				const date = new Date(this.year, this.month - 1, day.day);
				const dayOfWeek = date.getDay(); // 0 = 周日, 1-5 = 周一至周五, 6 = 周六
				const isWorkday = dayOfWeek >= 1 && dayOfWeek <= 5;
				
				// 检查日历中标记为缺勤的日期
				// 状态4表示缺勤，或者有缺勤的状态文本
				const isAbsent = day.status === 4 || 
							(day.statusText && day.statusText.includes('缺勤'));
				
				// 返回工作日中的缺勤日
				return isWorkday && isAbsent;
			});
			
			// 更新统计中的缺勤数
			this.statistics.absenceDays = workdayAbsences.length;
			console.log('从日历组件统计的缺勤天数:', this.statistics.absenceDays);
		},
		
		// 生成模拟数据（作为后备方案）
		generateMockData() {
			// 模拟统计数据
			this.statistics = {
				normalDays: 18,
				lateDays: 2,
				earlyDays: 1,
				absenceDays: 0,
				overtimeDays: 3
			};
			
			// 生成当月考勤记录
			this.attendanceList = [];
			const daysInMonth = new Date(this.year, this.month, 0).getDate();
			for (let i = 1; i <= daysInMonth; i++) {
				// 只生成工作日的记录（周一到周五）
				const date = new Date(this.year, this.month - 1, i);
				const day = date.getDay();
				if (day >= 1 && day <= 5) {
					const status = this.getRandomStatus();
					this.attendanceList.push({
						date: `${this.year}-${this.month.toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`,
						checkInTime: this.getCheckInTime(status),
						checkOutTime: this.getCheckOutTime(status),
						status
					});
				}
			}
		},
		
		// 上个月
		prevMonth() {
			if (this.month === 1) {
				this.year--;
				this.month = 12;
			} else {
				this.month--;
			}
			
			// 重置日历和数据
			this.generateCalendar();
			
			// 重置统计数据
			this.statistics = {
				normalDays: 0,
				lateDays: 0,
				earlyDays: 0,
				absenceDays: 0,
				overtimeDays: 0
			};
			
			// 清空考勤列表
			this.attendanceList = [];
			
			// 重新加载数据
			this.loadMonthlyData();
		},
		
		// 下个月
		nextMonth() {
			if (this.month === 12) {
				this.year++;
				this.month = 1;
			} else {
				this.month++;
			}
			
			// 重置日历和数据
			this.generateCalendar();
			
			// 重置统计数据
			this.statistics = {
				normalDays: 0,
				lateDays: 0,
				earlyDays: 0,
				absenceDays: 0,
				overtimeDays: 0
			};
			
			// 清空考勤列表
			this.attendanceList = [];
			
			// 重新加载数据
			this.loadMonthlyData();
		},
		
		// 查看日期详情
		viewDayDetail(day) {
			if (!day.isCurrentMonth) return;
			
			// 查找当天考勤记录
			const record = this.attendanceList.find(item => item.date === day.date);
			if (record) {
				this.viewRecordDetail(record);
			} else {
				uni.showToast({
					title: '该日无考勤记录',
					icon: 'none'
				});
			}
		},
		
		// 查看记录详情
		viewRecordDetail(record) {
			// 获取打卡状态文本
			const checkInStatusText = record.checkInStatus === 0 ? '正常' : 
									record.checkInStatus === 1 ? '迟到' : 
									record.checkInStatus === 3 ? '缺勤' : '未打卡';
									
			const checkOutStatusText = record.checkOutStatus === 0 ? '正常' : 
									record.checkOutStatus === 2 ? '早退' : 
									record.checkOutStatus === 3 ? '缺勤' : '未打卡';
			
			// 获取打卡地址信息
			const goAddress = record.rawData ? record.rawData.goAddress || '未记录' : '未记录';
			const outAddress = record.rawData ? record.rawData.outAddress || '未记录' : '未记录';
			
			uni.showModal({
				title: record.date + ' 考勤详情',
				content: `上班时间: ${record.checkInTime} (${checkInStatusText})\n上班地点: ${goAddress}\n\n下班时间: ${record.checkOutTime} (${checkOutStatusText})\n下班地点: ${outAddress}`,
				showCancel: false
			});
		},
		
		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				0: '正常',
				1: '迟到',
				2: '早退',
				3: '缺勤',
				4: '加班'
			};
			return statusMap[status] || '未知';
		},
		
		// 获取状态简短文本
		getStatusTextShort(status) {
			const statusMap = {
				0: '正常',
				1: '迟到',
				2: '早退',
				3: '加班',
				4: '缺勤'
			};
			return statusMap[status] || '';
		},
		
		// 模拟随机状态（实际项目中应从服务器获取真实数据）
		getRandomStatus() {
			const statuses = [0, 0, 0, 0, 0, 1, 2, 3, 4]; // 增加正常状态的权重
			return statuses[Math.floor(Math.random() * statuses.length)];
		},
		
		// 根据状态获取上班时间
		getCheckInTime(status) {
			switch (status) {
				case 0: return '08:30';
				case 1: return '09:15';
				case 2: return '08:30';
				case 3: return '08:30';
				default: return '--:--';
			}
		},
		
		// 根据状态获取下班时间
		getCheckOutTime(status) {
			switch (status) {
				case 0: return '17:30';
				case 1: return '17:30';
				case 2: return '16:45';
				case 3: return '19:30';
				default: return '--:--';
			}
		},
		
		// 格式化日期时间
		formatDateTime(date) {
			const hours = date.getHours().toString().padStart(2, '0');
			const minutes = date.getMinutes().toString().padStart(2, '0');
			return `${hours}:${minutes}`;
		}
	}
}
</script>

<style lang="scss">
.monthly-container {
	padding: 20rpx;
	background-color: #f8f8f8; // 稍微调整背景色
	min-height: calc(100vh - 110rpx); // 减去 tab header 的高度
}

.month-selector {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 0;
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

	.arrow {
		padding: 10rpx 30rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		
		.iconfont {
			font-size: 36rpx;
			color: #666;
		}
		
		/* 自定义左右箭头样式 */
		.custom-arrow-left, .custom-arrow-right {
			width: 16rpx;
			height: 16rpx;
			border-top: 3rpx solid #666;
			border-left: 3rpx solid #666;
		}
		
		.custom-arrow-left {
			transform: rotate(-45deg);
			display: inline-block;
		}
		
		.custom-arrow-right {
			transform: rotate(135deg);
			display: none; /* 默认隐藏，仅在图标字体失效时显示 */
		}
	}
	
	/* 当图标字体失效时显示自定义右箭头 */
	.right-arrow .iconfont:not(:empty) + .custom-arrow-right {
		display: none;
	}
	
	.right-arrow .iconfont:empty + .custom-arrow-right {
		display: inline-block;
	}

	.selected-month {
		font-size: 32rpx;
		font-weight: 500;
		color: #333;
	}
}

.statistics-card {
	display: flex;
	justify-content: space-around;
	background-color: #ffffff;
	padding: 30rpx 20rpx;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

	.card-item {
		display: flex;
		flex-direction: column;
		align-items: center;

		.item-value {
			font-size: 36rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 8rpx;
		}

		.item-label {
			font-size: 24rpx;
			color: #888;
		}
	}
}

.calendar-container {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 20rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

	.calendar-header {
		display: flex;
		justify-content: space-around;
		padding-bottom: 15rpx;
		border-bottom: 1rpx solid #eee;
		margin-bottom: 15rpx;

		.weekday {
			width: calc(100% / 7);
			text-align: center;
			font-size: 26rpx;
			color: #666;
		}
	}

	.calendar-body {
		display: flex;
		flex-wrap: wrap;

		.calendar-day {
			width: calc(100% / 7);
			height: 100rpx; // 增加高度
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			margin-bottom: 10rpx;
			position: relative;
			border-radius: 8rpx; // 添加圆角
			transition: background-color 0.2s;

			&.other-month {
				.day-number {
					color: #ccc;
				}
			}

			.day-number {
				font-size: 30rpx;
				color: #333;
				margin-bottom: 4rpx;
			}

			.day-status {
				font-size: 20rpx;
				position: absolute;
				bottom: 8rpx;
				padding: 2rpx 6rpx;
				border-radius: 4rpx;
			}

			// 状态样式
			&.status-1 {
				.day-status {
					background-color: #ffaa00; // 迟到 - 橙色
					color: white;
				}
			}
			&.status-2 {
				.day-status {
					background-color: #f0ad4e; // 早退 - 黄色
					color: white;
				}
			}
			&.status-3 {
				.day-status {
					background-color: #5bc0de; // 加班 - 蓝色
					color: white;
				}
			}
			&.status-4 {
				.day-status {
					background-color: #dd524d; // 缺勤 - 红色
					color: white;
				}
			}
			&.status-0 {
				// 正常可以不加特殊背景，或加浅绿色
				.day-status {
					background-color: #91dca5; // 正常 - 绿色
					color: white;
				}
			}
		}
	}
}

.attendance-list {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);

	.list-title {
		font-size: 32rpx;
		font-weight: 500;
		padding-bottom: 15rpx;
		border-bottom: 1rpx solid #eee;
		margin-bottom: 15rpx;
	}

	.list-item {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f0f0f0;

		&:last-child {
			border-bottom: none;
		}

		.item-left {
			flex: 1;
			margin-right: 20rpx;

			.item-date {
				font-size: 28rpx;
				font-weight: 500;
				color: #333;
				margin-bottom: 6rpx;
			}

			.item-time {
				font-size: 24rpx;
				color: #888;
				margin-bottom: 6rpx;
			}
		}

		.item-status {
			padding: 4rpx 16rpx;
			border-radius: 8rpx;
			font-size: 24rpx;
			white-space: nowrap;
			align-self: center;
		}
	}

	.empty-state {
		padding: 40rpx 0;
		text-align: center;

		.empty-text {
			font-size: 28rpx;
			color: #999;
		}
	}
}

// 加载中或无数据的占位符样式 (可选)
.placeholder {
	text-align: center;
	color: #999;
	padding: 50rpx;
	font-size: 28rpx;
}

.item-status-detail {
	font-size: 24rpx;
	color: #888;
	margin-top: 8rpx;
	display: flex;
	flex-wrap: wrap;
	
	.status-label {
		padding: 2rpx 8rpx;
		border-radius: 4rpx;
		font-size: 20rpx;
	}
	
	.status-separator {
		margin: 0 8rpx;
		color: #ddd;
	}
}

// 状态颜色
.status-normal {
	color: #18bc37;
	background-color: #e8f8e9;
}
.status-late {
	color: #ffaa00;
	background-color: #fff5e6;
}
.status-early {
	color: #f0ad4e;
	background-color: #fff9e6;
}
.status-absent {
	color: #dd524d;
	background-color: #ffefef;
}
.status-leave, .status-trip { // 请假和出差用同一种颜色
	color: #007AFF;
	background-color: #e6f2ff;
}

.status-label.status-normal {
	color: #18bc37;
	background-color: #e8f8e9;
	border: 1rpx solid #c1e9c5;
}

.status-label.status-late {
	color: #ffaa00;
	background-color: #fff5e6;
	border: 1rpx solid #ffd591;
}

.status-label.status-early {
	color: #f0ad4e;
	background-color: #fff9e6;
	border: 1rpx solid #ffe7a3;
}

.status-label.status-absent {
	color: #dd524d;
	background-color: #ffefef;
	border: 1rpx solid #ffa39e;
}
</style> 