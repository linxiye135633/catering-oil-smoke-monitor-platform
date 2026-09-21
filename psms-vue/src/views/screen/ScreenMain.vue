<template>
  <screen-container>
    <div class="screen-main">
      <!-- 顶栏 -->
      <header class="screen-header">
        <div class="brand">
          <svg class="brand-mark" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M12 22V9.6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            <circle cx="12" cy="7.4" r="2.2" stroke="currentColor" stroke-width="1.6" />
            <path d="M6.6 21c-1-1.6-1.6-3.2-1.6-4.8A7 7 0 0 1 12 9.2a7 7 0 0 1 7 7c0 1.6-.6 3.2-1.6 4.8" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" />
          </svg>
          <span class="brand-title">餐饮业油烟在线大数据监控平台</span>
          <i class="brand-divider"></i>
          <span class="brand-cabin">监管驾驶舱</span>
        </div>
        <div class="header-right">
          <span class="clock">{{ clock }}</span>
          <i class="clock-dot"></i>
          <a-tooltip :title="carousel ? '轮播中（' + carouselSec + 's/页），点击关闭' : '开启图表轮播'">
            <a-icon :type="carousel ? 'pause-circle' : 'play-circle'"
              :class="['carousel-btn', { on: carousel }]" @click="toggleCarousel" />
          </a-tooltip>
          <a-icon v-if="degradedCount > 0" type="warning" theme="filled" class="degraded-icon" />
        </div>
      </header>

      <!-- 内容四区（#E-21 聚合联调版；#E-22 图表区轮播） -->
      <main class="screen-body">
        <!-- 左区：地图（含A组风险角标#E-55、热力#E-20、点聚合#E-23） -->
        <section class="zone-map">
          <map-panel :points="points" :risk-top="riskTop" />
        </section>

        <!-- 右上：统计图表区（#E-28/29 实装 + #E-56 AI周报卡；#E-22 轮播时每页一卡放大） -->
        <section class="zone-charts" :class="{ 'carousel-mode': carousel }">
          <template v-if="!carousel">
            <chart-card title="告警趋势（近7日）" :loading="loading" :empty="!trend.length" empty-text="暂无告警数据">
              <trend-chart :data="trend" />
            </chart-card>
            <chart-card title="行业分布 / 处理率" :loading="loading" :empty="!industry.length && rateVal === 0" empty-text="暂无数据">
              <industry-process-charts :industry="industry" :process-rate="rateVal" />
            </chart-card>
            <chart-card title="AI 周报摘要" :loading="reportLoading" :empty="!weeklyReport" empty-text="等待生成">
              <div class="report-body" v-if="weeklyReport">
                <p class="rep-sum">{{ weeklyReport.summary }}</p>
                <p class="rep-line" v-if="weeklyReport.riskHint"><span class="tag tag-risk">风险</span> {{ weeklyReport.riskHint }}</p>
                <p class="rep-line" v-if="weeklyReport.suggestion"><span class="tag tag-sug">建议</span> {{ weeklyReport.suggestion }}</p>
                <span class="rep-degraded" v-if="weeklyReport.degraded">降级模式（纯统计）</span>
              </div>
            </chart-card>
          </template>
          <template v-else>
            <chart-card :title="carouselTitle" :loading="loading">
              <trend-chart v-if="carouselIdx === 0" :data="trend" />
              <industry-process-charts v-else-if="carouselIdx === 1" :industry="industry" :process-rate="rateVal" />
              <div class="report-body report-large" v-else-if="weeklyReport">
                <p class="rep-sum">{{ weeklyReport.summary }}</p>
                <p class="rep-line" v-if="weeklyReport.riskHint"><span class="tag tag-risk">风险</span> {{ weeklyReport.riskHint }}</p>
                <p class="rep-line" v-if="weeklyReport.suggestion"><span class="tag tag-sug">建议</span> {{ weeklyReport.suggestion }}</p>
                <span class="rep-degraded" v-if="weeklyReport.degraded">降级模式（纯统计）</span>
              </div>
            </chart-card>
          </template>
        </section>

        <!-- 右下：信用榜（#E-30 真实数据） -->
        <section class="zone-credit">
          <credit-board :list="creditBoard" :cycle="creditCycle" @select="showCreditDetail" />
        </section>

        <!-- 中带：告警滚动（#E-26） -->
        <section class="zone-alarm">
          <alarm-ticker :list="alarms" :degraded="partDegraded('alerts')" />
        </section>

        <!-- 底栏：区域总览卡片（KD-01/03/04，聚合regionOverview分区） -->
        <footer class="screen-footer">
          <div class="stat-card" v-for="s in stats" :key="s.label">
            <div class="stat-value">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </footer>
      </main>

      <!-- 信用明细抽屉（UC-E03扩展流：点击榜单企业） -->
      <a-drawer title="企业信用明细" placement="right" width="380" :visible="detailVisible" @close="detailVisible = false">
        <div v-if="creditDetail" class="credit-detail">
          <p class="cd-name">{{ creditDetail.companyName }}（{{ creditDetail.cycle }}）</p>
          <p class="cd-score">总分 <b>{{ creditDetail.totalScore }}</b> · {{ levelText(creditDetail.level) }}</p>
          <a-timeline style="margin-top:12px">
            <a-timeline-item v-for="(d, i) in creditDetail.details || []" :key="i"
              :color="Number(d.changeScore) < 0 ? 'red' : 'green'">
              {{ d.ruleCode }}：{{ d.changeScore }}分（{{ d.times }}次）<br />
              <span class="cd-src">依据：{{ d.sourceNo || '—' }} {{ d.remark || '' }}</span>
            </a-timeline-item>
          </a-timeline>
          <p class="cd-empty" v-if="!(creditDetail.details || []).length">本周期无扣分记录</p>
        </div>
      </a-drawer>
    </div>
  </screen-container>
