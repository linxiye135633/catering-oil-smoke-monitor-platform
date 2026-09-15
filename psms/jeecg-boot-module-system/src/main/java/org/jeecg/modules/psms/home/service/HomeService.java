package org.jeecg.modules.psms.home.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;


public interface HomeService extends IService<Map<String,Object>> {

    //餐饮企业接入数
    Map<String, Object> statCompany(int fidRegion);

    //排口测点数
    Map<String, Object> pointCompany(int fidRegion);

    //上报数据流量
    Map<String, Object> lampblackRateFlow(String table);

    //测点联网率
    Map<String, Object> pointRateFlow(String table,int fidRegion,String hours);

    //餐饮企业接入趋势
    Map<String, List<Object>> statiJoinYear(int fidRegion);

    //本月安装接入进度
    Map<String, Object> getRateProgress(int fidRegion);

    //测点设备
    Map<String, Object> getEquipmentPoint(String tableLampblack, String tablePurifier, int fidRegion);

    //餐饮企业-分类统计
    Map<String, Map<String,Object>> companyStat(int fidRegion);

    int getFlow(String tableLampblack, int fidRegion, String formatDay);
}
