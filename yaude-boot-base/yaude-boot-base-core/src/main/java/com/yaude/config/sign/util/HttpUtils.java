package com.yaude.config.sign.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import com.yaude.common.util.oConvertUtils;
import org.springframework.http.HttpMethod;

import com.alibaba.fastjson.JSONObject;

/**
 * http 工具類 獲取請求中的參數
 *
 * @author jeecg
 * @date 20210621
 */
@Slf4j
public class HttpUtils {

    /**
     * 將URL的參數和body參數合并
     *
     * @author jeecg
     * @date 20210621
     * @param request
     */
    public static SortedMap<String, String> getAllParams(HttpServletRequest request) throws IOException {

        SortedMap<String, String> result = new TreeMap<>();
        // 獲取URL上最后帶逗號的參數變量 sys/dict/getDictItems/sys_user,realname,username
        String pathVariable = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
        if (pathVariable.contains(",")) {
            log.info(" pathVariable: {}",pathVariable);
            String deString = URLDecoder.decode(pathVariable, "UTF-8");
            log.info(" pathVariable decode: {}",deString);
            result.put(SignUtil.xPathVariable, deString);
        }
        // 獲取URL上的參數
        Map<String, String> urlParams = getUrlParams(request);
        for (Map.Entry entry : urlParams.entrySet()) {
            result.put((String)entry.getKey(), (String)entry.getValue());
        }
        Map<String, String> allRequestParam = new HashMap<>(16);
        // get請求不需要拿body參數
        if (!HttpMethod.GET.name().equals(request.getMethod())) {
            allRequestParam = getAllRequestParam(request);
        }
        // 將URL的參數和body參數進行合并
        if (allRequestParam != null) {
            for (Map.Entry entry : allRequestParam.entrySet()) {
                result.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
        return result;
    }

    /**
     * 將URL的參數和body參數合并
     *
     * @author jeecg
     * @date 20210621
     * @param queryString
     */
    public static SortedMap<String, String> getAllParams(String url, String queryString, byte[] body, String method)
        throws IOException {

        SortedMap<String, String> result = new TreeMap<>();
        // 獲取URL上最后帶逗號的參數變量 sys/dict/getDictItems/sys_user,realname,username
        String pathVariable = url.substring(url.lastIndexOf("/") + 1);
        if (pathVariable.contains(",")) {
            log.info(" pathVariable: {}",pathVariable);
            String deString = URLDecoder.decode(pathVariable, "UTF-8");
            log.info(" pathVariable decode: {}",deString);
            result.put(SignUtil.xPathVariable, deString);
        }
        // 獲取URL上的參數
        Map<String, String> urlParams = getUrlParams(queryString);
        for (Map.Entry entry : urlParams.entrySet()) {
            result.put((String)entry.getKey(), (String)entry.getValue());
        }
        Map<String, String> allRequestParam = new HashMap<>(16);
        // get請求不需要拿body參數
        if (!HttpMethod.GET.name().equals(method)) {
            allRequestParam = getAllRequestParam(body);
        }
        // 將URL的參數和body參數進行合并
        if (allRequestParam != null) {
            for (Map.Entry entry : allRequestParam.entrySet()) {
                result.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
        return result;
    }

    /**
     * 獲取 Body 參數
     *
     * @date 15:04 20210621
     * @param request
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String str = "";
        StringBuilder wholeStr = new StringBuilder();
        // 一行一行的讀取body體里面的內容；
        while ((str = reader.readLine()) != null) {
            wholeStr.append(str);
        }
        // 轉化成json對象
        return JSONObject.parseObject(wholeStr.toString(), Map.class);
    }

    /**
     * 獲取 Body 參數
     *
     * @date 15:04 20210621
     * @param body
     */
    public static Map<String, String> getAllRequestParam(final byte[] body) throws IOException {
        if(body==null){
            return null;
        }
        String wholeStr = new String(body);
        // 轉化成json對象
        return JSONObject.parseObject(wholeStr.toString(), Map.class);
    }

    /**
     * 將URL請求參數轉換成Map
     *
     * @param request
     */
    public static Map<String, String> getUrlParams(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>(16);
        if (oConvertUtils.isEmpty(request.getQueryString())) {
            return result;
        }
        String param = "";
        try {
            param = URLDecoder.decode(request.getQueryString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] params = param.split("&");
        for (String s : params) {
            int index = s.indexOf("=");
            result.put(s.substring(0, index), s.substring(index + 1));
        }
        return result;
    }

    /**
     * 將URL請求參數轉換成Map
     * 
     * @param queryString
     */
    public static Map<String, String> getUrlParams(String queryString) {
        Map<String, String> result = new HashMap<>(16);
        if (oConvertUtils.isEmpty(queryString)) {
            return result;
        }
        String param = "";
        try {
            param = URLDecoder.decode(queryString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] params = param.split("&");
        for (String s : params) {
            int index = s.indexOf("=");
            result.put(s.substring(0, index), s.substring(index + 1));
        }
        return result;
    }
}