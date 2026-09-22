<template>
  <div class="dual-chart">
    <div ref="pie" class="half"></div>
    <div ref="gauge" class="half"></div>
  </div>
</template>

<script>
/**
 * IndustryPie + ProcessGauge 行业分布与处理率（#E-29，吴一萱）
 * 一组件两图（右上区第二格：左饼右仪表），共享一次 resize/dispose 管理；
 * 行业编码由前端字典映射（KD-07修正：base_company_type为警告表，编码翻译在此完成，
 * 字典未配置时显示编码原文）；处理率为百分数（KD-08）。
 */
import * as echarts from 'echarts'

const INDUSTRY_DICT = { '01': '正餐', '02': '快餐', '03': '烧烤', '04': '食堂', '05': '其他' }

export default {
  name: 'IndustryProcessCharts',
  props: {
    industry: { type: Array, default: () => [] },
    processRate: { type: [Number, String], default: 0 }
  },
  data () {
    return { pie: null, gauge: null }
  },
  watch: {
    industry: { handler () { this.renderPie() }, deep: true },
    processRate () { this.renderGauge() }
  },
  mounted () {
    this.pie = echarts.init(this.$refs.pie)
    this.gauge = echarts.init(this.$refs.gauge)
    this.renderPie()
    this.renderGauge()
    this._onResize = () => { this.pie && this.pie.resize(); this.gauge && this.gauge.resize() }
    window.addEventListener('resize', this._onResize)
  },
  beforeDestroy () {
    window.removeEventListener('resize', this._onResize)
    this.pie && this.pie.dispose()
    this.gauge && this.gauge.dispose()
    this.pie = this.gauge = null
  },
  methods: {
    renderPie () {
      if (!this.pie) return
      const rows = this.industry.map(r => ({
        name: INDUSTRY_DICT[r.industryCode] || r.industryCode || '未分类',
        value: r.companyCount
      }))
      this.pie.setOption({
        // 冷色科技系为主，规避与"超标/告警红"的语义混淆（红色在该页面专表告警）
        color: ['#1890ff', '#36cfc9', '#52c41a', '#faad14', '#cba6ff', '#9254de'],
        tooltip: { trigger: 'item', backgroundColor: 'rgba(16,38,63,.95)', borderColor: '#1890ff', textStyle: { color: '#fff' } },
        legend: { orient: 'vertical', right: 4, top: 'center', textStyle: { color: 'rgba(255,255,255,.65)', fontSize: 10 }, itemWidth: 10, itemHeight: 10 },
        series: [{
          type: 'pie', radius: ['42%', '68%'], center: ['38%', '50%'],
          label: { show: false },
          emphasis: { label: { show: true, formatter: '{b}\n{d}%', color: '#fff' } },
          data: rows
        }]
      })
    },
    renderGauge () {
      if (!this.gauge) return
      const val = Number(this.processRate) || 0
      this.gauge.setOption({
        series: [{
          type: 'gauge', center: ['50%', '58%'], radius: '88%',
          startAngle: 200, endAngle: -20, min: 0, max: 100,
          axisLine: { lineStyle: { width: 10, color: [[0.6, '#f5222d'], [0.85, '#faad14'], [1, '#52c41a']] } },
          pointer: { itemStyle: { color: 'rgba(255,255,255,.85)' }, length: '60%' },
          axisTick: { show: false }, splitLine: { show: false }, axisLabel: { show: false },
          detail: {
            valueAnimation: true, formatter: '{value}%', color: 'rgba(255,255,255,.85)', fontSize: 18,
            offsetCenter: [0, '62%']
          },
          title: { show: true, offsetCenter: [0, '88%'], color: 'rgba(255,255,255,.45)', fontSize: 10 },
          data: [{ value: val, name: '告警处理率' }]
        }]
      })
    }
  }
}
</script>

<style scoped>
.dual-chart { width: 100%; height: 100%; display: flex; }
.half { width: 50%; height: 100%; }
</style>
