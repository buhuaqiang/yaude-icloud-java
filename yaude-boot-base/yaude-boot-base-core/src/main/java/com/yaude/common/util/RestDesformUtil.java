package com.yaude.common.util;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.api.vo.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 通過 RESTful 風格的接口操縱 desform 里的數據
 *
 * @author sunjianlei
 */
public class RestDesformUtil {

    private static String domain = null;
    private static String path = null;

    static {
        domain = SpringContextUtils.getDomain();
        path = oConvertUtils.getString(SpringContextUtils.getApplicationContext().getEnvironment().getProperty("server.servlet.context-path"));
    }

    /**
     * 查詢數據
     *
     * @param desformCode
     * @param dataId
     * @param token
     * @return
     */
    public static Result queryOne(String desformCode, String dataId, String token) {
        String url = getBaseUrl(desformCode, dataId).toString();
        HttpHeaders headers = getHeaders(token);
        ResponseEntity<JSONObject> result = RestUtil.request(url, HttpMethod.GET, headers, null, null, JSONObject.class);
        return packageReturn(result);
    }

    /**
     * 新增數據
     *
     * @param desformCode
     * @param formData
     * @param token
     * @return
     */
    public static Result addOne(String desformCode, JSONObject formData, String token) {
        return addOrEditOne(desformCode, formData, token, HttpMethod.POST);
    }

    /**
     * 修改數據
     *
     * @param desformCode
     * @param formData
     * @param token
     * @return
     */
    public static Result editOne(String desformCode, JSONObject formData, String token) {
        return addOrEditOne(desformCode, formData, token, HttpMethod.PUT);
    }

    private static Result addOrEditOne(String desformCode, JSONObject formData, String token, HttpMethod method) {
        String url = getBaseUrl(desformCode).toString();
        HttpHeaders headers = getHeaders(token);
        ResponseEntity<JSONObject> result = RestUtil.request(url, method, headers, null, formData, JSONObject.class);
        return packageReturn(result);
    }

    /**
     * 刪除數據
     *
     * @param desformCode
     * @param dataId
     * @param token
     * @return
     */
    public static Result removeOne(String desformCode, String dataId, String token) {
        String url = getBaseUrl(desformCode, dataId).toString();
        HttpHeaders headers = getHeaders(token);
        ResponseEntity<JSONObject> result = RestUtil.request(url, HttpMethod.DELETE, headers, null, null, JSONObject.class);
        return packageReturn(result);
    }

    private static Result packageReturn(ResponseEntity<JSONObject> result) {
        if (result.getBody() != null) {
            return result.getBody().toJavaObject(Result.class);
        }
        return Result.error("操作失敗");
    }

    private static StringBuilder getBaseUrl() {
        StringBuilder builder = new StringBuilder(domain).append(path);
        builder.append("/desform/api");
        return builder;
    }

    private static StringBuilder getBaseUrl(String desformCode, String dataId) {
        StringBuilder builder = getBaseUrl();
        builder.append("/").append(desformCode);
        if (dataId != null) {
            builder.append("/").append(dataId);
        }
        return builder;
    }

    private static StringBuilder getBaseUrl(String desformCode) {
        return getBaseUrl(desformCode, null);
    }

    private static HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;
        headers.setContentType(MediaType.parseMediaType(mediaType));
        headers.set("Accept", mediaType);
        headers.set("X-Access-Token", token);
        return headers;
    }

}