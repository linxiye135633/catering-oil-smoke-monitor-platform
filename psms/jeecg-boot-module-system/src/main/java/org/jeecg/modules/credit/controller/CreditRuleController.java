package org.jeecg.modules.credit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.credit.entity.CreditRule;
import org.jeecg.modules.credit.engine.CreditEngine;
import org.jeecg.modules.credit.service.ICreditRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description: 信用评价指标
 * @Version: v2.0
 */
@Api(tags = "E组2.0-信用规则")
@Slf4j
@RestController
@RequestMapping("/v2/credit/rules")
public class CreditRuleController extends JeecgController<CreditRule, ICreditRuleService> {

    @Autowired
    private ICreditRuleService creditRuleService;
    @Autowired
    private CreditEngine creditEngine;

    /**
     * 分页列表查询（QueryGenerator 通用查询，字典列自动带出 dictText）
     */
    @AutoLog(value = "信用规则-分页列表查询")
    @ApiOperation(value = "信用规则-分页列表查询", notes = "信用规则-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(CreditRule creditRule,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<CreditRule> queryWrapper = QueryGenerator.initQueryWrapper(creditRule, req.getParameterMap());
        Page<CreditRule> page = new Page<>(pageNo, pageSize);
        IPage<CreditRule> pageList = creditRuleService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 新增（唯一性校验：25001 指标代码已存在 / 25006 分值非法）
     */
    @AutoLog(value = "信用规则-新增")
    @ApiOperation(value = "信用规则-新增", notes = "信用规则-新增")
    @RequiresPermissions("credit:rule:add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CreditRule creditRule) {
        String err = creditRuleService.checkBeforeSave(creditRule);
        if (err != null) {
            return Result.error(err, "25001".equals(err) ? "指标代码已存在" : "分值非法");
        }
        creditRuleService.save(creditRule);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑（指标代码不可改，编辑时排除自身校验）
     */
    @AutoLog(value = "信用规则-编辑")
    @ApiOperation(value = "信用规则-编辑", notes = "信用规则-编辑")
    @RequiresPermissions("credit:rule:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody CreditRule creditRule) {
        String err = creditRuleService.checkBeforeSave(creditRule);
        if (err != null) {
            return Result.error(err, "25001".equals(err) ? "指标代码已存在" : "分值非法");
        }
        creditRuleService.updateById(creditRule);
        return Result.OK("编辑成功！");
    }

    /**
     * 启停规则（子资源动作，POST /v2/credit/rules/{id}/enable）
     */
    @AutoLog(value = "信用规则-启停")
    @ApiOperation(value = "信用规则-启停", notes = "信用规则-启停")
    @RequiresPermissions("credit:rule:edit")
    @PostMapping(value = "/{id}/enable")
    public Result<?> enable(@PathVariable("id") String id, @RequestBody Map<String, String> body) {
        CreditRule rule = creditRuleService.getById(id);
        if (rule == null) {
            return Result.error(25002, "规则不存在");
        }
        rule.setEnabled(body.getOrDefault("enabled", "1"));
        creditRuleService.updateById(rule);
        return Result.OK("操作成功！");
    }

    /**
     * 手动触发当期信用计算（管理员；Quartz定时入口在 #E-14 接入，两者共用 CreditEngine）
     */
    @AutoLog(value = "信用规则-手动触发计算")
    @ApiOperation(value = "信用规则-手动触发计算", notes = "信用规则-手动触发计算")
    @RequiresPermissions("credit:result:calc")
    @PostMapping(value = "/calculate/trigger")
    public Result<?> triggerCalculate(@RequestParam(name = "cycle", required = false) String cycle) {
        return creditEngine.calculate(cycle);
    }

    /**
     * 通过id删除（逻辑删除，继承JeecgController另含 deleteBatch/exportXls/importExcel）
     */
    @AutoLog(value = "信用规则-删除")
    @ApiOperation(value = "信用规则-删除", notes = "信用规则-删除")
    @RequiresPermissions("credit:rule:delete")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        creditRuleService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "信用规则-批量删除")
    @ApiOperation(value = "信用规则-批量删除", notes = "信用规则-批量删除")
    @RequiresPermissions("credit:rule:delete")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.creditRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出excel（沿用1.0 AutoPOI）
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CreditRule creditRule, HttpServletResponse response) {
        return super.exportXls(request, creditRule, CreditRule.class, "信用评价指标");
    }
}
