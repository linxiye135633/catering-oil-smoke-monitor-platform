package org.jeecg.modules.demo.company.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.company.mapper.CompanyMapper;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.psms.base.mapper.PsmsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Resource
    private CompanyMapper companyMapper;

    @Override
    public Page<Map<String, Object>> queryList(Page<Map<String, Object>> page, List<String> ids,int fidRegion) {
        return companyMapper.queryList(page,ids,fidRegion);
    }

    @Override
    public List<Map<String, Object>> queryList(List<String> ids,int fidRegion) {
        return companyMapper.queryLists(ids,fidRegion);
    }
}
