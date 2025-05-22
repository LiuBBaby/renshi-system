<template>
  <view class="dept-tree-node">
    <!-- 节点本身的点击区域 -->
    <view class="tree-node" :class="{ 'selected': selectedId === node.id }" @click.stop="handleClick">
      <!-- 缩进处理 -->
      <view class="indent" :style="{ width: level * 20 + 'rpx' }"></view>
      
      <!-- 展开/折叠图标 -->
      <view class="toggle-area" @click.stop="toggleExpand">
        <view v-if="hasChildren" class="toggle-icon" :class="{ 'expanded': expanded }">
          <text>▶</text>
        </view>
        <view v-else class="placeholder"></view>
      </view>
      
      <!-- 节点名称 -->
      <text class="node-text" :class="{ 'selected-text': selectedId === node.id }">
        {{ node.name || node.label }}
      </text>
    </view>
    
    <!-- 子节点区域 -->
    <view v-if="hasChildren && expanded" class="children-area">
      <!-- 直接打印子节点数量，便于调试 -->
      <view v-if="node.children.length > 0" class="debug-info">{{ node.children.length }}个子节点</view>
      
      <!-- 递归渲染子节点 -->
      <view v-for="child in node.children" :key="child.id" class="child-node">
        <DeptTreeNode
          :node="child"
          :level="level + 1"
          :selected-id="selectedId"
          @node-click="$emit('node-click', $event)"
        />
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'DeptTreeNode',
  props: {
    node: {
      type: Object,
      required: true
    },
    level: {
      type: Number,
      default: 0
    },
    selectedId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    return {
      expanded: true, // 默认展开所有节点，方便查看
      debugId: Math.random().toString(36).substring(2, 8) // 用于调试的唯一ID
    };
  },
  created() {
    // 节点创建时的行为
    if (this.node) {
      // 使用节点自带的open属性或者默认展开所有节点
      if (this.node.open !== undefined) {
        this.expanded = this.node.open;
      } else {
        // 没有open属性时默认全部展开
        this.expanded = true;
      }
      
      console.log(`节点[${this.node.name}] 展开状态: ${this.expanded}`);
    }
  },
  computed: {
    hasChildren() {
      // 更健壮的子节点检测
      const result = this.node && this.node.children && Array.isArray(this.node.children) && this.node.children.length > 0;
      // 打印关键调试信息
      console.log(`[hasChildren]节点${this.node ? this.node.name : '未知'}: ${result ? '有子节点' : '无子节点'}, 子节点数量: ${this.node && this.node.children ? this.node.children.length : 0}`);
      if (this.node && this.node.children && this.node.children.length > 0) {
        console.log(`第一个子节点: ${this.node.children[0].name || this.node.children[0].label || '未命名'}`);
      }
      return result;
    }
  },
  methods: {
    toggleExpand() {
      this.expanded = !this.expanded;
      console.log(`[切换][${this.node.name}] 状态: ${this.expanded ? '展开' : '折叠'}, 子节点数: ${(this.node.children && this.node.children.length) || 0}`);
      
      // 更新节点的open属性，保持一致性
      if (this.node) {
        this.node.open = this.expanded;
      }
    },
    handleClick() {
      this.$emit('node-click', this.node);
    }
  }
};
</script>

<style scoped>
.dept-tree-node {
  width: 100%;
}

.tree-node {
  display: flex;
  align-items: center;
  padding: 16rpx 10rpx;
  border-bottom: 1rpx solid #f0f0f0;
  position: relative;
  transition: all 0.3s;
  flex-wrap: nowrap;
}

.tree-node.selected {
  background-color: #ecf5ff;
  border-left: 6rpx solid #409eff;
}

.tree-node:active {
  background-color: #f5f7fa;
}

.indent {
  display: inline-block;
  height: 100%;
}

.toggle-area {
  width: 50rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-icon {
  width: 30rpx;
  height: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f2f6fc;
  border-radius: 4rpx;
  transition: transform 0.3s;
}

.toggle-icon.expanded {
  transform: rotate(90deg);
  background-color: #ecf5ff;
}

.toggle-icon text {
  font-size: 18rpx;
  color: #409eff;
  line-height: 1;
}

.placeholder {
  width: 30rpx;
  height: 30rpx;
}

.node-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 28rpx;
  color: #333;
  padding-left: 10rpx;
}

.node-text.selected-text {
  color: #409eff;
  font-weight: 500;
}

.children-area {
  position: relative;
  padding-left: 40rpx;
  border-left: 2rpx solid #eaeaea;
  margin-left: 25rpx;
  display: block; /* 确保子节点区域正确显示 */
  overflow: visible; /* 确保内容不被裁剪 */
}

.debug-info {
  font-size: 24rpx;
  color: #409eff;
  padding: 6rpx 0;
  margin-left: 10rpx;
  background-color: #f0f9ff;
  border-radius: 4rpx;
  padding: 6rpx 10rpx;
  margin-bottom: 10rpx;
}

.child-node {
  position: relative;
}

.child-node:before {
  content: '';
  position: absolute;
  left: -15rpx;
  top: 20rpx;
  width: 10rpx;
  height: 2rpx;
  background-color: #ddd;
}
</style>
