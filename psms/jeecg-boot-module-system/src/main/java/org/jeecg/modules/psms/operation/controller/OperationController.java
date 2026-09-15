package org.jeecg.modules.psms.operation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags="psms-6-智能运维-operation")
@Slf4j
@RestController
@RequestMapping("/operation")
public class OperationController {
    //@AutoLog(value = "测试")
    //@ApiOperation(value="测试", notes="测试")
    @GetMapping(value = "/test")
    public Result<?> test() {
        return Result.OK("测试");
    }
}
