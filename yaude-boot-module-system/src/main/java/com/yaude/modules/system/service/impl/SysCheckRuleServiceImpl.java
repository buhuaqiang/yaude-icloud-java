package com.yaude.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import com.yaude.modules.system.entity.SysCheckRule;
import com.yaude.modules.system.mapper.SysCheckRuleMapper;
import com.yaude.modules.system.service.ISysCheckRuleService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * @Description: 編碼校驗規則
 * @Author: jeecg-boot
 * @Date: 2020-02-04
 * @Version: V1.0
 */
@Service
public class SysCheckRuleServiceImpl extends ServiceImpl<SysCheckRuleMapper, SysCheckRule> implements ISysCheckRuleService {

    /**
     * 位數特殊符號，用于檢查整個值，而不是裁剪某一段
     */
    private final String CHECK_ALL_SYMBOL = "*";

    @Override
    public SysCheckRule getByCode(String ruleCode) {
        LambdaQueryWrapper<SysCheckRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysCheckRule::getRuleCode, ruleCode);
        return super.getOne(queryWrapper);
    }

    /**
     * 通過用戶設定的自定義校驗規則校驗傳入的值
     *
     * @param checkRule
     * @param value
     * @return 返回 null代表通過校驗，否則就是返回的錯誤提示文本
     */
    @Override
    public JSONObject checkValue(SysCheckRule checkRule, String value) {
        if (checkRule != null && StringUtils.isNotBlank(value)) {
            String ruleJson = checkRule.getRuleJson();
            if (StringUtils.isNotBlank(ruleJson)) {
                // 開始截取的下標，根據規則的順序遞增，但是 * 號不計入遞增范圍
                int beginIndex = 0;
                JSONArray rules = JSON.parseArray(ruleJson);
                for (int i = 0; i < rules.size(); i++) {
                    JSONObject result = new JSONObject();
                    JSONObject rule = rules.getJSONObject(i);
                    // 位數
                    String digits = rule.getString("digits");
                    result.put("digits", digits);
                    // 驗證規則
                    String pattern = rule.getString("pattern");
                    result.put("pattern", pattern);
                    // 未通過時的提示文本
                    String message = rule.getString("message");
                    result.put("message", message);

                    // 根據用戶設定的區間，截取字符串進行驗證
                    String checkValue;
                    // 是否檢查整個值而不截取
                    if (CHECK_ALL_SYMBOL.equals(digits)) {
                        checkValue = value;
                    } else {
                        int num = Integer.parseInt(digits);
                        int endIndex = beginIndex + num;
                        // 如果結束下標大于給定的值的長度，則取到最后一位
                        endIndex = endIndex > value.length() ? value.length() : endIndex;
                        // 如果開始下標大于結束下標，則說明用戶還尚未輸入到該位置，直接賦空值
                        if (beginIndex > endIndex) {
                            checkValue = "";
                        } else {
                            checkValue = value.substring(beginIndex, endIndex);
                        }
                        result.put("beginIndex", beginIndex);
                        result.put("endIndex", endIndex);
                        beginIndex += num;
                    }
                    result.put("checkValue", checkValue);
                    boolean passed = Pattern.matches(pattern, checkValue);
                    result.put("passed", passed);
                    // 如果沒有通過校驗就返回錯誤信息
                    if (!passed) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

}
