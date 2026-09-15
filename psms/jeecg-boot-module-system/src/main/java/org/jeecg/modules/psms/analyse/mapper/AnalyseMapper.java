package org.jeecg.modules.psms.analyse.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

public interface AnalyseMapper extends BaseMapper<Map<String,Object>> {
    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> units(String tableLampblack, int fidRegion, String format,String formatDay);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> moreNum(String tableLampblack, int fidRegion, String format, String formatDay,String companyId);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> moreNums(String tableLampblack, int fidRegion, String format, String formatDay,String companyId);

    @InterceptorIgnore(tenantLine = "true")
    int runHourTime(String tableLampblack, int fidRegion, String formatDay, String companyId);
}
