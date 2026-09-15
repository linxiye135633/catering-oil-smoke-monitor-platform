package org.jeecg.modules.demo.institution.entity;

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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("base_institution")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "base_institution对象", description = "行政机构表")
public class Institution implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @Excel(name = "机构编号", width = 15)
    @ApiModelProperty(value = "机构编号")
    private String code;

    @Excel(name = "机构名称", width = 15)
    @ApiModelProperty(value = "机构名称")
    private String name;

    @Excel(name = "中文简称", width = 15)
    @ApiModelProperty(value = "中文简称")
    private String shortNameCn;

    @Excel(name = "英文简称", width = 15)
    @ApiModelProperty(value = "英文简称")
    private String shortNameEn;

    @Excel(name = "行政区域", width = 15)
    @ApiModelProperty(value = "行政区域")
    private String area;

    @Excel(name = "机构地址", width = 15)
    @ApiModelProperty(value = "机构地址")
    private String address;

    @Excel(name = "默认管理员登录账号", width = 15)
    @ApiModelProperty(value = "默认管理员登录账号")
    private String adminCode;

    @Excel(name = "登录密码", width = 15)
    @ApiModelProperty(value = "登录密码")
    private String adminPwd;

    @Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private String contact;

    @Excel(name = "联系人手机号", width = 15)
    @ApiModelProperty(value = "联系人手机号")
    private String contactTel;

    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private BigDecimal lng;

    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private BigDecimal lat;

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

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商
}
