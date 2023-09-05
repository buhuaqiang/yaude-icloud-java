package com.yaude.config.shiro.filters;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Scott
 * @create 2019-02-01 15:56
 * @desc 鑒權請求URL訪問權限攔截器
 */
@Slf4j
public class ResourceCheckFilter extends AccessControlFilter {

    private String errorUrl;

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    /**
     * 表示是否允許訪問 ，如果允許訪問返回true，否則false；
     *
     * @param servletRequest
     * @param servletResponse
     * @param o               表示寫在攔截器中括號里面的字符串 mappedValue 就是 [urls] 配置中攔截器參數部分
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String url = getPathWithinApplication(servletRequest);
        log.info("當前用戶正在訪問的 url => " + url);
        return subject.isPermitted(url);
    }

    /**
     * onAccessDenied：表示當訪問拒絕時是否已經處理了； 如果返回 true 表示需要繼續處理； 如果返回 false
     * 表示該攔截器實例已經處理了，將直接返回即可。
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        log.info("當 isAccessAllowed 返回 false 的時候，才會執行 method onAccessDenied ");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.sendRedirect(request.getContextPath() + this.errorUrl);

        // 返回 false 表示已經處理，例如頁面跳轉啥的，表示不在走以下的攔截器了（如果還有配置的話）
        return false;
    }

}