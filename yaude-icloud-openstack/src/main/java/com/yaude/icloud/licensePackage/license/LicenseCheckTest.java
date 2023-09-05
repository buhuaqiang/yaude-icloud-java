package com.yaude.icloud.licensePackage.license;

import de.schlichtherle.license.LicenseContent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * 在项目启动时安装证书
 *
 * @author zifangsky
 * @date 2018/4/24
 * @since 1.0.0
 */
@Component
public class LicenseCheckTest implements InitializingBean {
    private static Logger logger = LogManager.getLogger(LicenseCheckTest.class);

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
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isNotBlank(licensePath)){
//            logger.info("++++++++ 开始安装证书 ++++++++");
//
//            LicenseVerifyParam param = new LicenseVerifyParam();
//            param.setSubject(subject);
//            param.setPublicAlias(publicAlias);
//            param.setStorePass(storePass);
//            param.setLicensePath(licensePath);
//            param.setPublicKeysStorePath(publicKeysStorePath);
//
//            LicenseVerify licenseVerify = new LicenseVerify();
//            //安装证书
//            LicenseContent install = licenseVerify.install(param);
//            if(install == null){
//                throw  new RuntimeException("证书校验失败");
//            }
//
//            logger.info("++++++++ 证书安装结束 ++++++++");
        }
    }
}
