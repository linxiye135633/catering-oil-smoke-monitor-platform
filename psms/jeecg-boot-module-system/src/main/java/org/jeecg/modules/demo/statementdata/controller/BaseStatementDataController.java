package org.jeecg.modules.demo.statementdata.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.demo.statementdata.entity.BaseStatementData;
import org.jeecg.modules.demo.statementdata.service.IBaseStatementDataService;

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
 * 统计分析报告
 */
@Api(tags="统计分析报告")
@RestController
@RequestMapping("/statementdata/baseStatementData")
@Slf4j
public class BaseStatementDataController extends JeecgController<BaseStatementData, IBaseStatementDataService> {
	@Autowired
	private IBaseStatementDataService baseStatementDataService;

	/**
	 * 分页列表查询
	 */
	@AutoLog(value = "统计分析报告-分页列表查询")
	@ApiOperation(value="统计分析报告-分页列表查询", notes="统计分析报告-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(BaseStatementData baseStatementData,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BaseStatementData> queryWrapper = QueryGenerator.initQueryWrapper(baseStatementData, req.getParameterMap());
		Page<BaseStatementData> page = new Page<BaseStatementData>(pageNo, pageSize);
		IPage<BaseStatementData> pageList = baseStatementDataService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 */
	@AutoLog(value = "统计分析报告-添加")
	@ApiOperation(value="统计分析报告-添加", notes="统计分析报告-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BaseStatementData baseStatementData) {
		baseStatementDataService.save(baseStatementData);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 */
	@AutoLog(value = "统计分析报告-编辑")
	@ApiOperation(value="统计分析报告-编辑", notes="统计分析报告-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BaseStatementData baseStatementData) {
		baseStatementDataService.updateById(baseStatementData);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 */
	@AutoLog(value = "统计分析报告-通过id删除")
	@ApiOperation(value="统计分析报告-通过id删除", notes="统计分析报告-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		baseStatementDataService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 */
	@AutoLog(value = "统计分析报告-批量删除")
	@ApiOperation(value="统计分析报告-批量删除", notes="统计分析报告-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.baseStatementDataService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 */
	@AutoLog(value = "统计分析报告-通过id查询")
	@ApiOperation(value="统计分析报告-通过id查询", notes="统计分析报告-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BaseStatementData baseStatementData = baseStatementDataService.getById(id);
		if(baseStatementData==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(baseStatementData);
	}

    /**
    * 导出excel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BaseStatementData baseStatementData) {
        return super.exportXls(request, baseStatementData, BaseStatementData.class, "统计分析报告");
    }

    /**
      * 通过excel导入数据
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, BaseStatementData.class);
    }

}
