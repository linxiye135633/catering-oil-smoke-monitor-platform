package org.jeecg.modules.psms.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.psms.base.entity.LampBlack;

import java.util.List;
import java.util.Map;

public interface PsmsMapper extends BaseMapper<Map<String,Object>> {

    @InterceptorIgnore(tenantLine = "true")
    List<Map<String, Object>> getTestAllData(
            String tableLampblack,String tablePurifier,
            int fidRegion, String companyid, String pointid, String startTime, String endTime);

    @InterceptorIgnore(tenantLine = "true")
    IPage<Map<String,Object>> getNewestCreateTime(
            Page<Map<String,Object>> page,
            String tableLampblack,String tablePurifier,int fidRegion,
            @Param("ids") List<String> ids,
            @Param("hour") String hour,
            String pointMac,String startTime,String endTime);

    @InterceptorIgnore(tenantLine = "true")
    IPage<Map<String,Object>> getNewestCreateTime2(
            Page<Map<String,Object>> page,
            @Param("tableLampblack") String tableLampblack,
            @Param("tablePurifier") String tablePurifier,
            @Param("fidRegion") int fidRegion,
            @Param("ids") List<String> ids,
            @Param("pointMac") String pointMac);

    @InterceptorIgnore(tenantLine = "true")
    List<Map<String,Object>> getStatByCreateTime(@Param("table") String table, @Param("pointMac")String pointMac, @Param("createTime") String createTime);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getDetailsByTime(@Param("tableLampblack")String tableLampblack, @Param("ids")String ids, @Param("hour") String hour);

    @InterceptorIgnore(tenantLine = "true")
    List<Map<String, Object>> getWarnEvent1(String tableLampblack, String id, String pointId,String createTime, int fidRegion);

    @Select("select * from base_point_log where date_format(time,'%Y-%m-%d') = date_format(now(),'%Y-%m-%d') and point_mac = #{pointId}")
    List<Map<String, Object>> getWarnEvent2(String pointId);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getWarnEvent3(String id, String pointId, int fidRegion);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getWarnEvent4(String tableLampblack, String id, String pointId, int fidRegion);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> statCompanyExcessive(String tableLampblack, int fidRegion);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> statPointExcessive(String tableLampblack, int fidRegion);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getOverproof(String tableLampblack, int fidRegion,String formatDay);

    @InterceptorIgnore(tenantLine = "true")
    Map<String, Object> getOnlineNum(String tableLampblack, int fidRegion,String formatDay);

    @Insert("insert into base_lamp_blacks(id,code,msg,gmt_time,yy_jczs,yy_zxs,yy_cbs,yy_lxs,yy_ly,fid_region,create_time) values(#{id},#{code},#{msg},#{gmtTime},#{yyJczs},#{yyZxs},#{yyCbs},#{yyLxs},#{yyLy},#{fidRegion},#{createTime})")
    int insertEntity(LampBlack entity);

    @Select("SELECT * FROM base_region WHERE is_hastable = 1")
    List<Map<String, Object>> getRegion();

    @Select("DELETE FROM ${tables}\n" +
            "WHERE date_format(create_time,'%Y-%m-%d') = date_format(DATE_SUB(now(),INTERVAL 1 DAY),'%Y-%m-%d')")
    void deleteToDay(String tables);

    @InterceptorIgnore(tenantLine = "true")
    List<Map<String, Object>> getNowTimeDataForMac(String tableLampblack, String tablePurifier, String pointMac, int fidRegion);
}
