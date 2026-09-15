package org.jeecg.modules.demo.companytype.controller;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.companytype.service.CompanyTypeService;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags="表6-餐饮企业标签")
@Slf4j
@RestController
@RequestMapping("/company")
public class CompanyTypeController extends JeecgController<Company, CompanyService> {
    @Autowired
    private CompanyTypeService companyService;
}


