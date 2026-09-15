package org.jeecg.modules.psms.statement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="psms-5-统计报表-statement")
@Slf4j
@RestController
@RequestMapping("/statement")
public class StatementController {
    @AutoLog(value = "测试")
    @ApiOperation(value="测试", notes="测试")
    @GetMapping(value = "/test")
    public Result<?> test() {
        return Result.OK("测试");
    }
}
