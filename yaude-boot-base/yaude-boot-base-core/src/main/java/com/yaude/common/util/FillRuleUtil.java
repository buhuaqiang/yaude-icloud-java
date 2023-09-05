package com.yaude.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaude.common.handler.IFillRuleHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * 規則值自動生成工具類
 *
 * @author qinfeng
 * @舉例： 自動生成訂單號；自動生成當前日期
 */
@Slf4j
public class FillRuleUtil {

    /**
     * @param ruleCode ruleCode
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object executeRule(String ruleCode, JSONObject formData) {
        if (!StringUtils.isEmpty(ruleCode)) {
            try {
                // 獲取 Service
                ServiceImpl impl = (ServiceImpl) SpringContextUtils.getBean("sysFillRuleServiceImpl");
                // 根據 ruleCode 查詢出實體
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("rule_code", ruleCode);
                JSONObject entity = JSON.parseObject(JSON.toJSONString(impl.getOne(queryWrapper)));
                if (entity == null) {
                    log.warn("填值規則：" + ruleCode + " 不存在");
                    return null;
                }
                // 獲取必要的參數
                String ruleClass = entity.getString("ruleClass");
                JSONObject params = entity.getJSONObject("ruleParams");
                if (params == null) {
                    params = new JSONObject();
                }
                if (formData == null) {
                    formData = new JSONObject();
                }
                // 通過反射執行配置的類里的方法
                IFillRuleHandler ruleHandler = (IFillRuleHandler) Class.forName(ruleClass).newInstance();
                return ruleHandler.execute(params, formData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
