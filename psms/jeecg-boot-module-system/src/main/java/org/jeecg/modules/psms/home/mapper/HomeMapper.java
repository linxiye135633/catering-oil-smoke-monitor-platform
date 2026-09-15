package org.jeecg.modules.psms.home.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
public interface HomeMapper extends BaseMapper<Map<String,Object>> {

    Map<String, Object> statCompany(int fidRegion);

    Map<String, Object> pointCompany(int fidRegion);

    Map<String, Object> lampblackRateFlow(String table);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> pointRateFlow(String table,int fidRegion,String hours);

    int companyCount(int fidRegion,String monthv);

    @Select("select count(1) from (select point_mac from ${tableLampblack}  WHERE purifier_status = 1 group by point_mac) a")
    int getEquipmentPurifier(String tableLampblack);

    @Select("select count(1) from (select point_mac from ${tableLampblack} WHERE fan_status = 1 group by point_mac) a")
    int getEquipmentLampblack(String tableLampblack);


    int getElectric(int fidRegion);

    Map<String, Object> getBusinessCategory(int fidRegion);

    Map<String, Object> getUnitCategory(int fidRegion);
    @Select("SELECT count(1) FROM  ${tableLampblack}  WHERE yyyymmdd = #{formatDay}")
    int getFlow(String tableLampblack, int fidRegion, String formatDay);
}
