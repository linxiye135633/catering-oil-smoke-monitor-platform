# 餐饮业油烟监控平台 2.0 —— E组（监管驾驶舱）增量说明

> 本 README 为 2.0 交付的仓库级说明（对应《指导书》任务10"README更新2.0功能说明与各模块责任人"）。
> 1.0 平台原有 README 内容保留于 git 历史；本文档描述 E 组在 1.0 基线上的全部增量。

## 一、2.0 新增功能（方向E·监管驾驶舱）

| 功能 | 说明 | 优先级 |
|---|---|---|
| 监管驾驶舱大屏 | 1920×1080 等比缩放全屏容器，四区布局：区域地图（百度地图点位+超标热力+点聚合）/ 告警趋势 / 行业分布与处理率 / AI周报摘要 | MUST |
| 单一聚合接口轮询 | `/v2/dashboard/aggregate` 七分区一次返回（KPI/地图/告警/趋势/行业/处理率/信用榜），峰谷轮询周期可配（默认峰60s/谷300s） | MUST |
| 实时告警滚动 | 新告警高亮、无缝滚动、hover暂停、空态降级 | MUST |
| 企业信用红黄绿牌 | 规则可配置（CRUD/启停）+ 周期计算引擎（快照可追溯、封顶保护、并发锁）+ 红黄绿榜单（<60红/60-80黄/≥80绿） | MUST |
| 信用公示 | 周期计算结果发布/下架，公示页登录可查（契约P-02） | MUST |
| 信用明细抽屉 | 大屏点开企业可见扣分明细与依据（规则代码/次数/封顶说明） | MUST |
| AI 周报摘要 | SQL统计打底 + 大模型润色，密钥环境变量化，失败自动降级纯统计（数字零幻觉） | SHOULD |
| 大屏轮播 | 图表区自动轮换，间隔可配（localStorage记忆） | SHOULD |
| Redis 缓存 | 聚合结果缓存 + `X-Cache: HIT/MISS` 头，性能闭环（p50 12ms，NFR<500ms） | SHOULD |

## 二、模块责任人

| 模块 | 责任人 | 关键交付 |
|---|---|---|
| dashboard（聚合/点位/告警/缓存） | 董霞 | DashboardController/Mapper、聚合七分区、Redis缓存、峰谷轮询 |
| credit（信用评价/榜单公示/Quartz） | 李晓倩 | 信用三表、规则CRUD、CreditEngine、榜单公示接口、定时任务 |
| screen-map（大屏骨架/地图热力/轮播点聚合） | 王立洁 | ScreenContainer、四区布局、MapPanel、轮播、点聚合 |
| screen-charts（图表/轮询组件/告警滚动） | 吴一萱 | screenPoll、AlarmTicker、ChartCard、TrendChart、IndustryProcessCharts |
| stat（统计口径/AI周报/SQL优化） | 任涵艺 | StatController/Mapper、LlmClient、WeeklyReportController、口径清单、索引优化 |
| 测试（用例/断言/性能/安全矩阵） | 孙雅欣 | test_aggregate.py、system_verify.py、bench_aggregate.py、verify_kd.sql |
| 文档（五件套统筹/配置页/seed） | 沈福临 | 信用规则页、CreditPublicList、seed脚本、交付物文档体系 |

## 三、快速重建演示环境

```bash
# 1. 数据库：1.0基线 → 2.0增量 → 演示数据
mysql -uroot -p psms < db/psms.sql
mysql -uroot -p psms < db/db_v2_upgrade.sql
mysql -uroot -p psms < db/seed_alarm.sql
python scripts/seed_credit.py

# 2. 后端（JDK21）
cd psms/jeecg-boot-module-system/target
java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED -jar jeecg-boot-module-system-2.4.6.jar --server.port=18080

# 3. 前端（Node 17+ 需 legacy openssl）
cd psms-vue
cp .env.screen.example .env.development.local   # 配 API地址 与 百度AK（浏览器端类型）
yarn install && yarn serve                       # 或 NODE_OPTIONS=--openssl-legacy-provider

# 4. 自检
python scripts/test_aggregate.py http://localhost:18080/psms    # 20项断言
python scripts/system_verify.py http://localhost:18080/psms     # 39项系统验证
```

## 四、关键约定

- 接口前缀 `/v2`，错误码 25xxx 段；权限标识 `credit:*`/`dashboard:screen:view`
- 口径基线：告警数据源=base_company_type 警告表（KD-05），处理率=is_on=1（KD-08），热力权重=近30天超标频次×浓度（KD-02），详见《口径对齐清单》
- 账号权限：admin（全权）；账号 fid_region=1024 为市级（跨区可见），区域账号按 area_code 过滤
