package com.yaude.config.sign.interceptor;


import java.io.PrintWriter;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yaude.common.api.vo.Result;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.util.DateUtils;
import com.yaude.config.sign.util.BodyReaderHttpServletRequestWrapper;
import com.yaude.config.sign.util.HttpUtils;
import com.yaude.config.sign.util.SignUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 簽名攔截器
 * @author qinfeng
 */
@Slf4j
public class SignAuthInterceptor implements HandlerInterceptor {
    /**
     * 5分鐘有效期
     */
    private final static long MAX_EXPIRE = 5 * 60;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("request URI = " + request.getRequestURI());
        HttpServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        //獲取全部參數(包括URL和body上的)
        SortedMap<String, String> allParams = HttpUtils.getAllParams(requestWrapper);
        //對參數進行簽名驗證
        String headerSign = request.getHeader(CommonConstant.X_SIGN);
        String timesTamp = request.getHeader(CommonConstant.X_TIMESTAMP);

        //1.校驗時間有消息
        try {
            DateUtils.parseDate(timesTamp, "yyyyMMddHHmmss");
        } catch (Exception e) {
            throw new IllegalArgumentException("簽名驗證失敗:X-TIMESTAMP格式必須為:yyyyMMddHHmmss");
        }
        Long clientTimestamp = Long.parseLong(timesTamp);
        //判斷時間戳 timestamp=201808091113
        if ((DateUtils.getCurrentTimestamp() - clientTimestamp) > MAX_EXPIRE) {
            throw new IllegalArgumentException("簽名驗證失敗:X-TIMESTAMP已過期");
        }

        //2.校驗簽名
        boolean isSigned = SignUtil.verifySign(allParams,headerSign);

        if (isSigned) {
            log.debug("Sign 簽名通過！Header Sign : {}",headerSign);
            return true;
        } else {
            log.error("request URI = " + request.getRequestURI());
            log.error("Sign 簽名校驗失敗！Header Sign : {}",headerSign);
            //校驗失敗返回前端
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            Result<?> result = Result.error("Sign簽名校驗失敗！");
            out.print(JSON.toJSON(result));
            return false;
        }
    }

}
