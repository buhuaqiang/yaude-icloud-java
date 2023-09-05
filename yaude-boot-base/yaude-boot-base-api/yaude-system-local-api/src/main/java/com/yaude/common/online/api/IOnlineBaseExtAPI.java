package com.yaude.common.online.api;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.system.vo.DictModel;

import java.util.List;
import java.util.Map;

/**
 * 表單設計器【Online】翻譯API接口
 *
 * @author sunjianlei
 */
public interface IOnlineBaseExtAPI {

    /**
     * 【Online】 表單設計器專用：同步新增
     */
    String cgformPostCrazyForm(String tableName, JSONObject jsonObject) throws Exception;

    /**
     * 【Online】 表單設計器專用：同步編輯
     */
    String cgformPutCrazyForm(String tableName, JSONObject jsonObject) throws Exception;

    /**
     * online表單刪除數據
     *
     * @param cgformCode Online表單code
     * @param dataIds    數據ID，可逗號分割
     * @return
     */
    String cgformDeleteDataByCode(String cgformCode, String dataIds);

    /**
     * 通過online表名查詢數據，同時查詢出子表的數據
     *
     * @param tableName online表名
     * @param dataIds   online數據ID
     * @return
     */
    JSONObject cgformQueryAllDataByTableName(String tableName, String dataIds);

    /**
     * 對 cgreportGetData 的返回值做優化，封裝 DictModel 集合
     *
     * @return
     */
    List<DictModel> cgreportGetDataPackage(String code, String dictText, String dictCode, String dataList);

    /**
     * 【cgreport】通過 head code 獲取 sql語句，并執行該語句返回查詢數據
     *
     * @param code     報表Code，如果沒傳ID就通過code查
     * @param forceKey
     * @param dataList
     * @return
     */
    Map<String, Object> cgreportGetData(String code, String forceKey, String dataList);

}
