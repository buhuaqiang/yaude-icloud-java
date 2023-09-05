package com.yaude.common.util;

import cn.hutool.crypto.SecureUtil;
import com.yaude.common.exception.JeecgBootException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * sql注入處理工具類
 * 
 * @author zhoujf
 */
@Slf4j
public class SqlInjectionUtil {
	/**
	 * sign 用于表字典加簽的鹽值【SQL漏洞】
	 * （上線修改值 20200501，同步修改前端的鹽值）
	 */
	private final static String TABLE_DICT_SIGN_SALT = "20200501";
	private final static String xssStr = "'|and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+";

	/*
	* 針對表字典進行額外的sign簽名校驗（增加安全機制）
	* @param dictCode:
	* @param sign:
	* @param request:
	* @Return: void
	*/
	public static void checkDictTableSign(String dictCode, String sign, HttpServletRequest request) {
		//表字典SQL注入漏洞,簽名校驗
		String accessToken = request.getHeader("X-Access-Token");
		String signStr = dictCode + SqlInjectionUtil.TABLE_DICT_SIGN_SALT + accessToken;
		String javaSign = SecureUtil.md5(signStr);
		if (!javaSign.equals(sign)) {
			log.error("表字典，SQL注入漏洞簽名校驗失敗 ：" + sign + "!=" + javaSign+ ",dictCode=" + dictCode);
			throw new JeecgBootException("無權限訪問！");
		}
		log.info(" 表字典，SQL注入漏洞簽名校驗成功！sign=" + sign + ",dictCode=" + dictCode);
	}


	/**
	 * sql注入過濾處理，遇到注入關鍵字拋異常
	 * 
	 * @param value
	 * @return
	 */
	public static void filterContent(String value) {
		if (value == null || "".equals(value)) {
			return;
		}
		// 統一轉為小寫
		value = value.toLowerCase();
		String[] xssArr = xssStr.split("\\|");
		for (int i = 0; i < xssArr.length; i++) {
			if (value.indexOf(xssArr[i]) > -1) {
				log.error("請注意，存在SQL注入關鍵詞---> {}", xssArr[i]);
				log.error("請注意，值可能存在SQL注入風險!---> {}", value);
				throw new RuntimeException("請注意，值可能存在SQL注入風險!--->" + value);
			}
		}
		return;
	}

	/**
	 * sql注入過濾處理，遇到注入關鍵字拋異常
	 * 
	 * @param values
	 * @return
	 */
	public static void filterContent(String[] values) {
		String[] xssArr = xssStr.split("\\|");
		for (String value : values) {
			if (value == null || "".equals(value)) {
				return;
			}
			// 統一轉為小寫
			value = value.toLowerCase();
			for (int i = 0; i < xssArr.length; i++) {
				if (value.indexOf(xssArr[i]) > -1) {
					log.error("請注意，存在SQL注入關鍵詞---> {}", xssArr[i]);
					log.error("請注意，值可能存在SQL注入風險!---> {}", value);
					throw new RuntimeException("請注意，值可能存在SQL注入風險!--->" + value);
				}
			}
		}
		return;
	}

	/**
	 * @特殊方法(不通用) 僅用于字典條件SQL參數，注入過濾
	 * @param value
	 * @return
	 */
	@Deprecated
	public static void specialFilterContent(String value) {
		String specialXssStr = " exec | insert | select | delete | update | drop | count | chr | mid | master | truncate | char | declare |;|+|";
		String[] xssArr = specialXssStr.split("\\|");
		if (value == null || "".equals(value)) {
			return;
		}
		// 統一轉為小寫
		value = value.toLowerCase();
		for (int i = 0; i < xssArr.length; i++) {
			if (value.indexOf(xssArr[i]) > -1 || value.startsWith(xssArr[i].trim())) {
				log.error("請注意，存在SQL注入關鍵詞---> {}", xssArr[i]);
				log.error("請注意，值可能存在SQL注入風險!---> {}", value);
				throw new RuntimeException("請注意，值可能存在SQL注入風險!--->" + value);
			}
		}
		return;
	}


    /**
     * @特殊方法(不通用) 僅用于Online報表SQL解析，注入過濾
     * @param value
     * @return
     */
	@Deprecated
	public static void specialFilterContentForOnlineReport(String value) {
		String specialXssStr = " exec | insert | delete | update | drop | chr | mid | master | truncate | char | declare |";
		String[] xssArr = specialXssStr.split("\\|");
		if (value == null || "".equals(value)) {
			return;
		}
		// 統一轉為小寫
		value = value.toLowerCase();
		for (int i = 0; i < xssArr.length; i++) {
			if (value.indexOf(xssArr[i]) > -1 || value.startsWith(xssArr[i].trim())) {
				log.error("請注意，存在SQL注入關鍵詞---> {}", xssArr[i]);
				log.error("請注意，值可能存在SQL注入風險!---> {}", value);
				throw new RuntimeException("請注意，值可能存在SQL注入風險!--->" + value);
			}
		}
		return;
	}

}
