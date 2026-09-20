package org.jeecg.modules.dashboard.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.credit.mapper.CreditBoardMapper;
import org.jeecg.modules.dashboard.mapper.DashboardMapper;
import org.jeecg.modules.stat.mapper.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashboardController 大屏数据接口（#E-02/#E-03/#E-04/#E-55，董霞）
 *
 * #E-04 聚合接口（Sprint2轻量合并版）：一次返回五区数据，分区独立try/catch降级——
 * 任一分区失败返回 degraded:true，整体永不500（FR-E09验收标准）；
 * Sprint3 #E-06 在此基础上完成五接口全部合一+Redis缓存+峰谷参数化。
 *
 * 区域数据权限：沿用1.0 fidRegion机制（1024市级全部/其他本区域/0异常拒绝）。
 */
@Api(tags = "E组2.0-监管驾驶舱")
@Slf4j
@RestController
@RequestMapping("/v2/dashboard")
public class DashboardController {

    @Autowired
    private DashboardMapper dashboardMapper;
    @Autowired
    private StatMapper statMapper;
    @Autowired
    private CreditBoardMapper creditBoardMapper;
    @Autowired
    private org.jeecg.common.util.RedisUtil redisUtil;

    /**
     * #E-02 地图点位数据（含 #E-20 热力权重 heatWeight=近30天超标频次×浓度均值）
     */
    @AutoLog(value = "大屏-地图点位数据")
    @ApiOperation(value = "大屏-地图点位数据", notes = "点位+最新浓度+超标状态+热力权重")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/map/points")
    public Result<?> mapPoints() {
        int fidRegion = currentRegion();
        if (fidRegion < 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        List<Map<String, Object>> points = dashboardMapper.selectMapPoints(fidRegion, "bu_lampblack_data");
        return Result.OK(points);
    }

    /**
     * #E-03 告警滚动数据（KD-05：base_company_type 警告记录，getNewRisk同口径）
     */
    @AutoLog(value = "大屏-最新告警滚动")
    @ApiOperation(value = "大屏-最新告警滚动", notes = "警告中记录TOP N，时间倒序")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/alarms/latest")
    public Result<?> latestAlarms(@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        int fidRegion = currentRegion();
        if (fidRegion < 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        return Result.OK(dashboardMapper.selectLatestAlarms(Math.min(pageSize, 50), fidRegion));
    }

    /**
     * #E-55 A组风险企业预测榜（契约C-01）：优先透传A组接口，
     * 不可用/未配置时返回空列表（前端显示"预测数据生成中"，不加工不缓存——榜单实时性>性能）
     */
    @ApiOperation(value = "大屏-风险企业预测榜(A组)", notes = "A组契约C-01透传，未就绪返回空列表")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/risk-top")
    public Result<?> riskTop(@RequestParam(name = "top", defaultValue = "5") Integer top) {
        Map<String, Object> body = new HashMap<>(4);
        body.put("source", "A");
        body.put("list", fetchRiskTop(Math.min(top, 10)));
        return Result.OK(body);
    }

    /**
     * #E-04/#E-06 聚合接口（五区数据一次返回，分区独立降级）
     * #E-07 Redis缓存：key=区域+周期，TTL取sys_param(psms.dashboard.cacheTtl，默认30s)；
     * 响应头 X-Cache: HIT/MISS（TC-E-033断言依据）；缓存命中跳过全部分区查询。
     */
    @AutoLog(value = "大屏-聚合数据接口")
    @ApiOperation(value = "大屏-聚合数据接口", notes = "七分区一次返回，分区降级degraded:true，X-Cache头标识缓存命中")
    @RequiresPermissions("dashboard:screen:view")
    @GetMapping(value = "/aggregate")
    public Result<?> aggregate(@RequestParam(name = "areaCode", required = false) String areaCode,
                               @RequestParam(name = "cycle", required = false) String cycle,
                               javax.servlet.http.HttpServletResponse servletResponse) {
        final int fidRegion = currentRegion();
        if (fidRegion < 0) {
            return Result.error("账号权限异常，请联系管理员");
        }
        final String useCycle = cycle == null || cycle.isEmpty()
                ? new SimpleDateFormat("yyyy-MM").format(new Date()) : cycle;

        // #E-07 缓存命中：直接返回，响应头标HIT
        // 缓存值存JSON字符串——redisTemplate的Jackson序列化器不支持嵌套Object引用（Map<String,Object>），
        // 直接存body会SerializationException被吞，导致永远MISS
        String cacheKey = "psms:dashboard:agg:" + fidRegion + ":" + useCycle;
        try {
            Object cached = redisUtil.get(cacheKey);
            if (cached instanceof String) {
                servletResponse.addHeader("X-Cache", "HIT");
                return Result.OK(JSON.parseObject((String) cached));
            }
        } catch (Exception e) {
            log.warn("[aggregate] 缓存读取失败（降级直查）：{}", e.getMessage());
        }
        servletResponse.addHeader("X-Cache", "MISS");

        Map<String, Object> body = new HashMap<>(8);
        body.put("regionOverview", partition(() -> dashboardMapper.selectRegionOverview(fidRegion)));
        body.put("alerts", partition(() -> dashboardMapper.selectLatestAlarms(10, fidRegion)));
        body.put("creditBoard", partition(() -> creditBoardMapper.selectBoard(useCycle, null, 10, fidRegion)));
        body.put("trend", partition(() -> statMapper.selectAlarmTrend(7, fidRegion)));
        body.put("industry", partition(() -> statMapper.selectIndustryDistribution(fidRegion)));
        body.put("processRate", partition(() -> statMapper.selectProcessRate(useCycle, fidRegion)));
        body.put("riskTop", partition(() -> fetchRiskTop(5)));

        // #E-07 写缓存（TTL可配；含degraded分区的结果不缓存，避免坏数据驻留）
        boolean anyDegraded = body.values().stream()
                .anyMatch(v -> v instanceof Map && Boolean.TRUE.equals(((Map<?, ?>) v).get("degraded")));
        if (!anyDegraded) {
            try {
                long ttl = parseTtl(dashboardMapper.selectConfigValue("psms.dashboard.cacheTtl"));
                redisUtil.set(cacheKey, JSON.toJSONString(body), ttl);
            } catch (Exception e) {
                log.warn("[aggregate] 缓存写入失败（不影响返回）：{}", e.getMessage());
            }
        }
        return Result.OK(body);
    }

    /** TTL解析：秒数；缺省30s，下限5s上限300s（与MUST刷新≤5min对齐） */
    private long parseTtl(String raw) {
        try {
            long v = raw == null || raw.isEmpty() ? 30 : Long.parseLong(raw.trim());
            return Math.max(5, Math.min(300, v));
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    /** 分区执行器：异常只降级该分区（FR-E09：分区null+degraded，整体不500） */
    private Map<String, Object> partition(PartitionSupplier supplier) {
        Map<String, Object> m = new HashMap<>(4);
        try {
            m.put("data", supplier.get());
            m.put("degraded", false);
        } catch (Exception e) {
            log.warn("[aggregate] 分区降级：{}", e.getMessage());
            m.put("data", null);
            m.put("degraded", true);
        }
        return m;
    }

    private interface PartitionSupplier {
        Object get();
    }

    /** A组榜单透传（地址来自sys_param白名单配置，3s超时降级空列表） */
    private List<Map<String, Object>> fetchRiskTop(int top) {
        try {
            String url = dashboardMapper.selectConfigValue("psms.predict.riskTopUrl");
            if (url == null || url.isEmpty()) {
                return new ArrayList<>();
            }
            org.springframework.http.client.SimpleClientHttpRequestFactory f =
                    new org.springframework.http.client.SimpleClientHttpRequestFactory();
            f.setConnectTimeout(3000);
            f.setReadTimeout(3000);
            ResponseEntity<List> resp = new RestTemplate(f).getForEntity(url + "?top=" + top, List.class);
            return resp.getBody() == null ? new ArrayList<>() : resp.getBody();
        } catch (Exception e) {
            log.warn("[risk-top] A组接口不可用，降级空列表：{}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private int currentRegion() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        return fidRegion == 0 ? -1 : fidRegion;
    }
}
