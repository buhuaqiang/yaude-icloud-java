package com.yaude.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.entity.SysCheckRule;

/**
 * @Description: 編碼校驗規則
 * @Author: jeecg-boot
 * @Date: 2020-02-04
 * @Version: V1.0
 */
public interface ISysCheckRuleService extends IService<SysCheckRule> {

    /**
     * 通過 code 獲取規則
     *
     * @param ruleCode
     * @return
     */
    SysCheckRule getByCode(String ruleCode);


    /**
     * 通過用戶設定的自定義校驗規則校驗傳入的值
     *
     * @param checkRule
     * @param value
     * @return 返回 null代表通過校驗，否則就是返回的錯誤提示文本
     */
    JSONObject checkValue(SysCheckRule checkRule, String value);

}
