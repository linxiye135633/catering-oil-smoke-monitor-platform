package org.jeecg.modules.psms.supervision.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psms.supervision.mapper.SupervisionMapper;
import org.jeecg.modules.psms.supervision.service.SupervisionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SupervisionServiceImpl extends ServiceImpl<SupervisionMapper, Map<String,Object>> implements SupervisionService {
    @Resource
    private SupervisionMapper supervisionMapper;

    @Override
    public List<Map<String, Object>> stopMac(String id, String startTime,String endTime) {
        return supervisionMapper.stopMac(id, startTime,endTime);
    }

    @Override
    public Map<String,Object> getLampblackCount(String tableLampblack, String companyId, String format) {
        return supervisionMapper.getLampblackCount(tableLampblack,companyId,format);
    }

    @Override
    public int getPurifierCount(String tableLampblack,String tablePurifier, String companyId, String format) {
        return supervisionMapper.getPurifierCount(tableLampblack,tablePurifier,companyId,format);
    }

    @Override
    public Map<String, Object> secondaryVoltage(String tableLampblack, String tablePurifier, String companyId, String format) {
        return supervisionMapper.secondaryVoltage(tableLampblack,tablePurifier,companyId,format);
    }

    @Override
    public List<Map<String, Object>> getPointLog(List<String> macs, String format) {
        return supervisionMapper.getPointLog(macs,format);
    }

    @Override
    public int getNetwork(String tableLampblack, String companyId, String format) {
        return supervisionMapper.getNetwork(tableLampblack,companyId,format);
    }

    @Override
    public Map<String, Object> smokeCount(String tableLampblack, String companyId, String format) {
        return supervisionMapper.smokeCount(tableLampblack,companyId,format);
    }

    @Override
    public Map<String, Object> highVoltage(String tableLampblack, String tablePurifier, String companyId, String format) {
        return supervisionMapper.highVoltage(tableLampblack,tablePurifier,companyId,format);
    }

    @Override
    public Map<String, Object> getLabel(String fidRegion, String format) {
        return supervisionMapper.getLabel(fidRegion, format);
    }

    @Override
    public Map<String, Object> getRiskCount(String format, int i, String fidRegion) {
        return supervisionMapper.getRiskCount(format, i, fidRegion);
    }

    @Override
    public int getRiskVCount(String format, int i, String fidRegion) {
        return supervisionMapper.getRiskVCount(format, i, fidRegion);
    }

    @Override
    public Page<Map<String, Object>> getNewRisk(Page<Map<String, Object>> page, int fidRegion, String format) {
        return supervisionMapper.getNewRisk(page,fidRegion,format);
    }

    @Override
    public void getClearRisk(int fidRegion, String format, String name) {
        supervisionMapper.getClearRisk(fidRegion,format,name);
    }

    @Override
    public Page<Map<String, Object>> getClearRiskList(Page<Map<String, Object>> page, int fidRegion, String format) {
        return supervisionMapper.getClearRiskList(page,fidRegion,format);
    }

    @Override
    public void updateAlarm(String companyId, String startTime, String endTime) {
        supervisionMapper.updateAlarm(companyId,startTime,endTime);
    }

}
