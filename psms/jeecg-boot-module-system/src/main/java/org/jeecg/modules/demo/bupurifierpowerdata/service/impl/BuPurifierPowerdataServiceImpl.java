package org.jeecg.modules.demo.bupurifierpowerdata.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.demo.bupurifierpowerdata.entity.BuPurifierPowerdata;
import org.jeecg.modules.demo.bupurifierpowerdata.mapper.BuPurifierPowerdataMapper;
import org.jeecg.modules.demo.bupurifierpowerdata.service.IBuPurifierPowerdataService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 净化器电源模块数据
 * @Author: jeecg-boot
 * @Date:   2021-10-28
 * @Version: V1.0
 */
@Service
public class BuPurifierPowerdataServiceImpl extends ServiceImpl<BuPurifierPowerdataMapper, BuPurifierPowerdata> implements IBuPurifierPowerdataService {

    /*
    @Override
    public Page getYouYanList(Page page, String companyid, Integer pageNo, Integer pageSize) {
        return getPage(page, pageNo, pageSize, baseMapper.listYouYan(page, companyid), baseMapper.countYouYan(page, companyid), companyid);
    }*/
}
