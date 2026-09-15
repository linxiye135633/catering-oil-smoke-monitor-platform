package org.jeecg.modules.psms.statement.service;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface StatementService extends IService<Map<String,Object>> {

    Map<String, Object> getStatementData(String tableLampblack, String tablePurifier, String pointMac, String name);

    Map<String, Object> getStatementMonthData(String tableLampblack, String tablePurifier, String pointMac, String name,String format,int numv);

    Map<String, Object> getStatementSeasonData(String tableLampblack, String tablePurifier, String pointMac, String name,String format1,String format2,String format3,int numv);

    Map<String, Object> getStatDataTest(String tableLampblack, String tablePurifier, String pointMac, String sql, int numv);

}
