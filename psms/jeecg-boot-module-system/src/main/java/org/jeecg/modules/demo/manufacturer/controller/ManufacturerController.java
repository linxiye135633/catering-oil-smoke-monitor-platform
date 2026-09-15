package org.jeecg.modules.demo.manufacturer.controller;
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
import org.jeecg.modules.demo.manufacturer.entity.Manufacturer;
import org.jeecg.modules.demo.manufacturer.service.ManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Api(tags="表1-服务厂商")
@Slf4j
@RestController
@RequestMapping("/Manufacturer")
public class ManufacturerController extends JeecgController<Manufacturer, ManufacturerService> {
    @Autowired
    private ManufacturerService manufacturerService;

    /**
     * 分页列表查询
     */
    @AutoLog(value = "服务厂商-分页列表查询")
    @ApiOperation(value="服务厂商-分页列表查询", notes="服务厂商-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(@RequestParam(name="isPage",required=false) Boolean isPage,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        Page<Manufacturer> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Manufacturer> qw = new QueryWrapper<>();
        if(fidRegion!=1024){
            //qw.eq("fid_region",fidRegion);
        }
        qw.orderByDesc("create_time");
        if(null!=isPage && isPage){
            IPage<Manufacturer> pageList = manufacturerService.page(page, qw);
            return Result.OK(pageList);
        }else {
            List<Manufacturer> pageList = manufacturerService.list(qw);
            return Result.OK(pageList);
        }
    }

    /**
     *   添加
     */
    @AutoLog(value = "服务厂商-添加")
    @ApiOperation(value="服务厂商-添加", notes="服务厂商-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Manufacturer manufacturer) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        if(fidRegion==1024){
            return Result.error("管理员账号无法新增数据");
        }
        //manufacturer.setFidRegion(fidRegion);
        manufacturerService.save(manufacturer);
        return Result.OK(manufacturer.getId());
    }

    /**
     *  编辑
     */
    @AutoLog(value = "服务厂商-编辑")
    @ApiOperation(value="服务厂商-编辑", notes="服务厂商-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Manufacturer manufacturer) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        manufacturerService.updateById(manufacturer);
        return Result.OK("编辑成功!");
    }

    /**
     *   通过id删除
     */
    @AutoLog(value = "服务厂商-通过id删除")
    @ApiOperation(value="服务厂商-通过id删除", notes="服务厂商-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        manufacturerService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     *  批量删除
     */
    @AutoLog(value = "服务厂商-批量删除")
    @ApiOperation(value="服务厂商-批量删除", notes="服务厂商-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        manufacturerService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "服务厂商-通过id查询")
    @ApiOperation(value="服务厂商-通过id查询", notes="服务厂商-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        QueryWrapper<Manufacturer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }

        Manufacturer manufacturer = manufacturerService.getOne(queryWrapper);
        if(manufacturer==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(manufacturer);
    }


    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Manufacturer manufacturer) {
        return super.exportXls(request, manufacturer, Manufacturer.class, "服务厂商数据");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Manufacturer.class);
    }

}


