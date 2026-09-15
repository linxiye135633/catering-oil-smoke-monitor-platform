package org.jeecg.modules.psms.supervision.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.companyaudit.entity.BaseCompanyAudit;
import org.jeecg.modules.demo.companyaudit.service.IBaseCompanyAuditService;
import org.jeecg.modules.demo.companytype.entity.CompanyType;
import org.jeecg.modules.demo.companytype.service.CompanyTypeService;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.psms.supervision.service.SupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags="psms-3-监督监管-supervision")
@Slf4j
@RestController
@RequestMapping("/supervision")
public class SupervisionController {

    @Autowired
    private SupervisionService supervisionService;

    @Autowired
    private CompanyTypeService companyTypeService;

    @Autowired
    private IBaseCompanyAuditService baseCompanyAuditService;

    //企业治理-风险数值
    @AutoLog(value = "a企业治理-风险数值")
    @ApiOperation(value="a企业治理-风险数值", notes="a企业治理-风险数值")
    @GetMapping(value = "/getRiskCount")
    public Result<?> getRiskCount() {
        Map<String,Object> map = new LinkedHashMap<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        Map<String,Object>risk1 = supervisionService.getRiskCount(format,0,String.valueOf(fidRegion));
        Map<String,Object>risk2 = supervisionService.getRiskCount("",0,String.valueOf(fidRegion));
        map.put("a1",risk1.get("a1"));
        map.put("b1",risk2.get("a1"));

        map.put("a2",risk1.get("a2"));
        map.put("b2",risk2.get("a2"));

        map.put("a3",risk1.get("a3"));
        map.put("b3",risk2.get("a3"));

        int a4 = supervisionService.getRiskVCount(format,1,String.valueOf(fidRegion));
        int b4 = supervisionService.getRiskVCount("",1,String.valueOf(fidRegion));
        map.put("a4",a4);
        map.put("b4",b4);
        return Result.OK(map);
    }

    //企业治理-折线图
    @AutoLog(value = "a企业治理-折线图")
    @ApiOperation(value="a企业治理-折线图", notes="a企业治理-折线图")
    @GetMapping(value = "/getRiskLineChart")
    public Result<?> getRiskLineChart() {

        Map<String,List<Object>>map = new LinkedHashMap<>();
        List<Object> dax = new LinkedList<>();
        List<Object> data1 = new LinkedList<>();
        List<Object> data2 = new LinkedList<>();
        List<Object> data3 = new LinkedList<>();

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for(int i = 0;i<12;i++){
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH,-i);
            Date date =  c.getTime();
            String format = sdf.format(date);
            dax.add(format);
            Map<String,Object>risk = supervisionService.getRiskCount(format,0,String.valueOf(fidRegion));
            data1.add(risk.get("a1"));
            data2.add(risk.get("a2"));
            data3.add(risk.get("a3"));
        }

        Collections.reverse(dax);
        Collections.reverse(data1);
        Collections.reverse(data2);
        Collections.reverse(data3);

