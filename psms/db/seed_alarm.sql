-- =====================================================================
-- E组 seed_alarm.sql 演示数据构造脚本（#E-45，沈福临）
-- 用途：大屏演示"超标告警→地图热力→趋势图"可一键复现（录屏前先清库再跑本脚本）
-- 前置：psms.sql（1.0基础库）+ db_v2_upgrade.sql 已执行
-- 口径：浓度阈值1.0 mg/m³（GB 18483平台配置演示值），与 DashboardMapper 判定一致
-- =====================================================================

-- 1) 演示企业与测点（若库内已有企业可跳过本节；坐标为演示区域，lng/lat合法非0）
INSERT IGNORE INTO base_company(id, code, name, door_name, area_code, address, contact, contact_mobile, lng, lat, status, business_category, creator, create_time)
VALUES
('E-DEMO-C01','DEMO001','演示·川味火锅（人民路店）','川味火锅','131002','人民路12号','张三','13800000001',116.401234,39.910234,'01','01','admin',NOW()),
('E-DEMO-C02','DEMO002','演示·老城烧烤','老城烧烤','131002','建国道8号','李四','13800000002',116.412345,39.923456,'01','02','admin',NOW()),
('E-DEMO-C03','DEMO003','演示·家常菜馆','家常菜馆','131003','新华路20号','王五','13800000003',116.390123,39.895678,'01','01','admin',NOW()),
('E-DEMO-C04','DEMO004','演示·快餐店（中心店）','快餐店','131003','中心路5号','赵六','13800000004',116.420456,39.930123,'01','03','admin',NOW());

INSERT IGNORE INTO base_point(id, company_id, code, name, point_type, point_mac, status, conn_date, creator, create_time)
VALUES
('E-DEMO-P01','E-DEMO-C01','P0001','1号排口','01','DEMO-MAC-01','01',NOW(),'admin',NOW()),
('E-DEMO-P02','E-DEMO-C02','P0002','1号排口','01','DEMO-MAC-02','01',NOW(),'admin',NOW()),
('E-DEMO-P03','E-DEMO-C03','P0003','1号排口','01','DEMO-MAC-03','01',NOW(),'admin',NOW()),
('E-DEMO-P04','E-DEMO-C04','P0004','1号排口','01','DEMO-MAC-04','01',NOW(),'admin',NOW());

-- 2) 近7日告警趋势数据：C01高频超标（红牌候选）、C02中频、C03低频、C04正常
--    用存储过程批量生成，避免千行INSERT
DROP PROCEDURE IF EXISTS seed_lampblack;
DELIMITER $$
CREATE PROCEDURE seed_lampblack()
BEGIN
  DECLARE i INT DEFAULT 0;
  DECLARE d INT DEFAULT 0;
  -- 每企业每日条数：C01=8条超标为主，C02=5条偶发，C03=2条，C04=6条全正常
  WHILE d < 7 DO
    SET i = 0;
    WHILE i < 8 DO
      INSERT INTO bu_lampblack_data(id, point_mac, lampblack_data, matter_data, nmhc_data,
        lampblack_avgdata, matter_avgdata, nmhc_avgdata, temp, hum, fan_status, purifier_status,
        fan_current, purifier_current, creator, create_time)
      VALUES (REPLACE(UUID(),'-',''), 'DEMO-MAC-01',
        ROUND(0.8 + RAND() * 2.5, 2), ROUND(RAND() * 2, 2), 0.00,
        ROUND(0.8 + RAND() * 2, 2), ROUND(RAND() * 2, 2), 0.00,
        25, 40, 1, 1, 4.0, 6.5, 'seed',
        DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL FLOOR(RAND() * 10) HOUR);
      SET i = i + 1;
    END WHILE;
    SET i = 0;
    WHILE i < 5 DO
      INSERT INTO bu_lampblack_data(id, point_mac, lampblack_data, matter_data, nmhc_data,
        lampblack_avgdata, matter_avgdata, nmhc_avgdata, temp, hum, fan_status, purifier_status,
        fan_current, purifier_current, creator, create_time)
      VALUES (REPLACE(UUID(),'-',''), 'DEMO-MAC-02',
        ROUND(0.5 + RAND() * 1.8, 2), ROUND(RAND() * 1.5, 2), 0.00,
        ROUND(0.5 + RAND() * 1.5, 2), ROUND(RAND(), 2), 0.00,
        24, 38, 1, 1, 3.8, 6.0, 'seed',
        DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL FLOOR(RAND() * 10) HOUR);
      SET i = i + 1;
    END WHILE;
    -- C03 低频 + C04 全正常
    INSERT INTO bu_lampblack_data(id, point_mac, lampblack_data, fan_status, purifier_status, creator, create_time)
    VALUES (REPLACE(UUID(),'-',''), 'DEMO-MAC-03', ROUND(0.3 + RAND() * 1.2, 2), 1, 1, 'seed',
        DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL 12 HOUR);
    INSERT INTO bu_lampblack_data(id, point_mac, lampblack_data, fan_status, purifier_status, creator, create_time)
    VALUES (REPLACE(UUID(),'-',''), 'DEMO-MAC-04', ROUND(0.1 + RAND() * 0.5, 2), 1, 1, 'seed',
        DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL 12 HOUR);
    SET d = d + 1;
  END WHILE;
