package org.jeecg.modules.psms.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.psms.base.mapper.PsmsMapper;
import org.jeecg.modules.psms.base.service.PsmsService;
import org.jeecg.modules.psms.base.entity.LampBlack;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class PsmsServiceImpl extends ServiceImpl<PsmsMapper, Map<String,Object>> implements PsmsService {
    @Resource
    private PsmsMapper baseMapper;

    @Override
    public Page getYouYanList(Page page,
                              String tableLampblack,String tablePurifier,int fidRegion,
                              String companyid, String pointid, String startTime, String endTime, Integer pageNo, Integer pageSize) {

        List<Map<String,Object>> list = baseMapper.getTestAllData(
                tableLampblack,tablePurifier,fidRegion,
                companyid,pointid,startTime,endTime);
        Integer count = list.size();
        PagedListHolder<Map<String,Object>> pagedListHolder = new PagedListHolder<>(list);
        pagedListHolder.setPageSize(pageSize);
        pagedListHolder.setPage(pageNo-1);

        page.setTotal(count);
        page.setPages(count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        page.setRecords(pagedListHolder.getPageList());
        return page;
    }

    @Override
    public IPage<Map<String,Object>> getNewestCreateTime(
            Page<Map<String,Object>> page,
            String tableLampblack,String tablePurifier,int fidRegion,
            List<String> ids,String hour,String pointMac,String startTime,String endTime) {
        return baseMapper.getNewestCreateTime(page,tableLampblack,tablePurifier,fidRegion,ids,hour,pointMac,startTime,endTime);
    }

    @Override
    public IPage<Map<String, Object>> getNewestCreateTime2(
            Page<Map<String, Object>> page,
            String tableLampblack, String tablePurifier, int fidRegion,
            List<String> ids, String pointMac) {
        return baseMapper.getNewestCreateTime2(page,tableLampblack,tablePurifier,fidRegion,ids,pointMac);
    }

    @Override
    public Map<String, List<String>> getStatByCreateTime(String table,String pointMac,String createTime) {

        Map<String, List<String>>maps = new LinkedHashMap<>();
        String format = createTime;
        try {
            DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            Date date = sdf.parse(createTime);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE,1);
            format = sdf.format(c.getTime());
        } catch (Exception e){
            e.getMessage();
        }

        List<String>list1 = new LinkedList<>();
        List<String>list11 = new LinkedList<>();
        List<String>list2 = new LinkedList<>();
        List<String>list22 = new LinkedList<>();
        List<String>list3 = new LinkedList<>();
        List<String>list33 = new LinkedList<>();

        List<Map<String,Object>> listmap = baseMapper.getStatByCreateTime(table,pointMac,format);
        for(int i = 0; i<24; i++){
            list1.add("");list11.add("");list2.add("");list22.add("");list3.add("");list33.add("");
            String index = String.valueOf(i);
            if(!listmap.isEmpty()){
                listmap.forEach(m->{
                    if(m!=null){
                        String section = String.valueOf(m.get("section"));
                        if(StringUtils.isNotEmpty(section)){
                            if(section.equals(index)){
                                list1.set(Integer.parseInt(index),String.valueOf(m.get("lampblackDataMax")));
                                list11.set(Integer.parseInt(index),String.valueOf(m.get("lampblackDataAvg")));
                                list2.set(Integer.parseInt(index),String.valueOf(m.get("matterDataMax")));
                                list22.set(Integer.parseInt(index),String.valueOf(m.get("matterDataAvg")));
                                list3.set(Integer.parseInt(index),String.valueOf(m.get("nmhcDataMax")));
                                list33.set(Integer.parseInt(index),String.valueOf(m.get("nmhcDataAvg")));
                            }
                        }
                    }
                });
            }
        }

        //倒叙
        Collections.reverse(list1);
        Collections.reverse(list11);
        Collections.reverse(list2);
        Collections.reverse(list22);
        Collections.reverse(list3);
        Collections.reverse(list33);

        maps.put("top1",list1);
        maps.put("average1",list11);
        maps.put("top2",list2);
        maps.put("average2",list22);
        maps.put("top3",list3);
        maps.put("average3",list33);

        return maps;
    }

    @Override
    public Map<String, Object> getDetailsByTime(String tableLampblack, String id, String hour) {
        return baseMapper.getDetailsByTime(tableLampblack,id,hour);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getWarnEvent(String tableLampblack, String id,String pointId, int fidRegion) {

        Map<String, List<Map<String, Object>>>maps = new LinkedHashMap<>();
        List<Map<String, Object>>listMap1 = new LinkedList<>();
        List<Map<String, Object>>listMap2 = new LinkedList<>();
        String createTime = "";
        try {
            DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            //Date date = sdf.parse("2021-11-22");
            c.setTime(new Date());
            c.add(Calendar.DATE,1);
            createTime = sdf.format(c.getTime());
        } catch (Exception e){
            e.getMessage();
        }

        List<Map<String, Object>> warn1 = baseMapper.getWarnEvent1(tableLampblack,id,pointId,createTime, fidRegion);

        Map<String, Object> warn3 = baseMapper.getWarnEvent3(id,pointId,fidRegion);
        setWarnListMap(warn1,listMap1,listMap2,warn3);
        Collections.reverse(listMap1);
        //Collections.reverse(listMap2);
        //String mac = String.valueOf(warn3.get("mac"));
        //List<Map<String, Object>> warn2 = baseMapper.getWarnEvent2(mac);
        maps.put("a1",listMap1);//排放超标告警
        //maps.put("a2",listMap2);//疑似故障告警
        //maps.put("a3",warn2);//设备停机事件

        String tableLampblackNow = "bu_lampblack_data";
        if(fidRegion!=1024){
            tableLampblackNow = tableLampblackNow + "_"+ fidRegion + "_now";
        }
        Map<String, Object> getWarnEventNow = baseMapper.getWarnEvent4(tableLampblackNow,id,pointId,fidRegion);
        DateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        List<Map<String, Object>>listMap22 = new LinkedList<>();
        List<Map<String, Object>>listMap33 = new LinkedList<>();
        String name = String.valueOf(warn3.get("name"));
        if(null==getWarnEventNow || null==getWarnEventNow.get("mTime") || getWarnEventNow.get("mTime").equals("")){
            Map<String, Object> map = new LinkedHashMap<>();
            DateFormat sdfs = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            String dateNows = sdfs.format(new Date());
            map.put("time",dateNows);//查询日期
            map.put("text","设备异常，未接收到过任何数据");
            map.put("name",name);
            listMap22.add(map);
        }else {
            String mTime = (String)getWarnEventNow.get("mTime");
            if(!mTime.equals(dateNow)){
                DateFormat sdfs = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                String dateNows = sdfs.format(new Date());

                Map<String, Object> map = new LinkedHashMap<>();
                map.put("timeNow",dateNows);//查询日期：
                map.put("time",mTime);//最新一次上传数据时间为：
                map.put("text","今日未开机");
                map.put("name",name);
                listMap33.add(map);
            }
        }
        maps.put("a2",listMap22);
        maps.put("a3",listMap33);
        return maps;
    }


    private synchronized void setWarnListMap(List<Map<String, Object>> warn1,List<Map<String, Object>>listMap1,List<Map<String, Object>>listMap2, Map<String, Object> warn3){
        String name = String.valueOf(warn3.get("name"));
        AtomicBoolean ison = new AtomicBoolean(false);
        warn1.forEach(map->{
            String a1 = String.valueOf(map.get("a1")) ;
            String a2 = String.valueOf(map.get("a2")) ;
            if(a1.equals("1")){
                Map<String, Object> map1 = new LinkedHashMap<>();
                float lampblackDataMax = Float.parseFloat(String.valueOf(map.get("lampblackDataMax")));
                float matterData = Float.parseFloat(String.valueOf(map.get("matterData")));
                float nmhcData = Float.parseFloat(String.valueOf(map.get("nmhcData")));
                map1.put("time",map.get("create_time"));
                map1.put("state","1");
                map1.put("name",name);

                if(lampblackDataMax>1){
                    map1.put("data",lampblackDataMax);
                    map1.put("text","油烟超标");
                    map1.put("norm","<=1 mg/m3");
                }
                if(matterData>5){
                    map1.put("data",matterData);
                    map1.put("text","颗粒物超标");
                    map1.put("norm","<=5 mg/m3");
                }
                if(nmhcData>10){
                    map1.put("data",nmhcData);
                    map1.put("text","NmHc超标");
                    map1.put("norm","<=10 mg/m3");
                }
                listMap1.add(map1);
                ison.set(true);
            }else if(ison.get() && a1.equals("0")){
                Map<String, Object> map1 = new LinkedHashMap<>();
                map1.put("time",map.get("create_time"));
                map1.put("text","解除超标告警");
                map1.put("state","0");
                map1.put("name",name);
                listMap1.add(map1);
                ison.set(false);
            }

            if(a2.equals("1")){
                Map<String, Object> map2 = new LinkedHashMap<>();
                map2.put("time",map.get("create_time"));
                map2.put("text","设备未停机，但无法获取油烟数据");
                map2.put("name",name);
                listMap2.add(map2);
            }
        });
    }


    @Override
    public Map<String, Object> statCompanyExcessive(String tableLampblack, int fidRegion) {
        return baseMapper.statCompanyExcessive(tableLampblack,fidRegion);
    }

    @Override
    public Map<String, Object> statPointExcessive(String tableLampblack, int fidRegion) {
        return baseMapper.statPointExcessive(tableLampblack,fidRegion);
    }

    @Override
    public Map<String, Object> getOverproof(String tableLampblack, int fidRegion,String formatDay) {
        return baseMapper.getOverproof(tableLampblack,fidRegion,formatDay);
    }

    @Override
    public Map<String, Object> getOnlineNum(String tableLampblack, int fidRegion,String formatDay) {
        return baseMapper.getOnlineNum(tableLampblack,fidRegion,formatDay);
    }

    @Override
    public int insertEntity(LampBlack entity) {
        return baseMapper.insertEntity(entity);
    }

    @Override
    public List<Map<String, Object>> getRegion() {
        return baseMapper.getRegion();
    }

    @Override
    public void deleteToDay(String tables) {
        baseMapper.deleteToDay(tables);
    }

    @Override
    public List<Map<String, Object>> getNowTimeDataForMac(String tableLampblack, String tablePurifier, String pointMac, int fidRegion) {
        return baseMapper.getNowTimeDataForMac(tableLampblack,tablePurifier,pointMac,fidRegion);
    }
}