        map.put("dax",dax);
        map.put("data1",data1);
        map.put("data2",data2);
        map.put("data3",data3);
        return Result.OK(map);
    }

    //企业治理-新增风控企业列表
    @AutoLog(value = "a企业治理-新增风控企业列表")
    @ApiOperation(value="a企业治理-新增风控企业列表", notes="a企业治理-新增风控企业列表")
    @GetMapping(value = "/getNewRisk")
    public Result<?> getNewRisk(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        Page<Map<String, Object>> listMap = supervisionService.getNewRisk(page,fidRegion,format);
        return Result.OK(listMap);
    }

    //企业治理-解除风险控制
    @AutoLog(value = "a企业治理-解除风险控制")
    @ApiOperation(value="a企业治理-解除风险控制", notes="a企业治理-解除风险控制")
    @GetMapping(value = "/getClearRisk")
    public Result<?> getClearRisk(String name){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        supervisionService.getClearRisk(fidRegion,format,name);
        return Result.OK("接触成功");
    }

    //企业治理-已接触风险控制列表
    @AutoLog(value = "a企业治理-已解除风险控制列表")
    @ApiOperation(value="a企业治理-已解除风险控制列表", notes="a企业治理-已解除风险控制列表")
    @GetMapping(value = "/getClearRiskList")
    public Result<?> getClearRiskList(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        Page<Map<String, Object>> listMap= supervisionService.getClearRiskList(page,fidRegion,format);
        return Result.OK(listMap);
    }

    //b企业标签-获取标签
    @AutoLog(value = "b企业标签-获取标签")
    @ApiOperation(value="b企业标签-获取标签", notes="b企业标签-获取标签")
    @GetMapping(value = "/getLabel")
    public Result<?> getLabel() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-1);
        Date date =  c.getTime();
        String format = sdf.format(date);
        Map<String,Object> map = new LinkedHashMap<>();
        Map<String,Object> map1 = supervisionService.getLabel(String.valueOf(fidRegion),format);
        Map<String,Object> map2 = supervisionService.getLabel(String.valueOf(fidRegion),"");
        map.put("a1",map1.get("a1"));
        map.put("b1",map2.get("a1"));

        map.put("a2",map1.get("a2"));
        map.put("b2",map2.get("a2"));

        map.put("a3",map1.get("a3"));
        map.put("b3",map2.get("a3"));

        map.put("a4",map1.get("a4"));
        map.put("b4",map2.get("a4"));

        map.put("a5",map1.get("a5"));
        map.put("b5",map2.get("a5"));

        map.put("a6",map1.get("a6"));
        map.put("b6",map2.get("a6"));

        map.put("a7",map1.get("a7"));
        map.put("b7",map2.get("a7"));

        map.put("a8",map1.get("a8"));
        map.put("b8",map2.get("a8"));
        return Result.OK(map);
    }

    //b企业标签-标签列表
    @AutoLog(value = "b企业标签-标签列表")
    @ApiOperation(value="b企业标签-标签列表", notes="b企业标签-标签列表")
    @GetMapping(value = "/getListLabel")
    public Result<?> getListLabel(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        Page<CompanyType> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CompanyType> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.eq("is_on",0);
        query.orderByDesc("create_time");
        //query.apply("DATE_FORMAT(create_time,'%Y-%m-%d') = '"+format+"'");
        IPage<CompanyType> pageList = companyTypeService.page(page, query);
        return Result.OK(pageList);
    }


    //c告警反馈-统计数量
    @AutoLog(value = "c告警反馈-统计数量")
    @ApiOperation(value="c告警反馈-统计数量", notes="c告警反馈-统计数量")
    @GetMapping(value = "/getAlarmCount")
    public Result<?> getAlarmCount() {
        Map<String,Object> map = new LinkedHashMap<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());

        int a1 = supervisionService.getRiskVCount(format,0,String.valueOf(fidRegion));
        int b1 = supervisionService.getRiskVCount("",0,String.valueOf(fidRegion));

        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM");
        String formatv = sdfs.format(new Date());
        QueryWrapper<BaseCompanyAudit> queryPoint = new QueryWrapper<>();
        queryPoint.eq("audit_type",1);//1=申诉 2=报备
        queryPoint.apply("DATE_FORMAT(create_time,'%Y-%M') = '"+formatv+"'");
        int a2 = baseCompanyAuditService.count(queryPoint);
        int b2 = baseCompanyAuditService.count();

        queryPoint.clear();
        queryPoint.eq("audit_type",2);//1=申诉 2=报备
        queryPoint.apply("DATE_FORMAT(create_time,'%Y-%M') = '"+formatv+"'");
        int a3 = baseCompanyAuditService.count(queryPoint);
        int b3 = baseCompanyAuditService.count();

        map.put("a1",a1);
        map.put("b1",b1);

        map.put("a2",a2);
        map.put("b2",b2);

        map.put("a3",a3);
        map.put("b3",b3);
        return Result.OK(map);
    }

    //c告警反馈-告警企业列表
    @AutoLog(value = "c告警反馈-告警企业列表")
    @ApiOperation(value="c告警反馈-告警企业列表", notes="c告警反馈-告警企业列表")
    @GetMapping(value = "/getAlarmList")
    public Result<?> getAlarmList(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();

        Page<CompanyType> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CompanyType> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.eq("is_on","0");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        query.apply("DATE_FORMAT(create_time,'%Y-%m') = '"+format+"'");
        query.orderByDesc("create_time");
        IPage<CompanyType> pageList = companyTypeService.page(page, query);
        return Result.OK(pageList);
    }

    //c告警反馈-申诉
    @AutoLog(value = "c告警反馈-申诉")
    @ApiOperation(value="c告警反馈-申诉", notes="c告警反馈-申诉")
    @PostMapping(value = "/alarmAdd")
    public Result<?> alarmAdd(@RequestBody BaseCompanyAudit baseCompanyAudit) {
        baseCompanyAudit.setStatus("0");
        if(StringUtils.isEmpty(baseCompanyAudit.getCompanyId())){
            return Result.error("缺少必传参数companyId");
        }
        if(StringUtils.isEmpty(baseCompanyAudit.getAuditId())){
            return Result.error("缺少必传参数AuditId");
        }
        if(null==baseCompanyAudit.getAuditStartTime()){
            return Result.error("缺少必传参数AuditStartTime");
        }
        if(null==baseCompanyAudit.getAuditEndTime()){
            return Result.error("缺少必传参数AuditEndTime");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        baseCompanyAudit.setFidRegion(String.valueOf(fidRegion));
        baseCompanyAuditService.save(baseCompanyAudit);
        return Result.OK("添加成功！");
    }

    //c告警反馈-删除
    @AutoLog(value = "c告警反馈-删除")
    @ApiOperation(value="c告警反馈-删除", notes="c告警反馈-删除")
    @DeleteMapping(value = "/alarmDelete")
    public Result<?> alarmDelete(@RequestParam(name="id",required=true) String id) {
        //baseCompanyAuditService.removeById(id);
        companyTypeService.removeById(id);
        return Result.OK("删除成功!");
    }

    //c告警反馈-审核是否通过
    @AutoLog(value = "c告警反馈-审核是否通过")
    @ApiOperation(value="c告警反馈-审核是否通过", notes="c告警反馈-审核是否通过")
    @GetMapping(value = "/getAlarmPass")
    public Result<?> getAlarmPass(
            @RequestBody BaseCompanyAudit baseCompanyAudit) {
        //status = 0/null未审核 1待审核 2不通过 3审核通过
        String status = baseCompanyAudit.getStatus();
        if(!status.equals("2") && !status.equals("3")){
            Result.error("未添加是否通过状态!");
        }
        baseCompanyAudit.setUpdateTime(new Date());
        baseCompanyAuditService.updateById(baseCompanyAudit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String companyId = baseCompanyAudit.getCompanyId();
        String startTime = sdf.format(baseCompanyAudit.getAuditStartTime());
        String endTime = sdf.format(baseCompanyAudit.getAuditEndTime());
        supervisionService.updateAlarm(companyId,startTime,endTime);
        return Result.OK("设置成功");
    }

    //c告警反馈-待审核告警反馈（申诉）
    @AutoLog(value = "c告警反馈-待审核告警反馈（申诉）")
    @ApiOperation(value="c告警反馈-待审核告警反馈（申诉）", notes="c告警反馈-待审核告警反馈（申诉）")
    @GetMapping(value = "/getAlarmLists1")
    public Result<?> getAlarmLists1(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        QueryWrapper<BaseCompanyAudit> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.eq("audit_type",1);//1=申诉 2=报备
        query.ne("status",1);
        query.ne("status",2);
        query.ne("status",3);
        query.apply("DATE_FORMAT(create_time,'%Y-%M') = '"+format+"'");
        Page<BaseCompanyAudit> page = new Page<BaseCompanyAudit>(pageNo, pageSize);
        IPage<BaseCompanyAudit> pageList = baseCompanyAuditService.page(page, query);
        return Result.OK(pageList);
    }
    //c告警反馈-待审核报备申请
    @AutoLog(value = "c告警反馈-待审核报备申请")
    @ApiOperation(value="c告警反馈-待审核报备申请", notes="c告警反馈-待审核报备申请")
    @GetMapping(value = "/getAlarmLists2")
    public Result<?> getAlarmLists2(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        QueryWrapper<BaseCompanyAudit> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.eq("audit_type",2);//1=申诉 2=报备
        query.ne("status",1);
        query.ne("status",2);
        query.ne("status",3);
        query.apply("DATE_FORMAT(create_time,'%Y-%M') = '"+format+"'");
        Page<BaseCompanyAudit> page = new Page<BaseCompanyAudit>(pageNo, pageSize);
        IPage<BaseCompanyAudit> pageList = baseCompanyAuditService.page(page, query);
        return Result.OK(pageList);
    }

    //c告警反馈-已审核告警反馈（申诉）
    @AutoLog(value = "c告警反馈-已审核告警反馈（申诉）")
    @ApiOperation(value="c告警反馈-已审核告警反馈（申诉）", notes="c告警反馈-已审核告警反馈（申诉）")
    @GetMapping(value = "/getAlarmLists3")
    public Result<?> getAlarmLists3(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        QueryWrapper<BaseCompanyAudit> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.eq("audit_type",1);//1=申诉 2=报备
        query.and(q-> q.eq("status",1).or().eq("status",2).or().eq("status",3));
        query.apply("DATE_FORMAT(create_time,'%Y-%M') = '"+format+"'");
        Page<BaseCompanyAudit> page = new Page<BaseCompanyAudit>(pageNo, pageSize);
        IPage<BaseCompanyAudit> pageList = baseCompanyAuditService.page(page, query);
        return Result.OK(pageList);
    }

    //c告警反馈-已审核报备申请
    @AutoLog(value = "c告警反馈-已审核报备申请")
    @ApiOperation(value="c告警反馈-已审核报备申请", notes="c告警反馈-已审核报备申请")
    @GetMapping(value = "/getAlarmLists4")
    public Result<?> getAlarmLists4(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize){
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        QueryWrapper<BaseCompanyAudit> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.eq("audit_type",1);//1=申诉 2=报备
        query.and(q-> q.eq("status",1).or().eq("status",2).or().eq("status",3));
        query.apply("DATE_FORMAT(create_time,'%Y-%M') = '"+format+"'");
        Page<BaseCompanyAudit> page = new Page<BaseCompanyAudit>(pageNo, pageSize);
        IPage<BaseCompanyAudit> pageList = baseCompanyAuditService.page(page, query);
        return Result.OK(pageList);
    }
}
