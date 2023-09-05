package com.yaude.common.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.vo.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 導出返回信息
 */
@Slf4j
public class ImportExcelUtil {

    public static Result<?> imporReturnRes(int errorLines,int successLines,List<String> errorMessage) throws IOException {
        if (errorLines == 0) {
            return Result.ok("共" + successLines + "行數據全部導入成功！");
        } else {
            JSONObject result = new JSONObject(5);
            int totalCount = successLines + errorLines;
            result.put("totalCount", totalCount);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            result.put("msg", "總上傳行數：" + totalCount + "，已導入行數：" + successLines + "，錯誤行數：" + errorLines);
            String fileUrl = PmsUtil.saveErrorTxtByList(errorMessage, "userImportExcelErrorLog");
            int lastIndex = fileUrl.lastIndexOf(File.separator);
            String fileName = fileUrl.substring(lastIndex + 1);
            result.put("fileUrl", "/sys/common/static/" + fileUrl);
            result.put("fileName", fileName);
            Result res = Result.ok(result);
            res.setCode(201);
            res.setMessage("文件導入成功，但有錯誤。");
            return res;
        }
    }

    public static List<String> importDateSave(List<Object> list, Class serviceClass,List<String> errorMessage,String errorFlag)  {
        IService bean =(IService) SpringContextUtils.getBean(serviceClass);
        for (int i = 0; i < list.size(); i++) {
            try {
                boolean save = bean.save(list.get(i));
                if(!save){
                    throw new Exception(errorFlag);
                }
            } catch (Exception e) {
                String message = e.getMessage().toLowerCase();
                int lineNumber = i + 1;
                // 通過索引名判斷出錯信息
                if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_ROLE_CODE)) {
                    errorMessage.add("第 " + lineNumber + " 行：角色編碼已經存在，忽略導入。");
                } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_JOB_CLASS_NAME)) {
                    errorMessage.add("第 " + lineNumber + " 行：任務類名已經存在，忽略導入。");
                }else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_CODE)) {
                    errorMessage.add("第 " + lineNumber + " 行：職務編碼已經存在，忽略導入。");
                }else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_DEPART_ORG_CODE)) {
                    errorMessage.add("第 " + lineNumber + " 行：部門編碼已經存在，忽略導入。");
                }else {
                    errorMessage.add("第 " + lineNumber + " 行：未知錯誤，忽略導入");
                    log.error(e.getMessage(), e);
                }
            }
        }
        return errorMessage;
    }

    public static List<String> importDateSaveOne(Object obj, Class serviceClass,List<String> errorMessage,int i,String errorFlag)  {
        IService bean =(IService) SpringContextUtils.getBean(serviceClass);
        try {
            boolean save = bean.save(obj);
            if(!save){
                throw new Exception(errorFlag);
            }
        } catch (Exception e) {
            String message = e.getMessage().toLowerCase();
            int lineNumber = i + 1;
            // 通過索引名判斷出錯信息
            if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_ROLE_CODE)) {
                errorMessage.add("第 " + lineNumber + " 行：角色編碼已經存在，忽略導入。");
            } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_JOB_CLASS_NAME)) {
                errorMessage.add("第 " + lineNumber + " 行：任務類名已經存在，忽略導入。");
            }else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_CODE)) {
                errorMessage.add("第 " + lineNumber + " 行：職務編碼已經存在，忽略導入。");
            }else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_DEPART_ORG_CODE)) {
                errorMessage.add("第 " + lineNumber + " 行：部門編碼已經存在，忽略導入。");
            }else {
                errorMessage.add("第 " + lineNumber + " 行：未知錯誤，忽略導入");
                log.error(e.getMessage(), e);
            }
        }
        return errorMessage;
    }
}
