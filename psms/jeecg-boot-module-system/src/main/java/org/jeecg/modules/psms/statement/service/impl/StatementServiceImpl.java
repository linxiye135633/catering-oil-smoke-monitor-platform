package org.jeecg.modules.psms.statement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psms.base.mapper.PsmsMapper;
import org.jeecg.modules.psms.statement.mapper.StatementMapper;
import org.jeecg.modules.psms.statement.service.StatementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class StatementServiceImpl extends ServiceImpl<StatementMapper, Map<String,Object>> implements StatementService {

    @Resource
    private StatementMapper statementMapper;

    @Override
    public Map<String, Object> getStatementData(String tableLampblack, String tablePurifier, String pointMac, String name) {
        return statementMapper.getStatementData(tableLampblack,tablePurifier,pointMac,name);
    }

    @Override
    public Map<String, Object> getStatementMonthData(String tableLampblack, String tablePurifier, String pointMac, String name, String format,int numv) {
        return statementMapper.getStatementMonthData(tableLampblack,tablePurifier,pointMac,name,format,numv);
    }

    @Override
    public Map<String, Object> getStatementSeasonData(String tableLampblack, String tablePurifier, String pointMac, String name, String format1, String format2, String format3,int numv) {
        return statementMapper.getStatementSeasonData(tableLampblack,tablePurifier,pointMac,name,format1,format2,format3,numv);
    }

    @Override
    public Map<String, Object> getStatDataTest(String tableLampblack, String tablePurifier, String pointMac, String sql, int numv) {
        return statementMapper.getStatDataTest(tableLampblack, tablePurifier, pointMac, sql, numv);
    }


}
