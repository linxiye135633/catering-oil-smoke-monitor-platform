# E-部署文档 V0.1

> **视角：沈福临（文档主责）+董霞（Docker节）**。按模板04结构；核心标准：**他人按本文档从零部署成功**（W7组外同学盲跑验证后打勾）。
> 对应看板任务：#E-42（追溯标记，正式提交docx时删除本行）
> 依托仓库既有部署资产：`psms/docker-compose.yml`、`docker-compose-server.yml`、`psms-vue/Dockerfile`（1.0已含，E组零新增部署文件）。
> 本文所有命令与端口均为 2026-09-17（裸机联调）与 2026-09-20（复跑采集）实测记录，非设想值。

## 1 部署概述

目标：在课程服务器的 Docker 环境里跑通油烟监控平台 2.0（v2.0.0-release），既包含 1.0 功能回归，也包含我们组新增的监管驾驶舱模块。

拓扑一句话：浏览器 → Nginx(前端产物) → 后端 jar(/psms) → MySQL8(psms库) + Redis。我们在部署上最自豪的一点是：E组全部新增内容都寄生在1.0既有容器里（后端包/前端页面/增量SQL），**没有新增服务、没有新增中间件**——组内评审时认为这正是"在1.0基础上做2.0"的正确姿势，部署风险最小。

@@img:fig-05-topo.png|图1-1 2.0 部署拓扑（复用 1.0 docker-compose 四服务，E组增量以红色标注）

**E组增量清单（部署时需要放到位的东西）**

| 类别 | 数量 | 位置（合入仓库后） | 说明 |
|---|---|---|---|
| 后端 java | 24 文件 | `jeecg-boot-module-system/.../modules/{credit,dashboard,stat}` | credit 18 / dashboard 2 / stat 4（含 LlmClient） |
| 前端文件 | 13 文件 | `psms-vue/src/{views/screen,views/credit,components,utils,config}` | 大屏6 + 信用3 + 公共件2 + 轮询1 + 路由1 |
| 增量 SQL | 1 件 | `psms/db/db_v2_upgrade.sql` | 信用三表 + 字典 + 菜单权限 + 运行表补建 + Quartz 注册 |
| 演示 SQL | 1 件 | `sql/seed_alarm.sql` | 4家企业 + 7日超标数据 + 警告 + 开关机日志 |
| 演示脚本 | 1 件 | `scripts/seed_credit.py` | 一期信用结果（含边界值企业） |
| 运维脚本 | 4 件 | `sql/optimize_indexes.sql`、`sql/verify_kd.sql`、`scripts/{test_aggregate,system_verify,bench_aggregate}.py` | 索引/口径/断言/性能 |
| 配置样例 | 1 件 | `psms-vue/.env.example` | 百度AK + 峰谷轮询参数 |

@@img:shot-03-screen-main.png|图1-2 部署完成后的最终呈现：监管驾驶舱大屏实跑画面（2026-09-20 采集）

## 2 环境要求

| 软件 | 版本 | 说明 |
|---|---|---|
| JDK | 21 | 运行 jar 需 `--add-opens` 三参数（见 3.3） |
| MySQL | 8.x | psms 库；脚本按序：psms.sql → db_v2_upgrade.sql →（演示）seed |
| Redis | 5.0+ | 会话 + E组聚合缓存依赖；低版本服务端注意客户端握手兼容 |
| Node.js | 22/24 LTS | 仅构建前端；yarn 源 npmmirror |
| Docker | 24.x | compose 三件套：mysql / redis / backend + frontend |
| 浏览器 | Chrome 最新版 | 大屏推荐 1920×1080 全屏（F11） |

**端口约定（实测）**

| 用途 | 本地裸机联调 | Docker 部署 | 备注 |
|---|---|---|---|
| 后端服务 | 18080 | 8080 | context-path 均为 `/psms`；本地改 18080 是避开他组占用 |
| 前端 dev server | 3000 | — | 仅开发期；生产走 Nginx :80 |
| MySQL | 3306 | 3306 | 数据源 root/123456@psms（课程环境口令） |
| Redis | 6379 | 6379 | 聚合缓存库 |

