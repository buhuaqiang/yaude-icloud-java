package com.yaude.common.util.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.yaude.common.util.filter.FileTypeFilter;
import com.yaude.common.util.filter.StrAttackFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import com.yaude.common.util.CommonUtils;
import com.yaude.common.util.oConvertUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: 阿里云 oss 上傳工具類(高依賴版)
 * @Date: 2019/5/10
 */
@Slf4j
public class OssBootUtil {

    private static String endPoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bucketName;
    private static String staticDomain;

    public static void setEndPoint(String endPoint) {
        OssBootUtil.endPoint = endPoint;
    }

    public static void setAccessKeyId(String accessKeyId) {
        OssBootUtil.accessKeyId = accessKeyId;
    }

    public static void setAccessKeySecret(String accessKeySecret) {
        OssBootUtil.accessKeySecret = accessKeySecret;
    }

    public static void setBucketName(String bucketName) {
        OssBootUtil.bucketName = bucketName;
    }

    public static void setStaticDomain(String staticDomain) {
        OssBootUtil.staticDomain = staticDomain;
    }

    public static String getStaticDomain() {
        return staticDomain;
    }

    public static String getEndPoint() {
        return endPoint;
    }

    public static String getAccessKeyId() {
        return accessKeyId;
    }

    public static String getAccessKeySecret() {
        return accessKeySecret;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public static OSSClient getOssClient() {
        return ossClient;
    }

    /**
     * oss 工具客戶端
     */
    private static OSSClient ossClient = null;

    /**
     * 上傳文件至阿里云 OSS
     * 文件上傳成功,返回文件完整訪問路徑
     * 文件上傳失敗,返回 null
     *
     * @param file    待上傳文件
     * @param fileDir 文件保存目錄
     * @return oss 中的相對文件路徑
     */
    public static String upload(MultipartFile file, String fileDir,String customBucket) {
        String FILE_URL = null;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        String newBucket = bucketName;
        if(oConvertUtils.isNotEmpty(customBucket)){
            newBucket = customBucket;
        }
        try {
            //判斷桶是否存在,不存在則創建桶
            if(!ossClient.doesBucketExist(newBucket)){
                ossClient.createBucket(newBucket);
            }
            // 獲取文件名
            String orgName = file.getOriginalFilename();
            if("" == orgName){
              orgName=file.getName();
            }
            //update-begin-author:liusq date:20210809 for: 過濾上傳文件類型
            FileTypeFilter.fileTypeFilter(file);
            //update-end-author:liusq date:20210809 for: 過濾上傳文件類型
            orgName = CommonUtils.getFileName(orgName);
            String fileName = orgName.indexOf(".")==-1
                              ?orgName + "_" + System.currentTimeMillis()
                              :orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.lastIndexOf("."));
            if (!fileDir.endsWith("/")) {
                fileDir = fileDir.concat("/");
            }
            //update-begin-author:wangshuai date:20201012 for: 過濾上傳文件夾名特殊字符，防止攻擊
            fileDir= StrAttackFilter.filter(fileDir);
            //update-end-author:wangshuai date:20201012 for: 過濾上傳文件夾名特殊字符，防止攻擊
            fileUrl = fileUrl.append(fileDir + fileName);

            if (oConvertUtils.isNotEmpty(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
                FILE_URL = staticDomain + "/" + fileUrl;
            } else {
                FILE_URL = "https://" + newBucket + "." + endPoint + "/" + fileUrl;
            }
            PutObjectResult result = ossClient.putObject(newBucket, fileUrl.toString(), file.getInputStream());
            // 設置權限(公開讀)
//            ossClient.setBucketAcl(newBucket, CannedAccessControlList.PublicRead);
            if (result != null) {
                log.info("------OSS文件上傳成功------" + fileUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return FILE_URL;
    }

    /**
     * 獲取原始URL
    * @param url: 原始URL
    * @Return: java.lang.String
    */
    public static String getOriginalUrl(String url) {
        String originalDomain = "https://" + bucketName + "." + endPoint;
        if(url.indexOf(staticDomain)!=-1){
            url = url.replace(staticDomain,originalDomain);
        }
        return url;
    }

    /**
     * 文件上傳
     * @param file
     * @param fileDir
     * @return
     */
    public static String upload(MultipartFile file, String fileDir) {
        return upload(file, fileDir,null);
    }

    /**
     * 上傳文件至阿里云 OSS
     * 文件上傳成功,返回文件完整訪問路徑
     * 文件上傳失敗,返回 null
     *
     * @param file    待上傳文件
     * @param fileDir 文件保存目錄
     * @return oss 中的相對文件路徑
     */
    public static String upload(FileItemStream file, String fileDir) {
        String FILE_URL = null;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        try {
            String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            if (!fileDir.endsWith("/")) {
                fileDir = fileDir.concat("/");
            }
            fileDir = StrAttackFilter.filter(fileDir);
            fileUrl = fileUrl.append(fileDir + fileName);
            if (oConvertUtils.isNotEmpty(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
                FILE_URL = staticDomain + "/" + fileUrl;
            } else {
                FILE_URL = "https://" + bucketName + "." + endPoint + "/" + fileUrl;
            }
            PutObjectResult result = ossClient.putObject(bucketName, fileUrl.toString(), file.openStream());
            // 設置權限(公開讀)
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (result != null) {
                log.info("------OSS文件上傳成功------" + fileUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return FILE_URL;
    }

    /**
     * 刪除文件
     * @param url
     */
    public static void deleteUrl(String url) {
        deleteUrl(url,null);
    }

    /**
     * 刪除文件
     * @param url
     */
    public static void deleteUrl(String url,String bucket) {
        String newBucket = bucketName;
        if(oConvertUtils.isNotEmpty(bucket)){
            newBucket = bucket;
        }
        String bucketUrl = "";
        if (oConvertUtils.isNotEmpty(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
            bucketUrl = staticDomain + "/" ;
        } else {
            bucketUrl = "https://" + newBucket + "." + endPoint + "/";
        }
        url = url.replace(bucketUrl,"");
        ossClient.deleteObject(newBucket, url);
    }

    /**
     * 刪除文件
     * @param fileName
     */
    public static void delete(String fileName) {
        ossClient.deleteObject(bucketName, fileName);
    }

    /**
     * 獲取文件流
     * @param objectName
     * @param bucket
     * @return
     */
    public static InputStream getOssFile(String objectName,String bucket){
        InputStream inputStream = null;
        try{
            String newBucket = bucketName;
            if(oConvertUtils.isNotEmpty(bucket)){
                newBucket = bucket;
            }
            initOSS(endPoint, accessKeyId, accessKeySecret);
            OSSObject ossObject = ossClient.getObject(newBucket,objectName);
            inputStream = new BufferedInputStream(ossObject.getObjectContent());
        }catch (Exception e){
            log.info("文件獲取失敗" + e.getMessage());
        }
        return inputStream;
    }

    /**
     * 獲取文件流
     * @param objectName
     * @return
     */
    public static InputStream getOssFile(String objectName){
        return getOssFile(objectName,null);
    }

    /**
     * 獲取文件外鏈
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     */
    public static String getObjectURL(String bucketName, String objectName, Date expires) {
        initOSS(endPoint, accessKeyId, accessKeySecret);
        try{
            if(ossClient.doesObjectExist(bucketName,objectName)){
                URL url = ossClient.generatePresignedUrl(bucketName,objectName,expires);
                return URLDecoder.decode(url.toString(),"UTF-8");
            }
        }catch (Exception e){
            log.info("文件路徑獲取失敗" + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化 oss 客戶端
     *
     * @return
     */
    private static OSSClient initOSS(String endpoint, String accessKeyId, String accessKeySecret) {
        if (ossClient == null) {
            ossClient = new OSSClient(endpoint,
                    new DefaultCredentialProvider(accessKeyId, accessKeySecret),
                    new ClientConfiguration());
        }
        return ossClient;
    }


    /**
     * 上傳文件到oss
     * @param stream
     * @param relativePath
     * @return
     */
    public static String upload(InputStream stream, String relativePath) {
        String FILE_URL = null;
        String fileUrl = relativePath;
        initOSS(endPoint, accessKeyId, accessKeySecret);
        if (oConvertUtils.isNotEmpty(staticDomain) && staticDomain.toLowerCase().startsWith("http")) {
            FILE_URL = staticDomain + "/" + relativePath;
        } else {
            FILE_URL = "https://" + bucketName + "." + endPoint + "/" + fileUrl;
        }
        PutObjectResult result = ossClient.putObject(bucketName, fileUrl.toString(),stream);
        // 設置權限(公開讀)
        ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        if (result != null) {
            log.info("------OSS文件上傳成功------" + fileUrl);
        }
        return FILE_URL;
    }


}