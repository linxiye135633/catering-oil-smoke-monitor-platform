package org.jeecg.modules.psms.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class LampBlacks {
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    private String gmtTime;

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
}