**关键配置项（sys_param 表，运行期可改不需重启）**

| param_code | 用途 | 默认值 | 谁用 |
|---|---|---|---|
| psms.llm.apiKey | 大模型密钥 | 空（空则周报走降级模式） | AI 周报 |
| psms.llm.baseUrl | 大模型地址 | 空 | AI 周报 |
| psms.credit.selfcheckUrl | C组自查统计地址 | C组服务地址 | 信用引擎加分项 |
| psms.riskTop.url | A组风险榜地址 | A组服务地址 | 大屏角标透传 |
| psms.screen.cacheTtl | 聚合缓存秒数 | 30 | 后端聚合 |
| psms.credit.threshold | 等级阈值 | 60/80 | 信用引擎与前端牌色 |

> **本地联调实测补充（2026-09-17 裸机部署验证）**：①JDK21 运行须带 `--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED`；②Node 17+ 起 dev server 须 `NODE_OPTIONS=--openssl-legacy-provider`（yarn build 脚本已内置，裸 npx 启动需手动带）；③前端 `.env.development.local` 指向后端实际地址（该文件被 .gitignore 排除，不入库）；④完整速查见 `E组-代码/README.md` §4b。

## 3 部署步骤

### 3.1 代码获取

```bash
git clone <课程仓库地址> && cd <项目目录> && git checkout v2.0.0-release
# 分支说明：main=1.0基线；dev=集成线（E组合入点）；feature/E-*=成员分支
# 若从增量包合入：E组文件落位见本文 §1 增量清单表
```

### 3.2 数据库初始化（按序执行，顺序不可颠倒）

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS psms DEFAULT CHARSET utf8mb4"
mysql -uroot -p psms < psms/db/psms.sql                 # 1.0基础库（102表）
mysql -uroot -p psms < psms/db/db_v2_upgrade.sql        # 2.0增量（信用三表+字典+菜单+Quartz注册）
# 演示环境（可选，录屏/答辩前执行）：
mysql -uroot -p psms < sql/seed_alarm.sql
python scripts/seed_credit.py                           # 需 pymysql；生成含边界值的信用结果
```

**每步执行后的验证（实测）**

| 步 | 验证命令 | 期望 |
|---|---|---|
| 1 | `select count(*) from information_schema.tables where table_schema='psms'` | 102（1.0） |
| 2 | `show tables like 'bu_credit%'` | bu_credit_rule / record / detail 三表 |
| 2 | `select name,url from sys_permission where url like '%screen%' or url like '%credit%'` | 监管驾驶舱 + 大屏主页面 + 信用管理 + 信用规则配置 + 信用公示（5行） |
| 3 | `select count(*) from bu_credit_rule` | 3（R-ALARM-COUNT / R-FAN-OFF / R_SELFCHECK） |
| 4 | `select company_name,total_score,level from bu_credit_record` | 4 行：24 红 / 34 红 / 72 黄 / 100 绿 |

### 3.3 后端部署

配置修改点（`application-dev.yml`）：MySQL master.password、Redis host/port。大模型密钥 **不进配置文件**——`UPDATE sys_param SET param_value='<教师发放>' WHERE param_code='psms.llm.apiKey'` 或环境变量 `LLM_API_KEY`。

```bash
mvn install -DskipTests                       # 聚合工程构建（首次3~10min，配阿里云镜像）
# jar启动（JDK21必须带三参数；IDEA则填入VM options）
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -jar jeecg-boot-module-system/target/jeecg-boot-module-system-2.4.6.jar \
     --server.port=18080
```

启动成功标志：控制台输出 `Application Jeecg-Boot is running`；打开 `http://localhost:18080/psms/doc.html` 能看到 Swagger 且可搜到 `/v2/credit`、`/v2/dashboard`、`/v2/stat` 三组共 22 个 E组接口。

@@img:shot-08-swagger-v2.png|图3-1 后端启动后 /v2 接口文档实拍（22个接口在线可查，含错误码与参数说明）

