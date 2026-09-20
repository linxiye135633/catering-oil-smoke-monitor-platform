package org.jeecg.modules.credit.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
 * CreditBoardMapper（#E-11/#E-12）
 * 榜单口径 KD-09：按周期总分升序（红牌优先），等级边界由 CreditEngine 写入；
 * 数据权限：榜单按1.0 fidRegion 过滤（企业与区域关联 area_code）。
 */
@Mapper
public interface CreditBoardMapper {

    /**
     * 信用榜单（KD-09）：红牌优先=总分升序；level可选过滤（1红/2黄/3绿）
     */
    @Select("<script>" +
            "SELECT R.id, R.company_id AS companyId, R.company_name AS companyName, " +
            "       R.total_score AS totalScore, R.level, R.cycle, R.publish_status AS publishStatus " +
            "FROM bu_credit_record R JOIN base_company BC ON BC.id = R.company_id " +
            "WHERE R.cycle = #{cycle} AND R.del_flag = 0 " +
            "AND (#{fidRegion} = 1024 OR BC.area_code = #{fidRegion}) " +
            "<if test='level != null and level != \"\"'> AND R.level = #{level} </if>" +
            "ORDER BY R.total_score ASC, R.company_name ASC LIMIT #{topN}" +
            "</script>")
    List<Map<String, Object>> selectBoard(@Param("cycle") String cycle,
                                          @Param("level") String level,
                                          @Param("topN") int topN,
                                          @Param("fidRegion") int fidRegion);

    /**
     * 单企业评价结果（含明细，UC-E03点击行）
     */
    @Select("SELECT R.id, R.company_id AS companyId, R.company_name AS companyName, " +
            "       R.total_score AS totalScore, R.level, R.cycle, R.snapshot " +
            "FROM bu_credit_record R WHERE R.company_id = #{companyId} " +
            "AND R.cycle = #{cycle} AND R.del_flag = 0 LIMIT 1")
    Map<String, Object> selectCompanyResult(@Param("companyId") String companyId,
                                            @Param("cycle") String cycle);

    /**
     * 单企业扣分明细（可追溯举证，答辩用）
     */
    @Select("SELECT D.rule_code AS ruleCode, D.change_score AS changeScore, " +
            "       D.times, D.source_no AS sourceNo, D.remark " +
            "FROM bu_credit_detail D WHERE D.record_id = #{recordId} AND D.del_flag = 0 " +
            "ORDER BY D.change_score ASC")
    List<Map<String, Object>> selectDetails(@Param("recordId") String recordId);

    /**
     * 公示列表（publish_status=1 已发布，KD-09出口/契约P-02）
     * 分页采用 MyBatis-Plus Page 参数（Jeecg 标准用法）。
     * 注意：手写 LIMIT #{offset}, #{pageSize} 在 MP 分页插件下会返回空 Map
     * （联调实测，原因见联调记录），不可改回。
     */
    @Select("SELECT R.company_name AS companyName, R.total_score AS totalScore, " +
            "       R.level, R.cycle, R.update_time AS publishTime " +
            "FROM bu_credit_record R JOIN base_company BC ON BC.id = R.company_id " +
            "WHERE R.cycle = #{cycle} AND R.publish_status = '1' AND R.del_flag = 0 " +
            "AND (#{fidRegion} = 1024 OR BC.area_code = #{fidRegion}) " +
            "ORDER BY R.total_score ASC")
    List<Map<String, Object>> selectPublicList(@Param("cycle") String cycle,
                                               @Param("fidRegion") int fidRegion,
                                               Page<Map<String, Object>> page);

    @Select("SELECT COUNT(1) FROM bu_credit_record R JOIN base_company BC ON BC.id = R.company_id " +
            "WHERE R.cycle = #{cycle} AND R.publish_status = '1' AND R.del_flag = 0 " +
            "AND (#{fidRegion} = 1024 OR BC.area_code = #{fidRegion})")
    int countPublic(@Param("cycle") String cycle, @Param("fidRegion") int fidRegion);

    /**
     * 发布/下架公示（幂等：仅草稿→发布、发布→下架）
     */
    @org.apache.ibatis.annotations.Update("UPDATE bu_credit_record SET publish_status = #{status}, " +
            "update_time = NOW(), update_by = #{operator} " +
            "WHERE cycle = #{cycle} AND del_flag = 0 " +
            "AND publish_status = #{fromStatus}")
    int updatePublishStatus(@Param("cycle") String cycle,
                            @Param("status") String status,
                            @Param("fromStatus") String fromStatus,
                            @Param("operator") String operator);

    @Select("SELECT COUNT(1) FROM bu_credit_record WHERE cycle = #{cycle} AND del_flag = 0")
    int countByCycle(@Param("cycle") String cycle);
}
