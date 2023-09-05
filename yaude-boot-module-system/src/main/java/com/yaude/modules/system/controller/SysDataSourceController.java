package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.dynamic.db.DataSourceCachePool;
import com.yaude.modules.system.entity.SysDataSource;
import com.yaude.modules.system.service.ISysDataSourceService;
import com.yaude.modules.system.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 多數據源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "多數據源管理")
@RestController
@RequestMapping("/sys/dataSource")
public class SysDataSourceController extends JeecgController<SysDataSource, ISysDataSourceService> {

    @Autowired
    private ISysDataSourceService sysDataSourceService;

    /**
     * 分頁列表查詢
     *
     * @param sysDataSource
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "多數據源管理-分頁列表查詢")
    @ApiOperation(value = "多數據源管理-分頁列表查詢", notes = "多數據源管理-分頁列表查詢")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            SysDataSource sysDataSource,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req
    ) {
        QueryWrapper<SysDataSource> queryWrapper = QueryGenerator.initQueryWrapper(sysDataSource, req.getParameterMap());
        Page<SysDataSource> page = new Page<>(pageNo, pageSize);
        IPage<SysDataSource> pageList = sysDataSourceService.page(page, queryWrapper);
        try {
            List<SysDataSource> records = pageList.getRecords();
            records.forEach(item->{
                String dbPassword = item.getDbPassword();
                if(StringUtils.isNotBlank(dbPassword)){
                    String decodedStr = SecurityUtil.jiemi(dbPassword);
                    item.setDbPassword(decodedStr);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(pageList);
    }

    @GetMapping(value = "/options")
    public Result<?> queryOptions(SysDataSource sysDataSource, HttpServletRequest req) {
        QueryWrapper<SysDataSource> queryWrapper = QueryGenerator.initQueryWrapper(sysDataSource, req.getParameterMap());
        List<SysDataSource> pageList = sysDataSourceService.list(queryWrapper);
        JSONArray array = new JSONArray(pageList.size());
        for (SysDataSource item : pageList) {
            JSONObject option = new JSONObject(3);
            option.put("value", item.getCode());
            option.put("label", item.getName());
            option.put("text", item.getName());
            array.add(option);
        }
        return Result.ok(array);
    }

    /**
     * 添加
     *
     * @param sysDataSource
     * @return
     */
    @AutoLog(value = "多數據源管理-添加")
    @ApiOperation(value = "多數據源管理-添加", notes = "多數據源管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysDataSource sysDataSource) {
        try {
            String dbPassword = sysDataSource.getDbPassword();
            if(StringUtils.isNotBlank(dbPassword)){
                String encrypt = SecurityUtil.jiami(dbPassword);
                sysDataSource.setDbPassword(encrypt);
            }
            sysDataSourceService.save(sysDataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok("添加成功！");
    }

    /**
     * 編輯
     *
     * @param sysDataSource
     * @return
     */
    @AutoLog(value = "多數據源管理-編輯")
    @ApiOperation(value = "多數據源管理-編輯", notes = "多數據源管理-編輯")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SysDataSource sysDataSource) {
        try {
            SysDataSource d = sysDataSourceService.getById(sysDataSource.getId());
            DataSourceCachePool.removeCache(d.getCode());
            String dbPassword = sysDataSource.getDbPassword();
            if(StringUtils.isNotBlank(dbPassword)){
                String encrypt = SecurityUtil.jiami(dbPassword);
                sysDataSource.setDbPassword(encrypt);
            }
            sysDataSourceService.updateById(sysDataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok("編輯成功!");
    }

    /**
     * 通過id刪除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "多數據源管理-通過id刪除")
    @ApiOperation(value = "多數據源管理-通過id刪除", notes = "多數據源管理-通過id刪除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        SysDataSource sysDataSource = sysDataSourceService.getById(id);
        DataSourceCachePool.removeCache(sysDataSource.getCode());
        sysDataSourceService.removeById(id);
        return Result.ok("刪除成功!");
    }

    /**
     * 批量刪除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "多數據源管理-批量刪除")
    @ApiOperation(value = "多數據源管理-批量刪除", notes = "多數據源管理-批量刪除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(item->{
            SysDataSource sysDataSource = sysDataSourceService.getById(item);
            DataSourceCachePool.removeCache(sysDataSource.getCode());
        });
        this.sysDataSourceService.removeByIds(idList);
        return Result.ok("批量刪除成功！");
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @AutoLog(value = "多數據源管理-通過id查詢")
    @ApiOperation(value = "多數據源管理-通過id查詢", notes = "多數據源管理-通過id查詢")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        SysDataSource sysDataSource = sysDataSourceService.getById(id);
        return Result.ok(sysDataSource);
    }

    /**
     * 導出excel
     *
     * @param request
     * @param sysDataSource
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysDataSource sysDataSource) {
        return super.exportXls(request, sysDataSource, SysDataSource.class, "多數據源管理");
    }

    /**
     * 通過excel導入數據
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysDataSource.class);
    }

}
