package com.yaude.common.online.api;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.constant.ServiceNameConstants;
import com.yaude.common.online.api.factory.OnlineBaseExtAPIFallbackFactory;
import com.yaude.common.system.vo.DictModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 【Online】Feign API接口
 */
@Component
@FeignClient(contextId = "onlineBaseRemoteApi", value = ServiceNameConstants.SYSTEM_ONLINE, fallbackFactory = OnlineBaseExtAPIFallbackFactory.class)
public interface IOnlineBaseExtAPI {

    /**
     * 【Online】 表單設計器專用：同步新增
     */
    @PostMapping(value = "/online/api/cgform/crazyForm/{name}")
    String cgformPostCrazyForm(@PathVariable("name") String tableName, @RequestBody JSONObject jsonObject) throws Exception;

    /**
     * 【Online】 表單設計器專用：同步編輯
     */
    @PutMapping(value = "/online/api/cgform/crazyForm/{name}")
    String cgformPutCrazyForm(@PathVariable("name") String tableName, @RequestBody JSONObject jsonObject) throws Exception;

    /**
     * 通過online表名查詢數據，同時查詢出子表的數據
     *
     * @param tableName online表名
     * @param dataIds   online數據ID
     * @return
     */
    @GetMapping(value = "/online/api/cgform/queryAllDataByTableName")
    JSONObject cgformQueryAllDataByTableName(@RequestParam("tableName") String tableName, @RequestParam("dataIds") String dataIds);

    /**
     * online表單刪除數據
     *
     * @param cgformCode Online表單code
     * @param dataIds    數據ID，可逗號分割
     * @return
     */
    @DeleteMapping("/online/api/cgform/cgformDeleteDataByCode")
    String cgformDeleteDataByCode(@RequestParam("cgformCode") String cgformCode, @RequestParam("dataIds") String dataIds);

    /**
     * 【cgreport】通過 head code 獲取 sql語句，并執行該語句返回查詢數據
     *
     * @param code     報表Code，如果沒傳ID就通過code查
     * @param forceKey
     * @param dataList
     * @return
     */
    @GetMapping("/online/api/cgreportGetData")
    Map<String, Object> cgreportGetData(@RequestParam("code") String code, @RequestParam("forceKey") String forceKey, @RequestParam("dataList") String dataList);

    /**
     * 【cgreport】對 cgreportGetData 的返回值做優化，封裝 DictModel 集合
     *
     * @return
     */
    @GetMapping("/online/api/cgreportGetDataPackage")
    List<DictModel> cgreportGetDataPackage(@RequestParam("code") String code, @RequestParam("dictText") String dictText, @RequestParam("dictCode") String dictCode, @RequestParam("dataList") String dataList);

}
