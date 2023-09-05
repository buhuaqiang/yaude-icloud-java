package com.yaude.modules.system.rule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.handler.IFillRuleHandler;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.YouBianCodeUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysCategory;
import com.yaude.modules.system.mapper.SysCategoryMapper;

import java.util.List;

/**
 * @Author scott
 * @Date 2019/12/9 11:32
 * @Description: 分類字典編碼生成規則
 */
public class CategoryCodeRule implements IFillRuleHandler {

    public static final String ROOT_PID_VALUE = "0";

    @Override
    public Object execute(JSONObject params, JSONObject formData) {

        String categoryPid = ROOT_PID_VALUE;
        String categoryCode = null;

        if (formData != null && formData.size() > 0) {
            Object obj = formData.get("pid");
            if (oConvertUtils.isNotEmpty(obj)) categoryPid = obj.toString();
        } else {
            if (params != null) {
                Object obj = params.get("pid");
                if (oConvertUtils.isNotEmpty(obj)) categoryPid = obj.toString();
            }
        }

        /*
         * 分成三種情況
         * 1.數據庫無數據 調用YouBianCodeUtil.getNextYouBianCode(null);
         * 2.添加子節點，無兄弟元素 YouBianCodeUtil.getSubYouBianCode(parentCode,null);
         * 3.添加子節點有兄弟元素 YouBianCodeUtil.getNextYouBianCode(lastCode);
         * */
        //找同類 確定上一個最大的code值
        LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<SysCategory>().eq(SysCategory::getPid, categoryPid).isNotNull(SysCategory::getCode).orderByDesc(SysCategory::getCode);
        SysCategoryMapper baseMapper = (SysCategoryMapper) SpringContextUtils.getBean("sysCategoryMapper");
        List<SysCategory> list = baseMapper.selectList(query);
        if (list == null || list.size() == 0) {
            if (ROOT_PID_VALUE.equals(categoryPid)) {
                //情況1
                categoryCode = YouBianCodeUtil.getNextYouBianCode(null);
            } else {
                //情況2
                SysCategory parent = (SysCategory) baseMapper.selectById(categoryPid);
                categoryCode = YouBianCodeUtil.getSubYouBianCode(parent.getCode(), null);
            }
        } else {
            //情況3
            categoryCode = YouBianCodeUtil.getNextYouBianCode(list.get(0).getCode());
        }
        return categoryCode;
    }
}
