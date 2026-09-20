package org.jeecg.modules.credit.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * CreditStatMapper（#E-10 取数层）
 * 口径对齐《口径对齐清单》KD-11/KD-12：复用1.0 SupervisionMapper 风险口径与开关机口径，
 * 本Mapper仅按"企业+周期"维度重新包装，分子分母与1.0报表一致（孙雅欣KD核对项）。
 * 动态表名规则：信用计算按周期取历史主表 bu_lampblack_data（跨月统计禁查单月分表）。
 */
@Mapper
public interface CreditStatMapper {

    /** 参与评价的企业（已接入状态，含名称） */
    @Select("SELECT id, name FROM base_company WHERE status = '01' ")
    List<Map<String, Object>> selectActiveCompanies();

    /**
     * 周期内风险/超标次数（KD-11同口径，Sprint2修正）：数据源=base_company_type
     * 警告记录表（1.0 getNewRisk/getRiskCount 同源），按企业名称关联（1.0按name键）
     */
    @Select("SELECT COUNT(1) FROM base_company_type BCT " +
            "JOIN base_company BC ON BC.name = BCT.name " +
            "WHERE BC.id = #{companyId} " +
            "AND DATE_FORMAT(BCT.create_time, '%Y-%m') = #{cycle} " +
            "AND BCT.alarm_type > 0")
    Integer countRiskByCompany(@Param("companyId") String companyId, @Param("cycle") String cycle);

    /** 周期内风机关机次数（KD-12同口径：base_point_log is_on=0 计数，对齐 stopMac） */
    @Select("SELECT COUNT(1) FROM base_point_log BPL " +
            "JOIN base_point BP ON BP.point_mac = BPL.point_mac " +
            "WHERE BP.company_id = #{companyId} " +
            "AND DATE_FORMAT(BPL.time, '%Y-%m') = #{cycle} " +
            "AND BPL.is_on = 0")
    Integer countFanOffByCompany(@Param("companyId") String companyId, @Param("cycle") String cycle);

    /** 读取系统参数（跨组地址等配置项，避免硬编码） */
    @Select("SELECT param_value FROM sys_param WHERE param_code = #{code} LIMIT 1")
    String selectConfigValue(@Param("code") String code);
}
