package org.jeecg.modules.demo.operationsperson.service.impl;

import org.jeecg.modules.demo.operationsperson.entity.BaseOperationsPerson;
import org.jeecg.modules.demo.operationsperson.mapper.BaseOperationsPersonMapper;
import org.jeecg.modules.demo.operationsperson.service.IBaseOperationsPersonService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 运维人员
 * @Author: jeecg-boot
 * @Date:   2021-12-06
 * @Version: V1.0
 */
@Service
public class BaseOperationsPersonServiceImpl extends ServiceImpl<BaseOperationsPersonMapper, BaseOperationsPerson> implements IBaseOperationsPersonService {

}
