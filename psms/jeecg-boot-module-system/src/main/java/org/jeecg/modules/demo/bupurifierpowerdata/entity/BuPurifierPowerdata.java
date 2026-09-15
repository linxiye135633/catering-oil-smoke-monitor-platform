package org.jeecg.modules.demo.bupurifierpowerdata.entity;

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
 * @Description: 净化器电源模块数据
 * @Author: jeecg-boot
 * @Date:   2021-10-28
 * @Version: V1.0
 */
@Data
@TableName("bu_purifier_powerdata")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bu_purifier_powerdata对象", description="净化器电源模块数据")
public class BuPurifierPowerdata implements Serializable {
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
	/**净化器电源模块序号*/
	@Excel(name = "净化器电源模块序号", width = 15)
    @ApiModelProperty(value = "净化器电源模块序号")
    private String powerNumber;
	/**二次电压*/
	@Excel(name = "二次电压", width = 15)
    @ApiModelProperty(value = "二次电压")
    private BigDecimal secondVoltage;
	/**二次电流*/
	@Excel(name = "二次电流", width = 15)
    @ApiModelProperty(value = "二次电流")
    private BigDecimal secondCurrent;
	/**电源输出功率*/
	@Excel(name = "电源输出功率", width = 15)
    @ApiModelProperty(value = "电源输出功率")
    private Integer outputPower;
	/**累计工作时长*/
	@Excel(name = "累计工作时长", width = 15)
    @ApiModelProperty(value = "累计工作时长")
    private Integer workHour;
	/**电源开关状态*/
	@Excel(name = "电源开关状态", width = 15)
    @ApiModelProperty(value = "电源开关状态")
    private Integer powerOpenStatus;
	/**电源保护状态*/
	@Excel(name = "电源保护状态", width = 15)
    @ApiModelProperty(value = "电源保护状态")
    private Integer powerProtectStatus;
	/**电源短路保护*/
	@Excel(name = "电源短路保护", width = 15)
    @ApiModelProperty(value = "电源短路保护")
    private Integer powerCircuitProtect;
	/**电源超温保护*/
	@Excel(name = "电源超温保护", width = 15)
    @ApiModelProperty(value = "电源超温保护")
    private Integer powerOvertempProtect;
	/**手自动模式*/
	@Excel(name = "手自动模式", width = 15)
    @ApiModelProperty(value = "手自动模式")
    private Integer autoModel;
	/**下挂电源模块总数量*/
	@Excel(name = "下挂电源模块总数量", width = 15)
    @ApiModelProperty(value = "下挂电源模块总数量")
    private Integer powerModuleNum;
    /**油烟数据id*/
    @Excel(name = "油烟数据id", width = 15)
    @ApiModelProperty(value = "油烟数据id")
    private Integer buLampblackId;

}
