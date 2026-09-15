package org.jeecg.modules.demo.institution.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.institution.entity.Institution;
import org.jeecg.modules.demo.institution.mapper.InstitutionMapper;
import org.jeecg.modules.demo.institution.service.InstitutionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstitutionServiceImpl extends ServiceImpl<InstitutionMapper, Institution> implements InstitutionService {

}
