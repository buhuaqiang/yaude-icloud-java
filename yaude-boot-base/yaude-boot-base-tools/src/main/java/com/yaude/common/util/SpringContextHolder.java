
package com.yaude.common.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 以靜態變量保存Spring ApplicationContext, 可在任何代碼任何地方任何時候中取出ApplicaitonContext.
 *
 * @author zyf
 */
@Slf4j
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    /**
     * 實現ApplicationContextAware接口的context注入函數, 將其存入靜態變量.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // NOSONAR
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 取得存儲在靜態變量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 從靜態變量ApplicationContext中取得Bean, 自動轉型為所賦值對象的類型.
     */
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 從靜態變量ApplicationContext中取得Bean, 自動轉型為所賦值對象的類型.
     */
    public static <T> T getHandler(String name, Class<T> cls) {
        T t = null;
        if (ObjectUtil.isNotEmpty(name)) {
            checkApplicationContext();
            try {
                t = applicationContext.getBean(name, cls);
            } catch (Exception e) {
                log.error("####################" + name + "未定義");
            }
        }
        return t;
    }


    /**
     * 從靜態變量ApplicationContext中取得Bean, 自動轉型為所賦值對象的類型.
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 清除applicationContext靜態變量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,請在applicationContext.xml中定義SpringContextHolder");
        }
    }

}