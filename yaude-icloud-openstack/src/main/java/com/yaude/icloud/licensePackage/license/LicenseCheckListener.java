package com.yaude.icloud.licensePackage.license;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 在项目启动时安装证书
 *
 * @author zifangsky
 * @date 2018/4/24
 * @since 1.0.0
 */
//@Component
public class LicenseCheckListener implements ApplicationListener<ApplicationEvent> {
    private static Logger logger = LogManager.getLogger(LicenseCheckListener.class);

    /**
     * 证书subject
     */
    @Value("${license.subject}")
    private String subject;

    /**
     * 公钥别称
     */
    @Value("${license.publicAlias}")
    private String publicAlias;

    /**
     * 访问公钥库的密码
     */
    @Value("${license.storePass}")
    private String storePass;

    /**
     * 证书生成路径
     */
    @Value("${license.licensePath}")
    private String licensePath;

    /**
     * 密钥库存储路径
     */
    @Value("${license.publicKeysStorePath}")
    private String publicKeysStorePath;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("暗红色的好受点");
        if (event instanceof ApplicationStartingEvent){
            System.out.println("ApplicationStarting");
            return;
        }
        //确定springboot应用使用的Environment且context创建之前发送这个事件
        if (event instanceof ApplicationEnvironmentPreparedEvent){
            System.out.println("ApplicationEnvironmentPrepared");
            return;
        }
        //context已经创建且没有refresh发送个事件
        if (event instanceof ApplicationPreparedEvent){
            System.out.println("ApplicationPrepared");
            return;
        }
        //context已经refresh且application and command-line runners（如果有） 调用之前发送这个事件
        if (event instanceof ApplicationStartedEvent){
            System.out.println("ApplicationStarted");
            return;
        }

        if (event instanceof ApplicationReadyEvent){
            System.out.println("ApplicationReady");
            return;
        }
    }

}
