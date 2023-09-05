package com.yaude.common.util;

import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import com.yaude.common.api.CommonAPI;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author scott
 * @Date 2019/9/23 14:12
 * @Description: 編程校驗token有效性
 */
@Slf4j
public class TokenUtils {

    /**
     * 獲取 request 里傳遞的 token
     *
     * @param request
     * @return
     */
    public static String getTokenByRequest(HttpServletRequest request) {
        String token = request.getParameter("token");
        if (token == null) {
            token = request.getHeader("X-Access-Token");
        }
        return token;
    }

    /**
     * 驗證Token
     */
    public static boolean verifyToken(HttpServletRequest request, CommonAPI commonAPI, RedisUtil redisUtil) {
        log.debug(" -- url --" + request.getRequestURL());
        String token = getTokenByRequest(request);

        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("Token不能為空!");
        }

        // 解密獲得username，用于和數據庫進行對比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("Token非法無效!");
        }

        // 查詢用戶信息
        LoginUser user = commonAPI.getUserByName(username);
        if (user == null) {
            throw new AuthenticationException("用戶不存在!");
        }
        // 判斷用戶狀態
        if (user.getStatus() != 1) {
            throw new AuthenticationException("賬號已鎖定,請聯系管理員!");
        }
        // 校驗token是否超時失效 & 或者賬號密碼是否錯誤
        if (!jwtTokenRefresh(token, username, user.getPassword(), redisUtil)) {
            throw new AuthenticationException("Token失效，請重新登錄");
        }
        return true;
    }

    /**
     * 刷新token（保證用戶在線操作不掉線）
     * @param token
     * @param userName
     * @param passWord
     * @param redisUtil
     * @return
     */
    private static boolean jwtTokenRefresh(String token, String userName, String passWord, RedisUtil redisUtil) {
        String cacheToken = String.valueOf(redisUtil.get(CommonConstant.PREFIX_USER_TOKEN + token));
        if (oConvertUtils.isNotEmpty(cacheToken)) {
            // 校驗token有效性
            if (!JwtUtil.verify(cacheToken, userName, passWord)) {
                String newAuthorization = JwtUtil.sign(userName, passWord);
                // 設置Toekn緩存有效時間
                redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, newAuthorization);
                redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
            }
            //update-begin--Author:scott  Date:20191005  for：解決每次請求，都重寫redis中 token緩存問題
//            else {
//                redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, cacheToken);
//                // 設置超時時間
//                redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);
//            }
            //update-end--Author:scott  Date:20191005  for：解決每次請求，都重寫redis中 token緩存問題
            return true;
        }
        return false;
    }

    /**
     * 驗證Token
     */
    public static boolean verifyToken(String token, CommonAPI commonAPI, RedisUtil redisUtil) {
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("token不能為空!");
        }

        // 解密獲得username，用于和數據庫進行對比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token非法無效!");
        }

        // 查詢用戶信息
        LoginUser user = commonAPI.getUserByName(username);
        if (user == null) {
            throw new AuthenticationException("用戶不存在!");
        }
        // 判斷用戶狀態
        if (user.getStatus() != 1) {
            throw new AuthenticationException("賬號已被鎖定,請聯系管理員!");
        }
        // 校驗token是否超時失效 & 或者賬號密碼是否錯誤
        if (!jwtTokenRefresh(token, username, user.getPassword(), redisUtil)) {
            throw new AuthenticationException("Token失效，請重新登錄!");
        }
        return true;
    }

}
