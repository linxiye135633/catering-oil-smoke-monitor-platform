<template>
  <div class="chart-card">
    <div class="chart-card-title">
      <span class="bar"></span>
      <span>{{ title }}</span>
      <slot name="extra"></slot>
    </div>
    <div class="chart-card-body">
      <a-spin :spinning="loading" size="small">
        <div v-if="empty" class="chart-card-empty">{{ emptyText }}</div>
        <slot v-else></slot>
      </a-spin>
    </div>
  </div>
</template>

<script>
/**
 * ChartCard 图表卡片公共组件（#E-27，吴一萱）
 * 统一三态：加载(a-spin)/空态(emptyText)/正常(slot内容)
 * 深色主题随大屏（课程MUST），强调色#1890FF与1.0品牌同源（界面风格基线§2）
 * 供他组复用（公共组件加分项），依赖仅 antd spin，零业务耦合。
 */
export default {
  name: 'ChartCard',
  props: {
    title: { type: String, default: '' },
    loading: { type: Boolean, default: false },
    empty: { type: Boolean, default: false },
    emptyText: { type: String, default: '暂无数据' }
  }
}
</script>

<style lang="less" scoped>
.chart-card {
  background: rgba(16, 38, 63, 0.9);
  border: 1px solid rgba(24, 144, 255, 0.25);
  border-radius: 4px;
  height: 100%;
  display: flex;
  flex-direction: column;
  &-title {
    flex-shrink: 0;
    padding: 8px 12px;
    font-size: 14px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.92);
    display: flex;
    align-items: center;
    gap: 6px;
    border-bottom: 1px solid rgba(24, 144, 255, 0.12);
    .bar {
      display: inline-block;
      width: 3px;
      height: 13px;
      background: #1890ff;
    }
  }
  &-body {
    flex: 1;
    padding: 6px 12px 8px;
    overflow: hidden;
    /deep/ .ant-spin-nested-loading,
    /deep/ .ant-spin-container {
      height: 100%;
    }
  }
  &-empty {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(255, 255, 255, 0.45);
    font-size: 12px;
  }
}
</style>
