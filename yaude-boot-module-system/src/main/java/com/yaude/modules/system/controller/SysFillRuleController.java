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
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.FillRuleUtil;
import com.yaude.modules.system.entity.SysFillRule;
import com.yaude.modules.system.service.ISysFillRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 填值規則
 * @Author: jeecg-boot
 * @Date: 2019-11-07
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "填值規則")
@RestController
@RequestMapping("/sys/fillRule")
public class SysFillRuleController extends JeecgController<SysFillRule, ISysFillRuleService> {
    @Autowired
    private ISysFillRuleService sysFillRuleService;

    /**
     * 分頁列表查詢
     *
     * @param sysFillRule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "填值規則-分頁列表查詢")
    @ApiOperation(value = "填值規則-分頁列表查詢", notes = "填值規則-分頁列表查詢")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SysFillRule sysFillRule,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysFillRule> queryWrapper = QueryGenerator.initQueryWrapper(sysFillRule, req.getParameterMap());
        Page<SysFillRule> page = new Page<>(pageNo, pageSize);
        IPage<SysFillRule> pageList = sysFillRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 測試 ruleCode
     *
     * @param ruleCode
     * @return
     */
    @GetMapping(value = "/testFillRule")
    public Result testFillRule(@RequestParam("ruleCode") String ruleCode) {
        Object result = FillRuleUtil.executeRule(ruleCode, new JSONObject());
        return Result.ok(result);
    }

    /**
     * 添加
     *
     * @param sysFillRule
     * @return
     */
    @AutoLog(value = "填值規則-添加")
    @ApiOperation(value = "填值規則-添加", notes = "填值規則-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysFillRule sysFillRule) {
        sysFillRuleService.save(sysFillRule);
        return Result.ok("添加成功！");
    }

    /**
     * 編輯
     *
     * @param sysFillRule
     * @return
     */
    @AutoLog(value = "填值規則-編輯")
    @ApiOperation(value = "填值規則-編輯", notes = "填值規則-編輯")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SysFillRule sysFillRule) {
        sysFillRuleService.updateById(sysFillRule);
        return Result.ok("編輯成功!");
    }

    /**
     * 通過id刪除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "填值規則-通過id刪除")
    @ApiOperation(value = "填值規則-通過id刪除", notes = "填值規則-通過id刪除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysFillRuleService.removeById(id);
        return Result.ok("刪除成功!");
    }

    /**
     * 批量刪除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "填值規則-批量刪除")
    @ApiOperation(value = "填值規則-批量刪除", notes = "填值規則-批量刪除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysFillRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量刪除成功！");
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @AutoLog(value = "填值規則-通過id查詢")
    @ApiOperation(value = "填值規則-通過id查詢", notes = "填值規則-通過id查詢")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysFillRule sysFillRule = sysFillRuleService.getById(id);
        return Result.ok(sysFillRule);
    }

    /**
     * 導出excel
     *
     * @param request
     * @param sysFillRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysFillRule sysFillRule) {
        return super.exportXls(request, sysFillRule, SysFillRule.class, "填值規則");
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
        return super.importExcel(request, response, SysFillRule.class);
    }

    /**
     * 通過 ruleCode 執行自定義填值規則
     *
     * @param ruleCode 要執行的填值規則編碼
     * @param formData 表單數據，可根據表單數據的不同生成不同的填值結果
     * @return 運行后的結果
     */
    @PutMapping("/executeRuleByCode/{ruleCode}")
    public Result executeByRuleCode(@PathVariable("ruleCode") String ruleCode, @RequestBody JSONObject formData) {
        Object result = FillRuleUtil.executeRule(ruleCode, formData);
        return Result.ok(result);
    }


    /**
     * 批量通過 ruleCode 執行自定義填值規則
     *
     * @param ruleData 要執行的填值規則JSON數組：
     *                 示例： { "commonFormData": {}, rules: [ { "ruleCode": "xxx", "formData": null } ] }
     * @return 運行后的結果，返回示例： [{"ruleCode": "order_num_rule", "result": "CN2019111117212984"}]
     *
     */
    @PutMapping("/executeRuleByCodeBatch")
    public Result executeByRuleCodeBatch(@RequestBody JSONObject ruleData) {
        JSONObject commonFormData = ruleData.getJSONObject("commonFormData");
        JSONArray rules = ruleData.getJSONArray("rules");
        // 遍歷 rules ，批量執行規則
        JSONArray results = new JSONArray(rules.size());
        for (int i = 0; i < rules.size(); i++) {
            JSONObject rule = rules.getJSONObject(i);
            String ruleCode = rule.getString("ruleCode");
            JSONObject formData = rule.getJSONObject("formData");
            // 如果沒有傳遞 formData，就用common的
            if (formData == null) {
                formData = commonFormData;
            }
            // 執行填值規則
            Object result = FillRuleUtil.executeRule(ruleCode, formData);
            JSONObject obj = new JSONObject(rules.size());
            obj.put("ruleCode", ruleCode);
            obj.put("result", result);
            results.add(obj);
        }
        return Result.ok(results);
    }

}