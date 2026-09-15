package org.jeecg.modules.psms.analyse.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.psms.analyse.service.AnalyseService;
import org.jeecg.modules.psms.base.service.PsmsService;
import org.jeecg.modules.utils.RedisUtils;
import org.jeecg.modules.utils.ToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RestController
@RequestMapping("/analyseTask")
public class AnalyseTask {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private PsmsService psmsService;
    @Autowired
    private AnalyseService analyseService;
    @Autowired
    private CompanyService companyService;


    //每日凌晨一点  把智能统计的 结果都放到缓存里面
    @Scheduled(cron = "0 0 3 * * ?")
    public void executeAnalyseTask3() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        setData(format);
    }

    @GetMapping(value = "/getAnalyseTaskData")
    private void setData(String format){
        log.info("智能分析-每时缓存-start");
        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }

        long startTime = System.currentTimeMillis();
        List<Map<String,Object>> maps = psmsService.getRegion();
        String finalFormat = format;
        maps.forEach(data->{
            try{
                int fidRegion = Integer.parseInt(String.valueOf(data.get("id")));
                if(fidRegion == 131000){//暂时 只统计廊坊的数据
                    String tableLampblack = "bu_lampblack_data" + "_" + fidRegion;
                    unitsCache(finalFormat,fidRegion,tableLampblack);
                    unitsChartCache(finalFormat,fidRegion,tableLampblack);
                    moreNumCache(finalFormat,fidRegion,tableLampblack,"");
                    moreNumChartCache(finalFormat,fidRegion,tableLampblack,"");
                    alarmAnalysisCache(finalFormat,fidRegion,tableLampblack,"");

                    QueryWrapper<Company> qw = new QueryWrapper<>();
                    qw.eq("fid_region",fidRegion);
                    List<Company> pageList = companyService.list(qw);
                    for(Company company:pageList){
                        String companyId = company.getId();
                        moreNumCache(finalFormat,fidRegion,tableLampblack,companyId);
                        moreNumChartCache(finalFormat,fidRegion,tableLampblack,companyId);
                        alarmAnalysisCache(finalFormat,fidRegion,tableLampblack,companyId);
                        runHourTimeCache(finalFormat,fidRegion,tableLampblack,companyId);
                    }
                }
            }catch (Exception e){
                e.getMessage();
            }
        });

        long endTime = System.currentTimeMillis()-startTime;
        int time = (int) (endTime/1000);
        log.info("智能分析-每时缓存-end-耗时："+ ToolsUtils.setToTime(time));
    }

    //根据月份 清空缓存
    @GetMapping(value = "/cleanRedis")
    private void cleanRedis(String format){
        redisUtils.cleanRedis(format);
    }

    //缓存 - 超标单位构成
    private void unitsCache(String format,int fidRegion,String tableLampblack){
        Map<String,Object>map = analyseService.units(tableLampblack,fidRegion,format,"");
        redisUtils.set(format+"_units_"+fidRegion,map);
        log.info("unitsCache-"+format+"_units_"+fidRegion+":"+redisUtils.get(format+"_units_"+fidRegion).toString());
    }

    //缓存 - 超标单位构成 - 折线图
    private void unitsChartCache(String format,int fidRegion,String tableLampblack){
        List<String>days = ToolsUtils.getDateList(format);
        Map<String, List<Object>>map = new LinkedHashMap<>();
        List<Object> dax = new LinkedList<>();
        List<Object> data1 = new LinkedList<>();
        for (int i=0;i<days.size();i++) {
            String formatDay = days.get(i);
            dax.add(i+1);
            Map<String, Object> mapv = analyseService.units(tableLampblack, fidRegion, "", formatDay);
            data1.add(mapv.get("a2"));
        }
        map.put("dax",dax);
        map.put("data",data1);
        redisUtils.set(format+"_unitsChart_"+fidRegion,map);
        log.info("unitsChartCache-"+format+"_unitsChart_"+fidRegion+":"+redisUtils.get(format+"_unitsChart_"+fidRegion).toString());
    }

    //缓存 - 超标次数构成
    private void moreNumCache(String format,int fidRegion,String tableLampblack,String companyId){
        Map<String,Object>map = analyseService.moreNum(tableLampblack,fidRegion,format,"",companyId);
        redisUtils.set(format+"_moreNum_"+fidRegion+companyId,map);
        log.info("moreNumCache-"+format+"_moreNum_"+fidRegion+companyId+":"+redisUtils.get(format+"_moreNum_"+fidRegion+companyId).toString());
    }

    //缓存 - 超标次数构成 - 折线图
    private void moreNumChartCache(String format,int fidRegion,String tableLampblack,String companyId){

        Map<String, List<Object>>map = new LinkedHashMap<>();
        List<Object> dax = new LinkedList<>();
        List<Object> data1 = new LinkedList<>();
        List<String>days = ToolsUtils.getDateList(format);
        for (int i=0;i<days.size();i++) {
            String formatDay = days.get(i);
            dax.add(i+1);
            Map<String, Object> mapv = analyseService.moreNum(tableLampblack, fidRegion, "", formatDay, companyId);
            data1.add(mapv.get("a1"));
        }
        map.put("dax",dax);
        map.put("data",data1);
        redisUtils.set(format+"_moreNumChart_"+fidRegion+companyId,map);
        log.info("moreNumCache-"+format+"_moreNumChart_"+fidRegion+companyId+":"+redisUtils.get(format+"_moreNumChart_"+fidRegion+companyId).toString());
    }

    //缓存 - 告警分析 - 三个折线图
    private void alarmAnalysisCache(String format,int fidRegion,String tableLampblack,String companyId){

        Map<String, List<Object>>map = new LinkedHashMap<>();
        List<Object> dax = new LinkedList<>();
        List<Object> data1 = new LinkedList<>();
        List<Object> data2 = new LinkedList<>();
        List<Object> data3 = new LinkedList<>();
        List<String>days = ToolsUtils.getDateList(format);
        for (int i=0;i<days.size();i++) {
            String formatDay = days.get(i);
            dax.add(i+1);
            Map<String, Object> mapv = analyseService.moreNums(tableLampblack, fidRegion, "", formatDay, companyId);
            data1.add(mapv.get("a1"));
            data2.add(mapv.get("a2"));
            data3.add(mapv.get("a3"));
        }

        map.put("dax",dax);
        map.put("data1",data1);
        map.put("data2",data2);
        map.put("data3",data3);
        redisUtils.set(format+"_alarmAnalysis_"+fidRegion+companyId,map);
        log.info("moreNumCache-"+format+"_alarmAnalysis_"+fidRegion+companyId+":"+redisUtils.get(format+"_alarmAnalysis_"+fidRegion+companyId).toString());
    }

    private void runHourTimeCache(String format,int fidRegion,String tableLampblack,String companyId){
        Map<String, List<Object>>map = new LinkedHashMap<>();
        List<Object> dax = new LinkedList<>();
        List<Object> data1 = new LinkedList<>();
        List<String>days = ToolsUtils.getDateList(format);
        for (int i=0;i<days.size();i++) {
            String formatDay = days.get(i);
            dax.add(i+1);
            int n = analyseService.runHourTime(tableLampblack, fidRegion, formatDay, companyId);
            data1.add(n);
        }
        map.put("dax",dax);
        map.put("data",data1);
        redisUtils.set(format+"_runHourTime_"+fidRegion+companyId,map);
        log.info("moreNumCache-"+format+"_runHourTime_"+fidRegion+companyId+":"+redisUtils.get(format+"_runHourTime_"+fidRegion+companyId).toString());
    }
}
