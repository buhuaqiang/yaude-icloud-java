package com.yaude.common.system.util;

import com.yaude.common.system.vo.SysPermissionDataRuleModel;
import com.yaude.common.system.vo.SysUserCacheInfo;
import com.yaude.common.util.SpringContextUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: JeecgDataAutorUtils
 * @Description: 數據權限查詢規則容器工具類
 * @Author: 張代浩
 * @Date: 2012-12-15 下午11:27:39
 * 
 */
public class JeecgDataAutorUtils {
	
	public static final String MENU_DATA_AUTHOR_RULES = "MENU_DATA_AUTHOR_RULES";
	
	public static final String MENU_DATA_AUTHOR_RULE_SQL = "MENU_DATA_AUTHOR_RULE_SQL";
	
	public static final String SYS_USER_INFO = "SYS_USER_INFO";

	/**
	 * 往鏈接請求里面，傳入數據查詢條件
	 * 
	 * @param request
	 * @param dataRules
	 */
	public static synchronized void installDataSearchConditon(HttpServletRequest request, List<SysPermissionDataRuleModel> dataRules) {
		@SuppressWarnings("unchecked")
		List<SysPermissionDataRuleModel> list = (List<SysPermissionDataRuleModel>)loadDataSearchConditon();// 1.先從request獲取MENU_DATA_AUTHOR_RULES，如果存則獲取到LIST
		if (list==null) {
			// 2.如果不存在，則new一個list
			list = new ArrayList<SysPermissionDataRuleModel>();
		}
		for (SysPermissionDataRuleModel tsDataRule : dataRules) {
			list.add(tsDataRule);
		}
		request.setAttribute(MENU_DATA_AUTHOR_RULES, list); // 3.往list里面增量存指
	}

	/**
	 * 獲取請求對應的數據權限規則
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static synchronized List<SysPermissionDataRuleModel> loadDataSearchConditon() {
		return (List<SysPermissionDataRuleModel>) SpringContextUtils.getHttpServletRequest().getAttribute(MENU_DATA_AUTHOR_RULES);
				
	}

	/**
	 * 獲取請求對應的數據權限SQL
	 * 
	 * @return
	 */
	public static synchronized String loadDataSearchConditonSQLString() {
		return (String) SpringContextUtils.getHttpServletRequest().getAttribute(MENU_DATA_AUTHOR_RULE_SQL);
	}

	/**
	 * 往鏈接請求里面，傳入數據查詢條件
	 * 
	 * @param request
	 * @param sql
	 */
	public static synchronized void installDataSearchConditon(HttpServletRequest request, String sql) {
		String ruleSql = (String)loadDataSearchConditonSQLString();
		if (!StringUtils.hasText(ruleSql)) {
			request.setAttribute(MENU_DATA_AUTHOR_RULE_SQL,sql);
		}
	}

	/**
	 * 將用戶信息存到request
	 * @param request
	 * @param userinfo
	 */
	public static synchronized void installUserInfo(HttpServletRequest request, SysUserCacheInfo userinfo) {
		request.setAttribute(SYS_USER_INFO, userinfo);
	}

	/**
	 * 將用戶信息存到request
	 * @param userinfo
	 */
	public static synchronized void installUserInfo(SysUserCacheInfo userinfo) {
		SpringContextUtils.getHttpServletRequest().setAttribute(SYS_USER_INFO, userinfo);
	}

	/**
	 * 從request獲取用戶信息
	 * @return
	 */
	public static synchronized SysUserCacheInfo loadUserInfo() {
		return (SysUserCacheInfo) SpringContextUtils.getHttpServletRequest().getAttribute(SYS_USER_INFO);
				
	}
}
