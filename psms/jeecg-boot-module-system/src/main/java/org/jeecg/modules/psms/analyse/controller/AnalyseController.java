package org.jeecg.modules.psms.analyse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.annotations.Cache;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.service.PointService;
import org.jeecg.modules.psms.analyse.service.AnalyseService;
import org.jeecg.modules.utils.RedisUtils;
import org.jeecg.modules.utils.ToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="psms-4-智能分析-analyse")
@Slf4j
@RestController
@RequestMapping("/analyse")
public class AnalyseController {
    @Autowired
    private AnalyseService analyseService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private PointService pointService;
    //超标分析-超标单位构成
    @AutoLog(value = "超标分析-超标单位构成")
    @ApiOperation(value="超标分析-超标单位构成", notes="超标分析-超标单位构成")
    @GetMapping(value = "/units")
    public Result<?> units(@RequestParam(name="format",required=false) String format) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }

        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;

        try {
            if(fidRegion==131000){
                Map<String,Object>map = (Map<String,Object>)redisUtils.get(format+"_units_"+fidRegion);
                if(map.get("a5")==null){
                    QueryWrapper<Point> queryPoint = new QueryWrapper<>();
                    if(fidRegion!=1024){
                        queryPoint.eq("fid_region",fidRegion);
                    }
                    int pointAllCount = pointService.count(queryPoint);
                    int a5 = pointAllCount - Integer.parseInt(String.valueOf(map.get("a4")));
                    map.put("a4",pointAllCount);
                    map.put("a5",a5);
                }

                if(null!=map && !map.isEmpty()){
                    return Result.OK(map);
                }
            }

        }catch (Exception e){
            e.getMessage();
        }

        Map<String,Object>map = analyseService.units(tableLampblack,fidRegion,format,"");
        QueryWrapper<Point> queryPoint = new QueryWrapper<>();
        if(fidRegion!=1024){
            queryPoint.eq("fid_region",fidRegion);
        }
        int pointAllCount = pointService.count(queryPoint);
        int a5 = pointAllCount - Integer.parseInt(String.valueOf(map.get("a4")));
        map.put("a4",pointAllCount);
        map.put("a5",a5);
        redisUtils.set(format+"_units_"+fidRegion,map,12L);
        return Result.OK(map);
    }

    //超标分析-超标单位构成 折线图
    @AutoLog(value = "超标分析-超标单位构成 折线图")
    @ApiOperation(value="超标分析-超标单位构成 折线图", notes="超标分析-超标单位构成 折线图")
    @GetMapping(value = "/unitsChart")
    public Result<?> unitsChart (
            @RequestParam(name="format",required=false) String format) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }

        try {
            if(fidRegion==131000){
                Map<String, List<Object>>map = (Map<String, List<Object>>)redisUtils.get(format+"_unitsChart_"+fidRegion);
                if(null!=map && !map.isEmpty()){
                    return Result.OK(map);
                }
            }
        }catch (Exception e){
            e.getMessage();
        }


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
        redisUtils.set(format+"_unitsChart_"+fidRegion,map,12L);
        return Result.OK(map);
    }

    //超标分析-超标次数构成
    @AutoLog(value = "超标分析-超标次数构成")
    @ApiOperation(value="超标分析-超标次数构成", notes="超标分析-超标次数构成")
    @GetMapping(value = "/moreNum")
    public Result<?> moreNum( @RequestParam(name="format",required=false) String format) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }
        try {
            Map<String,Object>map = (Map<String,Object>)redisUtils.get(format+"_moreNum_"+fidRegion);
            if(null!=map && !map.isEmpty()){
                return Result.OK(map);
            }
        }catch (Exception e){
            e.getMessage();
        }

        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
        Map<String,Object>map = analyseService.moreNum(tableLampblack,fidRegion,format,"","");
        redisUtils.set(format+"_moreNum_"+fidRegion,map,12L);
        return Result.OK(map);
    }

    //超标分析-超标次数构成-折线图
    @AutoLog(value = "超标分析-超标次数构成-折线图")
    @ApiOperation(value="超标分析-超标次数构成-折线图", notes="超标分析-超标次数构成-折线图")
    @GetMapping(value = "/moreNumChart")
    public Result<?> moreNumChart(
            @RequestParam(name="format",required=false) String format,
            @RequestParam(name="companyId",required=false) String companyId) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
        if(StringUtils.isEmpty(companyId)) {
            companyId = "";
        }

        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }

        try {
            if(fidRegion==131000){
                Map<String, List<Object>>map = (Map<String, List<Object>>)redisUtils.get(format+"_moreNumChart_"+fidRegion+companyId);
                if(null!=map && !map.isEmpty()){
                    return Result.OK(map);
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

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

        //Collections.reverse(dax);
        //Collections.reverse(data1);

        map.put("dax",dax);
        map.put("data",data1);

        redisUtils.set(format+"_moreNumChart_"+fidRegion+companyId,map);
        return Result.OK(map);
    }

    //运行分析-运行时长
    @AutoLog(value = "运行分析-运行时长")
    @ApiOperation(value="运行分析-运行时长", notes="运行分析-运行时长")
    @GetMapping(value = "/runHourTime")
    public Result<?> runHourTime(
            @RequestParam(name="format",required=false) String format,
            String companyId) {
        if(StringUtils.isEmpty(companyId)) {
            companyId = "";
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }
        try {
            if(fidRegion==131000){
                Map<String, List<Object>>map = (Map<String, List<Object>>)redisUtils.get(format+"_runHourTime_"+fidRegion+companyId);
                if(null!=map && !map.isEmpty()){
                    return Result.OK(map);
                }
            }

        }catch (Exception e){
            e.getMessage();
        }

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
        return Result.OK(map);
    }

    //运行分析-三个折线图
    @AutoLog(value = "告警分析-三个折线图")
    @ApiOperation(value="告警分析-三个折线图", notes="告警分析-三个折线图")
    @GetMapping(value = "/alarmAnalysis")
    public Result<?> alarmAnalysis(
            @RequestParam(name="format",required=false) String format,
            String companyId) {
        if(StringUtils.isEmpty(companyId)) {
            companyId = "";
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
        if(StringUtils.isEmpty(format)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            format = sdf.format(new Date());
        }
        try {
            if(fidRegion==131000){
                Map<String, List<Object>>map = (Map<String, List<Object>>)redisUtils.get(format+"_alarmAnalysis_"+fidRegion+companyId);
                if(null!=map && !map.isEmpty()){
                    return Result.OK(map);
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

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
        redisUtils.set(format+"_alarmAnalysis_"+fidRegion+companyId,map,12L);
        return Result.OK(map);
    }

}
