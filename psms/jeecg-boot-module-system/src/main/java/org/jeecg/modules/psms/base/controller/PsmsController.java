package org.jeecg.modules.psms.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xkcoding.http.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.service.PointService;
import org.jeecg.modules.psms.base.service.PsmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Api(tags="psms-2-在线检测-即时接入-base")
@Slf4j
@RestController
@RequestMapping("/base")
public class PsmsController {

    @Autowired
    private PsmsService psmsService;
    @Autowired
    private PointService pointService;
    @Autowired
    private CompanyService companyService;

    /**
     * 根据餐饮企业ID查询全部数据
     */
    @AutoLog(value = "根据餐饮企业ID查询全部数据-分页列表查询")
    @ApiOperation(value="根据餐饮企业ID查询全部数据-分页列表查询", notes="根据餐饮企业ID查询全部数据-分页列表查询")
    @GetMapping(value = "/getAllId")
    public Result<?> queryPageList(@RequestParam(name="companyid",required=false) String companyid,
                                   @RequestParam(name="pointid",required=false) String pointid,
                                   @RequestParam(name="startTime",required=false) String startTime,
                                   @RequestParam(name="endTime",required=false) String endTime,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

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

        Page page = new Page(pageNo, pageSize);
        Page pageList = psmsService.getYouYanList(page, tableLampblack, tablePurifier,fidRegion,
                companyid,pointid,startTime,endTime, pageNo, pageSize);
        return Result.OK(pageList);
    }

