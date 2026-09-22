<template>
  <div class="credit-board">
    <div class="board-head">
      <span class="bar"></span>
      <span class="t">企业信用红黄绿榜</span>
      <span class="cycle">截至 {{ cycle || '—' }}</span>
    </div>
    <div class="board-body" v-if="list.length">
      <div class="board-item" v-for="(item, idx) in list" :key="idx" @click="$emit('select', item)">
        <span class="rank" :class="'r' + (idx + 1)">{{ idx + 1 }}</span>
        <span class="cname">{{ item.companyName }}</span>
        <span class="score"><b>{{ item.totalScore }}</b>分</span>
        <span class="level" :class="levelClass(item)">{{ levelText(item) }}</span>
      </div>
    </div>
    <div class="board-empty" v-else>本周期暂无评价结果{{ degraded ? '（降级模式）' : '' }}</div>
  </div>
</template>

<script>
/**
 * CreditBoard 信用红黄绿榜（#E-30，吴一萱）
 * 等级语义色对齐Ant Design色板（红#f5222d/黄#faad14/绿#52c41a，风格基线§2）；
 * 排序：红牌优先（后端按总分升序返回，前端原样展示不加工——口径KD-09）。
 */
export default {
  name: 'CreditBoard',
  props: {
    list: { type: Array, default: () => [] },
    cycle: { type: String, default: '' },
    degraded: { type: Boolean, default: false }
  },
  methods: {
    levelClass (item) {
      return { 1: 'red', 2: 'yellow', 3: 'green' }[item.level] || 'green'
    },
    levelText (item) {
      return { 1: '红牌', 2: '黄牌', 3: '绿牌' }[item.level] || '绿牌'
    }
  }
}
</script>

<style lang="less" scoped>
.credit-board {
  height: 100%;
  display: flex;
  flex-direction: column;
  .board-head {
    flex-shrink: 0;
    display: flex; align-items: center; gap: 6px;
    padding: 8px 12px;
    border-bottom: 1px solid rgba(24, 144, 255, 0.12);
    .bar { width: 3px; height: 13px; background: #1890ff; }
    .t { font-size: 14px; font-weight: 600; color: rgba(255, 255, 255, 0.92); }
    .cycle { margin-left: auto; font-size: 12px; color: rgba(255, 255, 255, 0.45); }
  }
  .board-body { flex: 1; overflow-y: auto; padding: 4px 12px 8px; }
  .board-item {
    height: 34px;
    display: flex;
    align-items: center;
    color: rgba(255, 255, 255, 0.85);
    font-size: 13px;
    border-bottom: 1px dashed rgba(24, 144, 255, 0.15);
    cursor: pointer;
    transition: background 0.2s ease;
    &:hover { background: rgba(24, 144, 255, 0.1); }
    .rank {
      width: 18px; height: 18px; line-height: 18px; text-align: center;
      border-radius: 2px; margin-right: 8px; font-size: 11px; font-weight: 600;
      color: rgba(255, 255, 255, 0.8); background: rgba(255, 255, 255, 0.12);
      &.r1 { background: #f5222d; color: #fff; }
      &.r2 { background: #fa8c16; color: #fff; }
      &.r3 { background: #1890ff; color: #fff; }
    }
    .cname { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .score {
      margin-right: 12px; color: rgba(255, 255, 255, 0.65);
      font-variant-numeric: tabular-nums;
      b { color: rgba(255, 255, 255, 0.9); font-weight: 600; }
    }
    .level {
      width: 44px; text-align: center; border-radius: 2px; font-size: 12px;
      &.red { color: #ff7875; border: 1px solid rgba(245, 34, 45, 0.6); background: rgba(245, 34, 45, 0.1); }
      &.yellow { color: #ffd666; border: 1px solid rgba(250, 173, 20, 0.6); background: rgba(250, 173, 20, 0.1); }
      &.green { color: #95de64; border: 1px solid rgba(82, 196, 26, 0.55); background: rgba(82, 196, 26, 0.1); }
    }
  }
  .board-empty {
    flex: 1; display: flex; align-items: center; justify-content: center;
    color: rgba(255, 255, 255, 0.45); font-size: 12px;
  }
}
</style>
