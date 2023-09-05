package com.yaude.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import com.yaude.common.constant.CommonConstant;
import com.yaude.common.util.DateUtils;
import com.yaude.common.util.PathMatcherUtil;
import com.yaude.config.sign.interceptor.SignAuthConfiguration;
import com.yaude.config.sign.util.HttpUtils;
import com.yaude.config.sign.util.SignUtil;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.springfox.SwaggerJsonSerializer;

import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (null != attributes) {
                HttpServletRequest request = attributes.getRequest();
                log.debug("Feign request: {}", request.getRequestURI());
                // 將token信息放入header中
                String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
                if(token==null || "".equals(token)){
                    token = request.getParameter("token");
                }
                log.debug("Feign request token: {}", token);
                requestTemplate.header(CommonConstant.X_ACCESS_TOKEN, token);

                //根據URL地址過濾請求 【字典表參數簽名驗證】
                if (PathMatcherUtil.matches(Arrays.asList(SignAuthConfiguration.urlList),requestTemplate.path())) {
                    try {
                        log.info("============================ [begin] fegin api url ============================");
                        log.info(requestTemplate.path());
                        log.info(requestTemplate.method());
                        String queryLine = requestTemplate.queryLine();
                        if(queryLine!=null && queryLine.startsWith("?")){
                            queryLine = queryLine.substring(1);
                        }
                        log.info(queryLine);
                        if(requestTemplate.body()!=null){
                            log.info(new String(requestTemplate.body()));
                        }
                        SortedMap<String, String> allParams = HttpUtils.getAllParams(requestTemplate.path(),queryLine,requestTemplate.body(),requestTemplate.method());
                        String sign = SignUtil.getParamsSign(allParams);
                        log.info(" Feign request params sign: {}",sign);
                        log.info("============================ [end] fegin api url ============================");
                        requestTemplate.header(CommonConstant.X_SIGN, sign);
                        requestTemplate.header(CommonConstant.X_TIMESTAMP, DateUtils.getCurrentTimestamp().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }



    /**
     * Feign 客戶端的日志記錄，默認級別為NONE
     * Logger.Level 的具體級別如下：
     * NONE：不記錄任何信息
     * BASIC：僅記錄請求方法、URL以及響應狀態碼和執行時間
     * HEADERS：除了記錄 BASIC級別的信息外，還會記錄請求和響應的頭信息
     * FULL：記錄所有請求與響應的明細，包括頭信息、請求體、元數據
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Feign支持文件上傳
     * @param messageConverters
     * @return
     */
    @Bean
    @Primary
    @Scope("prototype")
    public Encoder multipartFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    // update-begin--Author:sunjianlei Date:20210604 for： 給 Feign 添加 FastJson 的解析支持 ----------
    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(feignHttpMessageConverter());
    }

    /**
     * 設置解碼器為fastjson
     *
     * @return
     */
    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(this.getFastJsonConverter());
        return () -> httpMessageConverters;
    }

    private FastJsonHttpMessageConverter getFastJsonConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE);
        supportedMediaTypes.add(mediaTypeJson);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        FastJsonConfig config = new FastJsonConfig();
        config.getSerializeConfig().put(JSON.class, new SwaggerJsonSerializer());
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);

        return converter;
    }
    // update-end--Author:sunjianlei Date:20210604 for： 給 Feign 添加 FastJson 的解析支持 ----------

}
