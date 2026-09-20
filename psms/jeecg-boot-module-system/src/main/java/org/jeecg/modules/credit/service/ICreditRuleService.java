package org.jeecg.modules.credit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.credit.entity.CreditRule;

/**
 * @Description: 信用评价指标 Service
 */
public interface ICreditRuleService extends IService<CreditRule> {

    /**
     * 新增前校验：指标代码全局唯一
     * @param rule 规则
     * @return null=通过；否则返回错误信息
     */
    String checkBeforeSave(CreditRule rule);
}
