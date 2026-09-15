package org.jeecg.modules.psms.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.psms.base.entity.LampBlack;

import java.util.List;
import java.util.Map;


public interface PsmsService extends IService<Map<String,Object>> {

    Page getYouYanList(Page page,
                       String tableLampblack,String tablePurifier,int fidRegion,
                       String companyid, String pointid, String startTime, String endTime, Integer pageNo, Integer pageSize);

    IPage<Map<String,Object>> getNewestCreateTime(
            Page<Map<String,Object>> page,
            String tableLampblack,String tablePurifier,int fidRegion,
            List<String> ids,String hour,String pointMac,String startTime,String endTime);

    IPage<Map<String, Object>> getNewestCreateTime2(
            Page<Map<String, Object>> page,
            String tableLampblack, String tablePurifier, int fidRegion,
            List<String> ids, String pointMac);

    Map<String, List<String>> getStatByCreateTime(String table,String pointMac,String createTime);

    Map<String, Object> getDetailsByTime(String tableLampblack, String id, String hour);

    Map<String, List<Map<String, Object>>> getWarnEvent(String tableLampblack, String id,String pointId, int fidRegion);

    Map<String, Object> statCompanyExcessive(String tableLampblack, int fidRegion);

    Map<String, Object> statPointExcessive(String tableLampblack, int fidRegion);

    Map<String, Object> getOverproof(String tableLampblack, int fidRegion,String formatDay);

    Map<String, Object> getOnlineNum(String tableLampblack, int fidRegion,String formatDay);

    int insertEntity(LampBlack entity);

    List<Map<String, Object>> getRegion();

    void deleteToDay(String tablePurifier);

    List<Map<String, Object>> getNowTimeDataForMac(String tableLampblack, String tablePurifier, String pointMac, int fidRegion);
}
