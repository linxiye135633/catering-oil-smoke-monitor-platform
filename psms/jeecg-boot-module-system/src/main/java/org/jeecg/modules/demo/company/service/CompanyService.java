package org.jeecg.modules.demo.company.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.company.entity.Company;

import java.util.List;
import java.util.Map;

public interface CompanyService extends IService<Company> {

    Page<Map<String, Object>> queryList(Page<Map<String, Object>> page, List<String> ids,int fidRegion);

    List<Map<String, Object>> queryList(List<String> ids,int fidRegion);
}