    /**
     * 根据餐饮企业ID查询测点数据-分页列表查询
     */
    @AutoLog(value = "根据餐饮企业ID查询测点数据-分页列表查询")
    @ApiOperation(value="根据餐饮企业ID查询测点数据-分页列表查询", notes="根据餐饮企业ID查询测点数据-分页列表查询")
    @GetMapping(value = "/getPointByCompanyId")
    public Result<?> getPointByCompanyId(@RequestParam(name="companyid") String companyid,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id", companyid);
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }
        Page<Point> page = new Page<>(pageNo, pageSize);
        IPage<Point> pageList = pointService.page(page, queryWrapper);
        return Result.OK(pageList);
    }
    /**
     * 据餐饮企业name查询测点数据-分页列表查询
     */
    @AutoLog(value = "根据餐饮企业name查询测点数据-分页列表查询")
    @ApiOperation(value="根据餐饮企业name查询测点数据-分页列表查询", notes="根据餐饮企业name查询测点数据-分页列表查询")
    @GetMapping(value = "/getPointByCompanyName")
    public Result<?> getPointByCompanyName(
            @RequestParam(name="name") String name,
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        //door_name
        List<Company> pageList = companyService.list(queryWrapper);
        List<String>ids = new LinkedList<>();
        pageList.forEach(item->{
            String id = item.getId();
            ids.add(id);
        });

        QueryWrapper<Point> query = new QueryWrapper<>();
        query.in("company_id", ids);
        if(fidRegion!=1024){
            query.eq("fid_region",fidRegion);
        }
        Page<Point> page = new Page<>(pageNo, pageSize);
        IPage<Point> pageLists = pointService.page(page, query);
        return Result.OK(pageLists);
    }

    /**
     * 查询最新一条油烟数据-分页列表查询
     */
    @AutoLog(value = "查询最新一条油烟数据-分页列表查询")
    @ApiOperation(value="查询最新一条油烟数据-分页列表查询", notes="查询最新一条油烟数据-分页列表查询")
    @GetMapping(value = "/getNewestCreateTime")
    public Result<?> getNewestCreateTime(
            @RequestParam(name="id",required=false) String id,
            @RequestParam(name="hour",required=false) String hour,//1 2 3小时区间
            @RequestParam(name="searchName",required=false) String searchName,
            @RequestParam(name="pointMac",required=false) String pointMac,
            @RequestParam(name="startTime",required=false) String startTime,
            @RequestParam(name="endTime",required=false) String endTime,
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        List<String>ids = new LinkedList<>();
        if(StringUtil.isNotEmpty(id)){
            ids.add(id);
        }
        if(StringUtil.isNotEmpty(searchName)){
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("name", searchName);
            List<Company> pageList = companyService.list(queryWrapper);
            pageList.forEach(item->{
                String idv = item.getId();
                if(!ids.contains(id)){
                    ids.add(idv);
                }
            });
        }

        String tableLampblack = "bu_lampblack_data";
        String tablePurifier = "bu_purifier_powerdata";
        if(fidRegion!=1024){
            tableLampblack = tableLampblack + "_"+ fidRegion;
            tablePurifier = tablePurifier + "_"+ fidRegion;
        }else {
            fidRegion = 0;
        }
        /*
        if(StringUtils.isEmpty(id) && StringUtils.isEmpty(hour) && StringUtils.isEmpty(pointMac)){
            tableLampblack = tableLampblack + "_now";
            tablePurifier = tablePurifier + "_now";
        }else {
            tableLampblack = tableLampblack + "_today";
            tablePurifier = tablePurifier + "_today";
        }*/

        if(StringUtils.isNotEmpty(hour) || StringUtils.isNotEmpty(startTime) || StringUtils.isNotEmpty(endTime)){
            tableLampblack = tableLampblack + "_today";
            tablePurifier = tablePurifier + "_today";
        }else {
            tableLampblack = tableLampblack + "_now";
            tablePurifier = tablePurifier + "_now";
            Page<Map<String,Object>> page = new Page<>(pageNo, pageSize);
            IPage<Map<String,Object>> pageLists = psmsService.getNewestCreateTime2(page, tableLampblack, tablePurifier, fidRegion,ids,pointMac);
            return Result.OK(pageLists);
        }

        Page<Map<String,Object>> page = new Page<>(pageNo, pageSize);
        IPage<Map<String,Object>> pageLists = psmsService.getNewestCreateTime(page, tableLampblack, tablePurifier, fidRegion,ids,hour, pointMac,startTime,endTime);

        return Result.OK(pageLists);
    }
    /**
     * 查询最新一条油烟数据-分页列表查询
     */
    @AutoLog(value = "根据mac地址获取最新一条数据信息")
    @ApiOperation(value="根据mac地址获取最新一条数据信息", notes="根据mac地址获取最新一条数据信息")
    @GetMapping(value = "/getNowTimeDataForMac")
    public Result<?> getNowTimeDataForMac(@RequestParam(name="pointMac",required=true) String pointMac){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        String tableLampblack = "bu_lampblack_data";
        String tablePurifier = "bu_purifier_powerdata";
        if(fidRegion!=1024){
            tableLampblack = tableLampblack + "_" + fidRegion + "_now";
            tablePurifier = tablePurifier + "_" + fidRegion + "_now";
        }else {
            fidRegion = 0;
        }
        List<Map<String,Object>>pageLists = psmsService.getNowTimeDataForMac(tableLampblack,tablePurifier,pointMac,fidRegion);
        return Result.OK(pageLists);
    }


    //运行状态-折线图
    @AutoLog(value = "运行状态-折线图")
    @ApiOperation(value="运行状态-折线图", notes="运行状态-折线图")
    @GetMapping(value = "/getStatByCreateTime")
    public Result<?> getStatByCreateTime(String pointMac, String createTime){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        String tableLampblack = "bu_lampblack_data";
        if(fidRegion!=1024){
            tableLampblack = tableLampblack + "_"+ fidRegion + "_today";
        }

        Map<String,List<String>>map = psmsService.getStatByCreateTime(tableLampblack,pointMac,createTime);
        return Result.OK(map);
    }

    /**
     * app详情
     */
    @AutoLog(value = "app详情")
    @ApiOperation(value="app详情", notes="app详情")
    @GetMapping(value = "/getDetailsByTime")
    public Result<?> getDetailsByTime(String id,String hour) {//1 2 3小时区间

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        String tableLampblack = "bu_lampblack_data";
        if(fidRegion!=1024){
            tableLampblack = tableLampblack + "_"+ fidRegion + "_today";
        }

        Map<String,Object> map = psmsService.getDetailsByTime(tableLampblack,id,hour);
        return Result.OK(map);
    }

    /**
     * 告警事件-在线检测
     */
    @AutoLog(value = "告警事件-在线检测")
    @ApiOperation(value="告警事件-在线检测", notes="告警事件-在线检测")
    @GetMapping(value = "/getWarnEvent")
    public Result<?> getWarnEvent (@RequestParam(name="id",required=false) String id,
                                   @RequestParam(name="pointId",required=false) String pointId) {//1 2 3小时区间

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        String tableLampblack = "bu_lampblack_data";
        if(fidRegion!=1024){
            tableLampblack = tableLampblack + "_"+ fidRegion + "_today";
        }

        Map<String,List<Map<String,Object>>> map = psmsService.getWarnEvent(tableLampblack,id,pointId,fidRegion);

        return Result.OK(map);
    }

    /**
     * 地图概览-餐饮企业超标
     */
    @AutoLog(value = "地图概览-餐饮企业超标")
    @ApiOperation(value="地图概览-餐饮企业超标", notes="地图概览-餐饮企业超标")
    @GetMapping(value = "/statCompanyExcessive")
    public Result<?> statCompanyExcessive() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();

        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion + "_today";
        Map<String,Object>map = psmsService.statCompanyExcessive(tableLampblack,fidRegion);
        return Result.OK(map);
    }

    /**
     * 地图概览-餐饮企业超标
     */
    @AutoLog(value = "地图概览-测点超标")
    @ApiOperation(value="地图概览-测点超标", notes="地图概览-测点超标")
    @GetMapping(value = "/statPointExcessive")
    public Result<?> statPointExcessive() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();

        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion + "_today";
        Map<String,Object>map = psmsService.statPointExcessive(tableLampblack,fidRegion);
        return Result.OK(map);
    }


}


