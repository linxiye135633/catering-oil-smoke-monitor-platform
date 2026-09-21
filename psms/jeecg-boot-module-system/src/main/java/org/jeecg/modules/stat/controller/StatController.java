package org.jeecg.modules.stat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.stat.mapper.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * StatController 统计接口（#E-52/#E-53，任涵艺）
 * 口径红线：趋势查历史主表 bu_lampblack_data（跨月禁查单月分表），
 * 行业分布关联 base_company_type 字典表，全部与《口径对齐清单》KD-06/KD-07 一致。
 */
@Api(tags = "E组2.0-统计口径接口")
@Slf4j
@RestController
@RequestMapping("/v2/stat")
public class StatController {

    @Autowired
    private StatMapper statMapper;

    /**
     * #E-52 告警趋势（近N日，默认7）：分子=每日新增风险记录（KD-06同口径）
     * 跨日切分用 create_time（避免 yyyymmdd 批次偏差）
     */
    @AutoLog(value = "统计-告警趋势")
    @ApiOperation(value = "统计-告警趋势(近N日)", notes = "口径同1.0告警分析")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/alarm-trend")
    public Result<?> alarmTrend(@RequestParam(name = "days", defaultValue = "7") Integer days) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if (fidRegion == 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        int safeDays = Math.min(Math.max(days, 1), 31);
        List<Map<String, Object>> trend = statMapper.selectAlarmTrend(safeDays, fidRegion);
        return Result.OK(trend);
    }

    /**
     * #E-53 行业分布：按 business_category 分组企业数（KD-07同口径，
     * Sprint2修正：base_company_type 为警告表，编码由前端字典翻译）
     */
    @AutoLog(value = "统计-行业分布")
    @ApiOperation(value = "统计-行业分布", notes = "business_category编码分组")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/industry")
    public Result<?> industry() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if (fidRegion == 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        List<Map<String, Object>> industry = statMapper.selectIndustryDistribution(fidRegion);
        return Result.OK(industry);
    }

    /**
     * #E-54 告警处理率（KD-08）：分母=周期警告总数，分子=已解除数（is_on=1），
     * 数据源=base_company_type（1.0解除风险控制同表），与1.0报表可核对
     */
    @AutoLog(value = "统计-告警处理率")
    @ApiOperation(value = "统计-告警处理率", notes = "KD-08口径：已解除/周期警告总数")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/process-rate")
    public Result<?> processRate(@RequestParam(name = "cycle", required = false) String cycle) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if (fidRegion == 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        if (cycle == null || cycle.isEmpty()) {
            cycle = new java.text.SimpleDateFormat("yyyy-MM").format(new java.util.Date());
        }
        Map<String, Object> stat = statMapper.selectProcessRate(cycle, fidRegion);
        Map<String, Object> body = new java.util.HashMap<>(8);
        long total = stat == null ? 0 : Long.parseLong(String.valueOf(stat.getOrDefault("total", 0)));
        long cleared = stat == null ? 0 : Long.parseLong(String.valueOf(stat.getOrDefault("cleared", 0)));
        body.put("cycle", cycle);
        body.put("total", total);
        body.put("cleared", cleared);
        body.put("rate", total == 0 ? 0 : Math.round(cleared * 1000.0 / total) / 10.0);
        return Result.OK(body);
    }
}
