package org.jeecg.modules.psms.statement.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.service.PointService;
import org.jeecg.modules.demo.statementdata.entity.BaseStatementData;
import org.jeecg.modules.demo.statementdata.service.IBaseStatementDataService;
import org.jeecg.modules.demo.statistical.entity.BaseStatistical;
import org.jeecg.modules.demo.statistical.service.IBaseStatisticalService;
import org.jeecg.modules.psms.base.service.PsmsService;
import org.jeecg.modules.psms.statement.service.StatementService;
import org.jeecg.modules.utils.ToolsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RestController
@RequestMapping("/stateTask")
//每周凌晨三点 执行统计报告
public class stateTask {

    @Autowired
    private StatementService statementService;

    @Autowired
    private PsmsService psmsService;

    @Autowired
    private IBaseStatementDataService baseStatementDataService;

    @Autowired
    private PointService pointService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private IBaseStatisticalService baseStatisticalService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @GetMapping(value = "/getStateTask")
    public Result<?> getStateTask(int type){
        setReport(type);
        return Result.ok("获取成功");
    }

    //天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
    @Scheduled(cron = "0 1 1 * * MON")//每周一 早晨一点
    public void execute() {
        setReport(1);
    }

    //表示在每月的1日的凌晨2点调度任务
    @Scheduled(cron = "0 1 2 1 * ?")
    public void executeMonth(){
        setReport(2);
    }

    //每个季度的第一天零点进行统计
    @Scheduled(cron = "0 1 3 1 * ?")
    public void executeSeason(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        if(month==1 || month==4 || month==7 || month==10){
            setReport(3);
        }
    }

