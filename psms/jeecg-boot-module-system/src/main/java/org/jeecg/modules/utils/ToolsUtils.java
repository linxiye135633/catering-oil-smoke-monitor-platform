package org.jeecg.modules.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class ToolsUtils {

    //判断是否为数字
    public static boolean isNumeric(Object str){
        if(str!=null && StringUtils.isNotEmpty(str.toString())){
            try {
                Pattern pattern = Pattern.compile("[0-9]*");
                return pattern.matcher(str.toString()).matches();
            }catch (Exception e){
                e.getMessage();
                return false;
            }
        }else {
            return false;
        }
    }

    //实体类转map
    public static Map<String, Object> objectToMap(Object obj) {
        if(obj == null)
            return null;

        Map<String, Object> map = new HashMap<String, Object>();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());
        }catch (IntrospectionException e){
            log.error("[beanInfo = {}]自省过程发生异常", beanInfo, e);
        }

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = null;
            try {
                value = getter!=null ? getter.invoke(obj) : null;
            }catch (Exception e){
                log.error("[value = {}]object转map时发生异常", value, e);
            }
            map.put(key, value);
        }

        return map;
    }

    public static String setToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:"+unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    // 获取日期列表
    public static List<String> getDateList(String month) {
        // 计算的月份
        //String month = "2016-12";
        List<String> dateList = new LinkedList<String>();
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM").parse(month));

            SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = dateSdf.format(new Date());
            // 到下个月不在累计
            while (calendar.get(Calendar.MONTH) + 1 == Integer.parseInt(month.split("-")[1])) {
                // 至本年月日,不在计算
                if (dateSdf.format(calendar.getTime()).equals(nowDate)) {
                    break;
                }
                dateList.add(dateSdf.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1);
            }
        }catch (Exception e){
            e.getMessage();
        }

        return dateList;
    }

    public static String getLastMonth(int last) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - last); // 设置为上一个月
        date = calendar.getTime();
        String accDate = format.format(date);
        return accDate;
    }

    public static String getLastDays(String days,int last) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(days));
            c.add(Calendar.DATE,last);
            Date date =  c.getTime();
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
