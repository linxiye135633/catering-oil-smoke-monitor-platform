<template>
  <div ref="mapPanel" class="map-panel">
    <baidu-map v-if="ak" :ak="ak" :center="center" :zoom="12" class="bmap" @ready="onMapReady" :map-click="false">
      <bm-marker v-for="(p, idx) in renderPoints" :key="idx"
        :position="{ lng: p.lng, lat: p.lat }"
        :icon="iconOf(p)"
        @click="active = p">
      </bm-marker>
      <bm-info-window v-if="active" :position="{ lng: active.lng, lat: active.lat }" @close="active = null">
        <div class="pop">
          <p class="pop-name">{{ active.companyName }}</p>
          <p>最新浓度：{{ active.latestValue != null ? active.latestValue : '—' }} mg/m³</p>
          <p>近30天热力权重：{{ active.heatWeight || 0 }}</p>
          <p :class="active.excessive ? 'red' : 'green'">{{ active.excessive ? '当前超标' : '当前正常' }}</p>
        </div>
      </bm-info-window>
    </baidu-map>
    <div v-else class="map-fallback">地图未配置（VUE_APP_BMAP_AK），展示区域底图占位</div>
    <!-- A组风险预测榜角标（#E-55，契约C-01） -->
    <div class="risk-top" v-if="riskTop && riskTop.length">
      <div class="risk-title"><span class="bar"></span>风险预测 TOP{{ riskTop.length }}（A组）</div>
      <div class="risk-item" v-for="(r, i) in riskTop" :key="i">
        <span class="ri">{{ i + 1 }}</span>{{ r.companyName }}
        <span class="rlv">{{ r.riskLevel === 1 ? '高' : r.riskLevel === 2 ? '中' : '低' }}风险 {{ r.probability || '' }}%</span>
      </div>
    </div>
    <div class="risk-top empty" v-else-if="riskTopLoaded">预测数据生成中（A组）</div>

    <div class="quality-tip" v-if="invalidCount > 0 || clustered">{{ invalidCount > 0 ? invalidCount + ' 家企业坐标缺失未渲染' : '' }}{{ invalidCount > 0 && clustered ? '；' : '' }}{{ clustered ? '点位超200已聚合渲染' : '' }}</div>
  </div>
</template>

<script>
/**
 * MapPanel 区域总览地图区（#E-19/#E-20/#E-55，王立洁）
 * - 底座沿用1.0百度地图（vue-baidu-map），AK 从 .env 读取（治理1.0硬编码）；
 * - #E-20 热力图层：动态加载 BMapLib.HeatmapOverlay，权重=heatWeight（KD-02：
 *   近30天超标频次×浓度均值，后端聚合）；点位渲染与热力可并存；
 * - (0,0)缺失坐标过滤并计数（UC-E01扩展流3a）；
 * - #E-55 A组风险榜角标：数据原样展示不加工（口径责任在A组）。
 */
import BaiduMap from 'vue-baidu-map/components/map/Map.vue'
import BmMarker from 'vue-baidu-map/components/overlays/Marker.vue'
import BmInfoWindow from 'vue-baidu-map/components/overlays/InfoWindow.vue'

