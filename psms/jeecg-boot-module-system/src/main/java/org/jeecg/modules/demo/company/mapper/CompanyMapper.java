package org.jeecg.modules.demo.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.demo.company.entity.Company;

import java.util.List;
import java.util.Map;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
public interface CompanyMapper extends BaseMapper<Company> {

    Page<Map<String, Object>> queryList(Page<Map<String, Object>> page, List<String> ids,int fidRegion);

    List<Map<String, Object>> queryLists(List<String> ids,int fidRegion);
}
