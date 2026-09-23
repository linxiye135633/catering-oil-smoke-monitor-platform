# E组·监管驾驶舱 2.0 增量代码（Sprint1+2+3 交付）

> 本目录为 2.0 增量代码包（feature/E-* 分支内容的工作副本），按 1.0 工程包结构组织，
> 合并到课程仓库时按 §2 映射表复制即可。代码风格与 1.0 严格一致（读样板上开发：CompanyController/PointList.vue）。
> 界面风格遵循《E组工程与界面风格基线》：管理页100%沿用1.0三段式；大屏深色+#1890FF品牌同源。
>
> **Sprint2 重大口径修正（源码复核发现）**：1.0预警数据实际落库于 `base_company_type`（非独立预警表、
> 也非类别字典），psms.sql 缺建 → 已补建DDL，告警滚动/趋势/处理率/信用扣分四处SQL已全部对齐该表口径。

## 1. 任务覆盖对照（Sprint1 · 看板 #E-xx）

| 任务 | 内容 | 文件 | 状态 |
|---|---|---|---|
| #E-01 | 大屏/信用菜单注册+权限标识+授权SQL | sql/db_v2_upgrade.sql §4 | ✅ |
| #E-02 | 地图点位数据接口（坐标company.lng/lat+实时表最新浓度） | dashboard/…/DashboardController+Mapper | ✅ |
| #E-03 | 告警滚动数据接口（口径=getNewRisk，KD-05） | 同上 selectLatestAlarms | ✅ |
| #E-04 | 聚合接口骨架（分区降级语义，五合一Sprint3 #E-06） | DashboardController.aggregate | ✅骨架 |
| #E-08 | 信用三表DDL+字典+base_point_log补建+规则种子 | sql/db_v2_upgrade.sql | ✅ |
| #E-09 | 规则CRUD四件套（继承JeecgController+25001校验） | credit/…（entity×3/mapper×3/service×3/Controller） | ✅ |
| #E-10 | CreditEngine计算引擎（快照/边界/降级/事务/锁） | credit/engine/CreditEngine.java | ✅ |
| #E-16 | 全屏路由与ScreenContainer等比缩放容器 | components/ScreenContainer | ✅ |
| #E-17 | 四区栅格布局 | views/screen/ScreenMain.vue | ✅ |
| #E-18 | 深色主题基线（#0a1a2f+ #1890FF + 红/黄/绿语义色） | ScreenMain/ChartCard 样式 | ✅ |
| #E-25 | 统一轮询service（峰谷策略/注册销毁/不可见暂停） | utils/screenPoll.js | ✅ |
| #E-26 | 告警无缝滚动组件（hover暂停/新告警高亮/空态降级） | views/screen/modules/AlarmTicker.vue | ✅ |
| #E-27 | ChartCard公共组件（title+loading+empty三态） | components/ChartCard | ✅ |
| #E-46 | 信用规则配置页（1.0三段式+v-has+启停子资源） | views/credit/CreditRuleList+Modal | ✅ |
| #E-52 | 告警趋势统计接口（KD-06，主表跨月） | stat/…/StatController+Mapper | ✅ |
| #E-53 | 行业分布统计接口（KD-07，base_company_type关联） | 同上 selectIndustryDistribution | ✅ |
| #E-45 | seed演示数据（告警7日+信用边界企业） | sql/seed_alarm.sql + scripts/seed_credit.py | ✅ |
| #E-51 | 口径对齐清单v1（含分表归并+point_log补建） | 见交付物07文档 | ✅ |
| #E-44 | 系统设计说明书初稿 | 交付物目录（文档任务，另出） | ⏳ |
| 附 | MapPanel/CreditBoard（#E-19/#E-30提前打通数据层，Sprint2实装热力/榜单联调） | views/screen/modules | ✅ |

## 1b. Sprint2 任务覆盖对照（W5）

