package com.yaude.modules.cas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

public class CASServiceUtil {
	
	public static void main(String[] args) {
		String serviceUrl = "https://cas.8f8.com.cn:8443/cas/p3/serviceValidate";
		String service = "http://localhost:3003/user/login";
		String ticket = "ST-5-1g-9cNES6KXNRwq-GuRET103sm0-DESKTOP-VKLS8B3";
		String res = getSTValidate(serviceUrl,ticket, service);
		
		System.out.println("---------res-----"+res);
	}
	
	
	/**
     * 驗證ST
     */
    public static String getSTValidate(String url,String st, String service){
		try {
			url = url+"?service="+service+"&ticket="+st;
			/*CloseableHttpClient httpclient = createHttpClientWithNoSsl();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			String res = readResponse(response);*/


	        String res = HttpClientUtil.doGet(url, null, "utf-8");
	        return res == null ? null : (res == "" ? null : res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

    
    /**
     * 讀取 response body 內容為字符串
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static String readResponse(HttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String result = new String();
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        return result;
    }
    
    
    /**
     * 創建模擬客戶端（針對 https 客戶端禁用 SSL 驗證）
     *
     * @param cookieStore 緩存的 Cookies 信息
     * @return
     * @throws Exception
     */
    private static CloseableHttpClient createHttpClientWithNoSsl() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // don't check
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // don't check
                    }
                }
        };

        /*SSLContext ctx = SSLContext.getInstance("SSLv3");
        ctx.init(null, trustAllCerts, null);
        LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);
        return HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();*/

        HttpClientBuilder builder = HttpClients.custom();
        builder.setSSLHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostName, SSLSession sslSession) {
                return true; // 證書校驗通過
            }
        });
        SSLContext ctx = SSLContexts.custom().useProtocol("TLSv1.2").build();
        return builder.setSslcontext(ctx).build();


    }

}
