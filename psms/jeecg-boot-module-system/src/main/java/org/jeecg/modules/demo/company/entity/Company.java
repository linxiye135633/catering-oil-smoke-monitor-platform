package org.jeecg.modules.demo.company.entity;

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
@TableName("base_company")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="base_company对象", description="油烟数据")
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商
    @Excel(name = "行政机构", width = 15)
    @ApiModelProperty(value = "行政机构")
    private String InstitutionCode;//行政机构
    @Excel(name = "服务商编号", width = 15)
    @ApiModelProperty(value = "服务商编号")
    private String code;//服务商编号
    @Excel(name = "服务商名称", width = 15)
    @ApiModelProperty(value = "服务商名称")
    private String name;//服务商名称
    @Excel(name = "门头位置", width = 15)
    @ApiModelProperty(value = "门头位置")
    private String doorName;//门头位置
    @Excel(name = "单位行政区域", width = 15)
    @ApiModelProperty(value = "单位行政区域")
    private String areaCode;//单位行政区域
    @Excel(name = "单位所属街道", width = 15)
    @ApiModelProperty(value = "单位所属街道")
    private String streetCode;//单位所属街道
    @Excel(name = "单位地址", width = 15)
    @ApiModelProperty(value = "单位地址")
    private String address;//单位地址
    @Excel(name = "默认联系人", width = 15)
    @ApiModelProperty(value = "默认联系人")
    private String contact;//默认联系人
    @Excel(name = "联系人手机号", width = 15)
    @ApiModelProperty(value = "联系人手机号")
    private String contactMobile;//联系人手机号
    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private BigDecimal lng;//经度
    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;//纬度
    @Excel(name = "统一社会信用代码", width = 15)
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;//统一社会信用代码
    @Excel(name = "经营类别", width = 15)
    @ApiModelProperty(value = "经营类别")
    private String businessCategory;//经营类别
    @Excel(name = "单位类别", width = 15)
    @ApiModelProperty(value = "单位类别")
    private String unitCategory;//单位类别
    @Excel(name = "废气治理模式", width = 15)
    @ApiModelProperty(value = "废气治理模式")
    private String governanceModel;//废气治理模式
    @Excel(name = "营业面积", width = 15)
    @ApiModelProperty(value = "营业面积")
    private BigDecimal businessArea;//营业面积
    @Excel(name = "餐位数", width = 15)
    @ApiModelProperty(value = "餐位数")
    private int mealNumber;//餐位数
    @Excel(name = "标准折算灶头数", width = 15)
    @ApiModelProperty(value = "标准折算灶头数")
    private int stoveNumber;//标准折算灶头数
    @Excel(name = "排污许可证编码", width = 15)
    @ApiModelProperty(value = "排污许可证编码")
    private String permitCode;//排污许可证编码
    @Excel(name = "测点ID", width = 15)
    @ApiModelProperty(value = "测点ID")
    private String pointId;//测点ID
    @Excel(name = "起始时间（小时）", width = 15)
    @ApiModelProperty(value = "起始时间（小时）")
    private int startHour;//起始时间（小时）
    @Excel(name = "起始时间（分钟）", width = 15)
    @ApiModelProperty(value = "起始时间（分钟）")
    private int startMinute;//起始时间（分钟）
    @Excel(name = "结束时间（小时）", width = 15)
    @ApiModelProperty(value = "结束时间（小时）")
    private int endHour;//结束时间（小时）
    @Excel(name = "结束时间（分钟）", width = 15)
    @ApiModelProperty(value = "结束时间（分钟）")
    private int endMinute;//结束时间（分钟）
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private String status;//状态
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String creator;//创建人

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;//创建时间

    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private String updater;//修改人

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;//修改时间

    //uer 表关联
    private int fidRegion;

}
