package org.jeecg.modules.demo.point.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.mapper.PointMapper;
import org.jeecg.modules.demo.point.service.PointService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {

}
