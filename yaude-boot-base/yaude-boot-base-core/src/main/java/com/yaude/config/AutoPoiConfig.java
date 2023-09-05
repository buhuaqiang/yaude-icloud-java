package com.yaude.config;

import org.jeecgframework.core.util.ApplicationContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Scott
 * @Date: 2018/2/7
 * @description: autopoi 配置類
 */

@Configuration
public class AutoPoiConfig {

    /**
     * excel注解字典參數支持(導入導出字典值，自動翻譯)
     * 舉例： @Excel(name = "性別", width = 15, dicCode = "sex")
     * 1、導出的時候會根據字典配置，把值1,2翻譯成：男、女;
     * 2、導入的時候，會把男、女翻譯成1,2存進數據庫;
     * @return
     */
    @Bean
    public ApplicationContextUtil applicationContextUtil() {
        return new org.jeecgframework.core.util.ApplicationContextUtil();
    }

}
