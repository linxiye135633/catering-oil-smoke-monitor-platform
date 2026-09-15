package org.jeecg.modules.psms.supervision.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface SupervisionMapper extends BaseMapper<Map<String,Object>> {

    /**
     * 根据 餐饮企业id 和 起止时间获取开关机次数
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Map<String, Object>> stopMac(String id, String startTime,String endTime);

    /**
     * 根据餐饮企业id 和 日期获取油烟表条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    Map<String,Object> getLampblackCount(String tableLampblack, String companyId, String format);

    /**
     * 根据餐饮企业id和日期 获取净化器条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    int getPurifierCount(String tableLampblack, String tablePurifier, String companyId, String format);

    /**
     * 据餐饮企业id和日期  获取净化器 二次电压字段小于10的条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> secondaryVoltage(String tableLampblack, String tablePurifier, String companyId, String format);

    /**
     * 根据日期和mac集合 查询出开关机表数据
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Map<String, Object>> getPointLog(List<String> macs, String format);

    /**
     * 根据餐饮企业id和起止时间 获取油烟表条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    int getNetwork(String tableLampblack, String companyId, String format);

    /**
     * 根据餐饮企业id和日期查询 超标的条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> smokeCount(String tableLampblack, String companyId, String format);

    /**
     * 根据餐饮企业id和日期查询 超标的条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> highVoltage(String tableLampblack, String tablePurifier, String companyId, String format);

    /**
     * 根据日期查询 警告类型条目数
     */
    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getLabel(String fidRegion, String format);

    /**
     * 根据日期和is_on（0=统计 1=排除） 进行查询高中低风险
     */
    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getRiskCount(String format, int i, String fidRegion);

    /**
     * 根据日期和is_on（0=统计 1=排除） 查询标签企业数量
     */
    @InterceptorIgnore(tenantLine = "true")
    int getRiskVCount(String format, int i, String fidRegion);

    /**
     * 根据日期 查询出警告企业列表内容
     */
    @InterceptorIgnore(tenantLine = "true")
    Page<Map<String, Object>> getNewRisk(Page<Map<String, Object>> page, @Param("fidRegion")int fidRegion, @Param("format")String format);

    /**
     * 根据餐饮企业名称 解除某个月的警告状态
     */
    @InterceptorIgnore(tenantLine = "true")
    void getClearRisk(int fidRegion, String format, String name);

    /**
     * 根据日期查询出 接触风险企业
     */
    @InterceptorIgnore(tenantLine = "true")
    Page<Map<String, Object>> getClearRiskList(Page<Map<String, Object>> page, @Param("fidRegion")int fidRegion,  @Param("format")String format);

    /**
     * 根据餐饮企业id和时间区间 解除警告状态
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateAlarm(String companyId, String startTime, String endTime);
}
