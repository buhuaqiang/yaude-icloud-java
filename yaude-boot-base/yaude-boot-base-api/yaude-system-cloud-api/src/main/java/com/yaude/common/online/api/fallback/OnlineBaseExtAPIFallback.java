package com.yaude.common.online.api.fallback;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.system.vo.DictModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.online.api.IOnlineBaseExtAPI;

import java.util.List;
import java.util.Map;

/**
 * 進入fallback的方法 檢查是否token未設置
 */
@Slf4j
public class OnlineBaseExtAPIFallback implements IOnlineBaseExtAPI {

    @Setter
    private Throwable cause;

    @Override
    public String cgformPostCrazyForm(String tableName, JSONObject jsonObject) {
        return null;
    }

    @Override
    public String cgformPutCrazyForm(String tableName, JSONObject jsonObject) {
        return null;
    }

    @Override
    public JSONObject cgformQueryAllDataByTableName(String tableName, String dataIds) {
        return null;
    }

    @Override
    public String cgformDeleteDataByCode(String cgformCode, String dataIds) {
        return null;
    }

    @Override
    public Map<String, Object> cgreportGetData(String code, String forceKey, String dataList) {
        return null;
    }

    @Override
    public List<DictModel> cgreportGetDataPackage(String code, String dictText, String dictCode, String dataList) {
        return null;
    }

}
