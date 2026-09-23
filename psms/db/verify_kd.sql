-- =====================================================================
-- verify_kd.sql 口径核对脚本（#E-36 第一轮，任涵艺出数/孙雅欣验数）
-- 用途：程序化复算大屏每个数字（KD-01~KD-08），与1.0报表页人工核对配合使用；
-- 用法：mysql -uroot -p psms < sql/verify_kd.sql （或客户端逐段执行）
-- 双人核对制：出数人跑本脚本留档 → 验数人对照1.0报表逐项打勾签字
-- =====================================================================

-- 【KD-01】企业总数（对照：即时接入-企业列表总数）——区域参数按演示账号替换
SELECT 'KD-01 企业总数' AS item, COUNT(1) AS value
FROM base_company WHERE status = '01' AND del_flag = 0;

-- 【KD-02a】测点数 / 在线测点数（分母）/ 近30天超标测点数（分子）
SELECT 'KD-02 测点/在线/超标' AS item,
       (SELECT COUNT(1) FROM base_point WHERE status = '01') AS points,
       (SELECT COUNT(DISTINCT point_mac) FROM bu_lampblack_data
         WHERE create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR)) AS online24h,
       (SELECT COUNT(DISTINCT point_mac) FROM bu_lampblack_data
         WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND lampblack_data > 1.0) AS excessive30d;

-- 【KD-02b】热力权重抽样（前10，应与地图气泡 heatWeight 一致：频次×均值）
SELECT BP.company_id, COUNT(1) AS freq, ROUND(AVG(BLD.lampblack_data), 2) AS avgVal,
       ROUND(COUNT(1) * AVG(BLD.lampblack_data), 1) AS heatWeight
FROM bu_lampblack_data BLD JOIN base_point BP ON BP.point_mac = BLD.point_mac
WHERE BLD.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND BLD.lampblack_data > 1.0
GROUP BY BP.company_id ORDER BY heatWeight DESC LIMIT 10;

-- 【KD-03】超标企业数（24h，按企业去重——注意与测点数区分）
SELECT 'KD-03 超标企业数(24h)' AS item, COUNT(DISTINCT BP.company_id) AS value
FROM bu_lampblack_data BLD JOIN base_point BP ON BP.point_mac = BLD.point_mac
WHERE BLD.lampblack_data > 1.0 AND BLD.create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR);

-- 【KD-04】设备在线率（对照1.0 /home/pointRateFlow?hours=-24）
SELECT 'KD-04 在线率(24h)%' AS item,
       ROUND(100.0 * (SELECT COUNT(DISTINCT point_mac) FROM bu_lampblack_data
                      WHERE create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR))
             / NULLIF((SELECT COUNT(1) FROM base_point WHERE status = '01'), 0), 1) AS value;

-- 【KD-05】最新告警TOP10（与 /v2/dashboard/alarms/latest 返回一致；对照1.0预警列表）
SELECT 'KD-05' AS item, BCT.name AS companyName, BCT.alarm_type, BCT.create_time AS alarmTime
FROM base_company_type BCT
WHERE BCT.is_on = 0 AND BCT.alarm_type > 0
ORDER BY BCT.create_time DESC LIMIT 10;

-- 【KD-06】告警趋势7日（与 /v2/stat/alarm-trend 一致；对照1.0告警分析按日计数）
SELECT 'KD-06' AS item, DATE_FORMAT(BCT.create_time, '%Y-%m-%d') AS statDate, COUNT(1) AS alarmCount
FROM base_company_type BCT
WHERE BCT.create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND BCT.alarm_type > 0
GROUP BY DATE_FORMAT(BCT.create_time, '%Y-%m-%d') ORDER BY statDate;

-- 【KD-07】行业分布（与 /v2/stat/industry 一致；对照企业列表按类别筛选计数）
SELECT 'KD-07' AS item, business_category AS industryCode, COUNT(1) AS companyCount
FROM base_company WHERE status = '01' AND del_flag = 0
GROUP BY business_category ORDER BY companyCount DESC;

-- 【KD-08】告警处理率（当月；与 /v2/stat/process-rate 一致；分子=is_on=1已解除）
SELECT 'KD-08 处理率(当月)' AS item, COUNT(1) AS total,
       IFNULL(SUM(is_on = 1), 0) AS cleared,
       ROUND(100.0 * IFNULL(SUM(is_on = 1), 0) / NULLIF(COUNT(1), 0), 1) AS ratePct
FROM base_company_type
WHERE alarm_type > 0 AND DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m');

-- 【交叉】聚合接口抽查：以上任一结果与 /v2/dashboard/aggregate 对应分区比对；
-- 不一致时优先核对区域参数（fidRegion）与本脚本的全量口径差异。

-- =====================================================================
-- 核对签字（每轮一行）：轮次/日期/出数(任涵艺)/验数(孙雅欣)/结论
-- 第1轮 W4: ____ / ____ / 待签 / 待签
-- =====================================================================
