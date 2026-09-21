package org.jeecg.modules.stat.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * StatMapper（#E-52/#E-53/#E-54）
 * 口径对齐《口径对齐清单》（Sprint2修正版）：
 * - KD-06 告警趋势：数据源=base_company_type（1.0警告记录表，getNewRisk同源），
 *   分子=每日新增警告记录数（alarm_type>0），跨日切分用 create_time；
 * - KD-07 行业分布：按 base_company.business_category 分组（Sprint2修正：
 *   base_company_type 实为警告表而非类别字典，原JOIN错误已移除；类别名由前端字典翻译）。
 */
@Mapper
public interface StatMapper {

    /**
     * 近N日告警趋势（KD-06修正）：查1.0警告记录表，按自然日分组含当日
     */
    @Select("SELECT DATE_FORMAT(BCT.create_time, '%Y-%m-%d') AS statDate, " +
            "       COUNT(1)                              AS alarmCount " +
            "FROM base_company_type BCT " +
            "WHERE BCT.create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "AND BCT.alarm_type > 0 " +
            "AND (#{fidRegion} = 1024 OR BCT.fid_region = #{fidRegion}) " +
            "GROUP BY DATE_FORMAT(BCT.create_time, '%Y-%m-%d') " +
            "ORDER BY statDate")
    List<Map<String, Object>> selectAlarmTrend(@Param("days") int days,
                                               @Param("fidRegion") int fidRegion);

    /**
     * 行业分布（KD-07修正）：按经营类别编码分组企业数，编码由前端按字典翻译
     */
    @Select("SELECT BC.business_category AS industryCode, " +
            "       COUNT(BC.id)          AS companyCount " +
            "FROM base_company BC " +
            "WHERE BC.status = '01' " +
            "AND (#{fidRegion} = 1024 OR BC.area_code = #{fidRegion}) " +
            "GROUP BY BC.business_category " +
            "ORDER BY companyCount DESC")
    List<Map<String, Object>> selectIndustryDistribution(@Param("fidRegion") int fidRegion);

    /**
     * 告警处理率（KD-08，#E-54）：口径对齐1.0——
     * 分母=周期内警告记录总数（is_on=0警告中+1已解除）；
     * 分子=其中已解除数（is_on=1，getClearRisk动作产生）
     */
    @Select("SELECT COUNT(1) AS total, " +
            "       IFNULL(SUM(CASE WHEN BCT.is_on = 1 THEN 1 ELSE 0 END), 0) AS cleared " +
            "FROM base_company_type BCT " +
            "WHERE DATE_FORMAT(BCT.create_time, '%Y-%m') = #{cycle} " +
            "AND BCT.alarm_type > 0 " +
            "AND (#{fidRegion} = 1024 OR BCT.fid_region = #{fidRegion})")
    Map<String, Object> selectProcessRate(@Param("cycle") String cycle,
                                          @Param("fidRegion") int fidRegion);

    /**
     * 读取系统参数（大模型地址/密钥引用等配置，避免硬编码）
     */
    @Select("SELECT param_value FROM sys_param WHERE param_code = #{code} LIMIT 1")
    String selectConfigValue(@Param("code") String code);

    /**
     * #E-56 大模型调用审计落库（bu_ai_call_log，DDL见 db/v2_upgrade.sql §6）
     */
    @Insert("INSERT INTO bu_ai_call_log(id, scene, prompt, output, status, create_time) " +
            "VALUES(REPLACE(UUID(),'-',''), #{scene}, #{prompt}, #{output}, #{status}, NOW())")
    int insertAiCallLog(@Param("scene") String scene,
                        @Param("prompt") String prompt,
                        @Param("output") String output,
                        @Param("status") String status);
}
