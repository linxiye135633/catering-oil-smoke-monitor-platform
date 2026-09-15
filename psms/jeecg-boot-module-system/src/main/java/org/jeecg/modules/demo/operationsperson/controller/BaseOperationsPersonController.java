package org.jeecg.modules.demo.operationsperson.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.operationsperson.entity.BaseOperationsPerson;
import org.jeecg.modules.demo.operationsperson.service.IBaseOperationsPersonService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 运维人员
 * @Author: jeecg-boot
 * @Date:   2021-12-06
 * @Version: V1.0
 */
@Api(tags="表8-运维人员")
@RestController
@RequestMapping("/baseoperationsperson/baseOperationsPerson")
@Slf4j
public class BaseOperationsPersonController extends JeecgController<BaseOperationsPerson, IBaseOperationsPersonService> {
	@Autowired
	private IBaseOperationsPersonService baseOperationsPersonService;

	/**
	 * 分页列表查询
	 *
	 * @param baseOperationsPerson
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "运维人员-分页列表查询")
	@ApiOperation(value="运维人员-分页列表查询", notes="运维人员-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BaseOperationsPerson baseOperationsPerson,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BaseOperationsPerson> queryWrapper = QueryGenerator.initQueryWrapper(baseOperationsPerson, req.getParameterMap());
		Page<BaseOperationsPerson> page = new Page<BaseOperationsPerson>(pageNo, pageSize);
		IPage<BaseOperationsPerson> pageList = baseOperationsPersonService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param baseOperationsPerson
	 * @return
	 */
	@AutoLog(value = "运维人员-新增")
	@ApiOperation(value="运维人员-新增", notes="运维人员-新增")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BaseOperationsPerson baseOperationsPerson) {

		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		if(fidRegion==1024){
			return Result.error("管理员账号无法新增数据");
		}
		//BaseOperationsPerson bean = new BaseOperationsPerson();
		//BeanUtils.copyProperties(baseOperationsPerson, bean);

		baseOperationsPerson.setFidRegion(fidRegion);
		baseOperationsPersonService.save(baseOperationsPerson);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param baseOperationsPerson
	 * @return
	 */
	@AutoLog(value = "运维人员-编辑")
	@ApiOperation(value="运维人员-编辑", notes="运维人员-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BaseOperationsPerson baseOperationsPerson) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		baseOperationsPersonService.updateById(baseOperationsPerson);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "运维人员-通过id删除")
	@ApiOperation(value="运维人员-通过id删除", notes="运维人员-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		baseOperationsPersonService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "运维人员-批量删除")
	@ApiOperation(value="运维人员-批量删除", notes="运维人员-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.baseOperationsPersonService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "运维人员-通过id查询")
	@ApiOperation(value="运维人员-通过id查询", notes="运维人员-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BaseOperationsPerson baseOperationsPerson = baseOperationsPersonService.getById(id);
		if(baseOperationsPerson==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(baseOperationsPerson);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param baseOperationsPerson
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BaseOperationsPerson baseOperationsPerson) {
        return super.exportXls(request, baseOperationsPerson, BaseOperationsPerson.class, "运维人员");
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
        return super.importExcel(request, response, BaseOperationsPerson.class);
    }

}
