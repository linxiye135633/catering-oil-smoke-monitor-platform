package org.jeecg.modules.demo.point.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Api(tags="表5-测点")
@Slf4j
@RestController
@RequestMapping("/point")
public class PointController extends JeecgController<Point, PointService> {
    @Autowired
    private PointService pointService;

    /**
     * 分页列表查询
     */
    @AutoLog(value = "测点-分页列表查询")
    @ApiOperation(value="测点-分页列表查询", notes="测点-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Point point,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        Page<Point> page = new Page<Point>(pageNo, pageSize);
        QueryWrapper<Point> qw = new QueryWrapper<>();
        if(fidRegion!=1024){
            qw.eq("fid_region",fidRegion);
        }
        qw.orderByDesc("create_time");
        IPage<Point> pageList = pointService.page(page, qw);
        return Result.OK(pageList);
    }

    /**
     *   添加
     */
    @AutoLog(value = "测点-添加")
    @ApiOperation(value="测点-添加", notes="测点-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Point point) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        if(fidRegion==1024){
            return Result.error("管理员账号无法新增数据");
        }
        point.setFidRegion(fidRegion);

        pointService.save(point);
        return Result.OK(point.getId());
    }

    /**
     *  编辑
     */
    @AutoLog(value = "测点-编辑")
    @ApiOperation(value="测点-编辑", notes="测点-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Point point) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        pointService.updateById(point);
        return Result.OK("编辑成功!");
    }

    /**
     *   通过id删除
     */
    @AutoLog(value = "测点-通过id删除")
    @ApiOperation(value="测点-通过id删除", notes="测点-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        pointService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     *  批量删除
     */
    @AutoLog(value = "测点-批量删除")
    @ApiOperation(value="测点-批量删除", notes="测点-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        this.pointService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "测点-通过id查询")
    @ApiOperation(value="测点-通过id查询", notes="测点-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }

        Point point = pointService.getOne(queryWrapper);
        if(point==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(point);
    }


    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Point point) {
        return super.exportXls(request, point, Point.class, "测点数据");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Point.class);
    }

    @AutoLog(value = "测点count-添加")
    @ApiOperation(value="测点count-添加", notes="测点count-添加")
    @GetMapping(value = "/count")
    public int companyCount() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return 0;
        }

        QueryWrapper<Point> queryWrapper = new QueryWrapper<>();
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }

        return pointService.count(queryWrapper);
    }
}


