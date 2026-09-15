package org.jeecg.modules.demo.companyaudit.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 餐饮企业审核
 * @Author: jeecg-boot
 * @Date:   2021-12-01
 * @Version: V1.0
 */
@Data
@TableName("base_company_audit")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="base_company_audit对象", description="餐饮企业审核")
public class BaseCompanyAudit implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**告警编号*/
	@Excel(name = "告警编号", width = 15)
    @ApiModelProperty(value = "告警编号")
    private String companyId;
	/**申诉标题*/
	@Excel(name = "申诉标题", width = 15)
    @ApiModelProperty(value = "申诉标题")
    private String title;
	/**单位名称*/
	@Excel(name = "单位名称", width = 15)
    @ApiModelProperty(value = "单位名称")
    private String name;
	/**申诉开始时间*/
	@Excel(name = "申诉开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "申诉开始时间")
    private Date auditStartTime;
	/**申诉结束时间*/
	@Excel(name = "申诉结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "申诉结束时间")
    private Date auditEndTime;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private String linkman;
	/**申诉类型*/
	@Excel(name = "申诉类型", width = 15)
    @ApiModelProperty(value = "申诉类型")
    private String auditType;
	/**审核状态*/
	@Excel(name = "审核状态", width = 15)
    @ApiModelProperty(value = "审核状态")
    private String status;
	/**申诉详情*/
	@Excel(name = "申诉详情", width = 15)
    @ApiModelProperty(value = "申诉详情")
    private String auditText;

    @Excel(name = "关联账号权限", width = 15)
    @ApiModelProperty(value = "关联账号权限")
    private String fidRegion;

    @Excel(name = "关联标签表", width = 15)
    @ApiModelProperty(value = "关联标签表")
    private String auditId;

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商

}
