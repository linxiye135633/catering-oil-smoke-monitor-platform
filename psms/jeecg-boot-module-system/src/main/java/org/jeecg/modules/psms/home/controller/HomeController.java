package org.jeecg.modules.psms.home.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.psms.home.service.HomeService;
import org.jeecg.modules.psms.supervision.service.SupervisionService;
import org.jeecg.modules.utils.ToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="psms-1-首页-home")
@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;
    @Autowired
    private SupervisionService supervisionService;
    /**
     * 餐饮企业接入数
     */
    @AutoLog(value = "餐饮企业接入数")
    @ApiOperation(value="餐饮企业接入数", notes="餐饮企业接入数")
    @GetMapping(value = "/statCompany")
    public Result<?> statCompany() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        if(fidRegion==1024){
            fidRegion = 0;
        }
        Map<String,Object> pageLists = homeService.statCompany(fidRegion);
        return Result.OK(pageLists);
    }

    /**
     * 排口测点数
     */
    @AutoLog(value = "排口测点数")
    @ApiOperation(value="排口测点数", notes="排口测点数")
    @GetMapping(value = "/pointCompany")
    public Result<?> pointCompany() {
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        if(fidRegion==1024){
            fidRegion = 0;
        }

        Map<String,Object> pageLists = homeService.pointCompany(fidRegion);
        return Result.OK(pageLists);
    }

    /**
     * 上报数据流量
     */
    @AutoLog(value = "上报数据流量")
    @ApiOperation(value="上报数据流量", notes="上报数据流量")
    @GetMapping(value = "/lampblackRateFlow")
    public Result<?> lampblackRateFlow() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        String table = "bu_lampblack_data";
        if(fidRegion!=1024){
            table = table + "_"+ fidRegion;
        }
        Map<String,Object> pageLists = homeService.lampblackRateFlow(table);
        return Result.OK(pageLists);
    }

    /**
     * 测点联网率
     */
    @AutoLog(value = "测点联网率")
    @ApiOperation(value="测点联网率", notes="测点联网率")
    @GetMapping(value = "/pointRateFlow")
    public Result<?> pointRateFlow(float hours) {//传递
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        String table = "bu_lampblack_data";
        if(fidRegion!=1024){
            table = table + "_"+ fidRegion+ "_today";
        }else {
            fidRegion = 0;
        }

        Map<String,Object> pageLists = homeService.pointRateFlow(table,fidRegion,String.valueOf(hours));
        return Result.OK(pageLists);
    }

    /**
     * 餐饮企业接入趋势
     */
    @AutoLog(value = "餐饮企业接入趋势")
    @ApiOperation(value="餐饮企业接入趋势", notes="餐饮企业接入趋势")
    @GetMapping(value = "/statiJoinYear")
    public Result<?> statiJoinYear() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        Map<String, List<Object>>map = homeService.statiJoinYear(fidRegion);
        return Result.OK(map);
    }

    /**
     * 本月安装接入进度
     */
    @AutoLog(value = "本月安装接入进度")
    @ApiOperation(value="本月安装接入进度", notes="本月安装接入进度")
    @GetMapping(value = "/getRateProgress")
    public Result<?> getRateProgress() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        Map<String,Object>map = homeService.getRateProgress(fidRegion);
        return Result.OK(map);
    }

    /**
     * 测点设备
     */
    @AutoLog(value = "测点设备")
    @ApiOperation(value="测点设备", notes="本月安装接入进度-测点")
    @GetMapping(value = "/getEquipmentPoint")
    public Result<?> getEquipmentPoint() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        String tableLampblack = "bu_lampblack_data";
        String tablePurifier = "bu_purifier_powerdata";
        if(fidRegion!=1024){
            tableLampblack = tableLampblack + "_"+ fidRegion;
            tablePurifier = tablePurifier + "_"+ fidRegion;
        }else {
            fidRegion = 0;
        }

        Map<String,Object>map = homeService.getEquipmentPoint(tableLampblack,tablePurifier,fidRegion);
        return Result.OK(map);
    }

    /**
     * 餐饮企业-分类统计
     */
    @AutoLog(value = "餐饮企业-分类统计")
    @ApiOperation(value="餐饮企业-分类统计", notes="餐饮企业-分类统计")
    @GetMapping(value = "/companyStat")
    public Result<?> companyStat() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        Map<String,Map<String,Object>>map = homeService.companyStat(fidRegion);
        return Result.OK(map);
    }


    //首页-异常警告条目数
    @AutoLog(value = "首页-异常警告条目数")
    @ApiOperation(value="首页-异常警告条目数", notes="首页-异常警告条目数")
    @GetMapping(value = "/getLabel")
    public Result<?> getLabel() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-1);
        Date date =  c.getTime();
        String format = sdf.format(date);
        Map<String,Object> map = new LinkedHashMap<>();
        Map<String,Object> map1 = supervisionService.getLabel(String.valueOf(fidRegion),format);
        map.put("a1",map1.get("a2"));//设备未联动开启告警
        map.put("a2",map1.get("a1"));//营业时段停机告警
        map.put("a3",map1.get("a6"));//电场工况
        map.put("a4",map1.get("a5"));//排放超标异常告警
        return Result.OK(map);
    }

    //首页-当月数据流量
    @AutoLog(value = "首页-当月数据流量")
    @ApiOperation(value="首页-当月数据流量", notes="首页-当月数据流量")
    @GetMapping(value = "/getFlow")
    public Result<?> getFlow() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        String tableLampblack = "bu_lampblack_data" + "_" + fidRegion;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        List<String>days = ToolsUtils.getDateList(format);
        List<Object> dax = new LinkedList<>();
        List<Object> data = new LinkedList<>();
        Map<String, List<Object>>map = new LinkedHashMap<>();
        for (int i=0;i<days.size();i++) {
            String formatDay = days.get(i);
            dax.add(i+1);
            int num = homeService.getFlow(tableLampblack, fidRegion, formatDay);
            float n = ((float) num)/1000;
            data.add(Math.round(n));
        }
        map.put("dataX",dax);
        map.put("data",data);
        return Result.OK(map);
    }
}
