package com.yaude.modules.demo.test.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.aspect.annotation.PermissionData;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.util.DateUtils;
import com.yaude.common.util.RedisUtil;
import com.yaude.modules.demo.test.entity.JeecgDemo;
import com.yaude.modules.demo.test.service.IJeecgDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 單表示例
 * @Author: jeecg-boot
 * @Date:2018-12-29
 * @Version:V2.0
 */
@Slf4j
@Api(tags = "單表DEMO")
@RestController
@RequestMapping("/test/jeecgDemo")
public class JeecgDemoController extends JeecgController<JeecgDemo, IJeecgDemoService> {
    @Autowired
    private IJeecgDemoService jeecgDemoService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 分頁列表查詢
     *
     * @param jeecgDemo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation(value = "獲取Demo數據列表", notes = "獲取所有Demo數據列表")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> list(JeecgDemo jeecgDemo, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                          HttpServletRequest req) {
        QueryWrapper<JeecgDemo> queryWrapper = QueryGenerator.initQueryWrapper(jeecgDemo, req.getParameterMap());
        Page<JeecgDemo> page = new Page<JeecgDemo>(pageNo, pageSize);

        IPage<JeecgDemo> pageList = jeecgDemoService.page(page, queryWrapper);
        log.info("查詢當前頁：" + pageList.getCurrent());
        log.info("查詢當前頁數量：" + pageList.getSize());
        log.info("查詢結果數量：" + pageList.getRecords().size());
        log.info("數據總數：" + pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param jeecgDemo
     * @return
     */
    @PostMapping(value = "/add")
    @AutoLog(value = "添加測試DEMO")
    @ApiOperation(value = "添加DEMO", notes = "添加DEMO")
    public Result<?> add(@RequestBody JeecgDemo jeecgDemo) {
        jeecgDemoService.save(jeecgDemo);
        return Result.OK("添加成功！");
    }

    /**
     * 編輯
     *
     * @param jeecgDemo
     * @return
     */
    @PutMapping(value = "/edit")
    @ApiOperation(value = "編輯DEMO", notes = "編輯DEMO")
    @AutoLog(value = "編輯DEMO", operateType = CommonConstant.OPERATE_TYPE_3)
    public Result<?> edit(@RequestBody JeecgDemo jeecgDemo) {
        jeecgDemoService.updateById(jeecgDemo);
        return Result.OK("更新成功！");
    }

    /**
     * 通過id刪除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "刪除測試DEMO")
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "通過ID刪除DEMO", notes = "通過ID刪除DEMO")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        jeecgDemoService.removeById(id);
        return Result.OK("刪除成功!");
    }

    /**
     * 批量刪除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量刪除DEMO", notes = "批量刪除DEMO")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jeecgDemoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量刪除成功！");
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    @ApiOperation(value = "通過ID查詢DEMO", notes = "通過ID查詢DEMO")
    public Result<?> queryById(@ApiParam(name = "id", value = "示例id", required = true) @RequestParam(name = "id", required = true) String id) {
        JeecgDemo jeecgDemo = jeecgDemoService.getById(id);
        return Result.OK(jeecgDemo);
    }

    /**
     * 導出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public ModelAndView exportXls(HttpServletRequest request, JeecgDemo jeecgDemo) {
        //獲取導出表格字段
        String exportFields = jeecgDemoService.getExportFields();
        //分sheet導出表格字段
        return super.exportXlsSheet(request, jeecgDemo, JeecgDemo.class, "單表模型",exportFields,500);
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
        return super.importExcel(request, response, JeecgDemo.class);
    }

    // =====Redis 示例===============================================================================================

    /**
     * redis操作 -- set
     */
    @GetMapping(value = "/redisSet")
    public void redisSet() {
        redisUtil.set("name", "張三" + DateUtils.now());
    }

    /**
     * redis操作 -- get
     */
    @GetMapping(value = "/redisGet")
    public String redisGet() {
        return (String) redisUtil.get("name");
    }

    /**
     * redis操作 -- setObj
     */
    @GetMapping(value = "/redisSetObj")
    public void redisSetObj() {
        JeecgDemo p = new JeecgDemo();
        p.setAge(10);
        p.setBirthday(new Date());
        p.setContent("hello");
        p.setName("張三");
        p.setSex("男");
        redisUtil.set("user-zdh", p);
    }

    /**
     * redis操作 -- setObj
     */
    @GetMapping(value = "/redisGetObj")
    public Object redisGetObj() {
        return redisUtil.get("user-zdh");
    }

    /**
     * redis操作 -- get
     */
    @GetMapping(value = "/redis/{id}")
    public JeecgDemo redisGetJeecgDemo(@PathVariable("id") String id) {
        JeecgDemo t = jeecgDemoService.getByIdCacheable(id);
        log.info(t.toString());
        return t;
    }

    // ===Freemaker示例================================================================================

    /**
     * freemaker方式 【頁面路徑： src/main/resources/templates】
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/html")
    public ModelAndView ftl(ModelAndView modelAndView) {
        modelAndView.setViewName("demo3");
        List<String> userList = new ArrayList<String>();
        userList.add("admin");
        userList.add("user1");
        userList.add("user2");
        log.info("--------------test--------------");
        modelAndView.addObject("userList", userList);
        return modelAndView;
    }


    // ==========================================動態表單 JSON接收測試===========================================
    @PostMapping(value = "/testOnlineAdd")
    public Result<?> testOnlineAdd(@RequestBody JSONObject json) {
        log.info(json.toJSONString());
        return Result.OK("添加成功！");
    }

    /*----------------------------------------外部獲取權限示例------------------------------------*/

    /**
     * 【數據權限示例 - 編程】mybatisPlus java類方式加載權限
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/mpList")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> loadMpPermissonList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        QueryWrapper<JeecgDemo> queryWrapper = new QueryWrapper<JeecgDemo>();
        //編程方式，給queryWrapper裝載數據權限規則
        QueryGenerator.installAuthMplus(queryWrapper, JeecgDemo.class);
        Page<JeecgDemo> page = new Page<JeecgDemo>(pageNo, pageSize);
        IPage<JeecgDemo> pageList = jeecgDemoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 【數據權限示例 - 編程】mybatis xml方式加載權限
     *
     * @param jeecgDemo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/sqlList")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> loadSqlPermissonList(JeecgDemo jeecgDemo, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        IPage<JeecgDemo> pageList = jeecgDemoService.queryListWithPermission(pageSize, pageNo);
        return Result.OK(pageList);
    }
    /*----------------------------------------外部獲取權限示例------------------------------------*/
}