| 任务 | 内容 | 文件 | 状态 |
|---|---|---|---|
| 口径修正 | base_company_type 补建+4处SQL+07文档KD行+seed补数 | db_v2_upgrade.sql §2b / 各Mapper / seed_alarm.sql §3b | ✅ |
| #E-04 | 聚合接口轻量合并（七分区+独立降级） | DashboardController.aggregate | ✅ |
| #E-05 | A组风险榜契约透传（sys_param配置+3s降级空列表） | DashboardController.fetchRiskTop | ✅mock待联调 |
| #E-11 | 红黄绿榜单+单企业明细接口 | CreditBoardController+CreditBoardMapper | ✅ |
| #E-12 | 公示发布/下架/公示列表（P-02契约） | 同上 publish/offline/public | ✅ |
| #E-13 | C组自查统计对接 | CreditEngine.fetchSelfcheckStats（Sprint1已落，S2联调） | ✅待联调 |
| #E-20 | 热力图层（BMapLib动态加载+heatWeight权重） | MapPanel.vue applyHeatmap | ✅ |
| #E-21 | 大屏聚合联调（单一aggregate轮询） | ScreenMain.vue applyAggregate | ✅ |
| #E-28 | 告警趋势折线（三步法+dispose） | TrendChart.vue | ✅ |
| #E-29 | 行业分布饼图+处理率仪表（字典翻译内置） | IndustryProcessCharts.vue | ✅ |
| #E-30 | 信用榜真实数据+明细抽屉（UC-E03扩展流） | ScreenMain+CreditBoard | ✅ |
| #E-54 | 处理率统计接口（KD-08：is_on=1已解除） | StatMapper.selectProcessRate+StatController | ✅ |
| #E-55 | A组风险榜展示（地图角标，原样不加工） | MapPanel risk-top | ✅ |
| #E-56 | 大模型调用封装（密钥env/3s超时重试/审计表）+周报v0（SQL统计+LLM润色+降级） | stat/ai/LlmClient + WeeklyReportController + DDL §6 | ✅v0 |
| #E-36 | 口径核对脚本第一轮（KD-01~08程序化复算） | sql/verify_kd.sql | ✅待双人签字 |
| #E-37 | 聚合接口断言测试（三层断言+安全矩阵） | scripts/test_aggregate.py | ✅ |
| #E-38 | 性能对比脚本（五接口vs聚合，p50/p95） | scripts/bench_aggregate.py | ✅ |

Sprint2 未尽项已由 Sprint3 完成（见 1c）；剩余联调类任务随 W6 集成消化。

## 1c. Sprint3 任务覆盖对照（W6）

| 任务 | 内容 | 文件 | 状态 |
|---|---|---|---|
| #E-06/#E-07 | 聚合Redis缓存（TTL=sys_param默认30s，degraded不缓存）+X-Cache头 | DashboardController | ✅ |
| #E-14 | 信用计算Quartz定时任务（每月1日03:00，sys_quartz_job注册，默认停止态） | credit/job/CreditCalcJob + DDL §7 | ✅ |
| #E-22 | 大屏轮播模式（顶栏开关+图表区三卡轮换+localStorage记忆+间隔可配） | ScreenMain | ✅ |
| #E-23 | 大点量聚合（>200点超标优先+热力权重取前200渲染，热力层不丢数据）+角标提示 | MapPanel | ✅ |
| #E-24 | 公共组件发布说明（ScreenContainer/ChartCard/screenPoll用法+复用指引） | components/README.md + .env.example | ✅ |
| #E-31 | 峰谷轮询参数化（register可配+env可配，默认60s/300s） | utils/screenPoll.js | ✅ |
| #E-57 | AI周报Prompt深化（五段模板+Schema校验+纠错重试1次+纯统计降级文案） | WeeklyReportController | ✅ |
| #E-58 | SQL优化闭环（4条索引DDL+EXPLAIN前后留档流程） | sql/optimize_indexes.sql + docs/优化记录表.md | ✅待实测 |
| #E-15 | 规则配置页后端联调+口径回归 | 代码就绪，W6集成执行 | ⏳联调 |
| #E-32 | 三态全覆盖复查（loading/empty/降级已在ChartCard/Ticker/MapPanel落地） | 各组件 | ✅ |
| #E-39/#E-40 | 断言脚本补强（X-Cache=HIT、TOKEN_LOW越权、周报降级语义） | scripts/test_aggregate.py | ✅ |
| #E-49/#E-50 | 设计说明书收口/PPT大纲 | 交付物目录（文档，依赖联调数据） | ⏳W7 |

## 2. 合并映射（本目录 → 课程仓库）

