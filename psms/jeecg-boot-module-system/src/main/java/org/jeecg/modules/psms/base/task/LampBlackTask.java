package org.jeecg.modules.psms.base.task;

import net.sf.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psms.base.entity.LampBlack;
import org.jeecg.modules.psms.base.entity.LampBlacks;
import org.jeecg.modules.psms.base.service.PsmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@Component
@RequestMapping("/LampBlackTask")
public class LampBlackTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PsmsService psmsService;

    //每日凌晨1点执行 南京统计上传数据 和 清理today表数据
    @Scheduled(cron = "0 2 1 * * ?")//每日凌晨三点
    public void executes4() {
        //南京数据提交
        try {
            sendNanjingData();
        }catch (Exception e){
            e.getMessage();
        }

        try {
            //先备份下数据库 如果备份失败
            setClearToDay();
        }catch (Exception e){
            e.getMessage();
        }
    }

    //清楚当天表里面的昨天数据
    private void setClearToDay(){
        log.info("start-开始处理today表数据");
        List<Map<String,Object>> maps = psmsService.getRegion();
        maps.forEach(data->{
            try {
                String fidRegion = String.valueOf(data.get("id"));
                String tableLampblack = "bu_lampblack_data" + "_"+ fidRegion+"_today";
                String tablePurifier = "bu_purifier_powerdata" + "_"+ fidRegion+"_today";
                if(tableLampblack.length()==30){
                    psmsService.deleteToDay(tableLampblack);
                }
                if(tablePurifier.length()==34){
                    psmsService.deleteToDay(tablePurifier);
                }
            }catch (Exception e){
                e.getMessage();
            }
        });
        log.info("start-结束处理today表数据");
    }
    @GetMapping(value = "/sendNanjingData")
    public void sendNanjingData(){
        log.info("start-南京油烟统计");
        int fidRegion = 320100;//目前只统计给南京
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,-1);
            Date date =  c.getTime();
            String formatDay = sdf.format(date);

            String tableLampblack = "bu_lampblack_data_"+ fidRegion;

            Map<String,Object> map1 = psmsService.getOverproof(tableLampblack,fidRegion,formatDay);
            int overproof = Integer.parseInt(String.valueOf(map1.get("overproof")));

            Map<String,Object> map2 = psmsService.getOnlineNum(tableLampblack,fidRegion,formatDay);
            int total = Integer.parseInt(String.valueOf(map2.get("total")));
            int onlineNum = Integer.parseInt(String.valueOf(map2.get("onlineNum")));
            int offlineNum = Integer.parseInt(String.valueOf(map2.get("offlineNum")));

            log.info("start-南京油烟统计-total:"+total);
            log.info("start-南京油烟统计-onlineNum:"+onlineNum);
            log.info("start-南京油烟统计-overproof:"+overproof);
            log.info("start-南京油烟统计-offlineNum:"+offlineNum);

            LampBlacks beans = new LampBlacks();
            beans.setGmtTime(formatDay+" 23:59:59");
            beans.setYyJczs(total);
            beans.setYyZxs(onlineNum);
            beans.setYyCbs(overproof);
            beans.setYyLxs(onlineNum);
            beans.setYyLy("有限公司");
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://47.100.217.29:11016/api/lamp_blacks",beans,String.class); //提交的body内容为user对象，请求的返回的body类型为String
            String body = responseEntity.getBody();
            log.info("body:"+body);

            JSONObject jsonObject = JSONObject.fromObject(body);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");

            LampBlack bean = new LampBlack();
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            bean.setId(uuid);
            bean.setCode(code);
            bean.setMsg(msg);
            bean.setGmtTime(formatDay+" 23:59:59");
            bean.setYyJczs(total);
            bean.setYyZxs(onlineNum);
            bean.setYyCbs(overproof);
            bean.setYyLxs(offlineNum);
            bean.setYyLy("优丽德（北京）环保科技有限公司");
            bean.setFidRegion(fidRegion);
            SimpleDateFormat sdfv = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            bean.setCreateTime(sdfv.format(new Date()));
            psmsService.insertEntity(bean);

            log.info("end-南京油烟统计"+bean);
        }catch (Exception e){
            log.info("南京油烟统计-error:"+e.getMessage());
            LampBlack bean = new LampBlack();
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            bean.setId(uuid);
            bean.setCode(10000);
            bean.setMsg(e.getMessage());
            bean.setGmtTime("");
            SimpleDateFormat sdfv = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            bean.setCreateTime(sdfv.format(new Date()));
            bean.setFidRegion(fidRegion);
            psmsService.insertEntity(bean);
        }
        log.info("end-南京油烟统计");
    }

    @GetMapping(value = "/getTest")
    public void sendTest(){
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://192.168.1.10:9001/test/BaseThreshold/baseThreshold/getStatistics",String.class); //提交的body内容为user对象，请求的返回的body类型为String
        String body = responseEntity.getBody();
        log.info("body:"+body);
        JSONObject jsonObject = JSONObject.fromObject(body);
        Map<String, Object>map = (Map<String, Object>)jsonObject.get("result");
        log.info("body:"+map.toString());
    }

}
