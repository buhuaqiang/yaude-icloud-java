package com.yaude.common.aspect.annotation;

import com.yaude.common.constant.CommonConstant;
import com.yaude.common.constant.enums.ModuleType;

import java.lang.annotation.*;

/**
 * 系統日志注解
 * 
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2019年1月14日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {

	/**
	 * 日志內容
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 日志類型
	 * 
	 * @return 0:操作日志;1:登錄日志;2:定時任務;
	 */
	int logType() default CommonConstant.LOG_TYPE_2;
	
	/**
	 * 操作日志類型
	 * 
	 * @return （1查詢，2添加，3修改，4刪除）
	 */
	int operateType() default 0;

	/**
	 * 模塊類型 默認為common
	 * @return
	 */
	ModuleType module() default ModuleType.COMMON;
}
