<template>
	<view class="attendance-admin-container">
		<view class="tab-header" :class="{'single-tab': !hasEvaluatePermission}">
			<view class="tab-item" 
				  :class="{ active: currentTab === 0 }"
				  @click="switchTab(0)">
				<text>月度考勤</text>
			</view>
			<!-- 只有拥有权限时才显示考勤评价标签 -->
			<view class="tab-item" 
				  v-if="hasEvaluatePermission"
				  :class="{ active: currentTab === 1 }"
				  @click="switchTab(1)">
				<text>考勤评价</text>
			</view>
			<!-- 调整底部线宽度和位置 -->
			<view class="tab-line" :style="{ 
				width: hasEvaluatePermission ? '50%' : '100%',
				transform: hasEvaluatePermission ? `translateX(${currentTab * 100}%)` : 'translateX(0)'
			}"></view>
		</view>
		
		<view class="content-container">
			<!-- 月度考勤 -->
			<monthly-view v-if="currentTab === 0" />
			
			<!-- 考勤评价 -->
			<evaluate-view v-if="currentTab === 1 && hasEvaluatePermission" />
		</view>
	</view>
</template>

<script>
import MonthlyView from '@/pages/attendance/monthly.vue'
import EvaluateView from '@/pages/attendance/evaluate.vue'
import { checkPermi } from '@/utils/permission'

export default {
	components: {
		MonthlyView,
		EvaluateView
	},
	data() {
		return {
			currentTab: 0,
			hasEvaluatePermission: false
		}
	},
	created() {
		// 检查是否有考勤评价权限
		this.hasEvaluatePermission = checkPermi(['wxapp:attendance:evaluate'])
	},
	onLoad() {
		// 如果是从其他页面带参数过来，可以设置默认tab
		const tab = this.$mp?.query?.tab || 0
		let targetTab = parseInt(tab)
		
		// 如果目标tab是评价(1)但没有权限，则强制切换到月度考勤(0)
		if (targetTab === 1 && !this.hasEvaluatePermission) {
			targetTab = 0
		}
		
		this.currentTab = targetTab
	},
	methods: {
		switchTab(index) {
			this.currentTab = index;
			
			// 如果切换到考勤评价标签，通知子组件加载数据
			if (index === 1 && this.hasEvaluatePermission) {
				// 使用 nextTick 确保组件已经渲染
				this.$nextTick(() => {
					const evaluateComponent = this.$children.find(child => child.$options.name === 'EvaluateView');
					if (evaluateComponent && typeof evaluateComponent.loadEvaluationData === 'function') {
						evaluateComponent.loadEvaluationData();
					}
				});
			}
		}
	}
}
</script>

<style lang="scss">
.attendance-admin-container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 30rpx;
	
	.tab-header {
		display: flex;
		position: relative;
		background-color: #ffffff;
		height: 90rpx;
		box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
		
		&.single-tab .tab-item {
			flex: 1;
		}
		
		.tab-item {
			flex: 1;
			display: flex;
			align-items: center;
			justify-content: center;
			font-size: 30rpx;
			color: #666;
			position: relative;
			transition: all 0.2s;
			
			&.active {
				color: #007AFF;
				font-weight: 500;
			}
		}
		
		.tab-line {
			position: absolute;
			bottom: 0;
			left: 0;
			height: 4rpx;
			background-color: #007AFF;
			transition: transform 0.3s, width 0.3s;
		}
	}
	
	.content-container {
		margin-top: 20rpx;
	}
}
</style> 