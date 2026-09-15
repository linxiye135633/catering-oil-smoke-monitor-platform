package org.jeecg.modules.demo.institutionperson.controller;
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
import org.jeecg.modules.demo.institutionperson.entity.InstitutionPerson;
import org.jeecg.modules.demo.institutionperson.service.InstitutionPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Api(tags="表3-机构人员")
@Slf4j
@RestController
@RequestMapping("/InstitutionPerson")
public class InstitutionPersonController extends JeecgController<InstitutionPerson, InstitutionPersonService> {
    @Autowired
    private InstitutionPersonService institutionPersonService;

    /**
     * 分页列表查询
     */
    @AutoLog(value = "机构人员-分页列表查询")
    @ApiOperation(value="机构人员-分页列表查询", notes="机构人员-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(InstitutionPerson institutionPerson,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        Page<InstitutionPerson> page = new Page<>(pageNo, pageSize);
        QueryWrapper<InstitutionPerson> qw = new QueryWrapper<>();

        if(fidRegion!=1024){
            qw.eq("fid_region",fidRegion);
        }
        qw.orderByDesc("create_time");
        IPage<InstitutionPerson> pageList = institutionPersonService.page(page, qw);
        return Result.OK(pageList);
    }

    /**
     *   添加
     */
    @AutoLog(value = "机构人员-添加")
    @ApiOperation(value="机构人员-添加", notes="机构人员-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody InstitutionPerson institutionPerson) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        if(fidRegion==1024){
            return Result.error("管理员账号无法新增数据");
        }
        institutionPerson.setFidRegion(fidRegion);
        institutionPersonService.save(institutionPerson);
        return Result.OK(institutionPerson.getId());
    }

    /**
     *  编辑
     */
    @AutoLog(value = "机构人员-编辑")
    @ApiOperation(value="机构人员-编辑", notes="机构人员-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody InstitutionPerson institutionPerson) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        institutionPersonService.updateById(institutionPerson);
        return Result.OK("编辑成功!");
    }

    /**
     *   通过id删除
     */
    @AutoLog(value = "机构人员-通过id删除")
    @ApiOperation(value="机构人员-通过id删除", notes="机构人员-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        institutionPersonService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     *  批量删除
     */
    @AutoLog(value = "机构人员-批量删除")
    @ApiOperation(value="机构人员-批量删除", notes="机构人员-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        this.institutionPersonService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "机构人员-通过id查询")
    @ApiOperation(value="机构人员-通过id查询", notes="机构人员-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        QueryWrapper<InstitutionPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }
        InstitutionPerson institution = institutionPersonService.getOne(queryWrapper);
        if(institution==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(institution);
    }


    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InstitutionPerson institutionPerson) {
        return super.exportXls(request, institutionPerson, InstitutionPerson.class, "机构人员数据");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, InstitutionPerson.class);
    }

}


