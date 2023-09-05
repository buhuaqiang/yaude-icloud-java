package com.yaude.config.init;

import com.alibaba.druid.filter.config.ConfigTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.codegenerate.database.CodegenDatasourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 代碼生成器,自定義DB配置
 * 【加了此類，則online模式DB連接，使用平臺的配置，jeecg_database.properties配置無效;
 *  但是使用GUI模式代碼生成，還是走jeecg_database.properties配置】
 *  提醒： 達夢數據庫需要修改下面的參數${spring.datasource.dynamic.datasource.master.url:}配置
 * @author: scott
 * @date: 2021年02月18日 16:30
 */
@Slf4j
@Configuration
public class CodeGenerateDbConfig {
    @Value("${spring.datasource.dynamic.datasource.master.url:}")
    private String url;
    @Value("${spring.datasource.dynamic.datasource.master.username:}")
    private String username;
    @Value("${spring.datasource.dynamic.datasource.master.password:}")
    private String password;
    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name:}")
    private String driverClassName;
    @Value("${spring.datasource.dynamic.datasource.master.druid.public-key:}")
    private String publicKey;


    @Bean
    public CodeGenerateDbConfig initCodeGenerateDbConfig() {
        if(StringUtils.isNotBlank(url)){
            if(StringUtils.isNotBlank(publicKey)){
                try {
                    password = ConfigTools.decrypt(publicKey, password);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(" 代碼生成器數據庫連接，數據庫密碼解密失敗！");
                }
            }
            CodegenDatasourceConfig.initDbConfig(driverClassName,url, username, password);
            log.info(" 代碼生成器數據庫連接，使用application.yml的DB配置 ###################");
        }
        return null;
    }
}
