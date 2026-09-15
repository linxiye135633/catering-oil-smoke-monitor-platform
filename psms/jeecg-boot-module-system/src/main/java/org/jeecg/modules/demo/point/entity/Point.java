package org.jeecg.modules.demo.point.entity;
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
@TableName("base_point")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "base_point对象", description = "测点表")
public class Point {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "餐饮企业ID")
    private String companyId;//餐饮企业ID
    @Excel(name = "测点编号", width = 15)
    @ApiModelProperty(value = "测点编号")
    private String code;//测点编号
    @Excel(name = "测点名称", width = 15)
    @ApiModelProperty(value = "测点名称")
    private String name;//测点名称
    @Excel(name = "测点类型", width = 15)
    @ApiModelProperty(value = "测点类型")
    private String pointType;//测点类型
    @Excel(name = "测点MAC", width = 15)
    @ApiModelProperty(value = "测点MAC")
    private String pointMac;//测点MAC
    @Excel(name = "标准灶头数", width = 15)
    @ApiModelProperty(value = "标准灶头数")
    private int stoveNumber;//标准灶头数（个）
    @Excel(name = "SIM卡号", width = 15)
    @ApiModelProperty(value = "SIM卡号")
    private String simCode;//SIM卡号
    @Excel(name = "测点状态", width = 15)
    @ApiModelProperty(value = "测点状态")
    private String status;//测点状态

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接入日期")
    private Date connDate;//接入日期

    @Excel(name = "排口名称", width = 15)
    @ApiModelProperty(value = "排口名称")
    private String portName;//排口名称

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "sim卡截止日期")
    private Date simCloseDate;//sim卡截止日期

    @Excel(name = "净化器技术路线", width = 15)
    @ApiModelProperty(value = "净化器技术路线")
    private String techRoadmap;//净化器技术路线
    @Excel(name = "排风机设计风量", width = 15)
    @ApiModelProperty(value = "排风机设计风量")
    private BigDecimal airVolume;//排风机设计风量（立方米/秒）

    private int modelNumber;//电场模块数量

    private String creator;//创建人
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;//创建时间

    private String updater;//修改人

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;//修改时间

    @Excel(name = "企业名称", width = 15)
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;//排口名称

    //uer 表关联
    private int fidRegion;

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商
}
