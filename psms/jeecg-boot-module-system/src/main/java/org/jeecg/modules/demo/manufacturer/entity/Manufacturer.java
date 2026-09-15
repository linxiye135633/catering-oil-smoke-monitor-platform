package org.jeecg.modules.demo.manufacturer.entity;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.poi.hpsf.Decimal;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("base_manufacturer")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "base_manufacturer对象", description = "服务厂商表")
public class Manufacturer {
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @Excel(name = "服务商编号", width = 15)
    @ApiModelProperty(value = "服务商编号")
    private String code;

    @Excel(name = "服务商名称", width = 15)
    @ApiModelProperty(value = "服务商名称")
    private String name;

    @Excel(name = "服务商地址", width = 15)
    @ApiModelProperty(value = "服务商地址")
    private String address;

    @Excel(name = "默认联系人", width = 15)
    @ApiModelProperty(value = "默认联系人")
    private String contact;

    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private BigDecimal lng;

    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;

    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private String status;

    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String creator;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private String updater;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    //uer 表关联
    private int fidRegion;
}
