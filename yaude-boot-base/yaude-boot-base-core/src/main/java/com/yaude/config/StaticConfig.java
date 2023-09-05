package com.yaude.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 設置靜態參數初始化
 */
@Component
@Data
public class StaticConfig {

    @Value("${jeecg.oss.accessKey}")
    private String accessKeyId;

    @Value("${jeecg.oss.secretKey}")
    private String accessKeySecret;

    @Value(value = "${spring.mail.username}")
    private String emailFrom;

    /**
     * 簽名密鑰串
     */
    @Value(value = "${jeecg.signatureSecret}")
    private String signatureSecret;


    /*@Bean
    public void initStatic() {
       DySmsHelper.setAccessKeyId(accessKeyId);
       DySmsHelper.setAccessKeySecret(accessKeySecret);
       EmailSendMsgHandle.setEmailFrom(emailFrom);
    }*/

}