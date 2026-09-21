<template>
  <div ref="wrapper" class="screen-container-wrapper">
    <div ref="inner" class="screen-container" :style="innerStyle"><slot /></div>
  </div>
</template>

<script>
/**
 * ScreenContainer 大屏等比缩放容器（#E-16/#E-24 公共组件）
 *
 * 设计基准 1920×1080（课程MUST），transform:scale 等比缩放适配任意分辨率：
 * - 等比不拉伸：取 scale = min(w/1920, h/1080)，超宽屏两侧留深色底；
 * - 居中显示：外层 flex 居中；
 * - resize 防抖监听，组件销毁移除监听（防内存泄漏，稳定性NFR）。
 * 使用：<ScreenContainer :designWidth="1920" :designHeight="1080">…四区布局…</ScreenContainer>
 */
export default {
  name: 'ScreenContainer',
  props: {
    designWidth: { type: Number, default: 1920 },
    designHeight: { type: Number, default: 1080 }
  },
  data () {
    return { scale: 1 }
  },
  computed: {
    innerStyle () {
      return {
        width: this.designWidth + 'px',
        height: this.designHeight + 'px',
        transform: `scale(${this.scale})`
      }
    }
  },
  mounted () {
    this.handleResize()
    // 防抖：避免resize高频重排
    let timer = null
    this._onResize = () => {
      clearTimeout(timer)
      timer = setTimeout(this.handleResize, 100)
    }
    window.addEventListener('resize', this._onResize)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this._onResize)
  },
  methods: {
    handleResize () {
      const el = this.$refs.wrapper
      if (!el) return
      const w = el.clientWidth
      const h = el.clientHeight
      this.scale = Math.min(w / this.designWidth, h / this.designHeight)
    }
  }
}
</script>

<style lang="less" scoped>
.screen-container-wrapper {
  height: 100vh;
  width: 100%;
  overflow: hidden;
  /* 大屏深色底，顶部微蓝光晕营造纵深（极低透明度，克制的氛围而非装饰） */
  background:
    radial-gradient(1100px 520px at 50% -8%, rgba(24, 144, 255, 0.1), rgba(24, 144, 255, 0) 60%),
    #0a1a2f;
  display: flex;
  align-items: center;
  justify-content: center;
}
.screen-container {
  flex-shrink: 0;
  transform-origin: center center;
  position: relative;
}
</style>
