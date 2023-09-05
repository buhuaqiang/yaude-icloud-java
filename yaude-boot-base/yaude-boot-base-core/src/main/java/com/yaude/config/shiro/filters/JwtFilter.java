package com.yaude.config.shiro.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.util.oConvertUtils;
import com.yaude.config.mybatis.TenantContext;
import com.yaude.config.shiro.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 鑒權登錄攔截器
 * @Author: Scott
 * @Date: 2018/10/7
 **/
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 默認開啟跨域設置（使用單體）
     * 微服務情況下，此屬性設置為false
     */
    private boolean allowOrigin = true;

    public JwtFilter(){}
    public JwtFilter(boolean allowOrigin){
        this.allowOrigin = allowOrigin;
    }

    /**
     * 執行登錄認證
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            throw new AuthenticationException("Token失效，請重新登錄", e);
        }
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(CommonConstant.X_ACCESS_TOKEN);
        // update-begin--Author:lvdandan Date:20210105 for：JT-355 OA聊天添加token驗證，獲取token參數
        if (oConvertUtils.isEmpty(token)) {
            token = httpServletRequest.getParameter("token");
        }
        // update-end--Author:lvdandan Date:20210105 for：JT-355 OA聊天添加token驗證，獲取token參數

        JwtToken jwtToken = new JwtToken(token);
        // 提交給realm進行登入，如果錯誤他會拋出異常并被捕獲
        getSubject(request, response).login(jwtToken);
        // 如果沒有拋出異常則代表登入成功，返回true
        return true;
    }

    /**
     * 對跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if(allowOrigin){
            httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
            httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
            //update-begin-author:scott date:20200907 for:issues/I1TAAP 前后端分離，shiro過濾器配置引起的跨域問題
            // 是否允許發送Cookie，默認Cookie不包括在CORS請求之中。設為true時，表示服務器允許Cookie包含在請求中。
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            //update-end-author:scott date:20200907 for:issues/I1TAAP 前后端分離，shiro過濾器配置引起的跨域問題
        }
        // 跨域時會首先發送一個option請求，這里我們給option請求直接返回正常狀態
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        //update-begin-author:taoyan date:20200708 for:多租戶用到
        String tenant_id = httpServletRequest.getHeader(CommonConstant.TENANT_ID);
        TenantContext.setTenant(tenant_id);
        //update-end-author:taoyan date:20200708 for:多租戶用到
        return super.preHandle(request, response);
    }
}
