package org.jeecg.modules.demo.companytype.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("base_company_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="base_company对象", description="油烟数据")
public class CompanyType implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private String id;

    //服务商名称
    @Excel(name = "服务商名称", width = 15)
    @ApiModelProperty(value = "服务商名称")
    private String name;

    //创建时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    //测点编码
    @Excel(name = "测点编码", width = 15)
    @ApiModelProperty(value = "测点编码")
    private String pointMac;

    //告警类型
    //告警类型 1-营业时段停机 2-没有联动开启 3-二次电压过低
    //4-设备联网异常 5-烟气排放超标 6- 高压电场异常
    //7-设备压差异常 8-其他原因异常
    @Excel(name = "告警类型", width = 15)
    @ApiModelProperty(value = "告警类型")
    private int alarmType;


    @Excel(name = "告警类型", width = 15)
    @ApiModelProperty(value = "告警类型")
    private String companyId;

    //关联账号权限
    @Excel(name = "关联账号权限", width = 15)
    @ApiModelProperty(value = "关联账号权限")
    private String fidRegion;

    //统计开始时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date startTime;

    //统计结束时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date endTime;

    //统计次数
    @Excel(name = "统计次数", width = 15)
    @ApiModelProperty(value = "统计次数")
    private int amount;

    //统计次数
    @Excel(name = "是否申请取消此记录", width = 15)
    @ApiModelProperty(value = "是否申请取消此记录")
    private int isOn;

    //统计结束时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updataTime;

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商

}
