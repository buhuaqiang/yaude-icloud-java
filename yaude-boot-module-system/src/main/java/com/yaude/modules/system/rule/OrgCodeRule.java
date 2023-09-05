package com.yaude.modules.system.rule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.handler.IFillRuleHandler;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.YouBianCodeUtil;
import com.yaude.modules.system.entity.SysDepart;
import com.yaude.modules.system.service.ISysDepartService;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author scott
 * @Date 2019/12/9 11:33
 * @Description: 機構編碼生成規則
 */
public class OrgCodeRule implements IFillRuleHandler {

    @Override
    public Object execute(JSONObject params, JSONObject formData) {
        ISysDepartService sysDepartService = (ISysDepartService) SpringContextUtils.getBean("sysDepartServiceImpl");

        LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
        LambdaQueryWrapper<SysDepart> query1 = new LambdaQueryWrapper<SysDepart>();
        // 創建一個List集合,存儲查詢返回的所有SysDepart對象
        List<SysDepart> departList = new ArrayList<>();
        String[] strArray = new String[2];
        //定義部門類型
        String orgType = "";
        // 定義新編碼字符串
        String newOrgCode = "";
        // 定義舊編碼字符串
        String oldOrgCode = "";

        String parentId = null;
        if (formData != null && formData.size() > 0) {
            Object obj = formData.get("parentId");
            if (obj != null) parentId = obj.toString();
        } else {
            if (params != null) {
                Object obj = params.get("parentId");
                if (obj != null) parentId = obj.toString();
            }
        }

        //如果是最高級,則查詢出同級的org_code, 調用工具類生成編碼并返回
        if (StringUtil.isNullOrEmpty(parentId)) {
            // 線判斷數據庫中的表是否為空,空則直接返回初始編碼
            query1.eq(SysDepart::getParentId, "").or().isNull(SysDepart::getParentId);
            query1.orderByDesc(SysDepart::getOrgCode);
            departList = sysDepartService.list(query1);
            if (departList == null || departList.size() == 0) {
                strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
                strArray[1] = "1";
                return strArray;
            } else {
                SysDepart depart = departList.get(0);
                oldOrgCode = depart.getOrgCode();
                orgType = depart.getOrgType();
                newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
            }
        } else {//反之則查詢出所有同級的部門,獲取結果后有兩種情況,有同級和沒有同級
            // 封裝查詢同級的條件
            query.eq(SysDepart::getParentId, parentId);
            // 降序排序
            query.orderByDesc(SysDepart::getOrgCode);
            // 查詢出同級部門的集合
            List<SysDepart> parentList = sysDepartService.list(query);
            // 查詢出父級部門
            SysDepart depart = sysDepartService.getById(parentId);
            // 獲取父級部門的Code
            String parentCode = depart.getOrgCode();
            // 根據父級部門類型算出當前部門的類型
            orgType = String.valueOf(Integer.valueOf(depart.getOrgType()) + 1);
            // 處理同級部門為null的情況
            if (parentList == null || parentList.size() == 0) {
                // 直接生成當前的部門編碼并返回
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
            } else { //處理有同級部門的情況
                // 獲取同級部門的編碼,利用工具類
                String subCode = parentList.get(0).getOrgCode();
                // 返回生成的當前部門編碼
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
            }
        }
        // 返回最終封裝了部門編碼和部門類型的數組
        strArray[0] = newOrgCode;
        strArray[1] = orgType;
        return strArray;
    }
}
