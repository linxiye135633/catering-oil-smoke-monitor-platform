package org.jeecg.modules.credit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.credit.entity.CreditDetail;
import org.jeecg.modules.credit.mapper.CreditDetailMapper;
import org.jeecg.modules.credit.service.ICreditDetailService;
import org.springframework.stereotype.Service;

/**
 * @Description: 信用变动明细 ServiceImpl
 */
@Service
public class CreditDetailServiceImpl extends ServiceImpl<CreditDetailMapper, CreditDetail> implements ICreditDetailService {
}
