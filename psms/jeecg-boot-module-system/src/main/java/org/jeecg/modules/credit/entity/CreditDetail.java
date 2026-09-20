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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 信用变动明细（每笔加减分可追溯）
 * @Version: v2.0
 */
@Data
@TableName("bu_credit_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bu_credit_detail对象", description = "信用变动明细")
public class CreditDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**评价结果ID*/
    @ApiModelProperty(value = "评价结果ID")
    private String recordId;
    /**规则ID*/
    @ApiModelProperty(value = "规则ID")
    private String ruleId;
    /**规则代码*/
    @ApiModelProperty(value = "规则代码（快照语义）")
    private String ruleCode;
    /**本笔变动分*/
    @ApiModelProperty(value = "本笔变动分（负=扣分）")
    private BigDecimal changeScore;
    /**触发次数*/
    @ApiModelProperty(value = "触发次数")
    private Integer times;
    /**触发依据单号*/
    @ApiModelProperty(value = "触发依据单号（告警批次/整改单号）")
    private String sourceNo;
    /**依据类型*/
    @ApiModelProperty(value = "依据类型")
    @Dict(dicCode = "credit_source")
    private String sourceType;
    /**备注*/
    @ApiModelProperty(value = "备注（含降级说明）")
    private String remark;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
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
