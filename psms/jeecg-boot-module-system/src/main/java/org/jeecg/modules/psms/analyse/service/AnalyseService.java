package org.jeecg.modules.psms.analyse.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface AnalyseService extends IService<Map<String,Object>> {
    Map<String, Object> units(String tableLampblack, int fidRegion, String format,String formatDay);

    Map<String, Object> moreNum(String tableLampblack, int fidRegion, String format, String s,String companyId);
    Map<String, Object> moreNums(String tableLampblack, int fidRegion, String format, String s,String companyId);
    int runHourTime(String tableLampblack, int fidRegion, String formatDay, String companyId);
}
