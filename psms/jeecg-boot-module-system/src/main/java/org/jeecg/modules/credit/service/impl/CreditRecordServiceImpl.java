package org.jeecg.modules.credit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.credit.entity.CreditRecord;
import org.jeecg.modules.credit.mapper.CreditRecordMapper;
import org.jeecg.modules.credit.service.ICreditRecordService;
import org.springframework.stereotype.Service;

/**
 * @Description: 企业信用评价结果 ServiceImpl
 */
@Service
public class CreditRecordServiceImpl extends ServiceImpl<CreditRecordMapper, CreditRecord> implements ICreditRecordService {
}
