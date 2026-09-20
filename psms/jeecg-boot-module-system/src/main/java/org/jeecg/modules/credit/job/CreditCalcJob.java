package org.jeecg.modules.credit.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.credit.engine.CreditEngine;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CreditCalcJob 信用评价周期计算定时任务（#E-14，李晓倩）
 *
 * Jeecg Quartz 规范：实现 org.quartz.Job，通过 sys_quartz_job 表注册（INSERT见
 * db/v2_upgrade.sql §7，cron=每月1日03:00）；与 CreditRuleController 手动触发共用
 * CreditEngine（内部Redis锁防并发，25004防重复）。任务失败不影响下月执行（Quartz
 * misfire策略按表配置，课程环境默认简单恢复）。
 */
@Slf4j
public class CreditCalcJob implements Job {

    @Autowired
    private CreditEngine creditEngine;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // 周期为空 → 引擎内部取当月（yyyy-MM）
            Result<?> result = creditEngine.calculate(null);
            log.info("[CreditCalcJob] 信用周期计算完成：success={}, message={}",
                    result.isSuccess(), result.getMessage());
            if (!result.isSuccess()) {
                // 业务校验失败（如规则集不合法）记录告警日志，站内信通知接入 #E-14 联调
                log.warn("[CreditCalcJob] 计算未执行：{}", result.getMessage());
            }
        } catch (Exception e) {
            log.error("[CreditCalcJob] 定时计算异常（保留上期结果，不影响已发布公示）", e);
            throw new JobExecutionException(e);
        }
    }
}
