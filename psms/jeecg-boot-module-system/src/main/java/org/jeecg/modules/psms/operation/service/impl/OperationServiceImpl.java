package org.jeecg.modules.psms.operation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psms.operation.mapper.OperationMapper;
import org.jeecg.modules.psms.operation.service.OperationService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Map<String,Object>> implements OperationService {


}
