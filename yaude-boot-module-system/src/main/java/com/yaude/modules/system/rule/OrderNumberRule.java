package com.yaude.modules.system.rule;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.handler.IFillRuleHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 填值規則Demo：生成訂單號
 * 【測試示例】
 */
public class OrderNumberRule implements IFillRuleHandler {

    @Override
    public Object execute(JSONObject params, JSONObject formData) {
        String prefix = "CN";
        //訂單前綴默認為CN 如果規則參數不為空，則取自定義前綴
        if (params != null) {
            Object obj = params.get("prefix");
            if (obj != null) prefix = obj.toString();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        int random = RandomUtils.nextInt(90) + 10;
        String value = prefix + format.format(new Date()) + random;
        // 根據formData的值的不同，生成不同的訂單號
        String name = formData.getString("name");
        if (!StringUtils.isEmpty(name)) {
            value += name;
        }
        return value;
    }

}
