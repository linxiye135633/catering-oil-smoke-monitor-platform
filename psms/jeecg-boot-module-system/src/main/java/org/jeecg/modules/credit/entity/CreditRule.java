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
 * @Description: 信用评价指标
 * @Version: v2.0
 */
@Data
@TableName("bu_credit_rule")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bu_credit_rule对象", description = "信用评价指标")
public class CreditRule implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**指标代码*/
    @Excel(name = "指标代码", width = 15)
    @ApiModelProperty(value = "指标代码")
    private String ruleCode;
    /**指标名称*/
    @Excel(name = "指标名称", width = 15)
    @ApiModelProperty(value = "指标名称")
    private String ruleName;
    /**分值类型*/
    @Excel(name = "分值类型", width = 15, dicCode = "credit_score_type")
    @ApiModelProperty(value = "分值类型")
    @Dict(dicCode = "credit_score_type")
    private String scoreType;
    /**单次分值*/
    @Excel(name = "单次分值", width = 15)
    @ApiModelProperty(value = "单次分值（减分存负值）")
    private BigDecimal scoreValue;
    /**权重*/
    @Excel(name = "权重", width = 15)
    @ApiModelProperty(value = "权重")
    private BigDecimal weight;
    /**评价周期类型*/
    @Excel(name = "评价周期类型", width = 15, dicCode = "credit_cycle")
    @ApiModelProperty(value = "评价周期类型")
    @Dict(dicCode = "credit_cycle")
    private String cycleType;
    /**取数来源*/
    @Excel(name = "取数来源", width = 15, dicCode = "credit_source")
    @ApiModelProperty(value = "取数来源")
    @Dict(dicCode = "credit_source")
    private String dataSource;
    /**计分次数上限*/
    @Excel(name = "计分次数上限", width = 15)
    @ApiModelProperty(value = "计分次数上限（null不限）")
    private Integer maxTimes;
    /**是否启用*/
    @Excel(name = "是否启用", width = 15, dicCode = "enable_status")
    @ApiModelProperty(value = "是否启用")
    @Dict(dicCode = "enable_status")
    private String enabled;
    /**规则说明*/
    @Excel(name = "规则说明", width = 15)
    @ApiModelProperty(value = "规则说明")
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
