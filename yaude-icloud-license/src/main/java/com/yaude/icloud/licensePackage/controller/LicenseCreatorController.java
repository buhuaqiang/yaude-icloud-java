package com.yaude.icloud.licensePackage.controller;

import com.yaude.common.util.DateUtils;
import com.yaude.icloud.licensePackage.license.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 用于生成证书文件，不能放在给客户部署的代码里
 * @author zifangsky
 * @date 2018/4/26
 * @since 1.0.0
 */
@RestController
@RequestMapping("/license")
public class LicenseCreatorController {

    /**
     * 证书生成路径
     */
    @Value("${license.licensePath}")
    private String licensePath;
    /**
     * 证书生成路径
     */
    @Value("${license.licensePaths}")
    private String licensePaths;
    /**
     * 密钥别称
     */
    @Value("${license.privateAlias}")
    private String privateAlias;

    /**
     * 密钥密码
     */
    @Value("${license.keyPass}")
    private String keyPass;
    /**
     * 访问秘钥库的密码
     */
    @Value("${license.storePass}")
    private String storePass;
    /**
     * 密钥库存储路径
     */
    @Value("${license.privateKeysStorePath}")
    private String privateKeysStorePath;

    /**
     * 获取服务器硬件信息
     * @author zifangsky
     * @date 2018/4/26 13:13
     * @since 1.0.0
     * @param osName 操作系统类型，如果为空则自动判断
     * @return com.ccx.models.license.LicenseCheckModel
     */
    @RequestMapping(value = "/getServerInfos",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public LicenseCheckModel getServerInfos(@RequestParam(value = "osName",required = false) String osName) {
        //操作系统类型
        if(StringUtils.isBlank(osName)){
            osName = System.getProperty("os.name");
        }
        osName = osName.toLowerCase();

        AbstractServerInfos abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }

        return abstractServerInfos.getServerInfos();
    }

    /**
     * 生成证书
     * @author zifangsky
     * @date 2018/4/26 13:13
     * @since 1.0.0
     * @param param 生成证书需要的参数，如：{"subject":"ccx-models","privateAlias":"privateKey","keyPass":"5T7Zz5Y0dJFcqTxvzkH5LDGJJSGMzQ","storePass":"3538cef8e7","licensePath":"C:/Users/zifangsky/Desktop/license.lic","privateKeysStorePath":"C:/Users/zifangsky/Desktop/privateKeys.keystore","issuedTime":"2018-04-26 14:48:12","expiryTime":"2018-12-31 00:00:00","consumerType":"User","consumerAmount":1,"description":"这是证书描述信息","licenseCheckModel":{"ipAddress":["192.168.245.1","10.0.5.22"],"macAddress":["00-50-56-C0-00-01","50-7B-9D-F9-18-41"],"cpuSerial":"BFEBFBFF000406E3","mainBoardSerial":"L1HF65E00X9"}}
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/generateLicense",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Map<String,Object> generateLicense(@RequestBody(required = true) LicenseCreatorParam param) {
        Map<String,Object> resultMap = new HashMap<>(2);


        param.setLicensePath(licensePaths+param.getLicensePath());


        if(StringUtils.isBlank(param.getPrivateAlias())){
            param.setPrivateAlias(privateAlias);
        }
        if(StringUtils.isBlank(param.getKeyPass())){
            param.setKeyPass(keyPass);
        }
        if(StringUtils.isBlank(param.getStorePass())){
            param.setStorePass(storePass);
        }
        if(StringUtils.isBlank(param.getPrivateKeysStorePath())){
            param.setPrivateKeysStorePath(privateKeysStorePath);
        }

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();

        if(result){
            resultMap.put("result","ok");
            resultMap.put("msg",param);
        }else{
            resultMap.put("result","error");
            resultMap.put("msg","证书文件生成失败！");
        }

        return resultMap;
    }


    public static void main(String[] args) {
        LicenseCreatorParam param = new LicenseCreatorParam();
        param.setSubject("license_demo");  //证书名称
        param.setPrivateAlias("privateKey"); //密钥别称
        param.setKeyPass("private_password1234"); //密钥密码（需要妥善保管，不能让使用者知道）
        param.setStorePass("public_password1234"); //访问秘钥库的密码
        param.setLicensePath("D:/workspace/LicenseDemo/licenseFile/license.lic"); //证书生成路径
        param.setPrivateKeysStorePath("D:/workspace/LicenseDemo/licenseFile/privateKeys.keystore"); //密钥库存储路径
       // param.setIssuedTime(new Date()); //证书生效时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date issuedTime = DateUtils.str2Date("2021-11-01 00:00:01", sdf);
        Date expiryTime = DateUtils.str2Date("2022-11-04 09:40:00", sdf);
        param.setIssuedTime(issuedTime); //证书生效时间
        param.setExpiryTime(expiryTime); //证书失效时间
        param.setConsumerType("User");  //用户类型
        param.setConsumerAmount(1);    //用户数量
        param.setDescription("这是证书描述信息");   //描述信息


        LicenseCheckModel licenseCheckModel = new LicenseCheckModel();
        //licenseCheckModel.setIpAddress(new ArrayList<>(Arrays.asList("192.168.2.3","192.168.175.1","192.168.92.1")));
        licenseCheckModel.setIpAddress(new ArrayList<>(Arrays.asList("192.168.2.15")));
        licenseCheckModel.setMacAddress(new ArrayList<>(Arrays.asList("00-50-56-C0-00-08","00-50-56-C0-00-01","F0-2F-74-50-8F-B5")));
        licenseCheckModel.setCpuSerial("BFEBFBFF000A0653");
        licenseCheckModel.setMainBoardSerial("201278076100219");
        //licenseCheckModel.setOldIpAddress("192.168.2.11");
        param.setLicenseCheckModel(licenseCheckModel);

        if(StringUtils.isBlank(param.getLicensePath())){
            param.setLicensePath("D:/workspace/LicenseDemo/licenseFile/license.lic");
        }

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();

        if(result){
            System.out.println("证书生成成功!");
        }else{
            System.out.println("证书生成失败!");
        }


    }
}
