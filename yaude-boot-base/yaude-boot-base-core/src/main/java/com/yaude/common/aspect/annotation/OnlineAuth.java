package com.yaude.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * online請求攔截專用注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface OnlineAuth {

    /**
     * 請求關鍵字，在xxx/code之前的字符串
     * @return
     */
    String value();
}
