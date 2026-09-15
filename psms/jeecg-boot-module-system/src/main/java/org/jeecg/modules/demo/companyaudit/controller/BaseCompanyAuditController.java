package org.jeecg.modules.demo.companyaudit.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.companyaudit.entity.BaseCompanyAudit;
import org.jeecg.modules.demo.companyaudit.service.IBaseCompanyAuditService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 餐饮企业审核
 * @Author: jeecg-boot
 * @Date:   2021-12-01
 * @Version: V1.0
 */
@Api(tags="表6-餐饮企业审核")
@RestController
@RequestMapping("/companyaudit/baseCompanyAudit")
@Slf4j
public class BaseCompanyAuditController extends JeecgController<BaseCompanyAudit, IBaseCompanyAuditService> {
	@Autowired
	private IBaseCompanyAuditService baseCompanyAuditService;

	/**
	 * 分页列表查询
	 *
	 * @param baseCompanyAudit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "餐饮企业审核-分页列表查询")
	@ApiOperation(value="餐饮企业审核-分页列表查询", notes="餐饮企业审核-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BaseCompanyAudit baseCompanyAudit,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BaseCompanyAudit> queryWrapper = QueryGenerator.initQueryWrapper(baseCompanyAudit, req.getParameterMap());
		Page<BaseCompanyAudit> page = new Page<BaseCompanyAudit>(pageNo, pageSize);
		IPage<BaseCompanyAudit> pageList = baseCompanyAuditService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param baseCompanyAudit
	 * @return
	 */
	@AutoLog(value = "餐饮企业审核-添加")
	@ApiOperation(value="餐饮企业审核-添加", notes="餐饮企业审核-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BaseCompanyAudit baseCompanyAudit) {
		baseCompanyAuditService.save(baseCompanyAudit);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param baseCompanyAudit
	 * @return
	 */
	@AutoLog(value = "餐饮企业审核-编辑")
	@ApiOperation(value="餐饮企业审核-编辑", notes="餐饮企业审核-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BaseCompanyAudit baseCompanyAudit) {
		baseCompanyAuditService.updateById(baseCompanyAudit);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "餐饮企业审核-通过id删除")
	@ApiOperation(value="餐饮企业审核-通过id删除", notes="餐饮企业审核-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		baseCompanyAuditService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "餐饮企业审核-批量删除")
	@ApiOperation(value="餐饮企业审核-批量删除", notes="餐饮企业审核-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.baseCompanyAuditService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "餐饮企业审核-通过id查询")
	@ApiOperation(value="餐饮企业审核-通过id查询", notes="餐饮企业审核-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BaseCompanyAudit baseCompanyAudit = baseCompanyAuditService.getById(id);
		if(baseCompanyAudit==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(baseCompanyAudit);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param baseCompanyAudit
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BaseCompanyAudit baseCompanyAudit) {
        return super.exportXls(request, baseCompanyAudit, BaseCompanyAudit.class, "餐饮企业审核");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BaseCompanyAudit.class);
    }

}
