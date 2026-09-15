package org.jeecg.modules.psms.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("base_lamp_blacks")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="LampBlack", description="南京油烟数据统计上传")
public class LampBlack {
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    private int code;
    private String msg;

//    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "统计日期")
    private String gmtTime;

    /**创建日期*/
    //@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    //@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private String createTime;

    @ApiModelProperty(value = "油烟监测总数")
    private int yyJczs;

    @ApiModelProperty(value = "在线数")
    private int yyZxs;

    @ApiModelProperty(value = "超标数")
    private int yyCbs;

    @ApiModelProperty(value = "离线数")
    private int yyLxs;

    @ApiModelProperty(value = "统计来源")
    private String yyLy;

    private int fidRegion;

}
