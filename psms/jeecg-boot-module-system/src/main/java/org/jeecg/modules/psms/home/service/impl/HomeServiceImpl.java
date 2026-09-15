package org.jeecg.modules.psms.home.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.service.PointService;
import org.jeecg.modules.psms.home.mapper.HomeMapper;
import org.jeecg.modules.psms.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class HomeServiceImpl extends ServiceImpl<HomeMapper, Map<String,Object>> implements HomeService {
    @Resource
    private HomeMapper homeMapper;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private PointService pointService;

    //餐饮企业接入数
    @Override
    public Map<String, Object> statCompany(int fidRegion) {
        return homeMapper.statCompany(fidRegion);
    }

    //排口测点数
    @Override
    public Map<String, Object> pointCompany(int fidRegion) {
        return homeMapper.pointCompany(fidRegion);
    }

    //上报数据流量
    @Override
    public Map<String, Object> lampblackRateFlow(String table) {
        return homeMapper.lampblackRateFlow(table);
    }

    //测点联网率
    @Override
    public Map<String, Object> pointRateFlow(String table,int fidRegion,String hours) {
        return homeMapper.pointRateFlow(table,fidRegion,hours);
    }

    //餐饮企业接入趋势
    @Override
    public Map<String, List<Object>> statiJoinYear(int fidRegion) {
        Map<String,List<Object>>map = new LinkedHashMap<>();

        List<Object> dax = new LinkedList<>();
        List<Object> data1 = new LinkedList<>();
        List<Object> data2 = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        for(int i = 0;i<12;i++){
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH,-i);
            Date date =  c.getTime();
            QueryWrapper<Company> queryWrapper1 = new QueryWrapper<>();
            QueryWrapper<Point> queryWrapper2 = new QueryWrapper<>();
            String format = sdf.format(date);
            if(fidRegion!=1024){
                queryWrapper1.eq("fid_region",fidRegion);
                queryWrapper2.eq("fid_region",fidRegion);
            }
            queryWrapper1.apply("DATE_FORMAT(create_time,'%Y-%m') = '"+format+"'");
            queryWrapper2.apply("DATE_FORMAT(create_time,'%Y-%m') = '"+format+"'");

            dax.add(format);

            int num = companyService.count(queryWrapper1);
            data2.add(num);

            int num2 = pointService.count(queryWrapper2);
            data1.add(num2);
        }
        Collections.reverse(dax);
        Collections.reverse(data1);
        Collections.reverse(data2);
        map.put("dataX",dax);
        map.put("data1",data1);
        map.put("data2",data2);
        return map;
    }

    //本月安装接入进度
    @Override
    public Map<String, Object> getRateProgress(int fidRegion) {
        Map<String,Object>map = new LinkedHashMap<>();
        QueryWrapper<Company> queryCompany = new QueryWrapper<>();
        if(fidRegion!=1024){
            queryCompany.eq("fid_region",fidRegion);
        }
        int companyAllCount = companyService.count(queryCompany);
        int companyCount = homeMapper.companyCount(fidRegion,null);

        QueryWrapper<Point> queryPoint = new QueryWrapper<>();
        if(fidRegion!=1024){
            queryPoint.eq("fid_region",fidRegion);
        }

        int pointAllCount = pointService.count(queryPoint);
        queryPoint.eq("status","01");
        //queryPoint.or();
        //queryPoint.eq("status","01");
        int pointCount = pointService.count(queryPoint);

        map.put("a2",companyAllCount);//餐饮企业安装进度总数量
        map.put("a1",companyCount);//餐饮企业安装进度已安装
        map.put("a4",pointAllCount);//测点调试接入进度总数量
        map.put("a3",pointCount);//测点调试接入进度已接入

        queryPoint.clear();
        if(fidRegion!=1024){
            queryPoint.eq("fid_region",fidRegion);
        }

        queryPoint.eq("status","03");
        int pointWaitCount = pointService.count(queryPoint);
        int companyWaitCount = companyAllCount-companyCount;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String format = sdf.format(new Date());
        queryPoint.apply("DATE_FORMAT(create_time,'%Y,%M') = '"+format+"'");
        int pointMonthCount = pointService.count(queryPoint);
        int companyMonthCount = homeMapper.companyCount(fidRegion,"1");

        map.put("a5",pointWaitCount);//累计待接入测点数
        map.put("a6",companyWaitCount);//累计待安装餐饮企业数
        map.put("a7",pointMonthCount);//本月已接入测点数
        map.put("a8",companyMonthCount);//本月已安装餐饮企业数
        return map;
    }

    //测点设备
    @Override
    public Map<String, Object> getEquipmentPoint(String tableLampblack, String tablePurifier, int fidRegion) {
        Map<String,Object>map = new LinkedHashMap<>();
        int purifierCount = homeMapper.getEquipmentPurifier(tableLampblack);
        int lampblackCount = homeMapper.getEquipmentLampblack(tableLampblack);
        int electricCount = homeMapper.getElectric(fidRegion);

        QueryWrapper<Point> queryPoint = new QueryWrapper<>();
        if(fidRegion!=1024){
            queryPoint.eq("fid_region",fidRegion);
        }
        int pointAllCount = pointService.count(queryPoint);

        map.put("a1",purifierCount);//净化器总数量 -油烟反馈数据里面 净化器状态为1
        map.put("a2",lampblackCount);//排风机总数量 -油烟反馈数据里面 风机状态为1
        map.put("a3",pointAllCount);//监测仪总数量 -测点总数量
        map.put("a4",electricCount);//电场总数量 -测点表里面的一个参数
        return map;
    }

    //餐饮企业-分类统计
    @Override
    public Map<String, Map<String,Object>> companyStat(int fidRegion) {

        Map<String,Map<String,Object>>map = new LinkedHashMap<>();
        Map<String,Object>map1 = homeMapper.getBusinessCategory(fidRegion);
        Map<String,Object>map2 = homeMapper.getUnitCategory(fidRegion);
        map.put("a1",map1);//经营类别
        map.put("a2",map2);//单位类型

        return map;
    }

    @Override
    public int getFlow(String tableLampblack, int fidRegion, String formatDay) {
        return homeMapper.getFlow(tableLampblack,fidRegion,formatDay);
    }


}
