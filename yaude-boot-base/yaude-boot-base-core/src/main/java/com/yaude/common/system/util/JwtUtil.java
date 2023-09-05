package com.yaude.common.system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Joiner;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yaude.common.constant.CommonConstant;
import com.yaude.common.constant.DataBaseConstant;
import com.yaude.common.exception.JeecgBootException;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.system.vo.SysUserCacheInfo;
import com.yaude.common.util.DateUtils;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;

/**
 * @Author Scott
 * @Date 2018-07-12 14:23
 * @Desc JWT工具類
 **/
public class JwtUtil {

	// Token過期時間30分鐘（用戶登錄過期時間是此時間的兩倍，以token在reids緩存時間為準）
	public static final long EXPIRE_TIME = 30 * 60 * 1000;

	/**
	 * 校驗token是否正確
	 *
	 * @param token  密鑰
	 * @param secret 用戶的密碼
	 * @return 是否正確
	 */
	public static boolean verify(String token, String username, String secret) {
		try {
			// 根據密碼生成JWT效驗器
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();
			// 效驗TOKEN
			DecodedJWT jwt = verifier.verify(token);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * 獲得token中的信息無需secret解密也能獲得
	 *
	 * @return token中包含的用戶名
	 */
	public static String getUsername(String token) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getClaim("username").asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	/**
	 * 生成簽名,5min后過期
	 *
	 * @param username 用戶名
	 * @param secret   用戶的密碼
	 * @return 加密的token
	 */
	public static String sign(String username, String secret) {
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
		Algorithm algorithm = Algorithm.HMAC256(secret);
		// 附帶username信息
		return JWT.create().withClaim("username", username).withExpiresAt(date).sign(algorithm);

	}

	/**
	 * 根據request中的token獲取用戶賬號
	 * 
	 * @param request
	 * @return
	 * @throws JeecgBootException
	 */
	public static String getUserNameByToken(HttpServletRequest request) throws JeecgBootException {
		String accessToken = request.getHeader("X-Access-Token");
		String username = getUsername(accessToken);
		if (oConvertUtils.isEmpty(username)) {
			throw new JeecgBootException("未獲取到用戶");
		}
		return username;
	}
	
	/**
	  *  從session中獲取變量
	 * @param key
	 * @return
	 */
	public static String getSessionData(String key) {
		//${myVar}%
		//得到${} 后面的值
		String moshi = "";
		if(key.indexOf("}")!=-1){
			 moshi = key.substring(key.indexOf("}")+1);
		}
		String returnValue = null;
		if (key.contains("#{")) {
			key = key.substring(2,key.indexOf("}"));
		}
		if (oConvertUtils.isNotEmpty(key)) {
			HttpSession session = SpringContextUtils.getHttpServletRequest().getSession();
			returnValue = (String) session.getAttribute(key);
		}
		//結果加上${} 后面的值
		if(returnValue!=null){returnValue = returnValue + moshi;}
		return returnValue;
	}
	
	/**
	  * 從當前用戶中獲取變量
	 * @param key
	 * @param user
	 * @return
	 */
	//TODO 急待改造 sckjkdsjsfjdk
	public static String getUserSystemData(String key,SysUserCacheInfo user) {
		if(user==null) {
			user = JeecgDataAutorUtils.loadUserInfo();
		}
		//#{sys_user_code}%
		
		// 獲取登錄用戶信息
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		
		String moshi = "";
		if(key.indexOf("}")!=-1){
			 moshi = key.substring(key.indexOf("}")+1);
		}
		String returnValue = null;
		//針對特殊標示處理#{sysOrgCode}，判斷替換
		if (key.contains("#{")) {
			key = key.substring(2,key.indexOf("}"));
		} else {
			key = key;
		}
		//替換為系統登錄用戶帳號
		if (key.equals(DataBaseConstant.SYS_USER_CODE)|| key.toLowerCase().equals(DataBaseConstant.SYS_USER_CODE_TABLE)) {
			if(user==null) {
				returnValue = sysUser.getUsername();
			}else {
				returnValue = user.getSysUserCode();
			}
		}
		//替換為系統登錄用戶真實名字
		else if (key.equals(DataBaseConstant.SYS_USER_NAME)|| key.toLowerCase().equals(DataBaseConstant.SYS_USER_NAME_TABLE)) {
			if(user==null) {
				returnValue = sysUser.getRealname();
			}else {
				returnValue = user.getSysUserName();
			}
		}
		
		//替換為系統用戶登錄所使用的機構編碼
		else if (key.equals(DataBaseConstant.SYS_ORG_CODE)|| key.toLowerCase().equals(DataBaseConstant.SYS_ORG_CODE_TABLE)) {
			if(user==null) {
				returnValue = sysUser.getOrgCode();
			}else {
				returnValue = user.getSysOrgCode();
			}
		}
		//替換為系統用戶所擁有的所有機構編碼
		else if (key.equals(DataBaseConstant.SYS_MULTI_ORG_CODE)|| key.toLowerCase().equals(DataBaseConstant.SYS_MULTI_ORG_CODE_TABLE)) {
			if(user==null){
				//TODO 暫時使用用戶登錄部門，存在邏輯缺陷，不是用戶所擁有的部門
				returnValue = sysUser.getOrgCode();
			}else{
				if(user.isOneDepart()) {
					returnValue = user.getSysMultiOrgCode().get(0);
				}else {
					returnValue = Joiner.on(",").join(user.getSysMultiOrgCode());
				}
			}
		}
		//替換為當前系統時間(年月日)
		else if (key.equals(DataBaseConstant.SYS_DATE)|| key.toLowerCase().equals(DataBaseConstant.SYS_DATE_TABLE)) {
			returnValue = DateUtils.formatDate();
		}
		//替換為當前系統時間（年月日時分秒）
		else if (key.equals(DataBaseConstant.SYS_TIME)|| key.toLowerCase().equals(DataBaseConstant.SYS_TIME_TABLE)) {
			returnValue = DateUtils.now();
		}
		//流程狀態默認值（默認未發起）
		else if (key.equals(DataBaseConstant.BPM_STATUS)|| key.toLowerCase().equals(DataBaseConstant.BPM_STATUS_TABLE)) {
			returnValue = "1";
		}
		//update-begin-author:taoyan date:20210330 for:多租戶ID作為系統變量
		else if (key.equals(DataBaseConstant.TENANT_ID) || key.toLowerCase().equals(DataBaseConstant.TENANT_ID_TABLE)){
			returnValue = sysUser.getRelTenantIds();
			if(oConvertUtils.isEmpty(returnValue) || (returnValue!=null && returnValue.indexOf(",")>0)){
				returnValue = SpringContextUtils.getHttpServletRequest().getHeader(CommonConstant.TENANT_ID);
			}
		}
		//update-end-author:taoyan date:20210330 for:多租戶ID作為系統變量
		if(returnValue!=null){returnValue = returnValue + moshi;}
		return returnValue;
	}
	
//	public static void main(String[] args) {
//		 String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NjUzMzY1MTMsInVzZXJuYW1lIjoiYWRtaW4ifQ.xjhud_tWCNYBOg_aRlMgOdlZoWFFKB_givNElHNw3X0";
//		 System.out.println(JwtUtil.getUsername(token));
//	}
}
