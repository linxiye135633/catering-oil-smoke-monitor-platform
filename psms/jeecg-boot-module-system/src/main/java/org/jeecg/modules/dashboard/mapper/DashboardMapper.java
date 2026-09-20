package org.jeecg.modules.dashboard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * DashboardMapper（#E-02/#E-03/#E-04）
 * 口径对齐（Sprint2修正版）：
 * - KD-02 点位/超标/热力权重：权重=近30天超标频次×浓度均值（历史主表聚合）；
 * - KD-05 最新告警：base_company_type 警告记录表（1.0 getNewRisk 同源）；
 * - KD-01/03/04 区域总览：与1.0 /company/count、/base/statCompanyExcessive、
 *   /home/pointRateFlow 分子分母一致（区域联查版）。
 * SQL安全：表名无法#{}参数化，${tableName} 仅允许 Controller 传入枚举常量
 * （bu_lampblack_data / _now / _today 白名单），其余值一律 #{} 绑定。
 */
@Mapper
public interface DashboardMapper {

    /**
     * 地图点位：企业坐标 + 测点状态 + 实时表最新浓度 + 近30天超标热力权重（#E-20数据源）
     */
    @Select("SELECT BC.id            AS companyId, " +
            "       BC.name          AS companyName, " +
            "       BC.lng           AS lng, " +
            "       BC.lat           AS lat, " +
            "       BP.point_mac     AS pointMac, " +
            "       BP.status        AS pointStatus, " +
            "       N.lampblack_data AS latestValue, " +
            "       N.create_time    AS latestTime, " +
            "       IFNULL(F.freq, 0) * IFNULL(F.avgVal, 0) AS heatWeight, " +
            "       CASE WHEN N.lampblack_data > 1.0 THEN 1 ELSE 0 END AS excessive " +
            "FROM base_company BC " +
            "LEFT JOIN base_point BP ON BP.company_id = BC.id AND BP.status = '01' " +
            "LEFT JOIN (SELECT T.point_mac, T.lampblack_data, T.create_time " +
            "           FROM ${tableName} T " +
            "           INNER JOIN (SELECT point_mac, MAX(create_time) mt " +
            "                       FROM ${tableName} GROUP BY point_mac) M " +
            "           ON T.point_mac = M.point_mac AND T.create_time = M.mt) N " +
            "ON N.point_mac = BP.point_mac " +
            "LEFT JOIN (SELECT point_mac, COUNT(1) freq, AVG(lampblack_data) avgVal " +
            "           FROM bu_lampblack_data " +
            "           WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "           AND lampblack_data > 1.0 GROUP BY point_mac) F " +
            "ON F.point_mac = BP.point_mac " +
            "WHERE BC.status = '01' " +
            "AND (#{fidRegion} = 1024 OR BC.area_code = #{fidRegion})")
    List<Map<String, Object>> selectMapPoints(@Param("fidRegion") int fidRegion,
                                              @Param("tableName") String tableName);

    /**
     * 最新告警（KD-05）：base_company_type 警告记录表，与1.0 getNewRisk 完全同口径
     */
    @Select("SELECT BCT.id            AS riskId, " +
            "       BCT.name          AS companyName, " +
            "       BCT.mac           AS pointMac, " +
            "       BCT.alarm_type    AS alarmType, " +
            "       BCT.create_time   AS alarmTime " +
            "FROM base_company_type BCT " +
            "WHERE BCT.is_on = 0 AND BCT.alarm_type > 0 " +
            "AND (#{fidRegion} = 1024 OR BCT.fid_region = #{fidRegion}) " +
            "ORDER BY BCT.create_time DESC " +
            "LIMIT #{pageSize}")
    List<Map<String, Object>> selectLatestAlarms(@Param("pageSize") int pageSize,
                                                 @Param("fidRegion") int fidRegion);

    /**
     * 区域总览（KD-01/03/04合并查询，聚合接口regionOverview分区）
     * 口径：企业总数(status=01)、测点总数（经企业区域过滤，base_point无区域字段）、
     * 24h在线测点数、超标企业数（去重）
     */
    @Select("SELECT (SELECT COUNT(1) FROM base_company WHERE status='01' " +
            "         AND (#{fidRegion}=1024 OR area_code=#{fidRegion}))                    AS companyCount, " +
            "       (SELECT COUNT(1) FROM base_point BP JOIN base_company BC ON BC.id=BP.company_id " +
            "         WHERE BP.status='01' " +
            "         AND (#{fidRegion}=1024 OR BC.area_code=#{fidRegion}))                 AS pointCount, " +
            "       (SELECT COUNT(DISTINCT T.point_mac) FROM bu_lampblack_data T " +
            "         JOIN base_point BP ON BP.point_mac=T.point_mac " +
            "         JOIN base_company BC2 ON BC2.id=BP.company_id " +
            "         WHERE T.create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
            "         AND (#{fidRegion}=1024 OR BC2.area_code=#{fidRegion}))                 AS onlinePointCount, " +
            "       (SELECT COUNT(DISTINCT BP.company_id) FROM bu_lampblack_data T " +
            "         JOIN base_point BP ON BP.point_mac=T.point_mac " +
            "         JOIN base_company BC3 ON BC3.id=BP.company_id " +
            "         WHERE T.lampblack_data > 1.0 " +
            "         AND T.create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
            "         AND (#{fidRegion}=1024 OR BC3.area_code=#{fidRegion}))                 AS excessiveCompanyCount")
    Map<String, Object> selectRegionOverview(@Param("fidRegion") int fidRegion);

    /** 读取系统参数（跨组地址白名单配置） */
    @Select("SELECT param_value FROM sys_param WHERE param_code = #{code} LIMIT 1")
    String selectConfigValue(@Param("code") String code);
}
