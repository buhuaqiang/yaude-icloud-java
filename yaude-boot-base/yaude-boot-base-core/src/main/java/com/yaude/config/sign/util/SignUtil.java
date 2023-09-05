package com.yaude.config.sign.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.exception.JeecgBootException;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import com.yaude.config.StaticConfig;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.SortedMap;

/**
 * 簽名工具類
 * 
 * @author jeecg
 * @date 20210621
 */
@Slf4j
public class SignUtil {
    public static final String xPathVariable = "x-path-variable";

    /**
     * @param params
     *            所有的請求參數都會在這里進行排序加密
     * @return 驗證簽名結果
     */
    public static boolean verifySign(SortedMap<String, String> params,String headerSign) {
        if (params == null || StringUtils.isEmpty(headerSign)) {
            return false;
        }
        // 把參數加密
        String paramsSign = getParamsSign(params);
        log.info("Param Sign : {}", paramsSign);
        return !StringUtils.isEmpty(paramsSign) && headerSign.equals(paramsSign);
    }

    /**
     * @param params
     *            所有的請求參數都會在這里進行排序加密
     * @return 得到簽名
     */
    public static String getParamsSign(SortedMap<String, String> params) {
        //去掉 Url 里的時間戳
        params.remove("_t");
        String paramsJsonStr = JSONObject.toJSONString(params);
        log.info("Param paramsJsonStr : {}", paramsJsonStr);
        StaticConfig staticConfig = SpringContextUtils.getBean(StaticConfig.class);
        String signatureSecret = staticConfig.getSignatureSecret();
        if(oConvertUtils.isEmpty(signatureSecret) || signatureSecret.contains("${")){
            throw new JeecgBootException("簽名密鑰 ${jeecg.signatureSecret} 缺少配置 ！！");
        }
        return DigestUtils.md5DigestAsHex((paramsJsonStr + signatureSecret).getBytes()).toUpperCase();
    }
}