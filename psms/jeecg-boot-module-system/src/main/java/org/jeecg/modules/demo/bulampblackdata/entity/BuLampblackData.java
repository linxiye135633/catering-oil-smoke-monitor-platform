package org.jeecg.modules.demo.bulampblackdata.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 油烟数据
 * @Author: jeecg-boot
 * @Date:   2021-10-28
 * @Version: V1.0
 */
@Data
@TableName("bu_lampblack_data")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bu_lampblack_data对象", description="油烟数据")
public class BuLampblackData implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String creator;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updater;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**测点ID*/
	@Excel(name = "测点ID", width = 15)
    @ApiModelProperty(value = "测点ID")
    private String pointMac;
	/**油烟浓度瞬时值*/
	@Excel(name = "油烟浓度瞬时值", width = 15)
    @ApiModelProperty(value = "油烟浓度瞬时值")
    private float lampblackData;
	/**颗粒物浓度瞬时值*/
	@Excel(name = "颗粒物浓度瞬时值", width = 15)
    @ApiModelProperty(value = "颗粒物浓度瞬时值")
    private float matterData;
	/**非甲烷总烃瞬时值*/
	@Excel(name = "非甲烷总烃瞬时值", width = 15)
    @ApiModelProperty(value = "非甲烷总烃瞬时值")
    private float nmhcData;
	/**油烟浓度10分钟平均值*/
	@Excel(name = "油烟浓度10分钟平均值", width = 15)
    @ApiModelProperty(value = "油烟浓度10分钟平均值")
    private float lampblackAvgdata;
	/**颗粒物浓度10分钟平均值*/
	@Excel(name = "颗粒物浓度10分钟平均值", width = 15)
    @ApiModelProperty(value = "颗粒物浓度10分钟平均值")
    private float matterAvgdata;
	/**非甲烷总烃10分钟平均值*/
	@Excel(name = "非甲烷总烃10分钟平均值", width = 15)
    @ApiModelProperty(value = "非甲烷总烃10分钟平均值")
    private float nmhcAvgdata;
	/**温度*/
	@Excel(name = "温度", width = 15)
    @ApiModelProperty(value = "温度")
    private Integer temp;
	/**湿度*/
	@Excel(name = "湿度", width = 15)
    @ApiModelProperty(value = "湿度")
    private Integer hum;
	/**风机状态*/
	@Excel(name = "风机状态", width = 15)
    @ApiModelProperty(value = "风机状态")
    private Integer fanStatus;
	/**净化器状态*/
	@Excel(name = "净化器状态", width = 15)
    @ApiModelProperty(value = "净化器状态")
    private Integer purifierStatus;
	/**风机工作电流*/
	@Excel(name = "风机工作电流", width = 15)
    @ApiModelProperty(value = "风机工作电流")
    private float fanCurrent;
	/**净化器一次电流*/
	@Excel(name = "净化器一次电流", width = 15)
    @ApiModelProperty(value = "净化器一次电流")
    private float purifierCurrent;
	/**净化器前后压差*/
	@Excel(name = "净化器前后压差", width = 15)
    @ApiModelProperty(value = "净化器前后压差")
    private float purifierVoltageDiff;
	/**流速*/
	@Excel(name = "流速", width = 15)
    @ApiModelProperty(value = "流速")
    private float currentSpeed;

}
