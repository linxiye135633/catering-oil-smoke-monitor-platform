package org.jeecg.modules.credit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.credit.mapper.CreditBoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CreditBoardController 信用榜单与公示（#E-11/#E-12，李晓倩）
 * 契约P-02：/v2/credit/public 免业务权限（登录即可，公示语义）；
 * 管理动作（发布/下架/触发计算）挂 credit:result:* 权限。
 */
@Api(tags = "E组2.0-信用榜单与公示")
@Slf4j
@RestController
@RequestMapping("/v2/credit")
public class CreditBoardController {

    @Autowired
    private CreditBoardMapper boardMapper;

    /**
     * #E-11 信用榜单（红牌优先，KD-09）
     */
    @AutoLog(value = "信用-榜单查询")
    @ApiOperation(value = "信用榜单", notes = "按周期总分升序（红牌优先），level可选")
    @GetMapping(value = "/board")
    public Result<?> board(@RequestParam(name = "cycle", required = false) String cycle,
                           @RequestParam(name = "level", required = false) String level,
                           @RequestParam(name = "topN", defaultValue = "50") Integer topN) {
        int fidRegion = currentRegion();
        if (fidRegion < 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        if (cycle == null || cycle.isEmpty()) {
            cycle = new SimpleDateFormat("yyyy-MM").format(new Date());
        }
        List<Map<String, Object>> board = boardMapper.selectBoard(cycle, level, Math.min(topN, 200), fidRegion);
        Map<String, Object> body = new HashMap<>(4);
        body.put("cycle", cycle);
        body.put("list", board);
        return Result.OK(body);
    }

    /**
     * #E-11 单企业结果+扣分明细（UC-E03扩展流）
     */
    @AutoLog(value = "信用-单企业结果查询")
    @ApiOperation(value = "单企业信用结果与明细", notes = "明细逐条可追溯")
    @GetMapping(value = "/company")
    public Result<?> company(@RequestParam(name = "companyId") String companyId,
                             @RequestParam(name = "cycle", required = false) String cycle) {
        if (cycle == null || cycle.isEmpty()) {
            cycle = new SimpleDateFormat("yyyy-MM").format(new Date());
        }
        Map<String, Object> result = boardMapper.selectCompanyResult(companyId, cycle);
        if (result == null) {
            return Result.OK("该周期暂无评价结果");
        }
        result.put("details", boardMapper.selectDetails(String.valueOf(result.get("id"))));
        return Result.OK(result);
    }

    /**
     * #E-12 发布公示（草稿0→发布1；25003周期未发布查询、25005无可发布结果）
     */
    @AutoLog(value = "信用-发布公示")
    @ApiOperation(value = "发布信用公示", notes = "整周期发布")
    @RequiresPermissions("credit:result:publish")
    @PostMapping(value = "/publish")
    public Result<?> publish(@RequestParam(name = "cycle", required = false) String cycle) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (cycle == null || cycle.isEmpty()) {
            cycle = new SimpleDateFormat("yyyy-MM").format(new Date());
        }
        if (boardMapper.countByCycle(cycle) == 0) {
            return Result.error(25005, "该周期无评价结果，请先执行计算");
        }
        int n = boardMapper.updatePublishStatus(cycle, "1", "0", user.getUsername());
        return Result.OK("已发布 " + n + " 家企业信用结果");
    }

    /**
     * #E-12 下架公示（发布1→下架2）
     */
    @AutoLog(value = "信用-下架公示")
    @ApiOperation(value = "下架信用公示", notes = "整周期下架")
    @RequiresPermissions("credit:result:publish")
    @PutMapping(value = "/publish/{cycle}/offline")
    public Result<?> offline(@PathVariable("cycle") String cycle) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int n = boardMapper.updatePublishStatus(cycle, "2", "1", user.getUsername());
        return Result.OK("已下架 " + n + " 条公示记录");
    }

    /**
     * #E-12 公示列表（契约P-02：登录即可查）
     */
    @ApiOperation(value = "信用公示列表", notes = "仅已发布结果")
    @GetMapping(value = "/public")
    public Result<?> publicList(@RequestParam(name = "cycle", required = false) String cycle,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        int fidRegion = currentRegion();
        if (fidRegion < 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        if (cycle == null || cycle.isEmpty()) {
            cycle = new SimpleDateFormat("yyyy-MM").format(new Date());
        }
        int total = boardMapper.countPublic(cycle, fidRegion);
        if (total == 0) {
            return Result.error(25003, "该周期公示未发布");
        }
        // MP Page 参数分页：searchCount=false（total 已由 countPublic 单独统计）
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize, false);
        page.setRecords(boardMapper.selectPublicList(cycle, fidRegion, page));
        // 注意：不直接返回 Page<Map>——该形态在 Jeecg 消息转换器下会把每个 Map
        // 序列化为空对象（联调实测）；展开为普通 Map 结构（与 board 接口同模式）。
        Map<String, Object> data = new HashMap<>();
        data.put("records", page.getRecords());
        data.put("total", total);
        data.put("current", pageNo);
        data.put("size", pageSize);
        return Result.OK(data);
    }

    private int currentRegion() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        return fidRegion == 0 ? -1 : fidRegion;
    }
}
