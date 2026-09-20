package org.jeecg.modules.credit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.credit.entity.CreditRule;
import org.jeecg.modules.credit.mapper.CreditRuleMapper;
import org.jeecg.modules.credit.service.ICreditRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 信用评价指标 ServiceImpl
 */
@Service
public class CreditRuleServiceImpl extends ServiceImpl<CreditRuleMapper, CreditRule> implements ICreditRuleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkBeforeSave(CreditRule rule) {
        QueryWrapper<CreditRule> qw = new QueryWrapper<>();
        qw.eq("rule_code", rule.getRuleCode());
        if (rule.getId() != null) {
            qw.ne("id", rule.getId());
        }
        if (this.count(qw) > 0) {
            return "25001";
        }
        if (rule.getScoreValue() != null && rule.getScoreValue().abs().compareTo(new java.math.BigDecimal("999")) > 0) {
            return "25006";
        }
        return null;
    }
}
