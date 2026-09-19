-- =====================================================================
-- E组·监管驾驶舱 2.0 增量升级脚本  db/v2_upgrade.sql  (#E-08)
-- 执行顺序：1.0基础库 psms.sql 之后执行本脚本
-- 规范：指导书任务3阶段2（bu_前缀/id varchar(36)/审计字段/del_flag/字典挂载）
-- 差异说明：1.0既有表用 creator/updater、id varchar(64)；2.0新表按指导书规范
-- 责任人：李晓倩 | 交叉评审：任涵艺、董霞
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 信用评价三表（详见《E-信用模块设计包》）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bu_credit_rule` (
  `id`          varchar(36)  NOT NULL COMMENT '主键（雪花ID）',
  `rule_code`   varchar(60)  NOT NULL COMMENT '指标代码（全局唯一，如 R-ALARM-COUNT）',
  `rule_name`   varchar(300) NOT NULL COMMENT '指标名称（如 告警超标次数扣分）',
  `score_type`  varchar(10)  NOT NULL COMMENT '分值类型（字典 credit_score_type：1加分 2减分）',
  `score_value` decimal(6,2) NOT NULL COMMENT '单次分值（加分为正、减分为负值存储，便于直接求和）',
  `weight`      decimal(5,2) NOT NULL DEFAULT 1.00 COMMENT '权重（总分=Σ(分值×次数×权重)）',
  `cycle_type`  varchar(10)  NOT NULL COMMENT '评价周期类型（字典 credit_cycle：1月度 2季度 3年度）',
  `data_source` varchar(10)  NOT NULL DEFAULT '1' COMMENT '取数来源（字典 credit_source：1平台统计 2自查整改[C组]）',
  `max_times`   int          NULL COMMENT '单周期计分次数上限（防刷分，NULL不限）',
  `enabled`     varchar(10)  NOT NULL DEFAULT '1' COMMENT '是否启用（字典 enable_status：1启用 0停用）',
  `remark`      varchar(500) NULL COMMENT '规则说明',
  `create_by`   varchar(50)  NULL COMMENT '创建人',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   varchar(50)  NULL COMMENT '更新人',
  `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag`    tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除（0正常 1删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`),
  KEY `idx_cycle_enabled` (`cycle_type`,`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用评价指标表（E组2.0新增）';

CREATE TABLE IF NOT EXISTS `bu_credit_record` (
  `id`             varchar(36)  NOT NULL COMMENT '主键',
  `company_id`     varchar(64)  NOT NULL COMMENT '企业ID（逻辑外键→base_company.id，1.0主键64位）',
  `company_name`   varchar(300) NOT NULL COMMENT '企业名称（冗余，避免联表）',
  `cycle`          varchar(20)  NOT NULL COMMENT '评价周期（如 2026-09）',
  `total_score`    decimal(6,2) NOT NULL DEFAULT 100.00 COMMENT '周期总分（基准100，加减分后）',
  `level`          varchar(10)  NOT NULL COMMENT '信用等级（字典 credit_level：1红 2黄 3绿）',
  `snapshot`       json         NULL COMMENT '当期生效规则集快照（规则变更不回溯）',
  `publish_status` varchar(10)  NOT NULL DEFAULT '0' COMMENT '公示状态（字典 credit_publish：0草稿 1已发布 2已下架）',
  `calc_time`      datetime     NOT NULL COMMENT '计算完成时间',
  `create_by`      varchar(50)  NULL COMMENT '创建人（定时任务为system）',
  `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`      varchar(50)  NULL COMMENT '更新人',
  `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag`       tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_company_cycle` (`company_id`,`cycle`),
  KEY `idx_cycle_level` (`cycle`,`level`) COMMENT '榜单高频查询',
  KEY `idx_company` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信用评价结果表（E组2.0新增）';

CREATE TABLE IF NOT EXISTS `bu_credit_detail` (
  `id`           varchar(36)  NOT NULL COMMENT '主键',
  `record_id`    varchar(36)  NOT NULL COMMENT '评价结果ID（逻辑外键→bu_credit_record.id）',
  `rule_id`      varchar(36)  NOT NULL COMMENT '规则ID（逻辑外键→bu_credit_rule.id）',
  `rule_code`    varchar(60)  NOT NULL COMMENT '规则代码（冗余，快照语义）',
  `change_score` decimal(6,2) NOT NULL COMMENT '本笔变动分（负=扣分）',
  `times`        int          NOT NULL DEFAULT 1 COMMENT '触发次数',
  `source_no`    varchar(100) NULL COMMENT '触发依据单号（告警批次/整改单号，答辩举证用）',
  `source_type`  varchar(10)  NULL COMMENT '依据类型（字典 credit_source）',
  `remark`       varchar(500) NULL COMMENT '备注（含降级说明：如C组数据源缺失跳过）',
  `create_by`    varchar(50)  NULL COMMENT '创建人',
  `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`    varchar(50)  NULL COMMENT '更新人',
  `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag`     tinyint(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_record` (`record_id`),
  KEY `idx_rule` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用变动明细表（E组2.0新增）';

-- ---------------------------------------------------------------------
-- 2. 补建 base_point_log（1.0风险计算依赖但psms.sql缺失，任涵艺走读发现）
--    结构按 SupervisionMapper.xml 实际引用逆向
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `base_point_log` (
  `id`          varchar(64) NOT NULL COMMENT '主键',
  `point_mac`   varchar(64) NOT NULL COMMENT '测点MAC（关联base_point.point_mac）',
  `time`        datetime    NOT NULL COMMENT '开关机时间',
  `is_on`       tinyint(1)  NOT NULL COMMENT '0关机 1开机（风险SQL按is_on=0统计关机次数）',
  `create_time` datetime    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_mac_time` (`point_mac`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测点开关机日志（1.0运行表，psms.sql缺失此处补建）';

-- ---------------------------------------------------------------------
-- 2b. 补建 base_point_log 姊妹表：base_company_type（警告/风险记录表）
--     Sprint2 源码复核发现：该表为1.0预警数据源（getNewRisk/getRiskCount/getClearRisk
--     均查此表：alarm_type告警类型、is_on 0警告中/1已解除、fid_region区域），
--     psms.sql 同样缺失，按 SupervisionMapper 引用字段逆向补建。
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `base_company_type` (
  `id`          varchar(64) NOT NULL COMMENT '主键',
  `name`        varchar(300) NOT NULL COMMENT '企业名称（1.0按名称关联）',
  `mac`         varchar(64)  NULL COMMENT '测点MAC',
  `alarm_type`  int          NOT NULL DEFAULT 0 COMMENT '告警类型（>0为有效告警，风险SQL按alarm_type>0计数）',
  `is_on`       tinyint(1)   NOT NULL DEFAULT 0 COMMENT '0警告中 1已解除（getClearRisk置1并写updata_time）',
  `fid_region`  int          NOT NULL COMMENT '行政区域（数据权限过滤）',
  `create_time` datetime    NOT NULL COMMENT '告警产生时间',
  `updata_time` datetime     NULL COMMENT '解除时间（1.0原拼写，保持兼容不改名）',
  PRIMARY KEY (`id`),
  KEY `idx_region_time` (`fid_region`,`create_time`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='警告企业记录表（1.0运行表，psms.sql缺失此处补建）';

-- ---------------------------------------------------------------------
-- 3. 字典数据（sys_dict/sys_dict_item，Jeecg规范）
-- ---------------------------------------------------------------------
INSERT IGNORE INTO sys_dict(id, dict_name, dict_code, description, del_flag, create_time)
VALUES (REPLACE(UUID(),'-',''), '信用分值类型', 'credit_score_type', 'E组2.0信用模块', 0, NOW()),
       (REPLACE(UUID(),'-',''), '信用等级', 'credit_level', '红黄绿', 0, NOW()),
       (REPLACE(UUID(),'-',''), '信用公示状态', 'credit_publish', '0草稿1发布2下架', 0, NOW()),
       (REPLACE(UUID(),'-',''), '信用评价周期', 'credit_cycle', '月/季/年', 0, NOW()),
       (REPLACE(UUID(),'-',''), '信用取数来源', 'credit_source', '平台统计/自查整改C组', 0, NOW());

INSERT IGNORE INTO sys_dict_item(id, dict_id, item_text, item_value, sort_order, status, create_time)
SELECT REPLACE(UUID(),'-',''), d.id, t.text, t.val, t.sort, 1, NOW()
FROM sys_dict d JOIN (
  SELECT 'credit_score_type' dc, '加分' text, '1' val, 1 sort UNION ALL
  SELECT 'credit_score_type', '减分', '2', 2 UNION ALL
  SELECT 'credit_level', '红牌', '1', 1 UNION ALL
  SELECT 'credit_level', '黄牌', '2', 2 UNION ALL
  SELECT 'credit_level', '绿牌', '3', 3 UNION ALL
  SELECT 'credit_publish', '草稿', '0', 1 UNION ALL
  SELECT 'credit_publish', '已发布', '1', 2 UNION ALL
  SELECT 'credit_publish', '已下架', '2', 3 UNION ALL
  SELECT 'credit_cycle', '月度', '1', 1 UNION ALL
  SELECT 'credit_cycle', '季度', '2', 2 UNION ALL
  SELECT 'credit_cycle', '年度', '3', 3 UNION ALL
  SELECT 'credit_source', '平台统计', '1', 1 UNION ALL
  SELECT 'credit_source', '自查整改(C组)', '2', 2
) t ON d.dict_code = t.dc
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_item i WHERE i.dict_id = d.id AND i.item_value = t.val);

-- ---------------------------------------------------------------------
-- 4. 菜单与权限注册（#E-01：组件路径/权限标识/角色授权，替代手工菜单配置）
--    顶级：监管驾驶舱（大屏，跳新窗口）| 管理：信用管理（规则/公示）
-- ---------------------------------------------------------------------
-- 4.1 大屏菜单（/screen/main 已在前端 constantRouterMap 注册为全屏路由）
SET @menu_screen = REPLACE(UUID(),'-','');
INSERT IGNORE INTO sys_permission(id, parent_id, name, url, component, component_name, menu_type, sort_no, icon, status, del_flag, create_by, create_time, always_show)
VALUES (@menu_screen, NULL, '监管驾驶舱', '/screen/main', 'layouts/RouteView', 'ScreenMain', 0, 5, 'dashboard', '1', 0, 'admin', NOW(), 0);
INSERT IGNORE INTO sys_permission(id, parent_id, name, url, component, menu_type, sort_no, icon, status, del_flag, create_by, create_time)
VALUES (REPLACE(UUID(),'-',''), @menu_screen, '大屏主页面', '/screen/main', 'screen/ScreenMain', 1, 1, 'fund', '1', 0, 'admin', NOW());
INSERT IGNORE INTO sys_permission(id, parent_id, name, url, perms, menu_type, sort_no, del_flag, create_by, create_time)
VALUES (REPLACE(UUID(),'-',''), @menu_screen, '大屏查看', NULL, 'dashboard:screen:view', 2, 1, 0, 'admin', NOW());

-- 4.2 信用管理菜单（规则配置页+公示页，1.0三段式页面）
SET @menu_credit = REPLACE(UUID(),'-','');
INSERT IGNORE INTO sys_permission(id, parent_id, name, url, component, component_name, menu_type, sort_no, icon, status, del_flag, create_by, create_time, always_show)
VALUES (@menu_credit, NULL, '信用管理', '/credit', 'layouts/RouteView', 'CreditRoute', 0, 6, 'crown', '1', 0, 'admin', NOW(), 0);
INSERT IGNORE INTO sys_permission(id, parent_id, name, url, component, menu_type, sort_no, status, del_flag, create_by, create_time)
VALUES (REPLACE(UUID(),'-',''), @menu_credit, '信用规则配置', '/credit/rule', 'credit/CreditRuleList', 1, 1, '1', 0, 'admin', NOW()),
       (REPLACE(UUID(),'-',''), @menu_credit, '信用公示', '/credit/public', 'credit/CreditPublicList', 1, 2, '1', 0, 'admin', NOW());
INSERT IGNORE INTO sys_permission(id, parent_id, name, perms, menu_type, sort_no, status, del_flag, create_by, create_time)
VALUES (REPLACE(UUID(),'-',''), @menu_credit, '规则列表', 'credit:rule:list', 2, 1, '1', 0, 'admin', NOW()),
       (REPLACE(UUID(),'-',''), @menu_credit, '规则新增', 'credit:rule:add', 2, 2, '1', 0, 'admin', NOW()),
       (REPLACE(UUID(),'-',''), @menu_credit, '规则编辑', 'credit:rule:edit', 2, 3, '1', 0, 'admin', NOW()),
       (REPLACE(UUID(),'-',''), @menu_credit, '规则删除', 'credit:rule:delete', 2, 4, '1', 0, 'admin', NOW()),
       (REPLACE(UUID(),'-',''), @menu_credit, '结果发布', 'credit:result:publish', 2, 5, '1', 0, 'admin', NOW()),
       (REPLACE(UUID(),'-',''), @menu_credit, '手动计算', 'credit:result:calc', 2, 6, '1', 0, 'admin', NOW());

-- 4.3 授权给管理员角色（admin默认全有；此处给普通监管角色示例 role_code见1.0sys_role）
-- INSERT IGNORE INTO sys_role_permission(id, role_id, permission_id) SELECT REPLACE(UUID(),'-',''), r.id, p.id FROM sys_role r, sys_permission p WHERE r.role_code='fzadmin' AND p.del_flag=0 AND (p.id IN (@menu_screen,@menu_credit) OR p.parent_id IN (@menu_screen,@menu_credit));
-- ↑ 按班级实际角色开启（默认注释，验收演示时取消注释执行）

-- ---------------------------------------------------------------------
-- 5. 初始信用规则种子（演示可用：1加2减，含减分项满足25005校验）
-- ---------------------------------------------------------------------
INSERT IGNORE INTO bu_credit_rule(id, rule_code, rule_name, score_type, score_value, weight, cycle_type, data_source, max_times, enabled, remark, create_by, create_time)
VALUES (REPLACE(UUID(),'-',''), 'R-ALARM-COUNT', '超标告警次数扣分', '2', -2.00, 1.00, '1', '1', 30, '1', '每周期每条风险记录扣2分，上限30次', 'admin', NOW()),
       (REPLACE(UUID(),'-',''), 'R-FAN-OFF', '风机/净化器未开启扣分', '2', -1.00, 1.00, '1', '1', 20, '1', '每周期每次关机记录扣1分，上限20次', 'admin', NOW()),
       (REPLACE(UUID(),'-',''), 'R-SELFCHECK', '按期完成自查加分', '1', 1.00, 1.00, '1', '2', 10, '1', 'C组自查打卡每周期每次加1分，上限10次', 'admin', NOW());

-- 验证：
-- SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='psms' AND table_name IN ('bu_credit_rule','bu_credit_record','bu_credit_detail','base_point_log');  -- 预期4
-- SELECT COUNT(*) FROM sys_permission WHERE name LIKE '%驾驶舱%' OR name LIKE '%信用%';  -- 预期≥9

-- ---------------------------------------------------------------------
-- 5b. 系统参数表（1.0无此表，E组2.0新增：跨组地址/大模型配置/缓存TTL统一配置）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_param` (
  `id`          varchar(36)  NOT NULL COMMENT '主键',
  `param_name`  varchar(100) NOT NULL COMMENT '参数名称',
  `param_code`  varchar(60)  NOT NULL COMMENT '参数编码',
  `param_value` varchar(500) NULL COMMENT '参数值（密钥类由教师发放后运行期UPDATE，严禁入脚本）',
  `remark`      varchar(300) NULL COMMENT '说明',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_param_code` (`param_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表（E组2.0新增）';

-- ---------------------------------------------------------------------
-- 6. Sprint2 增量：AI调用审计表（#E-56，指导书任务4要求）+ 跨组/大模型系统参数
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bu_ai_call_log` (
  `id`          varchar(36)  NOT NULL COMMENT '主键',
  `scene`       varchar(60)  NOT NULL COMMENT '调用场景（weekly-report等）',
  `prompt`      text         NULL COMMENT '输入Prompt（截断2000字符）',
  `output`      text         NULL COMMENT '模型输出（截断4000字符）',
  `status`      varchar(60)  NOT NULL COMMENT '结果状态（OK:xxms/NO_API_KEY/ATTEMPT_x_FAIL等）',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
  PRIMARY KEY (`id`),
  KEY `idx_scene_time` (`scene`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大模型调用审计表（E组2.0新增）';

-- 系统参数（sys_param；密钥值由教师课后发放再 UPDATE，严禁写入本文件）
-- INSERT IGNORE INTO sys_param(id, param_name, param_code, param_value, create_time) VALUES
-- (REPLACE(UUID(),'-',''),'大模型BaseURL','psms.llm.baseUrl','https://课程大模型网关/v1/chat/completions',NOW()),
-- (REPLACE(UUID(),'-',''),'大模型APIKEY','psms.llm.apiKey','',NOW()),
-- (REPLACE(UUID(),'-',''),'大模型型号','psms.llm.model','glm-4-flash',NOW()),
-- (REPLACE(UUID(),'-',''),'A组风险榜地址','psms.predict.riskTopUrl','http://A组服务/v2/predict/risk-top',NOW()),
-- (REPLACE(UUID(),'-',''),'C组自查统计地址','psms.credit.selfcheckUrl','http://C组服务/v2/enterprise/selfcheck/summary-batch',NOW());

-- ---------------------------------------------------------------------
-- 7. Sprint3 增量：Quartz定时任务注册（#E-14）+ 聚合缓存TTL参数（#E-07）
-- ---------------------------------------------------------------------
INSERT IGNORE INTO sys_quartz_job(id, job_class_name, cron_expression, description, status, del_flag, create_by, create_time)
VALUES (REPLACE(UUID(),'-',''),
        'org.jeecg.modules.credit.job.CreditCalcJob',
        '0 0 3 1 * ?',
        'E组-信用评价周期计算（每月1日03:00，与手动触发共用CreditEngine，Redis锁防并发）',
        -1, 0, 'admin', NOW());
-- 注：status=-1为停止状态，课程环境部署后在 定时任务 菜单中启动（避免无规则数据时误跑）；
-- cron说明：秒 分 时 日 月 周 → 每月1日03:00:00。

INSERT IGNORE INTO sys_param(id, param_name, param_code, param_value, create_time)
VALUES (REPLACE(UUID(),'-',''),'大屏聚合缓存TTL(秒)','psms.dashboard.cacheTtl','30',NOW());
