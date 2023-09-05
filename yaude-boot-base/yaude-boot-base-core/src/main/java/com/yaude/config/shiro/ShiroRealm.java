package com.yaude.config.shiro;

import com.yaude.common.util.RedisUtil;
import com.yaude.config.mybatis.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Description: 用戶登錄鑒權和獲取用戶授權
 * @Author: Scott
 * @Date: 2019-4-23 8:13
 * @Version: 1.1
 */
@Component
@Slf4j
public class ShiroRealm extends AuthorizingRealm {
	@Lazy
    @Resource
    private CommonAPI commonAPI;

    @Lazy
    @Resource
    private RedisUtil redisUtil;

    /**
     * 必須重寫此方法，不然Shiro會報錯
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 權限信息認證(包括角色以及權限)是用戶訪問controller的時候才進行驗證(redis存儲的此處權限信息)
     * 觸發檢測用戶權限時才會調用此方法，例如checkRole,checkPermission
     *
     * @param principals 身份信息
     * @return AuthorizationInfo 權限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("===============Shiro權限認證開始============ [ roles、permissions]==========");
        String username = null;
        if (principals != null) {
            LoginUser sysUser = (LoginUser) principals.getPrimaryPrincipal();
            username = sysUser.getUsername();
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 設置用戶擁有的角色集合，比如“admin,test”
        Set<String> roleSet = commonAPI.queryUserRoles(username);
        System.out.println(roleSet.toString());
        info.setRoles(roleSet);

        // 設置用戶擁有的權限集合，比如“sys:role:add,sys:user:add”
        Set<String> permissionSet = commonAPI.queryUserAuths(username);
        info.addStringPermissions(permissionSet);
        System.out.println(permissionSet);
        log.info("===============Shiro權限認證成功==============");
        return info;
    }

    /**
     * 用戶信息認證是在用戶進行登錄的時候進行驗證(不存redis)
     * 也就是說驗證用戶輸入的賬號和密碼是否正確，錯誤拋出異常
     *
     * @param auth 用戶登錄的賬號密碼信息
     * @return 返回封裝了用戶信息的 AuthenticationInfo 實例
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.debug("===============Shiro身份認證開始============doGetAuthenticationInfo==========");
        String token = (String) auth.getCredentials();
        if (token == null) {
            log.info("————————身份認證失敗——————————IP地址:  "+ oConvertUtils.getIpAddrByRequest(SpringContextUtils.getHttpServletRequest()));
            throw new AuthenticationException("token為空!");
        }
        // 校驗token有效性
        LoginUser loginUser = this.checkUserTokenIsEffect(token);
        return new SimpleAuthenticationInfo(loginUser, token, getName());
    }

    /**
     * 校驗token的有效性
     *
     * @param token
     */
    public LoginUser checkUserTokenIsEffect(String token) throws AuthenticationException {
        // 解密獲得username，用于和數據庫進行對比
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token非法無效!");
        }

        // 查詢用戶信息
        log.debug("———校驗token是否有效————checkUserTokenIsEffect——————— "+ token);
        LoginUser loginUser = commonAPI.getUserByName(username);
        if (loginUser == null) {
            throw new AuthenticationException("用戶不存在!");
        }
        // 判斷用戶狀態
        if (loginUser.getStatus() != 1) {
            throw new AuthenticationException("賬號已被鎖定,請聯系管理員!");
        }
        // 校驗token是否超時失效 & 或者賬號密碼是否錯誤
        if (!jwtTokenRefresh(token, username, loginUser.getPassword())) {
            throw new AuthenticationException("Token失效，請重新登錄!");
        }
        //update-begin-author:taoyan date:20210609 for:校驗用戶的tenant_id和前端傳過來的是否一致
        String userTenantIds = loginUser.getRelTenantIds();
        if(oConvertUtils.isNotEmpty(userTenantIds)){
            String contextTenantId = TenantContext.getTenant();
            if(oConvertUtils.isNotEmpty(contextTenantId) && !"0".equals(contextTenantId)){
                if(String.join(",",userTenantIds).indexOf(contextTenantId)<0){
                    throw new AuthenticationException("用戶租戶信息變更,請重新登陸!");
                }
            }
        }
        //update-end-author:taoyan date:20210609 for:校驗用戶的tenant_id和前端傳過來的是否一致
        return loginUser;
    }

    /**
     * JWTToken刷新生命周期 （實現： 用戶在線操作不掉線功能）
     * 1、登錄成功后將用戶的JWT生成的Token作為k、v存儲到cache緩存里面(這時候k、v值一樣)，緩存有效期設置為Jwt有效時間的2倍
     * 2、當該用戶再次請求時，通過JWTFilter層層校驗之后會進入到doGetAuthenticationInfo進行身份驗證
     * 3、當該用戶這次請求jwt生成的token值已經超時，但該token對應cache中的k還是存在，則表示該用戶一直在操作只是JWT的token失效了，程序會給token對應的k映射的v值重新生成JWTToken并覆蓋v值，該緩存生命周期重新計算
     * 4、當該用戶這次請求jwt在生成的token值已經超時，并在cache中不存在對應的k，則表示該用戶賬戶空閑超時，返回用戶信息已失效，請重新登錄。
     * 注意： 前端請求Header中設置Authorization保持不變，校驗有效性以緩存中的token為準。
     *       用戶過期時間 = Jwt有效時間 * 2。
     *
     * @param userName
     * @param passWord
     * @return
     */
    public boolean jwtTokenRefresh(String token, String userName, String passWord) {
        String cacheToken = String.valueOf(redisUtil.get(CommonConstant.PREFIX_USER_TOKEN + token));
        if (oConvertUtils.isNotEmpty(cacheToken)) {
            // 校驗token有效性
            if (!JwtUtil.verify(cacheToken, userName, passWord)) {
                String newAuthorization = JwtUtil.sign(userName, passWord);
                // 設置超時時間
                redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, newAuthorization);
                redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME *2 / 1000);
                log.debug("——————————用戶在線操作，更新token保證不掉線—————————jwtTokenRefresh——————— "+ token);
            }
            //update-begin--Author:scott  Date:20191005  for：解決每次請求，都重寫redis中 token緩存問題
//			else {
//				// 設置超時時間
//				redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, cacheToken);
//				redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);
//			}
            //update-end--Author:scott  Date:20191005   for：解決每次請求，都重寫redis中 token緩存問題
            return true;
        }
        return false;
    }

    /**
     * 清除當前用戶的權限認證緩存
     *
     * @param principals 權限信息
     */
    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
