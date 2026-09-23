-- =====================================================================
-- optimize_indexes.sql 性能优化索引包（#E-58，任涵艺；指导书任务6"先度量后优化"）
-- 用法：W6集成联调后，先跑 EXPLAIN 段留档（优化前）→ 建索引 → 再跑 EXPLAIN 段（优化后）
--       → 前后 type/rows 截图填入 docs/优化记录表.md
-- 原则：只对高频查询字段建索引（企业id+时间、区域+时间）；避免索引失效四场景
--       （函数包裹索引列/隐式类型转换/前导模糊/OR连接非索引列——本包SQL已规避）
-- =====================================================================

-- ---------- 0. 优化前EXPLAIN留档（跑一次截图） ----------
EXPLAIN SELECT COUNT(1) FROM bu_lampblack_data WHERE create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) GROUP BY point_mac;
EXPLAIN SELECT COUNT(1) FROM base_company_type WHERE is_on = 0 AND alarm_type > 0 AND fid_region = 131002 ORDER BY create_time DESC;
EXPLAIN SELECT point_mac, COUNT(1), AVG(lampblack_data) FROM bu_lampblack_data
 WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND lampblack_data > 1.0 GROUP BY point_mac;

-- ---------- 1. 索引DDL（IF NOT EXISTS语义：先查information_schema再建） ----------
-- 1a) 油烟数据：测点+时间（在线率/最新值/趋势高频：KD-02/04/06）
SET @exist := (SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE() AND table_name = 'bu_lampblack_data' AND index_name = 'idx_mac_time');
SET @sql := IF(@exist = 0, 'ALTER TABLE bu_lampblack_data ADD INDEX idx_mac_time (point_mac, create_time)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1b) 油烟数据：时间+浓度（30天超标热力权重聚合：KD-02b）
SET @exist := (SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE() AND table_name = 'bu_lampblack_data' AND index_name = 'idx_time_value');
SET @sql := IF(@exist = 0, 'ALTER TABLE bu_lampblack_data ADD INDEX idx_time_value (create_time, lampblack_data)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1c) 警告记录：区域+时间（告警滚动/趋势/处理率：KD-05/06/08）
SET @exist := (SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE() AND table_name = 'base_company_type' AND index_name = 'idx_region_time2');
SET @sql := IF(@exist = 0, 'ALTER TABLE base_company_type ADD INDEX idx_region_time2 (fid_region, create_time)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1d) 信用明细：结果外键（明细抽屉：KD-09追溯）
SET @exist := (SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE() AND table_name = 'bu_credit_detail' AND index_name = 'idx_record2');
SET @sql := IF(@exist = 0, 'ALTER TABLE bu_credit_detail ADD INDEX idx_record2 (record_id)', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ---------- 2. 优化后EXPLAIN留档（重跑0段，比对type是否从ALL→range/ref、rows数量级） ----------
-- 预期：1a/1b/1c 三条查询至少一条 type 改善；若均无改善回滚（DROP INDEX）并记录原因。

-- ---------- 3. 慢查询自查（课程服务器有权限时） ----------
-- SHOW VARIABLES LIKE 'slow_query_log%';
-- SET GLOBAL slow_query_log = 1; SET GLOBAL long_query_time = 1;
-- 慢日志位留给 W7 部署后执行，配合 bench_aggregate.py 采样。
