package com.yaude;

import com.yaude.common.util.oConvertUtils;
import com.yaude.icloud.licensePackage.license.LicenseCheckListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* 單體啟動類（采用此類啟動為單體模式）
*/
@Slf4j
@ComponentScan(basePackages = {"com.yaude","com.yaude.icloud"})
@SpringBootApplication
public class JeecgSystemApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JeecgSystemApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
       /* if(true){
            throw new NumberFormatException();
        }*/
       /* SpringApplication springApplication = new SpringApplication();
        springApplication.addListeners(new LicenseCheckListener());
        ConfigurableApplicationContext application = SpringApplication.run(JeecgSystemApplication.class, args);*/
        ConfigurableApplicationContext application = SpringApplication.run(JeecgSystemApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = oConvertUtils.getString(env.getProperty("server.servlet.context-path"));
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Yaude-Boot is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                "Swagger文檔: \thttp://" + ip + ":" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");
        log.info("\n----------------------------------------------------------\n\t" +
                "测试版本变化 V1.0"
                );

    }



}