package com.yaude.modules.demo.test.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.modules.demo.test.entity.JeecgDemo;
import com.yaude.modules.demo.test.entity.JeecgOrderCustomer;
import com.yaude.modules.demo.test.entity.JeecgOrderMain;
import com.yaude.modules.demo.test.entity.JeecgOrderTicket;
import com.yaude.modules.demo.test.service.IJeecgDemoService;
import com.yaude.modules.demo.test.service.IJeecgOrderCustomerService;
import com.yaude.modules.demo.test.service.IJeecgOrderMainService;
import com.yaude.modules.demo.test.service.IJeecgOrderTicketService;
import com.yaude.modules.demo.test.vo.JeecgOrderMainPage;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 一對多示例（JEditableTable行編輯）
 * @Author: jeecg-boot
 * @Date:2019-02-15
 * @Version: V2.0
 */
@RestController
@RequestMapping("/test/jeecgOrderMain")
@Slf4j
public class JeecgOrderMainController extends JeecgController<JeecgOrderMain, IJeecgOrderMainService> {

    @Autowired
    private IJeecgOrderMainService jeecgOrderMainService;
    @Autowired
    private IJeecgOrderCustomerService jeecgOrderCustomerService;
    @Autowired
    private IJeecgOrderTicketService jeecgOrderTicketService;

    /**
     * 分頁列表查詢
     *
     * @param jeecgOrderMain
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/list")
    public Result<?> queryPageList(JeecgOrderMain jeecgOrderMain, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<JeecgOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(jeecgOrderMain, req.getParameterMap());
        Page<JeecgOrderMain> page = new Page<JeecgOrderMain>(pageNo, pageSize);
        IPage<JeecgOrderMain> pageList = jeecgOrderMainService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param jeecgOrderMainPage
     * @return
     */
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody JeecgOrderMainPage jeecgOrderMainPage) {
        JeecgOrderMain jeecgOrderMain = new JeecgOrderMain();
        BeanUtils.copyProperties(jeecgOrderMainPage, jeecgOrderMain);
        jeecgOrderMainService.saveMain(jeecgOrderMain, jeecgOrderMainPage.getJeecgOrderCustomerList(), jeecgOrderMainPage.getJeecgOrderTicketList());
        return Result.ok("添加成功！");
    }

    /**
     * 編輯
     *
     * @param jeecgOrderMainPage
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<?> eidt(@RequestBody JeecgOrderMainPage jeecgOrderMainPage) {
        JeecgOrderMain jeecgOrderMain = new JeecgOrderMain();
        BeanUtils.copyProperties(jeecgOrderMainPage, jeecgOrderMain);
        jeecgOrderMainService.updateCopyMain(jeecgOrderMain, jeecgOrderMainPage.getJeecgOrderCustomerList(), jeecgOrderMainPage.getJeecgOrderTicketList());
        return Result.ok("編輯成功！");
    }

    /**
     * 通過id刪除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        jeecgOrderMainService.delMain(id);
        return Result.ok("刪除成功!");
    }

    /**
     * 批量刪除
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jeecgOrderMainService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.ok("批量刪除成功!");
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        JeecgOrderMain jeecgOrderMain = jeecgOrderMainService.getById(id);
        return Result.ok(jeecgOrderMain);
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryOrderCustomerListByMainId")
    public Result<?> queryOrderCustomerListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<JeecgOrderCustomer> jeecgOrderCustomerList = jeecgOrderCustomerService.selectCustomersByMainId(id);
        return Result.ok(jeecgOrderCustomerList);
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryOrderTicketListByMainId")
    public Result<?> queryOrderTicketListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<JeecgOrderTicket> jeecgOrderTicketList = jeecgOrderTicketService.selectTicketsByMainId(id);
        return Result.ok(jeecgOrderTicketList);
    }

    /**
     * 導出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, JeecgOrderMain jeecgOrderMain) {
        // Step.1 組裝查詢條件
        QueryWrapper<JeecgOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(jeecgOrderMain, request.getParameterMap());
        //Step.2 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //獲取當前用戶
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        List<JeecgOrderMainPage> pageList = new ArrayList<JeecgOrderMainPage>();

        List<JeecgOrderMain> jeecgOrderMainList = jeecgOrderMainService.list(queryWrapper);
        for (JeecgOrderMain orderMain : jeecgOrderMainList) {
            JeecgOrderMainPage vo = new JeecgOrderMainPage();
            BeanUtils.copyProperties(orderMain, vo);
            // 查詢機票
            List<JeecgOrderTicket> jeecgOrderTicketList = jeecgOrderTicketService.selectTicketsByMainId(orderMain.getId());
            vo.setJeecgOrderTicketList(jeecgOrderTicketList);
            // 查詢客戶
            List<JeecgOrderCustomer> jeecgOrderCustomerList = jeecgOrderCustomerService.selectCustomersByMainId(orderMain.getId());
            vo.setJeecgOrderCustomerList(jeecgOrderCustomerList);
            pageList.add(vo);
        }

        // 導出文件名稱
        mv.addObject(NormalExcelConstants.FILE_NAME, "一對多訂單示例");
        // 注解對象Class
        mv.addObject(NormalExcelConstants.CLASS, JeecgOrderMainPage.class);
        // 自定義表格參數
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("自定義導出Excel內容標題", "導出人:" + sysUser.getRealname(), "自定義Sheet名字"));
        // 導出數據列表
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通過excel導入數據
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 獲取上傳文件對象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(2);
            params.setNeedSave(true);
            try {
                List<JeecgOrderMainPage> list = ExcelImportUtil.importExcel(file.getInputStream(), JeecgOrderMainPage.class, params);
                for (JeecgOrderMainPage page : list) {
                    JeecgOrderMain po = new JeecgOrderMain();
                    BeanUtils.copyProperties(page, po);
                    jeecgOrderMainService.saveMain(po, page.getJeecgOrderCustomerList(), page.getJeecgOrderTicketList());
                }
                return Result.ok("文件導入成功！");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件導入失敗：" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件導入失敗！");
    }

}
