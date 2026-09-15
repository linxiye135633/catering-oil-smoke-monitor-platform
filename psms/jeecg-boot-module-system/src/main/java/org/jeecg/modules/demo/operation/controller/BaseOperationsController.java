package org.jeecg.modules.demo.operation.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.operation.entity.BaseOperations;
import org.jeecg.modules.demo.operation.service.IBaseOperationsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.statistical.entity.BaseStatistical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 */
@Api(tags="表7-运维")
@RestController
@RequestMapping("/operations/baseOperations")
@Slf4j
public class BaseOperationsController extends JeecgController<BaseOperations, IBaseOperationsService> {
	@Autowired
	private IBaseOperationsService baseOperationsService;

	/**
	 * 分页列表查询
	 */
	@AutoLog(value = "运维-分页列表查询")
	@ApiOperation(value="运维-分页列表查询", notes="运维-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BaseOperations baseOperations,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}

		QueryWrapper<BaseOperations> queryWrapper = QueryGenerator.initQueryWrapper(baseOperations, req.getParameterMap());
		if(fidRegion!=1024){
			queryWrapper.eq("fid_region",fidRegion);
		}
		Page<BaseOperations> page = new Page<BaseOperations>(pageNo, pageSize);
		IPage<BaseOperations> pageList = baseOperationsService.page(page, queryWrapper);

		return Result.OK(pageList);
	}

	/**
	 *   添加
	 */
	@AutoLog(value = "运维-添加")
	@ApiOperation(value="运维-添加", notes="运维-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BaseOperations baseOperations) {
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

		baseOperations.setFidRegion(fidRegion);
		baseOperationsService.save(baseOperations);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 */
	@AutoLog(value = "运维-编辑")
	@ApiOperation(value="运维-编辑", notes="运维-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BaseOperations baseOperations) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		baseOperationsService.updateById(baseOperations);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 */
	@AutoLog(value = "运维-通过id删除")
	@ApiOperation(value="运维-通过id删除", notes="运维-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		baseOperationsService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 */
	@AutoLog(value = "运维-批量删除")
	@ApiOperation(value="运维-批量删除", notes="运维-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		this.baseOperationsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 */
	@AutoLog(value = "运维-通过id查询")
	@ApiOperation(value="运维-通过id查询", notes="运维-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}
		BaseOperations baseOperations = baseOperationsService.getById(id);
		if(baseOperations==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(baseOperations);
	}

    /**
    * 导出excel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BaseOperations baseOperations) {
        return super.exportXls(request, baseOperations, BaseOperations.class, "运维");
    }

    /**
      * 通过excel导入数据
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BaseOperations.class);
    }

}
