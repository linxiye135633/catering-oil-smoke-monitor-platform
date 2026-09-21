package org.jeecg.modules.stat.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.stat.ai.LlmClient;
import org.jeecg.modules.stat.mapper.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WeeklyReportController 大屏AI周报（#E-56 v0，任涵艺；COULD加分项）
 *
 * 原则（B组同款）：数字一律来自SQL统计，大模型只做文字层面总结——
 * 输入=本周统计JSON（趋势/处理率/行业），输出=结构化周报JSON；
 * 大模型3s超时/非法JSON → 降级返回纯统计数字并标识 degraded:true（FR-E10验收）。
 */
@Api(tags = "E组2.0-AI周报")
@Slf4j
@RestController
@RequestMapping("/v2/stat")
public class WeeklyReportController {

    @Autowired
    private StatMapper statMapper;
    @Autowired
    private LlmClient llmClient;

    @AutoLog(value = "AI周报-生成")
    @ApiOperation(value = "大屏AI周报摘要", notes = "SQL统计+大模型润色，超时降级纯统计")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/weekly-report")
    public Result<?> weeklyReport(@RequestParam(name = "cycle", required = false) String cycle) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if (fidRegion == 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        if (cycle == null || cycle.isEmpty()) {
            cycle = new SimpleDateFormat("yyyy-MM").format(new Date());
        }

        // 1. 数字全部来自SQL（口径KD-06/KD-08/KD-07，绝不交给大模型算）
        Map<String, Object> stats = new HashMap<>(8);
        List<Map<String, Object>> trend = statMapper.selectAlarmTrend(7, fidRegion);
        Map<String, Object> rate = statMapper.selectProcessRate(cycle, fidRegion);
        List<Map<String, Object>> industry = statMapper.selectIndustryDistribution(fidRegion);
        stats.put("weekTrend", trend);
        stats.put("processRate", rate);
        stats.put("industryTop3", industry.size() > 3 ? industry.subList(0, 3) : industry);

        Map<String, Object> body = new HashMap<>(8);
        body.put("stats", stats);

        // 2. 大模型仅做文字总结（#E-57 Prompt工程深化版）：
        //    结构化模板（角色/任务/输入Schema/输出Schema/约束）→ 解析校验 →
        //    字段缺失时带错误反馈重试一次 → 仍失败降级纯统计（FR-E10）
        String statsJson = com.alibaba.fastjson.JSON.toJSONString(stats);
        String prompt = buildPrompt(statsJson);
        JSONObject llmOut = llmClient.chatForJson("weekly-report", prompt);
        if (llmOut != null && !isValidReport(llmOut)) {
            String retryPrompt = prompt + "\n你上次的输出未通过校验（summary为空或超长），"
                    + "请严格按输出Schema重新生成。";
            llmOut = llmClient.chatForJson("weekly-report-retry", retryPrompt);
        }
        if (llmOut != null && isValidReport(llmOut)) {
            body.put("summary", trim(llmOut.getString("summary"), 300));
            body.put("riskHint", trim(llmOut.getString("riskHint"), 120));
            body.put("suggestion", trim(llmOut.getString("suggestion"), 120));
            body.put("degraded", false);
        } else {
            body.put("summary", buildFallbackSummary(stats));
            body.put("degraded", true); // FR-E10：降级模式标识（纯统计，绝无幻觉数字）
        }
        return Result.OK(body);
    }

    /** #E-57 结构化Prompt模板（角色-任务-输入Schema-输出Schema-约束五段） */
    private String buildPrompt(String statsJson) {
        return "角色：你是油烟监管平台的周报助手。\n"
                + "任务：基于下面的监管统计JSON，生成一句话周报摘要与两条提示。\n"
                + "输入Schema：{weekTrend:[{statDate,alarmCount}],processRate:{total,cleared},"
                + "industryTop3:[{industryCode,companyCount}]}\n"
                + "输入数据：" + statsJson + "\n"
                + "输出Schema（严格JSON，三个字段都必填）："
                + "{\"summary\":\"80~150字周报总结\",\"riskHint\":\"一句话风险提示\",\"suggestion\":\"一句话监管建议\"}\n"
                + "约束：只依据输入数据描述，禁止编造未出现的数字；不要使用markdown格式。";
    }

    /** 输出校验：三字段齐备且summary非空不超长 */
    private boolean isValidReport(JSONObject o) {
        return o != null
                && o.getString("summary") != null && !o.getString("summary").trim().isEmpty()
                && o.getString("summary").length() <= 400
                && o.getString("riskHint") != null
                && o.getString("suggestion") != null;
    }

    /** 降级摘要：纯统计口径生成（数字来自SQL，绝无幻觉） */
    @SuppressWarnings("unchecked")
    private String buildFallbackSummary(Map<String, Object> stats) {
        try {
            List<Map<String, Object>> trend = (List<Map<String, Object>>) stats.get("weekTrend");
            int total = trend == null ? 0 : trend.stream()
                    .mapToInt(t -> Integer.parseInt(String.valueOf(t.get("alarmCount") == null ? 0 : t.get("alarmCount")))).sum();
            Map<String, Object> rate = (Map<String, Object>) stats.get("processRate");
            long rTotal = rate == null ? 0 : Long.parseLong(String.valueOf(rate.getOrDefault("total", 0)));
            long rCleared = rate == null ? 0 : Long.parseLong(String.valueOf(rate.getOrDefault("cleared", 0)));
            double r = rTotal == 0 ? 0 : Math.round(rCleared * 1000.0 / rTotal) / 10.0;
            return String.format("本周新增告警%d条，告警处理率%.1f%%（已解除%d/共%d）。", total, r, rCleared, rTotal);
        } catch (Exception e) {
            return "本周数据见统计区。";
        }
    }

    private String trim(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }
}
