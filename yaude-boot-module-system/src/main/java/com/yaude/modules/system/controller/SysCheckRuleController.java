package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.modules.system.entity.SysCheckRule;
import com.yaude.modules.system.service.ISysCheckRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * @Description: 編碼校驗規則
 * @Author: jeecg-boot
 * @Date: 2020-02-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "編碼校驗規則")
@RestController
@RequestMapping("/sys/checkRule")
public class SysCheckRuleController extends JeecgController<SysCheckRule, ISysCheckRuleService> {

    @Autowired
    private ISysCheckRuleService sysCheckRuleService;

    /**
     * 分頁列表查詢
     *
     * @param sysCheckRule
     * @param pageNo
     * @param pageSize
     * @param request
     * @return
     */
    @AutoLog(value = "編碼校驗規則-分頁列表查詢")
    @ApiOperation(value = "編碼校驗規則-分頁列表查詢", notes = "編碼校驗規則-分頁列表查詢")
    @GetMapping(value = "/list")
    public Result queryPageList(
            SysCheckRule sysCheckRule,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        QueryWrapper<SysCheckRule> queryWrapper = QueryGenerator.initQueryWrapper(sysCheckRule, request.getParameterMap());
        Page<SysCheckRule> page = new Page<>(pageNo, pageSize);
        IPage<SysCheckRule> pageList = sysCheckRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }


    /**
     * 通過id查詢
     *
     * @param ruleCode
     * @return
     */
    @AutoLog(value = "編碼校驗規則-通過Code校驗傳入的值")
    @ApiOperation(value = "編碼校驗規則-通過Code校驗傳入的值", notes = "編碼校驗規則-通過Code校驗傳入的值")
    @GetMapping(value = "/checkByCode")
    public Result checkByCode(
            @RequestParam(name = "ruleCode") String ruleCode,
            @RequestParam(name = "value") String value
    ) throws UnsupportedEncodingException {
        SysCheckRule sysCheckRule = sysCheckRuleService.getByCode(ruleCode);
        if (sysCheckRule == null) {
            return Result.error("該編碼不存在");
        }
        JSONObject errorResult = sysCheckRuleService.checkValue(sysCheckRule, URLDecoder.decode(value, "UTF-8"));
        if (errorResult == null) {
            return Result.ok();
        } else {
            Result<Object> r = Result.error(errorResult.getString("message"));
            r.setResult(errorResult);
            return r;
        }
    }

    /**
     * 添加
     *
     * @param sysCheckRule
     * @return
     */
    @AutoLog(value = "編碼校驗規則-添加")
    @ApiOperation(value = "編碼校驗規則-添加", notes = "編碼校驗規則-添加")
    @PostMapping(value = "/add")
    public Result add(@RequestBody SysCheckRule sysCheckRule) {
        sysCheckRuleService.save(sysCheckRule);
        return Result.ok("添加成功！");
    }

    /**
     * 編輯
     *
     * @param sysCheckRule
     * @return
     */
    @AutoLog(value = "編碼校驗規則-編輯")
    @ApiOperation(value = "編碼校驗規則-編輯", notes = "編碼校驗規則-編輯")
    @PutMapping(value = "/edit")
    public Result edit(@RequestBody SysCheckRule sysCheckRule) {
        sysCheckRuleService.updateById(sysCheckRule);
        return Result.ok("編輯成功!");
    }

    /**
     * 通過id刪除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "編碼校驗規則-通過id刪除")
    @ApiOperation(value = "編碼校驗規則-通過id刪除", notes = "編碼校驗規則-通過id刪除")
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestParam(name = "id", required = true) String id) {
        sysCheckRuleService.removeById(id);
        return Result.ok("刪除成功!");
    }

    /**
     * 批量刪除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "編碼校驗規則-批量刪除")
    @ApiOperation(value = "編碼校驗規則-批量刪除", notes = "編碼校驗規則-批量刪除")
    @DeleteMapping(value = "/deleteBatch")
    public Result deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysCheckRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量刪除成功！");
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @AutoLog(value = "編碼校驗規則-通過id查詢")
    @ApiOperation(value = "編碼校驗規則-通過id查詢", notes = "編碼校驗規則-通過id查詢")
    @GetMapping(value = "/queryById")
    public Result queryById(@RequestParam(name = "id", required = true) String id) {
        SysCheckRule sysCheckRule = sysCheckRuleService.getById(id);
        return Result.ok(sysCheckRule);
    }

    /**
     * 導出excel
     *
     * @param request
     * @param sysCheckRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysCheckRule sysCheckRule) {
        return super.exportXls(request, sysCheckRule, SysCheckRule.class, "編碼校驗規則");
    }

    /**
     * 通過excel導入數據
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysCheckRule.class);
    }

}