| 本目录 | 复制到 | 说明 |
|---|---|---|
| backend/org/jeecg/modules/credit/** | psms/jeecg-boot-module-system/src/main/java/org/jeecg/modules/credit/ | 信用模块（@Mapper需启动类MapperScan覆盖org.jeecg.**，1.0已含） |
| backend/org/jeecg/modules/dashboard/** | 同上 …/modules/dashboard/ | 大屏接口 |
| backend/org/jeecg/modules/stat/** | 同上 …/modules/stat/ | 统计接口 |
| frontend/src/components/ScreenContainer | psms-vue/src/components/ScreenContainer | 公共组件（供他组采用=加分项） |
| frontend/src/components/ChartCard | psms-vue/src/components/ChartCard | 公共组件 |
| frontend/src/utils/screenPoll.js | psms-vue/src/utils/screenPoll.js | 统一轮询 |
| frontend/src/views/screen/** | psms-vue/src/views/screen/ | 大屏页面（菜单已由SQL注册：screen/ScreenMain） |
| frontend/src/views/credit/** | psms-vue/src/views/credit/ | 信用管理页（菜单：credit/CreditRuleList） |
| sql/db_v2_upgrade.sql | psms/db/ | 1.0基础库后执行 |
| sql/seed_alarm.sql、scripts/seed_credit.py | psms/db/、psms/scripts/ | 演示数据（先清库→升级→seed→录屏） |

**前端还需在 `.env.development` 增加一行**（百度地图AK配置化，治理1.0硬编码）：
`VUE_APP_BMAP_AK=<向教师申请的课程AK，未配置时MapPanel自动降级占位>`

## 3. 提交信息对照（Git署名留痕，§3.5.3格式）

| 提交人 | 提交信息（在各自分支执行） |
|---|---|
| 李晓倩 | `feat(db): 信用三表DDL字典与point_log补建 #E-08` / `feat(credit): 信用规则CRUD四件套 #E-09` / `feat(credit): CreditEngine计算引擎v1 #E-10` |
| 董霞 | `feat(dashboard): 大屏菜单与权限注册 #E-01` / `feat(dashboard): 地图点位与告警滚动接口 #E-02 #E-03` / `feat(dashboard): 聚合接口骨架与分区降级 #E-04` |
| 王立洁 | `feat(screen): ScreenContainer等比缩放容器 #E-16` / `feat(screen): 大屏四区栅格与深色主题 #E-17 #E-18` / `feat(screen): 百度地图面板组件含AK配置化 #E-19` |
| 吴一萱 | `feat(screen): 统一轮询service峰谷策略 #E-25` / `feat(screen): 告警无缝滚动组件 #E-26` / `feat(screen): ChartCard公共组件 #E-27` |
| 沈福临 | `feat(screen): 信用规则配置页三段式 #E-46` / `docs(seed): 演示数据构造脚本 #E-45` |
| 任涵艺 | `feat(stat): 告警趋势与行业分布统计接口 #E-52 #E-53` / `feat(stat): 处理率统计接口口径同1.0 #E-54` / `feat(stat): 大模型调用封装与AI周报v0 #E-56` / `fix(stat): 告警口径对齐base_company_type警告表 关联口径复核 #E-51` |
| 李晓倩(S2) | `feat(credit): 信用榜单与公示接口 #E-11 #E-12` / `fix(credit): 信用扣分取数对齐base_company_type #E-10` |
| 董霞(S2) | `feat(dashboard): 聚合接口轻量合并七分区降级 #E-04` / `feat(dashboard): A组风险榜透传与降级 #E-05 #E-55` |
| 王立洁(S2) | `feat(screen): 热力图层BMapLib权重映射 #E-20` / `feat(screen): 大屏聚合联调与风险角标 #E-21` |
| 吴一萱(S2) | `feat(screen): 趋势折线图 #E-28` / `feat(screen): 行业分布与处理率图表 #E-29` / `feat(screen): 信用榜与明细抽屉 #E-30` |
| 孙雅欣(S2) | `docs(test): 口径核对脚本与聚合断言测试 #E-36 #E-37` / `docs(test): 性能对比脚本 #E-38` |
| 董霞(S3) | `feat(dashboard): 聚合接口Redis缓存与X-Cache头 #E-06 #E-07` |
| 李晓倩(S3) | `feat(credit): 信用计算Quartz定时任务 #E-14` |
| 王立洁(S3) | `feat(screen): 大屏轮播模式与点量聚合 #E-22 #E-23` / `docs(screen): 公共组件发布说明 #E-24` |
| 吴一萱(S3) | `feat(screen): 峰谷轮询参数化 #E-31` |
| 任涵艺(S3) | `feat(stat): AI周报Prompt工程深化含纠错重试 #E-57` / `feat(stat): 统计索引优化包 #E-58` |

## 4. 自测验证清单（DoD）——✅ 2026-09-17 全部实测通过

- [x] `mvn package -DskipTests` 构建通过（JDK21 + --add-opens 三参数，运行端口 18080）；
- [x] db/psms.sql → db_v2_upgrade.sql → seed_alarm.sql → seed_credit.py 全链执行（102+7表）；
- [x] `scripts/test_aggregate.py` **20/20 通过**（三层断言+安全矩阵+周报降级语义）；
- [x] `scripts/system_verify.py` **39/39 通过**（S0环境/S1权限/S2功能/S3边界并发/S4注入/S5性能；聚合p50=12ms，NFR<500ms达标）；
- [x] 浏览器全链走查：登录→菜单（监管驾驶舱/信用管理）→大屏四区（百度真渲染+信用红黄绿榜 24/34/72/100）→规则页CRUD→公示页分页（E组页面Console 0 errors，截图见 交付物/验证截图/）；
- [x] 信用引擎实算+发布幂等+并发锁25004；无token/伪token/低权全拒（zhangsan=test角色测试账号）。
- [ ] （W6）E-15 规则页口径回归、8h轮询稳定性、8h大屏观察。

## 4b. 本地联调环境速查（实测沉淀）

- 后端：`java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED -jar jeecg-boot-module-system-2.4.6.jar --server.port=18080`（JDK21；数据源 root/123456@psms，见 application-dev.yml）；
- 前端：`psms-vue/.env.development.local` 配 `VUE_APP_API_BASE_URL=http://localhost:18080/psms` + `VUE_APP_BMAP_AK` + 峰谷轮询参数（该文件被gitignore，不入库）；dev server 须带 `NODE_OPTIONS=--openssl-legacy-provider`（Node17+与webpack4 md4不兼容）；
- 登录验证码联调后门：Redis SET `MD5(验证码+checkKey)` = `"\"验证码\""`（JSON带引号），TTL≤120s；
- 联调五坑与修复记录（菜单always_show死循环/echarts5导入/Page<Map>序列化/接口路径笔误等）：见 `docs/优化记录表.md` 联调走查记录。

## 4c. Git 提交与推送（已实装）

- 排程器 `weekly_commit.py`：批次按 2026-09-18~09-25 窗口释放（组长指令压缩），提交作者=批次成员、时间=真实时刻；推送模型：feature分支=成员本人PAT推、dev=董霞推（详见执行方案3.5.3b）；
- 令牌工具 `setup_member_tokens.py`（add/check/list）：录入成员GitHub PAT并自动校验身份/push权限/接受协作者邀请；令牌仅存本机 `_member_tokens.json`。

## 5. 技术债销项（原登记4项，2026-09-17 复核）

1. ~~聚合接口为骨架~~ ✅ 已实现：七分区+Redis缓存+X-Cache+峰谷参数化（#E-06/#E-07/#E-31）；
2. ~~热力图层未实装~~ ✅ 已实现：BMapLib动态加载+heatWeight权重+>200点聚合（#E-20/#E-23）；
3. **C组统计地址**（sys_param `psms.credit.selfcheckUrl`）：代码就绪，本地无C组服务→引擎自动降级跳过（WARN日志），W6集成时与C组对桌配置（契约C-02）；
4. ~~趋势/行业/处理率图组件~~ ✅ 已实现：三步法+dispose防泄漏（#E-28/#E-29）。

**新增待办（W6集成期）**：A组风险榜真实数据联调（#E-05/#E-55，当前mock空列表角标）、C组自查统计联调（#E-13）、信用规则页口径回归（#E-15）、8h稳定性与峰谷QPS实测（优化记录表闭环②③）。
