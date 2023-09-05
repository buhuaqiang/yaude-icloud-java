package com.yaude.common.util;

import javax.servlet.http.HttpServletRequest;

import com.yaude.common.constant.ServiceNameConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SpringContextUtils implements ApplicationContextAware {

	/**
	 * 上下文對象實例
	 */
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtils.applicationContext = applicationContext;
	}

	/**
	 * 獲取applicationContext
	 *
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	  * 獲取HttpServletRequest
	 */
	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/**
	*  獲取項目根路徑 basePath
	*/
	public static String getDomain(){
		HttpServletRequest request = getHttpServletRequest();
		StringBuffer url = request.getRequestURL();
		//微服務情況下，獲取gateway的basePath
		String basePath = request.getHeader(ServiceNameConstants.X_GATEWAY_BASE_PATH);
		if(oConvertUtils.isNotEmpty(basePath)){
			return basePath;
		}else{
			return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
		}
	}

	public static String getOrigin(){
		HttpServletRequest request = getHttpServletRequest();
		return request.getHeader("Origin");
	}
	
	/**
	 * 通過name獲取 Bean.
	 *
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	/**
	 * 通過class獲取Bean.
	 *
	 * @param clazz
	 * @param       <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	/**
	 * 通過name,以及Clazz返回指定的Bean
	 *
	 * @param name
	 * @param clazz
	 * @param       <T>
	 * @return
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}
}