    private void setReport(int type){
        log.info("统计报表开始");
        List<Map<String,Object>> maps = psmsService.getRegion();
        maps.forEach(data->{
            try {
                String fidRegion = String.valueOf(data.get("id"));

                String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
                String tablePurifier = "bu_purifier_powerdata" + "_"+ fidRegion;

                QueryWrapper<Point> query = new QueryWrapper<>();
                query.eq("fid_region",fidRegion);
                query.groupBy("point_mac");
                List<Point>listData = pointService.list(query);

                StringBuilder buf = new StringBuilder();
                buf.append(" ( ");
                int numv = 1440 * 7;
                for (int i = 0; i < 7; i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String dates = ToolsUtils.getLastDays(sdf.format(new Date()), -(i + 1));
                    if (i>0) buf.append(" or ");
                    buf.append(" BLD.yyyymmdd = '").append(dates).append("'");
                }
                buf.append(" ) ");

                for(Point p:listData){
                    String companyId = p.getCompanyId();
                    String pointMac = p.getPointMac();
                    if(StringUtils.isEmpty(pointMac)){
                        continue;
                    }
                    QueryWrapper<Company> querys = new QueryWrapper<>();
                    querys.eq("id",companyId);
                    Company c = companyService.getOne(querys);
                    String name = c.getName();
                    switch (type){
                        case 1:{
                            Map<String,Object>map = statementService.getStatDataTest(tableLampblack,tablePurifier,pointMac,buf.toString(),numv);
                            //Map<String,Object>map = statementService.getStatementData(tableLampblack,tablePurifier,pointMac,name);
                            getLampblackData(map,pointMac,name,fidRegion,7);
                        }break;
                        case 2:{
                            String format = ToolsUtils.getLastMonth(1);
                            List<String>days = ToolsUtils.getDateList(format);
                            Map<String,Object>map = statementService.getStatementMonthData(tableLampblack,tablePurifier,pointMac,name,format,days.size()*1440);
                            getLampblackData(map,pointMac,name,fidRegion,days.size());
                        }break;
                        case 3:{
                            String format1 = ToolsUtils.getLastMonth(1);
                            String format2 = ToolsUtils.getLastMonth(2);
                            String format3 = ToolsUtils.getLastMonth(3);
                            List<String>days1 = ToolsUtils.getDateList(format1);
                            List<String>days2 = ToolsUtils.getDateList(format2);
                            List<String>days3 = ToolsUtils.getDateList(format3);
                            int size = days1.size()+days2.size()+days3.size();
                            Map<String,Object>map = statementService.getStatementSeasonData(tableLampblack,tablePurifier,pointMac,name,format1,format2,format3,size*1440);
                            getLampblackData(map,pointMac,name,fidRegion,size);
                        }break;
                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String format = sdf.format(new Date());
                setpdf(format,fidRegion,type);
            }catch (Exception e){
                e.getMessage();
            }
        });

        log.info("统计报表结束");
    }

    private void getLampblackData(Map<String,Object>map,String pointMac,String name,String fidRegion,int days){

        log.info(name+"-map:："+map.toString());
        BaseStatementData baseStatementData = new BaseStatementData();
        //数据 采集量
        int num = map.get("num")==null?0:Integer.parseInt(String.valueOf(map.get("num")));
        baseStatementData.setDataAmount(num);

        //数据 完整率
        float ratio = map.get("ratio")==null?0:Float.parseFloat(String.valueOf(map.get("ratio")));
        ratio = ratio*100;
        BigDecimal bigDecimalRatio = BigDecimal.valueOf(ratio);
        baseStatementData.setDataRate(bigDecimalRatio);

        //风机 开启时长
        int fan = map.get("fan")==null?0:Integer.parseInt(String.valueOf(map.get("fan")));
        baseStatementData.setDraughtTime(fan);

        //净化器 联动时长
        int purifier = map.get("purifier")==null?0:Integer.parseInt(String.valueOf(map.get("purifier")));
        baseStatementData.setLinkageTime(purifier);

        //联动率
        float fanRatio = map.get("fanRatio")==null?0:Float.parseFloat(String.valueOf(map.get("fanRatio")));
        fanRatio = fanRatio*100;
        BigDecimal bigDecimalFanRatio = BigDecimal.valueOf(fanRatio);
        baseStatementData.setPurifierLinkageRate(bigDecimalFanRatio);

        //油烟浓度 0-1时长
        int h1 = map.get("h1")==null?0:Integer.parseInt(String.valueOf(map.get("h1")));
        baseStatementData.setLampblackHour1(h1);

        //油烟浓度 1.01-2时长
        int h2 = map.get("h2")==null?0:Integer.parseInt(String.valueOf(map.get("h2")));
        baseStatementData.setLampblackHour2(h2);

        //油烟浓度 2.01-4时长
        int h3 = map.get("h3")==null?0:Integer.parseInt(String.valueOf(map.get("h3")));
        baseStatementData.setLampblackHour3(h3);

        //油烟浓度 4.01+时长
        int h4 = map.get("h4")==null?0:Integer.parseInt(String.valueOf(map.get("h4")));
        baseStatementData.setLampblackHour4(h4);

        //超标 总时长
        int h5 = map.get("h5")==null?0:Integer.parseInt(String.valueOf(map.get("h5")));
        baseStatementData.setLampblackAllHour(h5);

        //标称 最大 最小 平均 备注 暂无统计
        baseStatementData.setPointMac(pointMac);
        baseStatementData.setName(name);
        baseStatementData.setCreateTime(new Date());
        baseStatementData.setDays(days);
        baseStatementData.setFidRegion(Integer.parseInt(fidRegion));
        baseStatementDataService.save(baseStatementData);

    }

    //@GetMapping(value = "/getpdf")
    private void setpdf(String format,String fidRegion,int type) {
        QueryWrapper<BaseStatementData> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.apply("DATE_FORMAT(create_time,'%Y-%m-%d') = '"+format+"'");
        query.orderByDesc("data_amount");
        List<BaseStatementData>listData = baseStatementDataService.list(query);
        try {
            String ctxPath = uploadpath;
            String fileName = System.currentTimeMillis()+".pdf";
            File file = new File(ctxPath + File.separator + "pdf" + File.separator );
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String savePath = file.getPath() + File.separator + fileName;
            TestPdf pdf = new TestPdf();
            String path = pdf.createPDF(savePath,listData,type,7);

            if(StringUtils.isNotEmpty(path)){

                BaseStatistical baseStatistical = new BaseStatistical();

                switch (type){
                    case 1:
                        baseStatistical.setName("统计周报");
                        break;
                    case 2:
                        baseStatistical.setName("统计月报");
                        break;
                    case 3:
                        baseStatistical.setName("统计季报");
                        break;
                }

                baseStatistical.setFileName(fileName);
                baseStatistical.setCreateTime(new Date());
                baseStatistical.setFileUrl("pdf"+File.separator+fileName);
                baseStatistical.setState("已经生成");
                baseStatistical.setMsg("");
                baseStatistical.setFidRegion(Integer.parseInt(fidRegion));
                baseStatisticalService.save(baseStatistical);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }



    @GetMapping(value = "/getPdfMobth")
    public void getPdfMobth(String format,String fidRegion){
        String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
        String tablePurifier = "bu_purifier_powerdata" + "_"+ fidRegion;

        QueryWrapper<Point> query = new QueryWrapper<>();
        query.eq("fid_region",fidRegion);
        query.groupBy("point_mac");
        List<Point>listData = pointService.list(query);

        int daysnum = 0;
        List<BaseStatementData>dataList = new LinkedList<>();
        for(Point p:listData) {
            String companyId = p.getCompanyId();
            String pointMac = p.getPointMac();
            if (StringUtils.isEmpty(pointMac)) {
                continue;
            }
            QueryWrapper<Company> querys = new QueryWrapper<>();
            querys.eq("id",companyId);
            Company c = companyService.getOne(querys);
            String name = c.getName();
            List<String>days = ToolsUtils.getDateList(format);
            daysnum = days.size();
            Map<String,Object>map = statementService.getStatementMonthData(tableLampblack,tablePurifier,pointMac,name,format,daysnum*1440);
            dataList.add(getLampblackDatas(map,pointMac,name,fidRegion,daysnum));
        }
        dataList.sort((o1, o2) -> {
            int i = o2.getDataAmount() - o1.getDataAmount();
            if (i == 0) {
                return o1.getLampblackHour1() - o2.getLampblackHour1();
            }
            return i;
        });

        setpdfs(dataList,2,daysnum);

    }
    @GetMapping(value = "/getpdf")
    public void getPdf(String format,String fidRegion,int days){
        try {

            String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion;
            String tablePurifier = "bu_purifier_powerdata" + "_"+ fidRegion;

            QueryWrapper<Point> query = new QueryWrapper<>();
            query.eq("fid_region",fidRegion);
            query.groupBy("point_mac");
            List<Point>listData = pointService.list(query);

            StringBuilder buf = new StringBuilder();
            buf.append(" ( ");
            int numv = 1440 * days;
            for (int i = 0; i < days; i++) {
                String dates = ToolsUtils.getLastDays(format, -(i + 1));
                if (i>0) buf.append(" or ");
                buf.append(" BLD.yyyymmdd = '").append(dates).append("'");
            }
            buf.append(" ) ");

            List<BaseStatementData>dataList = new LinkedList<>();
            for(Point p:listData){
                String companyId = p.getCompanyId();
                String pointMac = p.getPointMac();
                if(StringUtils.isEmpty(pointMac)){
                    continue;
                }
                QueryWrapper<Company> querys = new QueryWrapper<>();
                querys.eq("id",companyId);
                Company c = companyService.getOne(querys);
                String name = c.getName();
                Map<String,Object>map = statementService.getStatDataTest(tableLampblack,tablePurifier,pointMac,buf.toString(),numv);
                dataList.add(getLampblackDatas(map,pointMac,name,fidRegion,days));
            }

            dataList.sort((o1, o2) -> {
                int i = o2.getDataAmount() - o1.getDataAmount();
                if (i == 0) {
                    return o1.getLampblackHour1() - o2.getLampblackHour1();
                }
                return i;
            });

            setpdfs(dataList,1,days);
        }catch (Exception e){
            e.getMessage();
        }
    }

    public BaseStatementData getLampblackDatas(Map<String,Object>map,String pointMac,String name,String fidRegion,int days){

        log.info(name+"-map:："+map.toString());
        BaseStatementData baseStatementData = new BaseStatementData();
        //数据 采集量
        int num = map.get("num")==null?0:Integer.parseInt(String.valueOf(map.get("num")));
        baseStatementData.setDataAmount(num);

        //数据 完整率
        float ratio = map.get("ratio")==null?0:Float.parseFloat(String.valueOf(map.get("ratio")));
        ratio = ratio*100;
        BigDecimal bigDecimalRatio = BigDecimal.valueOf(ratio);
        baseStatementData.setDataRate(bigDecimalRatio.setScale(2,BigDecimal.ROUND_HALF_UP));

        //风机 开启时长
        int fan = map.get("fan")==null?0:Integer.parseInt(String.valueOf(map.get("fan")));
        baseStatementData.setDraughtTime(fan);

        //净化器 联动时长
        int purifier = map.get("purifier")==null?0:Integer.parseInt(String.valueOf(map.get("purifier")));
        baseStatementData.setLinkageTime(purifier);

        //联动率
        float fanRatio = map.get("fanRatio")==null?0:Float.parseFloat(String.valueOf(map.get("fanRatio")));
        fanRatio = fanRatio*100;
        BigDecimal bigDecimalFanRatio = BigDecimal.valueOf(fanRatio);
        baseStatementData.setPurifierLinkageRate(bigDecimalFanRatio.setScale(2,BigDecimal.ROUND_HALF_UP));

        //油烟浓度 0-1时长
        int h1 = map.get("h1")==null?0:Integer.parseInt(String.valueOf(map.get("h1")));
        baseStatementData.setLampblackHour1(h1);

        //油烟浓度 1.01-2时长
        int h2 = map.get("h2")==null?0:Integer.parseInt(String.valueOf(map.get("h2")));
        baseStatementData.setLampblackHour2(h2);

        //油烟浓度 2.01-4时长
        int h3 = map.get("h3")==null?0:Integer.parseInt(String.valueOf(map.get("h3")));
        baseStatementData.setLampblackHour3(h3);

        //油烟浓度 4.01+时长
        int h4 = map.get("h4")==null?0:Integer.parseInt(String.valueOf(map.get("h4")));
        baseStatementData.setLampblackHour4(h4);

        //超标 总时长
        int h5 = map.get("h5")==null?0:Integer.parseInt(String.valueOf(map.get("h5")));
        baseStatementData.setLampblackAllHour(h5);

        //标称 最大 最小 平均 备注 暂无统计
        baseStatementData.setPointMac(pointMac);
        baseStatementData.setName(name);
        baseStatementData.setCreateTime(new Date());
        baseStatementData.setDays(days);
        baseStatementData.setFidRegion(Integer.parseInt(fidRegion));
        return baseStatementData;
    }

    private void setpdfs(List<BaseStatementData>listData,int type,int days) {
        try {
            String ctxPath = uploadpath;
            String fileName = System.currentTimeMillis() + ".pdf";
            File file = new File(ctxPath + File.separator + "pdf" + File.separator);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String savePath = file.getPath() + File.separator + fileName;
            TestPdf pdf = new TestPdf();
            String path = pdf.createPDF(savePath, listData, type,days);
            log.info("pdf 已生成 " + path);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
