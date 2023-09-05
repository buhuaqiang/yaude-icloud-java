package com.yaude.common.system.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.common.system.query.QueryGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: Controller基類
 * @Author: dangzhenghui@163.com
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public class JeecgController<T, S extends IService<T>> {
    @Autowired
    S service;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    /**
     * 導出excel
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, T object, Class<T> clazz, String title) {
        // Step.1 組裝查詢條件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 獲取導出數據
        List<T> pageList = service.list(queryWrapper);
        List<T> exportList = null;

        // 過濾選中數據
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(getId(item))).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此處設置的filename無效 ,前端會重更新設置一下
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        //update-begin--Author:liusq  Date:20210126 for：圖片導出報錯，ImageBasePath未設置--------------------
        ExportParams  exportParams=new ExportParams(title + "報表", "導出人:" + sysUser.getRealname(), title);
        exportParams.setImageBasePath(upLoadPath);
        //update-end--Author:liusq  Date:20210126 for：圖片導出報錯，ImageBasePath未設置----------------------
        mv.addObject(NormalExcelConstants.PARAMS,exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }
    /**
     * 根據每頁sheet數量導出多sheet
     *
     * @param request
     * @param object 實體類
     * @param clazz 實體類class
     * @param title 標題
     * @param exportFields 導出字段自定義
     * @param pageNum 每個sheet的數據條數
     * @param request
     */
    protected ModelAndView exportXlsSheet(HttpServletRequest request, T object, Class<T> clazz, String title,String exportFields,Integer pageNum) {
        // Step.1 組裝查詢條件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // Step.2 計算分頁sheet數據
        double total = service.count();
        int count = (int)Math.ceil(total/pageNum);
        // Step.3 多sheet處理
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <=count ; i++) {
            Page<T> page = new Page<T>(i, pageNum);
            IPage<T> pageList = service.page(page, queryWrapper);
            List<T> records = pageList.getRecords();
            List<T> exportList = null;
            // 過濾選中數據
            String selections = request.getParameter("selections");
            if (oConvertUtils.isNotEmpty(selections)) {
                List<String> selectionList = Arrays.asList(selections.split(","));
                exportList = records.stream().filter(item -> selectionList.contains(getId(item))).collect(Collectors.toList());
            } else {
                exportList = records;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            ExportParams  exportParams=new ExportParams(title + "報表", "導出人:" + sysUser.getRealname(), title+i,upLoadPath);
            exportParams.setType(ExcelType.XSSF);
            //map.put("title",exportParams);//表格Title
            map.put(NormalExcelConstants.PARAMS,exportParams);//表格Title
            map.put(NormalExcelConstants.CLASS,clazz);//表格對應實體
            map.put(NormalExcelConstants.DATA_LIST, exportList);//數據集合
            listMap.add(map);
        }
        // Step.4 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此處設置的filename無效 ,前端會重更新設置一下
        mv.addObject(NormalExcelConstants.MAP_LIST, listMap);
        return mv;
    }


    /**
     * 根據權限導出excel，傳入導出字段參數
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, T object, Class<T> clazz, String title,String exportFields) {
        ModelAndView mv = this.exportXls(request,object,clazz,title);
        mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportFields);
        return mv;
    }

    /**
     * 獲取對象ID
     *
     * @return
     */
    private String getId(T item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通過excel導入數據
     *
     * @param request
     * @param response
     * @return
     */
    protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<T> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 獲取上傳文件對象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<T> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                //update-begin-author:taoyan date:20190528 for:批量插入數據
                long start = System.currentTimeMillis();
                service.saveBatch(list);
                //400條 saveBatch消耗時間1592毫秒  循環插入消耗時間1947毫秒
                //1200條  saveBatch消耗時間3687毫秒 循環插入消耗時間5212毫秒
                log.info("消耗時間" + (System.currentTimeMillis() - start) + "毫秒");
                //update-end-author:taoyan date:20190528 for:批量插入數據
                return Result.ok("文件導入成功！數據行數：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件導入失敗:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件導入失敗！");
    }
}