**产物核对**：`jeecg-boot-module-system-2.4.6.jar` 约 191MB（含依赖），构建时间应与最后一次代码变更一致。

### 3.4 前端部署

```bash
cd psms-vue
# 1) 环境变量：把 .env.example 中 E组新增项按需并入本地配置
#    VUE_APP_API_BASE_URL=http://localhost:18080/psms
#    VUE_APP_BMAP_AK=<课程发放的百度地图AK>
#    VUE_APP_POLL_PEAK=60000  VUE_APP_POLL_VALLEY=300000
cp .env.example .env.development.local        # 该文件不入库
# 2) 依赖与构建
yarn install && yarn build                    # build脚本已内置 --openssl-legacy-provider
# 3) 发布：产物 dist/ 拷到 Nginx 站点目录；.env.production 的 API 地址指向服务器
```

**本地开发模式（联调用）**：`NODE_OPTIONS=--openssl-legacy-provider yarn serve` → 浏览器访问 `http://localhost:3000`。

### 3.5 小程序端（C组适用）

**本方向不适用**：小程序由企业自查方向（C组）交付并部署，其构建与发布步骤见 C 组《部署文档》。E组与小程序侧仅有一项部署交集——信用引擎读取 C组自查统计接口（`psms.credit.selfcheckUrl`），该地址未配置时信用计算自动跳过加分项、不报错。

### 3.6 Docker容器化部署（第7周演练）

```bash
# 仓库内既有：psms/docker-compose.yml（mysql/redis/backend/frontend四服务）
cd psms && docker-compose up -d --build
# 增量要点：backend镜像重建前，先在挂载的初始化脚本目录补入 db_v2_upgrade.sql
#          （compose 中 mysql 的 depends_on 会驱动首次自动执行）
# E组验收项：大屏全屏路由验证——浏览器 F11 访问 /screen/main 全屏无滚动条
docker-compose ps          # 四服务均 Up
docker logs -f backend | grep "Jeecg-Boot is running"
```

## 4 验证清单

| 验证项 | 操作 | 预期结果 | 结果（09-17/09-20 实测） |
|---|---|---|---|
| 后台登录 | admin 登录 | 进入系统，菜单出现「监管驾驶舱」「信用管理」 | 通过（图4-1） |
| Swagger三域 | 搜 /v2 前缀 | credit/dashboard/stat 三组 22 接口可见 | 通过（图3-1） |
| 大屏四区 | 打开监管驾驶舱 | 深色 1080p 四区渲染；热力/告警滚动/信用榜有数（seed后） | 通过（图1-2） |
| 聚合缓存 | 刷新两次看响应头 | 第二次 `X-Cache: HIT` | 通过（MISS 107ms → HIT 40ms） |
| 规则全流程 | 配置页新增→启停→删除 | 列表实时刷新；重复代码报 25001 提示 | 通过（图4-2） |
| 信用链路 | 手动触发计算→查看榜单→发布公示 | 边界企业 59.99 红 / 60 黄 / 79.99 黄 / 80 绿；公示页可见 | 通过（榜单 24/34 红·72 黄·100 绿，公示4条） |
| 并发保护 | 同周期重复触发计算 | 返回 code=25004，不产生脏数据 | 通过（system_verify S3） |
| 越权拦截 | 无 token / 低权账号访问 /v2 | 401 / 403，不返回业务数据 | 通过（S1 六项） |
| 1.0回归 | 预警列表 / 在线监控 | 数据正常无报错 | 通过（Console 0 error，1.0 基线 4 error） |
| Quartz任务 | 定时任务菜单启动 CreditCalcJob | 状态运行中；下月1日自动计算 | 通过（注册成功，触发待长周期观察） |

@@img:shot-02-home-menu.png|图4-1 验证项「后台登录」实拍：左侧菜单出现监管驾驶舱与信用管理

@@img:shot-04-credit-rule.png|图4-2 验证项「规则全流程」实拍：三条内置规则与新增/编辑/停用/删除入口

