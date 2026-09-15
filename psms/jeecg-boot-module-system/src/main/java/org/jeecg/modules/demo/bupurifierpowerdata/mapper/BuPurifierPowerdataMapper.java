package org.jeecg.modules.demo.bupurifierpowerdata.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.demo.bupurifierpowerdata.entity.BuPurifierPowerdata;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 净化器电源模块数据
 * @Author: jeecg-boot
 * @Date:   2021-10-28
 * @Version: V1.0
 */
public interface BuPurifierPowerdataMapper extends BaseMapper<BuPurifierPowerdata> {

    @Select("SELECT\n" +
            "\tBC.id as \"id\",\n" +
            "\tBC.name as \"name\",\n" +
            "\tBC.manufacturer_code as \"manufacturerCode\",\n" +
            "\tBLD.lampblack_data as \"lampblackData\",\n" +
            "\tBLD.matter_data as \"matterData\",\n" +
            "\tBLD.nmhc_data as \"nmhcData\",\n" +
            "\tBPP.second_voltage as \"secondVoltage\",\n" +
            "\tBPP.second_current as \"secondCurrent\",\n" +
            "\tBLD.purifier_status as \"purifierStatus\",\n" +
            "\tBLD.purifier_voltage_diff as \"purifierVoltageDiff\",\n" +
            "\tBLD.fan_status as \"fanStatus\",\n" +
            "\tBLD.fan_current as \"fanCurrent\",\n" +
            "\tBLD.create_time as \"createTime\"\n" +
            "FROM\n" +
            "\tbase_company BC\n" +
            "\tLEFT JOIN base_point BP ON BP.company_id = BC.id\n" +
            "\tLEFT JOIN bu_lampblack_data BLD ON BLD.point_id = BP.id\n" +
            "\tLEFT JOIN bu_purifier_powerdata BPP ON BPP.bu_lampblack_id = BLD.id\n" +
            "WHERE\n" +
            "\tBC.id = #{companyid}")
    List<Map<String, Object>> listYouYan(Page page, @Param("companyid") String companyid);

    @Select("SELECT\n" +
            "\tcount(*)\n" +
            "FROM\n" +
            "\tbase_company BC\n" +
            "\tLEFT JOIN base_point BP ON BP.company_id = BC.id\n" +
            "\tLEFT JOIN bu_lampblack_data BLD ON BLD.point_id = BP.id\n" +
            "\tLEFT JOIN bu_purifier_powerdata BPP ON BPP.bu_lampblack_id = BLD.id\n" +
            "WHERE\n" +
            "\tBC.id = #{companyid}")
    Integer countYouYan(Page page, @Param("companyid") String companyid);
}
