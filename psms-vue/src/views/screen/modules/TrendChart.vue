<template>
  <div ref="chart" class="trend-chart"></div>
</template>

<script>
/**
 * TrendChart 告警趋势折线图（#E-28，吴一萱）
 * 图表三步法：容器初始化 → option配置 → resize自适应+dispose销毁（防内存泄漏）；
 * 数据转换：后端已group by（行式 [{statDate,alarmCount}]），前端reduce转类别轴+系列。
 */
import * as echarts from 'echarts'

export default {
  name: 'TrendChart',
  props: {
    data: { type: Array, default: () => [] }
  },
  data () {
    return { chart: null }
  },
  watch: {
    data: {
      handler () { this.render() },
      deep: true
    }
  },
  mounted () {
    this.init()
    this._onResize = () => this.chart && this.chart.resize()
    window.addEventListener('resize', this._onResize)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this._onResize)
    if (this.chart) {
      this.chart.dispose() // 稳定性NFR：销毁防泄漏
      this.chart = null
    }
  },
  methods: {
    init () {
      this.chart = echarts.init(this.$refs.chart)
      this.render()
    },
    render () {
      if (!this.chart) return
      // 行式数据reduce为类别轴+数值系列（补零：无告警日显示0而非断线）
      const byDate = {}
      this.data.forEach(r => { byDate[r.statDate] = r.alarmCount })
      const dates = this.data.map(r => r.statDate)
      const values = dates.map(d => byDate[d] || 0)
      this.chart.setOption({
        grid: { left: 40, right: 16, top: 20, bottom: 24 },
        tooltip: { trigger: 'axis', backgroundColor: 'rgba(16,38,63,.95)', borderColor: '#1890ff', textStyle: { color: '#fff' } },
        xAxis: {
          type: 'category', data: dates, boundaryGap: false,
          axisLine: { lineStyle: { color: 'rgba(255,255,255,.3)' } },
          axisLabel: { color: 'rgba(255,255,255,.65)', fontSize: 10, formatter: v => v.slice(5) }
        },
        yAxis: {
          type: 'value', minInterval: 1,
          axisLine: { show: false }, splitLine: { lineStyle: { color: 'rgba(255,255,255,.08)' } },
          axisLabel: { color: 'rgba(255,255,255,.65)', fontSize: 10 }
        },
        series: [{
          type: 'line', data: values, smooth: true, symbolSize: 5,
          lineStyle: { color: '#1890ff', width: 2 },
          itemStyle: { color: '#1890ff' },
          areaStyle: {
            color: {
              type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(24,144,255,.45)' },
                { offset: 1, color: 'rgba(24,144,255,.02)' }
              ]
            }
          }
        }]
      })
    }
  }
}
</script>

<style scoped>
.trend-chart { width: 100%; height: 100%; }
</style>
