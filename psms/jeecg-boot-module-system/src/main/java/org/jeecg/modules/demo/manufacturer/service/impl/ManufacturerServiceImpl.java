package org.jeecg.modules.demo.manufacturer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.manufacturer.entity.Manufacturer;
import org.jeecg.modules.demo.manufacturer.mapper.ManufacturerMapper;
import org.jeecg.modules.demo.manufacturer.service.ManufacturerService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ManufacturerServiceImpl extends ServiceImpl<ManufacturerMapper, Manufacturer> implements ManufacturerService {

}