## 5 回滚方法

```bash
# 0) 部署前必备份
mysqldump -uroot -p psms > psms_backup_$(date +%F).sql
# 1) 代码回退
git checkout <上一可用tag>            # 或 git checkout main 回到1.0基线
mvn install -DskipTests && yarn build
# 2) 库还原（2.0增量表全部回退）
mysql -uroot -p psms < psms_backup_$(date +%F).sql
# 3) 缓存清理（防旧结构缓存残留）
redis-cli FLUSHDB
# 4) 重启后端与前端，按第4章验证清单复测并记录
```

**回滚要点**：`db_v2_upgrade.sql` 为纯增量（新增表 + 新增菜单 + 新增 sys_param + Quartz 注册），不改 1.0 任何表结构与数据，因此回滚只需还原备份库；若仅需回退 E组功能而不还原数据，可在菜单管理中停用 E组两项菜单、并从 Nginx 移除 /screen 路由即可。

## 6 常见部署问题FAQ

| # | 现象 | 原因 | 解决 |
|---|---|---|---|
| 1 | 后端启动 Redis 连接失败 | Redis 未启动 | 启动 Redis 服务 / compose 内 redis 容器 |
| 2 | 后端启动报反射/模块访问错误 | JDK21 未带 `--add-opens` 三参数 | 按 3.3 命令补齐；IDEA 填 VM options |
| 3 | 前端 `yarn serve` 报 `digital envelope routines::unsupported` | Node 17+ 与 webpack4 md4 不兼容 | 加 `NODE_OPTIONS=--openssl-legacy-provider` |
| 4 | 大屏地图空白，其余区正常 | `VUE_APP_BMAP_AK` 未配或 AK 失效 | .env 配课程 AK；内网改本地部署 js；组件本身会自动降级为占位底图 |
| 5 | 大屏无告警/榜单数据 | 未跑 seed，或警告表 `base_company_type` 缺数据（告警数据源） | 按 3.2 顺序补执行 seed_alarm.sql |
| 6 | 信用管理菜单点开白屏/报 RangeError 调用栈溢出 | 菜单 `always_show=1` 触发 1.0 路由生成的 redirect 自引用死循环 | 将该目录菜单 `always_show` 改 0（与 1.0 目录形态一致），已在 db_v2_upgrade.sql 修正 |
| 7 | 公示接口返回 records 为若干空对象（total 正常） | `Page<Map>` 在 Jeecg 消息转换器下序列化为空对象 | Controller 展开为普通 Map（records/total/current/size），已修 |
| 8 | 趋势/行业图表不渲染，控制台报 `reading 'init'` | ECharts5 无默认导出，误写 `import echarts` | 改 `import * as echarts` |
| 9 | 页面 404 但菜单存在 | 菜单组件路径与实际文件大小写不一致 | 核对 `screen/ScreenMain` 与菜单 SQL 的 component 字段 |
| 10 | 定时任务不跑 | `sys_quartz_job` 注册后默认停止态(-1) | 定时任务菜单手动启动 CreditCalcJob |
| 11 | 周报显示「降级模式（纯统计）」 | 大模型密钥/地址未配 | 配 sys_param 或环境变量；功能不中断，属预期降级 |
| 12 | 大屏数字与明细报表不一致 | 不应出现（同源同口径） | 跑 `sql/verify_kd.sql` 复算并登记缺陷——这是答辩生命线项 |
| 13 | 端口 8080 被占用 | 他组服务或本机其他程序占用 | 本地按 3.3 用 `--server.port=18080`，前端 API 地址同步改 |

## 章节贡献署名表（个人交付物归档依据）

| 章节 | 执笔人 | 占比 |
|---|---|---|
| 1/2/3.1-3.5/4/6 | 沈福临 | 60% |
| 3.3后端/3.6 Docker/5回滚 | 董霞 | 30% |
| 4验证清单信用链路项 | 李晓倩 | 10% |

> 学号以学籍表为准，归档时由组长统一补填后与 Git 提交记录交叉核验。
