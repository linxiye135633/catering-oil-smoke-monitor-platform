package org.jeecg.modules.demo.company.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xkcoding.http.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.company.entity.Company;
import org.jeecg.modules.demo.point.entity.Point;
import org.jeecg.modules.demo.company.service.CompanyService;
import org.jeecg.modules.demo.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Api(tags="表4-餐饮企业")
@Slf4j
@RestController
@RequestMapping("/company")
public class CompanyController extends JeecgController<Company, CompanyService> {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private PointService pointService;

    /**
     * 分页列表查询
     */
    @AutoLog(value = "餐饮企业-分页列表查询")
    @ApiOperation(value="餐饮企业-分页列表查询", notes="餐饮企业-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(@RequestParam(name="str",required=false) String str,
                                   @RequestParam(name="isPage",required=false) Boolean isPage,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        Page<Company> page = new Page<Company>(pageNo, pageSize);
        QueryWrapper<Company> qw = new QueryWrapper<>();
        if(StringUtil.isNotEmpty(str)){
            qw.like("name", str).or().like("door_name", str);
            QueryWrapper<Point> queryWrapper = new QueryWrapper<Point>();
            queryWrapper.like("point_mac", str);
            List<Point> pageLists = pointService.list(queryWrapper);
            if(!pageLists.isEmpty()){
                List<String>ids = new LinkedList<>();
                pageLists.forEach(item->{
                    String id = item.getCompanyId();
                    ids.add(id);
                });
                qw.or();
                qw.in("id", ids);
            }
        }

        if(fidRegion!=1024){
            qw.eq("fid_region",fidRegion);
        }

        qw.orderByDesc("create_time");
        if(null!=isPage && isPage){
            IPage<Company> pageList = companyService.page(page, qw);
            return Result.OK(pageList);
        }else {
            List<Company> pageList = companyService.list(qw);
            return Result.OK(pageList);
        }
    }

    @AutoLog(value = "餐饮企业-分页列表查询")
    @ApiOperation(value="餐饮企业-分页列表查询", notes="餐饮企业-分页列表查询")
    @GetMapping(value = "/newlist")
    public Result<?> queryPageNewList(
            @RequestParam(name="str",required=false) String str,
            @RequestParam(name="isPage",required=false) Boolean isPage,
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        Page<Map<String,Object>> page = new Page<>(pageNo, pageSize);
        List<String>ids = new LinkedList<>();
        if(StringUtil.isNotEmpty(str)){
            QueryWrapper<Company> queryCompany = new QueryWrapper<>();
            if(fidRegion!=1024){
                queryCompany.eq("fid_region",fidRegion);
            }
            queryCompany.like("name", str).or().like("door_name", str);
            List<Company> companyList = companyService.list(queryCompany);
            if(!companyList.isEmpty()){
                companyList.forEach(item->{
                    String id = item.getId();
                    if(!ids.contains(id)){
                        ids.add(id);
                    }
                });
            }

            QueryWrapper<Point> queryPoint = new QueryWrapper<>();
            if(fidRegion!=1024){
                queryPoint.eq("fid_region",fidRegion);
            }
            queryPoint.like("point_mac", str);
            List<Point> pointList = pointService.list(queryPoint);
            if(!pointList.isEmpty()){
                pointList.forEach(item->{
                    String id = item.getCompanyId();
                    if(!ids.contains(id)){
                        ids.add(id);
                    }
                });
            }
        }


        if(null!=isPage && isPage){
            Page<Map<String,Object>>map = companyService.queryList(page,ids,fidRegion);
            return Result.OK(map);
        }else {
            List<Map<String,Object>>map = companyService.queryList(ids,fidRegion);
            //for()
            return Result.OK(map);
        }

    }


    /**
     *   添加
     */
    @AutoLog(value = "餐饮企业-添加")
    @ApiOperation(value="餐饮企业-添加", notes="餐饮企业-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Company company) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        if(fidRegion==1024){
            return Result.error("管理员账号无法新增数据");
        }

        company.setFidRegion(fidRegion);
        companyService.save(company);
        return Result.OK(company.getId());
    }

    /**
     *  编辑
     */
    @AutoLog(value = "餐饮企业-编辑")
    @ApiOperation(value="餐饮企业-编辑", notes="餐饮企业-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Company company) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }

        companyService.updateById(company);
        return Result.OK("编辑成功!");
    }

    /**
     *   通过id删除
     */
    @AutoLog(value = "餐饮企业-通过id删除")
    @ApiOperation(value="餐饮企业-通过id删除", notes="餐饮企业-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        companyService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     *  批量删除
     */
    @AutoLog(value = "餐饮企业-批量删除")
    @ApiOperation(value="餐饮企业-批量删除", notes="餐饮企业-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        this.companyService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "餐饮企业-通过id查询")
    @ApiOperation(value="餐饮企业-通过id查询", notes="餐饮企业-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return Result.error("账号权限异常，请联系管理员");
        }
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }

        Company company = companyService.getOne(queryWrapper);

        if(company==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(company);
    }

    /**
     * 导出excel
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Company company) {
        return super.exportXls(request, company, Company.class, "测点数据");
    }

    /**
     * 通过excel导入数据
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Company.class);
    }

    @AutoLog(value = "餐饮企业count-添加")
    @ApiOperation(value="餐饮企业count-添加", notes="餐饮企业count-添加")
    @GetMapping(value = "/count")
    public int companyCount() {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        int fidRegion = sysUser.getFidRegion();
        if(fidRegion==0){
            return 0;
        }

        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        if(fidRegion!=1024){
            queryWrapper.eq("fid_region",fidRegion);
        }

        return companyService.count(queryWrapper);

    }


}


