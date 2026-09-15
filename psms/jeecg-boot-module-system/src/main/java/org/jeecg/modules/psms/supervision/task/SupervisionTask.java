package org.jeecg.modules.psms.supervision.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.demo.companytype.entity.CompanyType;
import org.jeecg.modules.demo.companytype.service.CompanyTypeService;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.service.PointService;
import org.jeecg.modules.psms.supervision.service.SupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class SupervisionTask {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PointService pointService;

    @Autowired
    private CompanyTypeService companyTypeService;

    @Autowired
    private SupervisionService supervisionService;
    //每隔5秒执行一次："*/5 * * * * ?"
    //每隔1分钟执行一次："0 */1 * * * ?"
    //每日凌晨两点统计 昨天的告警企业信息
    //告警企业整体流程需要重新优化和测试
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        log.info("start-监视监管");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-1);
        Date date =  c.getTime();
        String format = sdf.format(date);

        List<Company> companyData = companyService.list();
        for(Company company:companyData){
            //1=营业时段停机标签
            stopMac(company,format);
            //2=没有联动开启标签
            linkage(company,format);
            //3-二次电压过低标签
            secondaryVoltage(company,format);
            //4-设备联网异常标签
            network(company,format);
            //5-烟气排放超标标签
            smoke(company,format);
            //6-高压电场异常标签
            highVoltage(company,format);
            //7-设备压差异常标签
            differentialPressure(company,format);
            //8-其他原因异常标签
            otherUnusual(company,format);
        }

        log.info("end-监视监管");
    }

    //停机警告  查询餐饮企业表中营业时间区间 在这个区间内
    private void stopMac(Company company,String format){
        try{
            String id = company.getId();
            int startHour = company.getStartHour();
            int startMinute  = company.getStartMinute();
            int endHour = company.getEndHour();
            int endMinute = company.getEndMinute();
            if(startHour>0 && startMinute>0 && endHour>0 && endMinute>0){
                String startTime = format+" "+ startHour + ":" + startMinute;
                String endTime = format+" "+ endHour + ":" + endMinute;
                List<Map<String,Object>> listMap = supervisionService.stopMac(id,startTime,endTime);
                listMap.forEach(map->{
                    int num = Integer.parseInt(String.valueOf(map.get("num")));
                    String mac = String.valueOf(map.get("mac"));
                    saveCompanyTyp(company, num, mac,format,1);
                });
            }else {
                log.info("餐饮企业营业时间不详 无法统计-营业时段停机状态");
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    //联动开机 fan_status=1 purifier_status=1 差值
    private void linkage(Company company,String format){
        try{
            String fidRegion = String.valueOf(company.getFidRegion());
            String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
            String companyId = company.getId();
            Map<String,Object> LampblackMap = supervisionService.getLampblackCount(tableLampblack,companyId,format);
            String mac = String.valueOf(LampblackMap.get("mac"));
            int LampblackCount = Integer.parseInt(String.valueOf(LampblackMap.get("fan")));
            int purifierCount = Integer.parseInt(String.valueOf(LampblackMap.get("purifier")));
            int num = Math.abs(LampblackCount-purifierCount);
            if(num>5){//大于10则提示未联动开启
                saveCompanyTyp(company, num, mac,format,2);
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    //二次电压  净化器表数据 二次电压小于10 则异常
    private void secondaryVoltage(Company company,String format){
        try{
            String fidRegion = String.valueOf(company.getFidRegion());
            String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
            String tablePurifier = "bu_purifier_powerdata" + "_"+ fidRegion;
            String companyId = company.getId();
            Map<String,Object>map = supervisionService.secondaryVoltage(tableLampblack,tablePurifier,companyId,format);
            int num = Integer.parseInt(String.valueOf(map.get("num")));
            if(num>0){
                String mac = String.valueOf(map.get("mac"));
                saveCompanyTyp(company, num, mac,format,3);
            }
        }catch (Exception e){
            e.getMessage();
        }

    }

    //设备联网  日期内没有数据
    private synchronized void network(Company company,String format){
        try{
            String fidRegion = String.valueOf(company.getFidRegion());
            String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
            String companyId = company.getId();
            QueryWrapper<Point> query = new QueryWrapper<>();
            query.eq("company_id",companyId);
            query.groupBy("point_mac");
            List<Point>listPoint = pointService.list(query);
            List<String>macs = new LinkedList<>();
            for(Point p:listPoint){
                String m = p.getPointMac();
                macs.add(m);
            }
            int n = supervisionService.getNetwork(tableLampblack,companyId,format);
            if(n>0){
                String mac = String.valueOf(macs.get(0));
                saveCompanyTyp(company, n, mac,format,4);
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    //烟气排放超标 数值有一个超标则异常
    private void smoke(Company company,String format){
        try{
            String fidRegion = String.valueOf(company.getFidRegion());
            String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
            String companyId = company.getId();
            Map<String, Object> map = supervisionService.smokeCount(tableLampblack,companyId,format);
            int num = Integer.parseInt(String.valueOf(map.get("num")));
            if(num>0){
                String mac = String.valueOf(map.get("mac"));
                saveCompanyTyp(company, num, mac,format,5);
            }
        }catch (Exception e){
            e.getMessage();
        }

    }

    //高压电场异常标签 净化器表电场字段无数据 则异常
    private void highVoltage(Company company,String format){
        try{
            String fidRegion = String.valueOf(company.getFidRegion());
            String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
            String tablePurifier = "bu_purifier_powerdata" + "_"+ fidRegion;
            String companyId = company.getId();
            Map<String,Object>map = supervisionService.highVoltage(tableLampblack,tablePurifier,companyId,format);
            int num = Integer.parseInt(String.valueOf(map.get("num")));
            if(num>0){
                String mac = String.valueOf(map.get("mac"));
                saveCompanyTyp(company, num, mac,format,6);
            }
        }catch (Exception e){
            e.getMessage();
        }

    }

    //设备压差异常标签
    private void differentialPressure(Company company,String format){

    }

    //其他异常标签
    private void otherUnusual(Company company,String format){

    }

    private void saveCompanyTyp(Company company, int num, String mac,String format,int type){
        try {
            CompanyType bean = new CompanyType();
            bean.setName(company.getName());
            bean.setAlarmType(type);
            bean.setCreateTime(new Date());
            bean.setPointMac(mac);
            bean.setAmount(num);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = simpleDateFormat.parse(format + " " + "00:00:00");
            Date endDate = simpleDateFormat.parse(format + " " + "23:59:59");
            bean.setStartTime(startDate);
            bean.setEndTime(endDate);
            bean.setFidRegion(String.valueOf(company.getFidRegion()));
            companyTypeService.save(bean);
        }catch (Exception e){
            e.getMessage();
        }
    }

}