export default {
  name: 'MapPanel',
  components: { BaiduMap, BmMarker, BmInfoWindow },
  props: {
    points: { type: Array, default: () => [] },
    riskTop: { type: Array, default: () => [] }
  },
  data () {
    return {
      ak: process.env.VUE_APP_BMAP_AK || '',
      center: { lng: 116.4, lat: 39.9 },
      active: null,
      mapObj: null,
      heatmap: null,
      riskTopLoaded: false
    }
  },
  computed: {
    validPoints () {
      return this.points.filter(p => Number(p.lng) !== 0 && Number(p.lat) !== 0)
    },
    invalidCount () {
      return this.points.length - this.validPoints.length
    },
    /** #E-23 点聚合（轻量版）：>200点时按"超标优先+热力权重"取前200渲染，
     *  其余点位仍参与热力层（数据不丢、渲染不卡）；超量时角标提示 */
    renderPoints () {
      if (this.validPoints.length <= 200) return this.validPoints
      return this.validPoints
        .slice()
        .sort((a, b) => (b.excessive - a.excessive) || (Number(b.heatWeight) - Number(a.heatWeight)))
        .slice(0, 200)
    },
    clustered () {
      return this.validPoints.length > 200
    }
  },
  watch: {
    riskTop () { this.riskTopLoaded = true },
    points: {
      handler () { this.applyHeatmap() },
      deep: true
    }
  },
  beforeDestroy () {
    this.heatmap = null
    this.mapObj = null
  },
  methods: {
    onMapReady ({ BMap, map }) {
      this.mapObj = map
      this.BMap = BMap
      this.loadHeatmapLib(() => this.applyHeatmap())
    },
    /** 动态加载 BMapLib 热力扩展（加载一次）。
     *  本库自包含 h337 + BMapLib.HeatmapOverlay，已落库为本地静态资源，
     *  避免依赖百度 CDN（该环境 CDN/referer 策略会拦截外链库）。 */
    loadHeatmapLib (callback) {
      if (window.BMapLib) {
        callback()
        return
      }
      const s = document.createElement('script')
      s.src = process.env.BASE_URL + 'libs/bmap/Heatmap_min.js'
      s.onload = callback
      s.onerror = () => console.warn('[MapPanel] 热力扩展加载失败，仅展示点位')
      document.head.appendChild(s)
    },
    /** 应用热力图层：data.count=heatWeight，max取样本最大值保证归一化 */
    applyHeatmap () {
      if (!this.mapObj || !window.BMapLib || !this.validPoints.length) return
      if (!this.heatmap) {
        this.heatmap = new window.BMapLib.HeatmapOverlay({ radius: 24, visible: true })
        this.mapObj.addOverlay(this.heatmap)
      }
      const data = this.validPoints
        .filter(p => Number(p.heatWeight) > 0)
        .map(p => ({ lng: Number(p.lng), lat: Number(p.lat), count: Number(p.heatWeight) }))
      const max = data.reduce((m, d) => Math.max(m, d.count), 1)
      this.heatmap.setDataSet({ data, max })
    },
    iconOf (p) {
      // 内联 SVG 图标，避免外链图片在受限网络下裂图
      const mk = (fill, stroke) => 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(
        `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="32" viewBox="0 0 24 32">` +
        `<path d="M12 1C6 1 1.5 5.5 1.5 11.5C1.5 19 12 31 12 31S22.5 19 22.5 11.5C22.5 5.5 18 1 12 1Z" fill="${fill}" stroke="${stroke}" stroke-width="1.5"/>` +
        `<circle cx="12" cy="11.5" r="4" fill="#fff"/></svg>`)
      return p.excessive
        ? { url: mk('#f5222d', '#ffa39e'), size: { width: 24, height: 32 } }
        : { url: mk('#1890ff', '#91d5ff'), size: { width: 24, height: 32 } }
    }
  }
}
</script>

<style lang="less" scoped>
.map-panel {
  position: relative;
  height: 100%;
  .bmap, .map-fallback { width: 100%; height: 100%; }
  .map-fallback {
    display: flex; align-items: center; justify-content: center;
    color: rgba(255, 255, 255, 0.45);
    border: 1px dashed rgba(24, 144, 255, 0.3);
  }
  .quality-tip {
    position: absolute; right: 8px; bottom: 8px; font-size: 12px; color: #faad14;
    background: rgba(16, 38, 63, 0.9); padding: 2px 8px; border-radius: 2px;
  }
  .risk-top {
    position: absolute; left: 12px; top: 12px; width: 208px; z-index: 10;
    background: rgba(16, 38, 63, 0.94); border: 1px solid rgba(245, 34, 45, 0.5);
    border-radius: 4px; padding: 8px 10px; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
    .risk-title {
      display: flex; align-items: center; gap: 6px;
      color: #ff7875; font-size: 12px; font-weight: 600; margin-bottom: 6px;
      .bar { width: 3px; height: 11px; background: #f5222d; }
    }
    .risk-item {
      color: rgba(255, 255, 255, 0.85); font-size: 12px; line-height: 22px;
      display: flex; align-items: center;
      .ri {
        width: 15px; height: 15px; line-height: 15px; text-align: center;
        background: rgba(245, 34, 45, 0.9); color: #fff; border-radius: 2px;
        margin-right: 6px; font-size: 10px; font-weight: 600; flex-shrink: 0;
      }
      .rlv { margin-left: auto; color: #ffc069; font-size: 11px; }
      overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
    }
    &.empty { border-color: rgba(24, 144, 255, 0.35); color: rgba(255, 255, 255, 0.45); font-size: 12px; }
  }
  .pop {
    color: #333; min-width: 160px;
    .pop-name { font-weight: 600; }
    .red { color: #f5222d; }
    .green { color: #52c41a; }
  }
}
</style>
