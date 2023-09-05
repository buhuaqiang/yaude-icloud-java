package com.yaude.modules.system.controller;

import com.yaude.modules.oss.entity.OSSFile;
import com.yaude.modules.oss.service.IOSSFileService;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.CommonUtils;
import com.yaude.common.util.MinioUtil;
import com.yaude.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * minio文件上傳示例
 */
@Slf4j
@RestController
@RequestMapping("/sys/upload")
public class SysUploadController {
    @Autowired
    private IOSSFileService ossFileService;

    /**
     * 上傳
     * @param request
     */
    @PostMapping(value = "/uploadMinio")
    public Result<?> uploadMinio(HttpServletRequest request) {
        Result<?> result = new Result<>();
        String bizPath = request.getParameter("biz");
        if(oConvertUtils.isEmpty(bizPath)){
            bizPath = "";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");// 獲取上傳文件對象
        String orgName = file.getOriginalFilename();// 獲取文件名
        orgName = CommonUtils.getFileName(orgName);
        String file_url =  MinioUtil.upload(file,bizPath);
        if(oConvertUtils.isEmpty(file_url)){
            return Result.error("上傳失敗,請檢查配置信息是否正確!");
        }
        //保存文件信息
        OSSFile minioFile = new OSSFile();
        minioFile.setFileName(orgName);
        minioFile.setUrl(file_url);
        ossFileService.save(minioFile);
        result.setMessage(file_url);
        result.setSuccess(true);
        return result;
    }
}
