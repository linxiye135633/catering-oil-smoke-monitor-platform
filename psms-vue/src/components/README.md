# E组公共组件发布说明（#E-24，供其他组复用=加分项）

> 两组件均**零业务耦合**（仅依赖 ant-design-vue），复制目录即可用。采用后请在组间互评"可借鉴点"注明来源。

## 1. ScreenContainer 大屏等比缩放容器

- 能力：以任意设计基准（默认1920×1080）等比缩放适配任意分辨率/投影，超宽屏留深色底不拉伸；resize 防抖、销毁清理。
- 用法：

```vue
<template>
  <screen-container :design-width="1920" :design-height="1080">
    <!-- 按1920×1080绝对布局写你的大屏内容 -->
  </screen-container>
</template>
<script>
import ScreenContainer from '@/components/ScreenContainer'
export default { components: { ScreenContainer } }
</script>
```

- 适用：E组大屏已用；A组预测看板、B组报告预览页等全屏场景可直接复用。
- 注意：容器内图表请自行 dispose（参考 E组 TrendChart.vue 写法）。

## 2. ChartCard 图表卡片（深色版三态卡）

- 能力：标题（#1890FF 强调条）+ a-spin 加载态 + 空态文案 + 内容插槽，四行接入。
- 用法：

```vue
<chart-card title="告警趋势" :loading="loading" :empty="!list.length" empty-text="暂无数据">
  <trend-chart :data="list" />
</chart-card>
```

- 浅色后台页面也可用（卡片底色为深色大屏定制，浅色场景自行覆盖 `.chart-card` 背景即可）。

## 3. screenPoll 统一轮询服务（utils/screenPoll.js）

- 能力：注册制轮询（key去重）、峰谷间隔参数化（默认峰60s/谷300s，MUST≤5min达标）、页面不可见暂停、失败静默待下轮。
- 适用：任何需要周期拉取的页面（B组知识库刷新、D组工单列表轮询等）。

```js
import screenPoll from '@/utils/screenPoll'
const poll = screenPoll.register({ key: 'myPage', url: '/v2/xxx', handler: d => this.data = d })
poll.start()
// beforeDestroy: screenPoll.unregister('myPage')
```

## 4. 环境变量样例（复制到各自 .env.development 按需修改）

见 `frontend/.env.example`。百度地图AK、轮询峰谷值均已配置化——不要复制1.0把AK硬编码进组件的做法。
