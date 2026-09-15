package org.jeecg.modules.demo.statementdata.entity;

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
 * @Description: 统计分析报告
 * @Author: jeecg-boot
 * @Date:   2021-12-09
 * @Version: V1.0
 */
@Data
@TableName("base_statement_data")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="base_statement_data对象", description="统计分析报告")
public class BaseStatementData implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**监测天数*/
	@Excel(name = "监测天数", width = 15)
    @ApiModelProperty(value = "监测天数")
    private Integer days;
	/**企业名称*/
	@Excel(name = "企业名称", width = 15)
    @ApiModelProperty(value = "企业名称")
    private String name;
	/**数据采集量*/
	@Excel(name = "数据采集量", width = 15)
    @ApiModelProperty(value = "数据采集量")
    private Integer dataAmount;
	/**数据完整率*/
	@Excel(name = "数据完整率", width = 15)
    @ApiModelProperty(value = "数据完整率")
    private BigDecimal dataRate;
	/**风机开启时长*/
	@Excel(name = "风机开启时长", width = 15)
    @ApiModelProperty(value = "风机开启时长")
    private Integer draughtTime;
	/**净化器联动时长*/
	@Excel(name = "净化器联动时长", width = 15)
    @ApiModelProperty(value = "净化器联动时长")
    private Integer linkageTime;
	/**净化器联动率*/
	@Excel(name = "净化器联动率", width = 15)
    @ApiModelProperty(value = "净化器联动率")
    private BigDecimal purifierLinkageRate;
	/**油烟浓度0-1时长*/
	@Excel(name = "油烟浓度0-1时长", width = 15)
    @ApiModelProperty(value = "油烟浓度0-1时长")
    private Integer lampblackHour1;
	/**油烟浓度 1.01-2时长*/
	@Excel(name = "油烟浓度 1.01-2时长", width = 15)
    @ApiModelProperty(value = "油烟浓度 1.01-2时长")
    private Integer lampblackHour2;
	/**油烟浓度 2.01-4时长*/
	@Excel(name = "油烟浓度 2.01-4时长", width = 15)
    @ApiModelProperty(value = "油烟浓度 2.01-4时长")
    private Integer lampblackHour3;
	/**油烟浓度 4.01+时长*/
	@Excel(name = "油烟浓度 4.01+时长", width = 15)
    @ApiModelProperty(value = "油烟浓度 4.01+时长")
    private Integer lampblackHour4;
	/**超标总时长*/
	@Excel(name = "超标总时长", width = 15)
    @ApiModelProperty(value = "超标总时长")
    private Integer lampblackAllHour;
	/**标称*/
	@Excel(name = "标称", width = 15)
    @ApiModelProperty(value = "标称")
    private String nominal;
	/**最大*/
	@Excel(name = "最大", width = 15)
    @ApiModelProperty(value = "最大")
    private Integer maximum;
	/**最小*/
	@Excel(name = "最小", width = 15)
    @ApiModelProperty(value = "最小")
    private Integer minimum;
	/**平均*/
	@Excel(name = "平均", width = 15)
    @ApiModelProperty(value = "平均")
    private Integer average;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String comment;
	/**编码*/
	@Excel(name = "编码", width = 15)
    @ApiModelProperty(value = "编码")
    private Integer code;

    private int fidRegion;

    @Excel(name = "测点MAC", width = 15)
    @ApiModelProperty(value = "测点MAC")
    private String pointMac;//测点MAC

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商

}
