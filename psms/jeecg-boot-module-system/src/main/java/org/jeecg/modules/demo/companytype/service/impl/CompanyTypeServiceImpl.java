package org.jeecg.modules.demo.companytype.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.companytype.entity.CompanyType;
import org.jeecg.modules.demo.companytype.mapper.CompanyTypeMapper;
import org.jeecg.modules.demo.companytype.service.CompanyTypeService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CompanyTypeServiceImpl extends ServiceImpl<CompanyTypeMapper, CompanyType> implements CompanyTypeService {

}
