package org.jeecg.modules.credit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 企业信用评价结果（每周期每企业一条）
 * @Version: v2.0
 */
@Data
@TableName("bu_credit_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bu_credit_record对象", description = "企业信用评价结果")
public class CreditRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**企业ID*/
    @ApiModelProperty(value = "企业ID")
    private String companyId;
    /**企业名称*/
    @Excel(name = "企业名称", width = 15)
    @ApiModelProperty(value = "企业名称（冗余）")
    private String companyName;
    /**评价周期*/
    @Excel(name = "评价周期", width = 15)
    @ApiModelProperty(value = "评价周期（如2026-09）")
    private String cycle;
    /**周期总分*/
    @Excel(name = "周期总分", width = 15)
    @ApiModelProperty(value = "周期总分（基准100）")
    private BigDecimal totalScore;
    /**信用等级*/
    @Excel(name = "信用等级", width = 15, dicCode = "credit_level")
    @ApiModelProperty(value = "信用等级")
    @Dict(dicCode = "credit_level")
    private String level;
    /**规则集快照*/
    @ApiModelProperty(value = "当期生效规则集快照JSON（规则变更不回溯）")
    private String snapshot;
    /**公示状态*/
    @Excel(name = "公示状态", width = 15, dicCode = "credit_publish")
    @ApiModelProperty(value = "公示状态")
    @Dict(dicCode = "credit_publish")
    private String publishStatus;
    /**计算完成时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "计算完成时间")
    private Date calcTime;
    /**创建人*/
    @ApiModelProperty(value = "创建人（定时任务为system）")
    private String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**逻辑删除*/
    @TableLogic
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
}
