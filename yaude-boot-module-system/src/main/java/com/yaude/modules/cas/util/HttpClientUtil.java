package com.yaude.modules.cas.util;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName HttpClientUtil.java
 * @Description TODO
 * @createTime 2021年10月29日 18:04:00
 */
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
/**
 * 利用HttpClient進行post請求的工具類
 * @ClassName: HttpClientUtil
 * @Description: TODO
 * @author Devin <xxx>
 * @date 2017年2月7日 下午1:43:38
 *
 */
public class HttpClientUtil {
    @SuppressWarnings("resource")
    public static String doGet(String url,String jsonstr,String charset){
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}