package org.jeecg.modules.demo.statistical.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.statistical.entity.BaseStatistical;
import org.jeecg.modules.demo.statistical.service.IBaseStatisticalService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 统计报表
 * @Author: jeecg-boot
 * @Date:   2021-12-06
 * @Version: V1.0
 */
@Api(tags="表9-统计报表")
@RestController
@RequestMapping("/basestatistical/baseStatistical")
@Slf4j
public class BaseStatisticalController extends JeecgController<BaseStatistical, IBaseStatisticalService> {
	@Autowired
	private IBaseStatisticalService baseStatisticalService;

	/**
	 * 分页列表查询
	 *
	 * @param baseStatistical
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "统计报表-分页列表查询")
	@ApiOperation(value="统计报表-分页列表查询", notes="统计报表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BaseStatistical baseStatistical,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		int fidRegion = sysUser.getFidRegion();
		if(fidRegion==0){
			return Result.error("账号权限异常，请联系管理员");
		}

		QueryWrapper<BaseStatistical> queryWrapper = QueryGenerator.initQueryWrapper(baseStatistical, req.getParameterMap());
		if(fidRegion!=1024){
			queryWrapper.eq("fid_region",fidRegion);
		}
		Page<BaseStatistical> page = new Page<BaseStatistical>(pageNo, pageSize);
		IPage<BaseStatistical> pageList = baseStatisticalService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param baseStatistical
	 * @return
	 */
	//@AutoLog(value = "统计报表-添加")
	//@ApiOperation(value="统计报表-添加", notes="统计报表-添加")
	//@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BaseStatistical baseStatistical) {
		baseStatisticalService.save(baseStatistical);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param baseStatistical
	 * @return
	 */
	//@AutoLog(value = "统计报表-编辑")
	//@ApiOperation(value="统计报表-编辑", notes="统计报表-编辑")
	//@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BaseStatistical baseStatistical) {
		baseStatisticalService.updateById(baseStatistical);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "统计报表-通过id删除")
	//@ApiOperation(value="统计报表-通过id删除", notes="统计报表-通过id删除")
	//@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		baseStatisticalService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	//@AutoLog(value = "统计报表-批量删除")
	//@ApiOperation(value="统计报表-批量删除", notes="统计报表-批量删除")
	//@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.baseStatisticalService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "统计报表-通过id查询")
	//@ApiOperation(value="统计报表-通过id查询", notes="统计报表-通过id查询")
	//@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BaseStatistical baseStatistical = baseStatisticalService.getById(id);
		if(baseStatistical==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(baseStatistical);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param baseStatistical
    */
    //@RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BaseStatistical baseStatistical) {
        return super.exportXls(request, baseStatistical, BaseStatistical.class, "统计报表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BaseStatistical.class);
    }

}
