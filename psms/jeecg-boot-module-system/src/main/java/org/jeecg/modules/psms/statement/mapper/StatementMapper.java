package org.jeecg.modules.psms.statement.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

public interface StatementMapper extends BaseMapper<Map<String,Object>> {

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getStatementData(String tableLampblack, String tablePurifier, String pointMac, String name);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getStatementMonthData(String tableLampblack, String tablePurifier, String pointMac, String name,String format,int numv);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getStatementSeasonData(String tableLampblack, String tablePurifier, String pointMac, String name,String format1,String format2,String format3,int numv);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getStatDataTest(String tableLampblack, String tablePurifier, String pointMac, String sql,int numv);


}
