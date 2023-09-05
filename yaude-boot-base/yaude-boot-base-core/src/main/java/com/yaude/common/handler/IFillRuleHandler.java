package com.yaude.common.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * 填值規則接口
 *
 * @author Yan_東
 * 如需使用填值規則功能，規則實現類必須實現此接口
 */
public interface IFillRuleHandler {

    /**
     * @param params 頁面配置固定參數
     * @param formData  動態表單參數
     * @return
     */
    public Object execute(JSONObject params, JSONObject formData);

}

