package com.yaude.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring Boot 2.0 解決跨域問題
 *
 * @Author qinfeng
 *
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Value("${jeecg.path.webapp}")
    private String webAppPath;
    @Value("${spring.resource.static-locations}")
    private String staticLocations;

    /**
     * 靜態資源的配置 - 使得可以從磁盤中讀取 Html、圖片、視頻、音頻等
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + upLoadPath + "//", "file:" + webAppPath + "//")
                .addResourceLocations(staticLocations.split(","));
    }

    /**
     * 方案一： 默認訪問根路徑跳轉 doc.html頁面 （swagger文檔頁面）
     * 方案二： 訪問根路徑改成跳轉 index.html頁面 （簡化部署方案： 可以把前端打包直接放到項目的 webapp，上面的配置）
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("doc.html");
    }

    @Bean
    @Conditional(CorsFilterCondition.class)
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        //是否允許請求帶有驗證信息
        corsConfiguration.setAllowCredentials(true);
        // 允許訪問的客戶端域名
        corsConfiguration.addAllowedOrigin("*");
        // 允許服務端訪問的客戶端請求頭
        corsConfiguration.addAllowedHeader("*");
        // 允許訪問的方法名,GET POST等
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    /**
    * 添加Long轉json精度丟失的配置
    * @Return: void
    */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
    }

    

}
