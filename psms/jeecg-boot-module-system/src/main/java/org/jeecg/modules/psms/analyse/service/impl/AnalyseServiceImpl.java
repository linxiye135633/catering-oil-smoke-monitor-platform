package org.jeecg.modules.psms.analyse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psms.analyse.mapper.AnalyseMapper;
import org.jeecg.modules.psms.analyse.service.AnalyseService;
import org.jeecg.modules.psms.supervision.mapper.SupervisionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class AnalyseServiceImpl extends ServiceImpl<AnalyseMapper, Map<String,Object>> implements AnalyseService {
    @Resource
    private AnalyseMapper analyseMapper;
    @Override
    public Map<String, Object> units(String tableLampblack, int fidRegion, String format,String formatDay) {
        return analyseMapper.units(tableLampblack,fidRegion,format,formatDay);
    }

    @Override
    public Map<String, Object> moreNum(String tableLampblack, int fidRegion, String format, String formatDay,String companyId) {
        return analyseMapper.moreNum(tableLampblack,fidRegion,format,formatDay,companyId);
    }

    @Override
    public Map<String, Object> moreNums(String tableLampblack, int fidRegion, String format, String formatDay, String companyId) {
        return analyseMapper.moreNums(tableLampblack,fidRegion,format,formatDay,companyId);
    }

    @Override
    public int runHourTime(String tableLampblack, int fidRegion, String formatDay, String companyId) {
        return analyseMapper.runHourTime(tableLampblack,fidRegion,formatDay,companyId);
    }
}