</template>

<script>
/**
 * ScreenMain 大屏主页面（#E-16/17/18/21/30/55，王立洁+吴一萱）
 * Sprint2 聚合联调版：五区数据走单一 /v2/dashboard/aggregate 轮询（#E-04轻量合并），
 * 地图点位独立轮询（数据量大）；分区独立降级（degradedCount 驱动顶栏黄标）。
 */
import ScreenContainer from '@/components/ScreenContainer'
import ChartCard from '@/components/ChartCard'
import MapPanel from './modules/MapPanel'
import AlarmTicker from './modules/AlarmTicker'
import CreditBoard from './modules/CreditBoard'
import TrendChart from './modules/TrendChart'
import IndustryProcessCharts from './modules/IndustryProcessCharts'
import screenPoll from '@/utils/screenPoll'
import { getAction } from '@/api/manage'

export default {
  name: 'ScreenMain',
  components: { ScreenContainer, ChartCard, MapPanel, AlarmTicker, CreditBoard, TrendChart, IndustryProcessCharts },
  data () {
    return {
      clock: '',
      loading: false,
      points: [],
      alarms: [],
      trend: [],
      industry: [],
      processRate: null,
      creditBoard: [],
      creditCycle: '',
      riskTop: [],
      parts: {},
      weeklyReport: null,
      reportLoading: false,
      detailVisible: false,
      creditDetail: null,
      carousel: false,
      carouselIdx: 0,
      carouselSec: 30
    }
  },
  computed: {
    carouselTitle () {
      return ['告警趋势（近7日）', '行业分布 / 处理率', 'AI 周报摘要'][this.carouselIdx] || ''
    },
    degradedCount () {
      return Object.values(this.parts).filter(p => p && p.degraded).length
    },
    rateVal () {
      if (!this.processRate) return 0
      const total = Number(this.processRate.total || 0)
      const cleared = Number(this.processRate.cleared || 0)
      return total === 0 ? 0 : Math.round(cleared * 1000 / total) / 10
    },
    stats () {
      const o = (this.parts.regionOverview && this.parts.regionOverview.data) || {}
      const online = o.pointCount ? Math.round((o.onlinePointCount || 0) * 1000 / o.pointCount) / 10 : '—'
      return [
        { label: '企业总数', value: o.companyCount != null ? o.companyCount : '—' },
        { label: '监测点数', value: o.pointCount != null ? o.pointCount : '—' },
        { label: '设备在线率(24h)', value: o.pointCount ? online + '%' : '—' },
        { label: '超标企业数(24h)', value: o.excessiveCompanyCount != null ? o.excessiveCompanyCount : '—' }
      ]
    }
  },
  mounted () {
    this.tickClock()
    this._clockTimer = setInterval(this.tickClock, 1000)
    // #E-22 轮播开关记忆（localStorage），间隔可配
    this.carouselSec = Number(localStorage.getItem('screen:carousel:sec')) || 30
    if (localStorage.getItem('screen:carousel:on') === '1') {
      this.startCarousel()
    }
    // 单一聚合轮询（峰谷策略见 screenPoll）
    this.aggPoll = screenPoll.register({
      key: 'dashboard:aggregate',
      url: '/v2/dashboard/aggregate',
      handler: d => this.applyAggregate(d || {})
    })
    this.pointsPoll = screenPoll.register({
      key: 'dashboard:points',
      url: '/v2/dashboard/map/points',
      handler: d => { this.points = d || [] }
    })
    this.aggPoll.start()
    this.pointsPoll.start()
    this.loadWeeklyReport()
  },
  beforeDestroy () {
    clearInterval(this._clockTimer)
    this.stopCarousel()
    screenPoll.unregister('dashboard:aggregate')
    screenPoll.unregister('dashboard:points')
  },
  methods: {
    /** #E-22 大屏轮播：图表区三卡轮换，开关记忆、到点切换且不产生额外请求（数据仍由聚合轮询供给） */
    toggleCarousel () {
      this.carousel ? this.stopCarousel() : this.startCarousel()
    },
    startCarousel () {
      this.carousel = true
      localStorage.setItem('screen:carousel:on', '1')
      this._carouselTimer = setInterval(() => {
        this.carouselIdx = (this.carouselIdx + 1) % 3
      }, this.carouselSec * 1000)
    },
    stopCarousel () {
      this.carousel = false
      this.carouselIdx = 0
      localStorage.setItem('screen:carousel:on', '0')
      clearInterval(this._carouselTimer)
    },
    tickClock () {
      const p = n => String(n).padStart(2, '0')
      const d = new Date()
      this.clock = `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
    },
    applyAggregate (d) {
      this.loading = true
      this.parts = d
      this.alarms = (d.alerts && d.alerts.data) || []
      this.trend = (d.trend && d.trend.data) || []
      this.industry = (d.industry && d.industry.data) || []
      this.processRate = (d.processRate && d.processRate.data) || null
      const board = (d.creditBoard && d.creditBoard.data) || []
      this.creditBoard = board
      this.creditCycle = board.length ? board[0].cycle : ''
      this.riskTop = (d.riskTop && d.riskTop.data && d.riskTop.data.list) || []
      this.loading = false
    },
    partDegraded (name) {
      return !!(this.parts[name] && this.parts[name].degraded)
    },
    async loadWeeklyReport () {
      this.reportLoading = true
      try {
        const res = await getAction('/v2/stat/weekly-report')
        if (res.success) this.weeklyReport = res.result
      } catch (e) {
        this.weeklyReport = { summary: 'AI摘要暂不可用', degraded: true }
      } finally {
        this.reportLoading = false
      }
    },
    async showCreditDetail (item) {
      this.detailVisible = true
      this.creditDetail = item // 先展示榜单行，明细异步补
      try {
        const res = await getAction('/v2/credit/company', { companyId: item.companyId, cycle: item.cycle })
        if (res.success && res.result && res.result.details) {
          this.creditDetail = res.result
        }
      } catch (e) { /* 保留榜单行数据 */ }
    },
    levelText (l) {
      return { 1: '红牌', 2: '黄牌', 3: '绿牌' }[l] || '—'
    }
  }
}
</script>

<style lang="less" scoped>
@brand: #1890ff;
@bg-card: rgba(16, 38, 63, 0.9);

.screen-main {
  width: 1920px;
  height: 1080px;
  background: #0a1a2f;
  display: flex;
  flex-direction: column;
  color: rgba(255, 255, 255, 0.85);
}
.screen-header {
  height: 72px;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 28px;
  border-bottom: 1px solid rgba(@brand, 0.28);
  .brand {
    display: flex; align-items: center;
    .brand-mark {
      width: 26px; height: 26px; color: @brand; margin-right: 12px; flex-shrink: 0;
    }
    .brand-title {
      font-size: 22px; font-weight: 600; letter-spacing: 1px; color: #ffffff;
    }
    .brand-divider {
      width: 1px; height: 18px; background: rgba(255, 255, 255, 0.18); margin: 0 18px;
    }
    .brand-cabin {
      font-size: 16px; letter-spacing: 4px; color: @brand; font-weight: 500;
    }
  }
  .header-right {
    display: flex; align-items: center;
    .clock {
      color: rgba(255, 255, 255, 0.8); font-size: 15px; letter-spacing: 1px;
      font-variant-numeric: tabular-nums;
    }
    .clock-dot {
      width: 5px; height: 5px; border-radius: 50%; background: #52c41a; margin: 0 18px;
    }
    .degraded-icon { color: #faad14; font-size: 16px; margin-left: 4px; }
  }
}
.screen-body {
  flex: 1;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  grid-template-rows: 1fr 1fr 84px 46px;
  gap: 8px 16px;
  padding: 12px 16px;
  grid-template-areas: 'map charts' 'map credit' 'alarm alarm' 'footer footer';
  /* 地图 / 信用 / 告警区统一卡片容器，与 ChartCard 相同边框与底，保证四区视觉一致 */
  .zone-map, .zone-credit, .zone-alarm {
    background: @bg-card;
    border: 1px solid rgba(@brand, 0.26);
    border-radius: 4px;
    overflow: hidden;
  }
  .zone-map { grid-area: map; }
  .zone-charts {
    grid-area: charts; display: grid; grid-template-rows: 1fr 1fr 1fr; gap: 8px;
    &.carousel-mode { grid-template-rows: 1fr; }
  }
  .zone-credit { grid-area: credit; }
  .zone-alarm { grid-area: alarm; }
  .carousel-btn {
    font-size: 18px; color: rgba(255, 255, 255, 0.5); cursor: pointer; margin-right: 12px;
    &.on { color: #1890ff; }
  }
  .report-large { font-size: 14px; line-height: 1.8; padding: 8px; }
}
.screen-footer {
  grid-area: footer;
  display: flex; gap: 16px;
  .stat-card {
    flex: 1; position: relative; overflow: hidden;
    display: flex; align-items: center; justify-content: center; gap: 10px;
    background: @bg-card; border: 1px solid rgba(@brand, 0.25); border-radius: 4px;
    &::before {
      content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 3px;
      background: @brand; opacity: 0.7;
    }
    .stat-value { font-size: 24px; color: @brand; font-weight: 600; font-variant-numeric: tabular-nums; }
    .stat-label { font-size: 13px; color: rgba(255, 255, 255, 0.6); }
  }
}
.report-body {
  font-size: 12px; color: rgba(255, 255, 255, 0.8); height: 100%; overflow-y: auto; padding: 4px;
  .rep-sum { line-height: 1.7; }
  .rep-line { display: flex; align-items: flex-start; gap: 8px; margin-top: 8px; line-height: 1.6; }
  .tag {
    flex-shrink: 0; padding: 0 6px; border-radius: 2px; font-size: 11px; line-height: 20px;
    &.tag-risk { color: #ff9c6e; border: 1px solid rgba(250, 140, 22, 0.55); background: rgba(250, 140, 22, 0.08); }
    &.tag-sug { color: #73d13d; border: 1px solid rgba(82, 196, 26, 0.5); background: rgba(82, 196, 26, 0.08); }
  }
  .rep-degraded { color: #faad14; border: 1px solid #faad14; padding: 0 4px; border-radius: 2px; font-size: 11px; }
}
.credit-detail {
  color: #333;
  .cd-name { font-size: 15px; font-weight: 600; }
  .cd-score { font-size: 13px; b { color: #1890ff; font-size: 18px; } }
  .cd-src { color: #999; font-size: 11px; }
  .cd-empty { color: #999; }
}
</style>
