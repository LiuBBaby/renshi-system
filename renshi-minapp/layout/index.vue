<template>
  <view class="app-container">
    <slot></slot>
  </view>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
    }
  },
  created() {
  },
  methods: {
  }
}
</script>

<style>
.app-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
}
</style> 