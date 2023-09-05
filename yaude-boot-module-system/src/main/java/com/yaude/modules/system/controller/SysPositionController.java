package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.query.QueryGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.ImportExcelUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysPosition;
import com.yaude.modules.system.service.ISysPositionService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 職務表
 * @Author: jeecg-boot
 * @Date: 2019-09-19
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "職務表")
@RestController
@RequestMapping("/sys/position")
public class SysPositionController {

    @Autowired
    private ISysPositionService sysPositionService;

    /**
     * 分頁列表查詢
     *
     * @param sysPosition
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "職務表-分頁列表查詢")
    @ApiOperation(value = "職務表-分頁列表查詢", notes = "職務表-分頁列表查詢")
    @GetMapping(value = "/list")
    public Result<IPage<SysPosition>> queryPageList(SysPosition sysPosition,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        Result<IPage<SysPosition>> result = new Result<IPage<SysPosition>>();
        QueryWrapper<SysPosition> queryWrapper = QueryGenerator.initQueryWrapper(sysPosition, req.getParameterMap());
        Page<SysPosition> page = new Page<SysPosition>(pageNo, pageSize);
        IPage<SysPosition> pageList = sysPositionService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param sysPosition
     * @return
     */
    @AutoLog(value = "職務表-添加")
    @ApiOperation(value = "職務表-添加", notes = "職務表-添加")
    @PostMapping(value = "/add")
    public Result<SysPosition> add(@RequestBody SysPosition sysPosition) {
        Result<SysPosition> result = new Result<SysPosition>();
        try {
            sysPositionService.save(sysPosition);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失敗");
        }
        return result;
    }

    /**
     * 編輯
     *
     * @param sysPosition
     * @return
     */
    @AutoLog(value = "職務表-編輯")
    @ApiOperation(value = "職務表-編輯", notes = "職務表-編輯")
    @PutMapping(value = "/edit")
    public Result<SysPosition> edit(@RequestBody SysPosition sysPosition) {
        Result<SysPosition> result = new Result<SysPosition>();
        SysPosition sysPositionEntity = sysPositionService.getById(sysPosition.getId());
        if (sysPositionEntity == null) {
            result.error500("未找到對應實體");
        } else {
            boolean ok = sysPositionService.updateById(sysPosition);
            //TODO 返回false說明什麼？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通過id刪除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "職務表-通過id刪除")
    @ApiOperation(value = "職務表-通過id刪除", notes = "職務表-通過id刪除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sysPositionService.removeById(id);
        } catch (Exception e) {
            log.error("刪除失敗", e.getMessage());
            return Result.error("刪除失敗!");
        }
        return Result.ok("刪除成功!");
    }

    /**
     * 批量刪除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "職務表-批量刪除")
    @ApiOperation(value = "職務表-批量刪除", notes = "職務表-批量刪除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<SysPosition> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysPosition> result = new Result<SysPosition>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("參數不識別！");
        } else {
            this.sysPositionService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("刪除成功!");
        }
        return result;
    }

    /**
     * 通過id查詢
     *
     * @param id
     * @return
     */
    @AutoLog(value = "職務表-通過id查詢")
    @ApiOperation(value = "職務表-通過id查詢", notes = "職務表-通過id查詢")
    @GetMapping(value = "/queryById")
    public Result<SysPosition> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysPosition> result = new Result<SysPosition>();
        SysPosition sysPosition = sysPositionService.getById(id);
        if (sysPosition == null) {
            result.error500("未找到對應實體");
        } else {
            result.setResult(sysPosition);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 導出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 組裝查詢條件
        QueryWrapper<SysPosition> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                SysPosition sysPosition = JSON.parseObject(deString, SysPosition.class);
                queryWrapper = QueryGenerator.initQueryWrapper(sysPosition, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysPosition> pageList = sysPositionService.list(queryWrapper);
        //導出文件名稱
        mv.addObject(NormalExcelConstants.FILE_NAME, "職務表列表");
        mv.addObject(NormalExcelConstants.CLASS, SysPosition.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("職務表列表數據", "導出人:Jeecg", "導出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通過excel導入數據
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 錯誤信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 獲取上傳文件對像
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Object>  listSysPositions = ExcelImportUtil.importExcel(file.getInputStream(), SysPosition.class, params);
                List<String> list = ImportExcelUtil.importDateSave(listSysPositions, ISysPositionService.class, errorMessage, CommonConstant.SQL_INDEX_UNIQ_CODE);
                errorLines+=list.size();
                successLines+=(listSysPositions.size()-errorLines);
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
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
    }

    /**
     * 通過code查詢
     *
     * @param code
     * @return
     */
    @AutoLog(value = "職務表-通過code查詢")
    @ApiOperation(value = "職務表-通過code查詢", notes = "職務表-通過code查詢")
    @GetMapping(value = "/queryByCode")
    public Result<SysPosition> queryByCode(@RequestParam(name = "code", required = true) String code) {
        Result<SysPosition> result = new Result<SysPosition>();
        QueryWrapper<SysPosition> queryWrapper = new QueryWrapper<SysPosition>();
        queryWrapper.eq("code",code);
        SysPosition sysPosition = sysPositionService.getOne(queryWrapper);
        if (sysPosition == null) {
            result.error500("未找到對應實體");
        } else {
            result.setResult(sysPosition);
            result.setSuccess(true);
        }
        return result;
    }
}
