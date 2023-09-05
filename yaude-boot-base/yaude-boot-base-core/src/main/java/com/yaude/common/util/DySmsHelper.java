package com.yaude.common.util;

import com.yaude.config.StaticConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created on 17/6/7.
 * 短信API產品的DEMO程序,工程中包含了一個SmsDemo類，直接通過
 * 執行main函數即可體驗短信產品API功能(只需要將AK替換成開通了云通信-短信產品功能的AK即可)
 * 工程依賴了2個jar包(存放在工程的libs目錄下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 *
 * 備注:Demo工程編碼采用UTF-8
 * 國際短信發送請勿參照此DEMO
 */
public class DySmsHelper {
	
	private final static Logger logger=LoggerFactory.getLogger(DySmsHelper.class);

    //產品名稱:云通信短信API產品,開發者無需替換
    static final String product = "Dysmsapi";
    //產品域名,開發者無需替換
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此處需要替換成開發者自己的AK(在阿里云訪問控制臺尋找)
    static  String accessKeyId;
    static  String accessKeySecret;

    public static void setAccessKeyId(String accessKeyId) {
        DySmsHelper.accessKeyId = accessKeyId;
    }

    public static void setAccessKeySecret(String accessKeySecret) {
        DySmsHelper.accessKeySecret = accessKeySecret;
    }

    public static String getAccessKeyId() {
        return accessKeyId;
    }

    public static String getAccessKeySecret() {
        return accessKeySecret;
    }
    
    
    public static boolean sendSms(String phone,JSONObject templateParamJson,DySmsEnum dySmsEnum) throws ClientException {
    	//可自助調整超時時間
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //update-begin-author：taoyan date:20200811 for:配置類數據獲取
        StaticConfig staticConfig = SpringContextUtils.getBean(StaticConfig.class);
        setAccessKeyId(staticConfig.getAccessKeyId());
        setAccessKeySecret(staticConfig.getAccessKeySecret());
        //update-end-author：taoyan date:20200811 for:配置類數據獲取
        
        //初始化acsClient,暫不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        
        //驗證json參數
        validateParam(templateParamJson,dySmsEnum);
        
        //組裝請求對象-具體描述見控制臺-文檔部分內容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待發送手機號
        request.setPhoneNumbers(phone);
        //必填:短信簽名-可在短信控制臺中找到
        request.setSignName(dySmsEnum.getSignName());
        //必填:短信模板-可在短信控制臺中找到
        request.setTemplateCode(dySmsEnum.getTemplateCode());
        //可選:模板中的變量替換JSON串,如模板內容為"親愛的${name},您的驗證碼為${code}"時,此處的值為
        request.setTemplateParam(templateParamJson.toJSONString());
        
        //選填-上行短信擴展碼(無特殊需求用戶請忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可選:outId為提供給業務方擴展字段,最終在短信回執消息中將此值帶回給調用者
        //request.setOutId("yourOutId");

        boolean result = false;

        //hint 此處可能會拋出異常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        logger.info("短信接口返回的數據----------------");
        logger.info("{Code:" + sendSmsResponse.getCode()+",Message:" + sendSmsResponse.getMessage()+",RequestId:"+ sendSmsResponse.getRequestId()+",BizId:"+sendSmsResponse.getBizId()+"}");
        if ("OK".equals(sendSmsResponse.getCode())) {
            result = true;
        }
        return result;
        
    }
    
    private static void validateParam(JSONObject templateParamJson,DySmsEnum dySmsEnum) {
    	String keys = dySmsEnum.getKeys();
    	String [] keyArr = keys.split(",");
    	for(String item :keyArr) {
    		if(!templateParamJson.containsKey(item)) {
    			throw new RuntimeException("模板缺少參數："+item);
    		}
    	}
    }
    

//    public static void main(String[] args) throws ClientException, InterruptedException {
//    	JSONObject obj = new JSONObject();
//    	obj.put("code", "1234");
//    	sendSms("13800138000", obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
//    }
}
