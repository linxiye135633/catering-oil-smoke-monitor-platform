package org.jeecg.modules.demo.institutionperson.entity;


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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("base_institution_person")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "base_institution_person对象", description = "机构人员表")
public class InstitutionPerson implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @Excel(name = "人员编号", width = 15)
    @ApiModelProperty(value = "人员编号")
    private String code;

    @Excel(name = "人员姓名", width = 15)
    @ApiModelProperty(value = "人员姓名")
    private String name;

    @Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @Excel(name = "部门", width = 15)
    @ApiModelProperty(value = "部门")
    private String department;

    @Excel(name = "职位", width = 15)
    @ApiModelProperty(value = "职位")
    private String position;

    @Excel(name = "行政机构", width = 15)
    @ApiModelProperty(value = "行政机构")
    private String institutionCode;

    @Excel(name = "所属组织", width = 15)
    @ApiModelProperty(value = "所属组织")
    private String orgCode;

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

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商

}
