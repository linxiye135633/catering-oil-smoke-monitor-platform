<template>
  <div class="alarm-ticker" @mouseenter="paused = true" @mouseleave="paused = false">
    <span class="ticker-label"><span class="bar"></span>实时告警</span>
    <div class="ticker-window" ref="window">
      <div class="ticker-track" v-if="list.length" :style="trackStyle" @transitionend="loop">
        <div class="ticker-item" v-for="(item, idx) in list" :key="idx" :class="{ fresh: isFresh(item) }">
          <span class="t">{{ item.alarmTime }}</span>
          <span class="name">{{ item.companyName }}</span>
          <span class="type" :class="typeClass(item)">{{ typeText(item) }}</span>
          <span class="val" v-if="item.value != null">{{ item.value }} mg/m³</span>
        </div>
      </div>
      <div class="ticker-empty" v-else>当前无风险告警</div>
    </div>
    <span v-if="degraded" class="degraded-tag">降级模式</span>
  </div>
</template>

<script>
/**
 * AlarmTicker 实时告警无缝滚动条（#E-26，吴一萱）
 * - requestAnimationFrame/transition 双轨无缝滚动：首条滚出后追加到队尾循环；
 * - hover 暂停（值班员查看需求，用例TC-E-009）；
 * - 新告警高亮（10分钟内 fresh 类，FR-E02验收）；空态不报错；降级黄标。
 * 数据由父组件通过 list 属性注入（来源：screenPoll 订阅 /v2/dashboard/alarms/latest）。
 */
export default {
  name: 'AlarmTicker',
  props: {
    list: { type: Array, default: () => [] },
    degraded: { type: Boolean, default: false }
  },
  data () {
    return { paused: false, offset: 0, ticking: false }
  },
  computed: {
    trackStyle () {
      return { transform: `translateY(${this.offset}px)`, transition: this.paused ? 'none' : 'transform 0.5s linear' }
    }
  },
  mounted () {
    this.startLoop()
  },
  beforeDestroy () {
    cancelAnimationFrame(this._raf)
  },
  methods: {
    startLoop () {
      const step = () => {
        if (!this.paused && this.list.length) {
          this.offset -= 0.6
          // 滚出整个列表后回位，保持无缝循环
          const trackH = this.$refs.window && this.$refs.window.firstElementChild
            ? this.$refs.window.firstElementChild.scrollHeight : 0
          const winH = this.$refs.window ? this.$refs.window.clientHeight : 0
          if (trackH > winH && -this.offset >= trackH - winH + 36) {
            this.offset = 0
          }
        }
        this._raf = requestAnimationFrame(step)
      }
      this._raf = requestAnimationFrame(step)
    },
    loop () {},
    isFresh (item) {
      if (!item.alarmTime) return false
      return Date.now() - new Date(item.alarmTime.replace(/-/g, '/')).getTime() < 10 * 60 * 1000
    },
    typeClass (item) {
      const t = String(item.alarmType)
      if (['1', '5', '6'].includes(t)) return 'red'
      if (['2', '3', '7'].includes(t)) return 'orange'
      return 'yellow'
    },
    /** 告警类型字典（与 supervision/Warning.vue 口径一致） */
    typeText (item) {
      const dict = { 1: '营业时段停机', 2: '没有联动开启', 3: '二次电压过低', 4: '设备联网异常', 5: '烟气排放超标', 6: '高压电场异常', 7: '设备压差异常', 8: '其他原因异常' }
      return dict[String(item.alarmType)] || ('异常类型' + item.alarmType)
    }
  }
}
</script>

<style lang="less" scoped>
.alarm-ticker {
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
  position: relative;
  overflow: hidden;
  .ticker-label {
    display: flex; align-items: center; gap: 6px;
    color: rgba(255, 255, 255, 0.92); font-size: 14px; font-weight: 600;
    margin-right: 24px; white-space: nowrap;
    .bar { width: 3px; height: 13px; background: #1890ff; }
  }
  .ticker-window {
    flex: 1;
    overflow: hidden;
    height: 52px;
    align-self: center;
  }
  .ticker-track {
    display: flex;
    flex-direction: column;
    will-change: transform;
  }
  .ticker-item {
    height: 30px;
    display: flex;
    align-items: center;
    color: rgba(255, 255, 255, 0.85);
    font-size: 13px;
    .t { width: 150px; color: rgba(255, 255, 255, 0.5); font-variant-numeric: tabular-nums; }
    .name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .type {
      margin: 0 14px;
      padding: 1px 8px;
      border: 1px solid;
      border-radius: 2px;
      font-size: 12px;
      &.red { color: #ff7875; border-color: rgba(245, 34, 45, 0.6); background: rgba(245, 34, 45, 0.1); }
      &.orange { color: #ffc069; border-color: rgba(250, 140, 22, 0.6); background: rgba(250, 140, 22, 0.1); }
      &.yellow { color: #ffd666; border-color: rgba(250, 173, 20, 0.6); background: rgba(250, 173, 20, 0.1); }
    }
    .val { color: rgba(255, 255, 255, 0.65); }
    &.fresh {
      background: rgba(245, 34, 45, 0.1);
      box-shadow: inset 3px 0 0 rgba(245, 34, 45, 0.8);
      /* 新告警一次性淡入，避免持续闪烁干扰值班 */
      animation: fadein 0.6s ease;
    }
  }
  .ticker-empty {
    height: 100%;
    display: flex;
    align-items: center;
    color: rgba(255, 255, 255, 0.45);
    font-size: 12px;
  }
  .degraded-tag {
    position: absolute;
    right: 12px; top: 8px;
    font-size: 11px;
    color: #faad14;
    border: 1px solid rgba(250, 173, 20, 0.6);
    background: rgba(250, 173, 20, 0.08);
    padding: 0 5px; line-height: 18px; border-radius: 2px;
  }
}
@keyframes fadein {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
