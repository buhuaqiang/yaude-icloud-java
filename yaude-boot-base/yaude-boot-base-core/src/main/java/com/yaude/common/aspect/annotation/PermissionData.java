package com.yaude.common.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  *  數據權限注解
 * @Author taoyan
 * @Date 2019年4月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface PermissionData {
	/**
	 * 暫時沒用
	 * @return
	 */
	String value() default "";
	
	
	/**
	 * 配置菜單的組件路徑,用于數據權限
	 */
	String pageComponent() default "";
}