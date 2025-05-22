<template>
  <view class="parent-view">
    <slot></slot>
  </view>
</template>

<script>
export default {
  name: 'ParentView',
  data() {
    return {}
  }
}
</script>

<style>
.parent-view {
  width: 100%;
  height: 100%;
}
</style> 