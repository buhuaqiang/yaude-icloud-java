package com.yaude.config;

import com.yaude.common.constant.CommonConstant;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 微服務環境加載條件
 */
public class JeecgCloudCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Object object = context.getEnvironment().getProperty(CommonConstant.CLOUD_SERVER_KEY);
        //如果沒有服務注冊發現的配置 說明是單體應用
        if(object==null){
            return false;
        }
        return true;
    }
}