END$$
DELIMITER ;
CALL seed_lampblack();
DROP PROCEDURE IF EXISTS seed_lampblack;

-- 3) 开关机日志（信用 R-FAN-OFF 扣分依据 + 风险口径）：C01/C02 各若干次关机
INSERT INTO base_point_log(id, point_mac, time, is_on, create_time)
SELECT REPLACE(UUID(),'-',''), 'DEMO-MAC-01', DATE_SUB(NOW(), INTERVAL n DAY) + INTERVAL 10 HOUR, 0, NOW()
FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 5 UNION SELECT 6 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t;
INSERT INTO base_point_log(id, point_mac, time, is_on, create_time)
SELECT REPLACE(UUID(),'-',''), 'DEMO-MAC-02', DATE_SUB(NOW(), INTERVAL n DAY) + INTERVAL 11 HOUR, 0, NOW()
FROM (SELECT 1 n UNION SELECT 4 UNION SELECT 7) t;

-- 3b) 警告记录（base_company_type，Sprint2口径修正后的告警滚动/趋势/处理率数据源）：
--     C01 高频警告中(红牌候选)、C02 中频+部分已解除、C03 低频已解除、C04 无警告
DROP PROCEDURE IF EXISTS seed_risk;
DELIMITER $$
CREATE PROCEDURE seed_risk()
BEGIN
  DECLARE d INT DEFAULT 0;
  DECLARE i INT DEFAULT 0;
  WHILE d < 7 DO
    SET i = 0;
    WHILE i < 8 DO
      INSERT INTO base_company_type(id, name, mac, alarm_type, is_on, fid_region, create_time)
      VALUES (REPLACE(UUID(),'-',''), '演示·川味火锅（人民路店）', 'DEMO-MAC-01', 1, 0, 131002,
              DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL FLOOR(RAND() * 10) HOUR);
      SET i = i + 1;
    END WHILE;
    SET i = 0;
    WHILE i < 5 DO
      INSERT INTO base_company_type(id, name, mac, alarm_type, is_on, fid_region, create_time, updata_time)
      VALUES (REPLACE(UUID(),'-',''), '演示·老城烧烤', 'DEMO-MAC-02', 1,
              IF(i < 2, 1, 0), 131002,
              DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL FLOOR(RAND() * 10) HOUR,
              IF(i < 2, DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL 20 HOUR, NULL));
      SET i = i + 1;
    END WHILE;
    INSERT INTO base_company_type(id, name, mac, alarm_type, is_on, fid_region, create_time, updata_time)
    VALUES (REPLACE(UUID(),'-',''), '演示·家常菜馆', 'DEMO-MAC-03', 1, 1, 131003,
            DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL 12 HOUR,
            DATE_SUB(NOW(), INTERVAL d DAY) + INTERVAL 18 HOUR);
    SET d = d + 1;
  END WHILE;
END$$
DELIMITER ;
CALL seed_risk();
DROP PROCEDURE IF EXISTS seed_risk;

-- 3c) 实时表（1.0运行期由LampBlackTask维护，演示环境预建）
CREATE TABLE IF NOT EXISTS `bu_lampblack_data_now` LIKE `bu_lampblack_data`;

-- 4) 实时表同步一份当日数据（大屏地图 latestValue 取 bu_lampblack_data_now）
INSERT INTO bu_lampblack_data_now(id, point_mac, lampblack_data, matter_data, nmhc_data,
  lampblack_avgdata, matter_avgdata, nmhc_avgdata, temp, hum, fan_status, purifier_status,
  fan_current, purifier_current, creator, create_time)
SELECT REPLACE(UUID(),'-',''), point_mac, lampblack_data, matter_data, nmhc_data,
  lampblack_avgdata, matter_avgdata, nmhc_avgdata, temp, hum, fan_status, purifier_status,
  fan_current, purifier_current, 'seed', NOW()
FROM (SELECT * FROM bu_lampblack_data WHERE create_time >= CURDATE() ORDER BY create_time DESC LIMIT 20) t;

-- 验证：
-- SELECT point_mac, COUNT(*) n, SUM(lampblack_data>1.0) ex FROM bu_lampblack_data GROUP BY point_mac;  -- C01超标为主
-- SELECT COUNT(*) FROM base_point_log WHERE is_on=0;  -- C01:8次 C02:3次 → 信用扣分差异可见
