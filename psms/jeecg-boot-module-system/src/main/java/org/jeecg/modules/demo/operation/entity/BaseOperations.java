package org.jeecg.modules.demo.operation.entity;

import java.io.Serializable;
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
 * @Description: 运维
 * @Author: jeecg-boot
 * @Date:   2021-12-03
 * @Version: V1.0
 */
@Data
@TableName("base_operations")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="base_operations对象", description="运维")
public class BaseOperations implements Serializable {
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
	/**关联部门*/
	@Excel(name = "关联部门", width = 15)
    @ApiModelProperty(value = "关联部门")
    private Integer fidRegion;
	/**客户单位*/
	@Excel(name = "客户单位", width = 15)
    @ApiModelProperty(value = "客户单位")
    private String unit;
	/**关联账号*/
	@Excel(name = "关联账号", width = 15)
    @ApiModelProperty(value = "关联账号")
    private String userid;
	/**工单信息*/
	@Excel(name = "工单信息", width = 15)
    @ApiModelProperty(value = "工单信息")
    private String information;
	/**运维工程师*/
	@Excel(name = "运维工程师", width = 15)
    @ApiModelProperty(value = "运维工程师")
    private String operations;
	/**客户联系人*/
	@Excel(name = "客户联系人", width = 15)
    @ApiModelProperty(value = "客户联系人")
    private String customer;
	/**运维结果*/
	@Excel(name = "运维结果", width = 15)
    @ApiModelProperty(value = "运维结果")
    private String results;
	/**工单受理*/
	@Excel(name = "工单受理", width = 15)
    @ApiModelProperty(value = "工单受理")
    private String acceptance;
	/**维护类型*/
	@Excel(name = "维护类型", width = 15)
    @ApiModelProperty(value = "维护类型")
    private String types;

    /**维护类型*/
    @Excel(name = "报告", width = 15)
    @ApiModelProperty(value = "报告")
    private String report;

    @Excel(name = "服务厂商", width = 15)
    @ApiModelProperty(value = "服务厂商")
    private String manufacturerCode;//服务厂商

}
