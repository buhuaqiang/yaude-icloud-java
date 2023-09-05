package com.yaude.modules.dlglong.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.api.vo.Result;
import com.yaude.common.constant.VXESocketConst;
import com.yaude.common.system.query.MatchTypeEnum;
import com.yaude.common.system.query.QueryCondition;
import com.yaude.common.system.query.QueryGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.yaude.modules.demo.mock.vxe.websocket.VXESocket;
import com.yaude.modules.dlglong.entity.MockEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/mock/dlglong")
public class DlMockController {

    /**
     * 模擬更改狀態
     *
     * @param id
     * @param status
     * @return
     */
    @GetMapping("/change1")
    public Result mockChange1(@RequestParam("id") String id, @RequestParam("status") String status) {
        /* id 為 行的id（rowId），只要獲取到rowId，那么只需要調用 VXESocket.sendMessageToAll() 即可 */

        // 封裝行數據
        JSONObject rowData = new JSONObject();
        // 這個字段就是要更改的行數據ID
        rowData.put("id", id);
        // 這個字段就是要更改的列的key和具體的值
        rowData.put("status", status);
        // 模擬更改數據
        this.mockChange(rowData);

        return Result.ok();
    }

    /**
     * 模擬更改拖輪狀態
     *
     * @param id
     * @param tug_status
     * @return
     */
    @GetMapping("/change2")
    public Result mockChange2(@RequestParam("id") String id, @RequestParam("tug_status") String tug_status) {
        /* id 為 行的id（rowId），只要獲取到rowId，那么只需要調用 VXESocket.sendMessageToAll() 即可 */

        // 封裝行數據
        JSONObject rowData = new JSONObject();
        // 這個字段就是要更改的行數據ID
        rowData.put("id", id);
        // 這個字段就是要更改的列的key和具體的值
        JSONObject tugStatus = JSON.parseObject(tug_status);
        rowData.put("tug_status", tugStatus);
        // 模擬更改數據
        this.mockChange(rowData);

        return Result.ok();
    }

    /**
     * 模擬更改進度條狀態
     *
     * @param id
     * @param progress
     * @return
     */
    @GetMapping("/change3")
    public Result mockChange3(@RequestParam("id") String id, @RequestParam("progress") String progress) {
        /* id 為 行的id（rowId），只要獲取到rowId，那么只需要調用 VXESocket.sendMessageToAll() 即可 */

        // 封裝行數據
        JSONObject rowData = new JSONObject();
        // 這個字段就是要更改的行數據ID
        rowData.put("id", id);
        // 這個字段就是要更改的列的key和具體的值
        rowData.put("progress", progress);
        // 模擬更改數據
        this.mockChange(rowData);

        return Result.ok();
    }

    private void mockChange(JSONObject rowData) {
        // 封裝socket數據
        JSONObject socketData = new JSONObject();
        // 這里的 socketKey 必須要和調度計劃頁面上寫的 socketKey 屬性保持一致
        socketData.put("socketKey", "page-dispatch");
        // 這里的 args 必須得是一個數組，下標0是行數據，下標1是caseId，一般不用傳
        socketData.put("args", new Object[]{rowData, ""});
        // 封裝消息字符串，這里的 type 必須是 VXESocketConst.TYPE_UVT
        String message = VXESocket.packageMessage(VXESocketConst.TYPE_UVT, socketData);
        // 調用 sendMessageToAll 發送給所有在線的用戶
        VXESocket.sendMessageToAll(message);
    }

    /**
     * 模擬更改【大船待審】狀態
     *
     * @param status
     * @return
     */
    @GetMapping("/change4")
    public Result mockChange4(@RequestParam("status") String status) {
        // 封裝socket數據
        JSONObject socketData = new JSONObject();
        // 這里的 key 是前端注冊時使用的key，必須保持一致
        socketData.put("key", "dispatch-dcds-status");
        // 這里的 args 必須得是一個數組，每一位都是注冊方法的參數，按順序傳遞
        socketData.put("args", new Object[]{status});

        // 封裝消息字符串，這里的 type 必須是 VXESocketConst.TYPE_UVT
        String message = VXESocket.packageMessage(VXESocketConst.TYPE_CSD, socketData);
        // 調用 sendMessageToAll 發送給所有在線的用戶
        VXESocket.sendMessageToAll(message);

        return Result.ok();
    }

    /**
     * 【模擬】即時保存單行數據
     *
     * @param rowData 行數據，實際使用時可以替換成一個實體類
     */
    @PutMapping("/immediateSaveRow")
    public Result mockImmediateSaveRow(@RequestBody JSONObject rowData) throws Exception {
        System.out.println("即時保存.rowData：" + rowData.toJSONString());
        // 延時1.5秒，模擬網慢堵塞真實感
        Thread.sleep(500);
        return Result.ok();
    }

    /**
     * 【模擬】即時保存整個表格的數據
     *
     * @param tableData 表格數據（實際使用時可以替換成一個List實體類）
     */
    @PostMapping("/immediateSaveAll")
    public Result mockImmediateSaveAll(@RequestBody JSONArray tableData) throws Exception {
        // 【注】：
        // 1、tableData里包含該頁所有的數據
        // 2、如果你實現了“即時保存”，那么除了新增的數據，其他的都是已經保存過的了，
        //    不需要再進行一次update操作了，所以可以在前端傳數據的時候就遍歷判斷一下，
        //    只傳新增的數據給后臺insert即可，否者將會造成性能上的浪費。
        // 3、新增的行是沒有id的，通過這一點，就可以判斷是否是新增的數據

        System.out.println("即時保存.tableData：" + tableData.toJSONString());
        // 延時1.5秒，模擬網慢堵塞真實感
        Thread.sleep(1000);
        return Result.ok();
    }

    /**
     * 獲取模擬數據
     *
     * @param pageNo   頁碼
     * @param pageSize 頁大小
     * @param parentId 父ID，不傳則查詢頂級
     * @return
     */
    @GetMapping("/getData")
    public Result getMockData(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            // 父級id，根據父級id查詢子級，如果為空則查詢頂級
            @RequestParam(name = "parentId", required = false) String parentId
    ) {
        // 模擬JSON數據路徑
        String path = "classpath:com/yaude/modules/dlglong/json/dlglong.json";
        // 讀取JSON數據
        JSONArray dataList = readJsonData(path);
        if (dataList == null) {
            return Result.error("讀取數據失敗！");
        }
        IPage<JSONObject> page = this.queryDataPage(dataList, parentId, pageNo, pageSize);
        return Result.ok(page);
    }

    /**
     * 獲取模擬“調度計劃”頁面的數據
     *
     * @param pageNo   頁碼
     * @param pageSize 頁大小
     * @param parentId 父ID，不傳則查詢頂級
     * @return
     */
    @GetMapping("/getDdjhData")
    public Result getMockDdjhData(
            // SpringMVC 會自動將參數注入到實體里
            MockEntity mockEntity,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            // 父級id，根據父級id查詢子級，如果為空則查詢頂級
            @RequestParam(name = "parentId", required = false) String parentId,
            @RequestParam(name = "status", required = false) String status,
            // 高級查詢條件
            @RequestParam(name = "superQueryParams", required = false) String superQueryParams,
            // 高級查詢模式
            @RequestParam(name = "superQueryMatchType", required = false) String superQueryMatchType,
            HttpServletRequest request
    ) {
        // 獲取查詢條件（前臺傳遞的查詢參數）
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 遍歷輸出到控制臺
        System.out.println("\ngetDdjhData - 普通查詢條件：");
        for (String key : parameterMap.keySet()) {
            System.out.println("-- " + key + ": " + JSON.toJSONString(parameterMap.get(key)));
        }
        // 輸出高級查詢
        try {
            System.out.println("\ngetDdjhData - 高級查詢條件：");
            // 高級查詢模式
            MatchTypeEnum matchType = MatchTypeEnum.getByValue(superQueryMatchType);
            if (matchType == null) {
                System.out.println("-- 高級查詢模式：不識別（" + superQueryMatchType + "）");
            } else {
                System.out.println("-- 高級查詢模式：" + matchType.getValue());
            }
            superQueryParams = URLDecoder.decode(superQueryParams, "UTF-8");
            List<QueryCondition> conditions = JSON.parseArray(superQueryParams, QueryCondition.class);
            if (conditions != null) {
                for (QueryCondition condition : conditions) {
                    System.out.println("-- " + JSON.toJSONString(condition));
                }
            } else {
                System.out.println("-- 沒有傳遞任何高級查詢條件");
            }
            System.out.println();
        } catch (Exception e) {
            log.error("-- 高級查詢操作失敗：" + superQueryParams, e);
            e.printStackTrace();
        }

        /* 注：實際使用中不用寫上面那種繁瑣的代碼，這里只是為了直觀的輸出到控制臺里而寫的示例，
              使用下面這種寫法更簡潔方便 */

        // 封裝成 MyBatisPlus 能識別的 QueryWrapper，可以直接使用這個對象進行SQL篩選條件拼接
        // 這個方法也會自動封裝高級查詢條件，但是高級查詢參數名必須是superQueryParams和superQueryMatchType
        QueryWrapper<MockEntity> queryWrapper = QueryGenerator.initQueryWrapper(mockEntity, parameterMap);
        System.out.println("queryWrapper： " + queryWrapper.getCustomSqlSegment());

        // 模擬JSON數據路徑
        String path = "classpath:com/yaude/modules/dlglong/json/ddjh.json";
        if ("8".equals(status)) {
            path = "classpath:com/yaude/modules/dlglong/json/ddjh_s8.json";
        }
        // 讀取JSON數據
        JSONArray dataList = readJsonData(path);
        if (dataList == null) {
            return Result.error("讀取數據失敗！");
        }

        IPage<JSONObject> page = this.queryDataPage(dataList, parentId, pageNo, pageSize);
        // 逐行查詢子表數據，用于計算拖輪狀態
        List<JSONObject> records = page.getRecords();
        for (JSONObject record : records) {
            Map<String, Integer> tugStatusMap = new HashMap<>();
            String id = record.getString("id");
            // 查詢出主表的拖輪
            String tugMain = record.getString("tug");
            // 判斷是否有值
            if (StringUtils.isNotBlank(tugMain)) {
                // 拖輪根據分號分割
                String[] tugs = tugMain.split(";");
                // 查詢子表數據
                List<JSONObject> subRecords = this.queryDataPage(dataList, id, null, null).getRecords();
                // 遍歷子表和拖輪數據，找出進行計算反推拖輪狀態
                for (JSONObject subData : subRecords) {
                    String subTug = subData.getString("tug");
                    if (StringUtils.isNotBlank(subTug)) {
                        for (String tug : tugs) {
                            if (tug.equals(subTug)) {
                                // 計算拖輪狀態邏輯
                                int statusCode = 0;

                                /* 如果有發船時間、作業開始時間、作業結束時間、回船時間，則主表中的拖輪列中的每個拖輪背景色要即時變色 */

                                // 有發船時間，狀態 +1
                                String departureTime = subData.getString("departure_time");
                                if (StringUtils.isNotBlank(departureTime)) {
                                    statusCode += 1;
                                }
                                // 有作業開始時間，狀態 +1
                                String workBeginTime = subData.getString("work_begin_time");
                                if (StringUtils.isNotBlank(workBeginTime)) {
                                    statusCode += 1;
                                }
                                // 有作業結束時間，狀態 +1
                                String workEndTime = subData.getString("work_end_time");
                                if (StringUtils.isNotBlank(workEndTime)) {
                                    statusCode += 1;
                                }
                                // 有回船時間，狀態 +1
                                String returnTime = subData.getString("return_time");
                                if (StringUtils.isNotBlank(returnTime)) {
                                    statusCode += 1;
                                }
                                // 保存拖輪狀態，key是拖輪的值，value是狀態，前端根據不同的狀態碼，顯示不同的顏色，這個顏色也可以后臺計算完之后返回給前端直接使用
                                tugStatusMap.put(tug, statusCode);
                                break;
                            }
                        }
                    }
                }
            }
            // 新加一個字段用于保存拖輪狀態，不要直接覆蓋原來的，這個字段可以不保存到數據庫里
            record.put("tug_status", tugStatusMap);
        }
        page.setRecords(records);
        return Result.ok(page);
    }

    /**
     * 模擬查詢數據，可以根據父ID查詢，可以分頁
     *
     * @param dataList 數據列表
     * @param parentId 父ID
     * @param pageNo   頁碼
     * @param pageSize 頁大小
     * @return
     */
    private IPage<JSONObject> queryDataPage(JSONArray dataList, String parentId, Integer pageNo, Integer pageSize) {
        // 根據父級id查詢子級
        JSONArray dataDB = dataList;
        if (StringUtils.isNotBlank(parentId)) {
            JSONArray results = new JSONArray();
            List<String> parentIds = Arrays.asList(parentId.split(","));
            this.queryByParentId(dataDB, parentIds, results);
            dataDB = results;
        }
        // 模擬分頁（實際中應用SQL自帶的分頁）
        List<JSONObject> records = new ArrayList<>();
        IPage<JSONObject> page;
        long beginIndex, endIndex;
        // 如果任意一個參數為null，則不分頁
        if (pageNo == null || pageSize == null) {
            page = new Page<>(0, dataDB.size());
            beginIndex = 0;
            endIndex = dataDB.size();
        } else {
            page = new Page<>(pageNo, pageSize);
            beginIndex = page.offset();
            endIndex = page.offset() + page.getSize();
        }
        for (long i = beginIndex; (i < endIndex && i < dataDB.size()); i++) {
            JSONObject data = dataDB.getJSONObject((int) i);
            data = JSON.parseObject(data.toJSONString());
            // 不返回 children
            data.remove("children");
            records.add(data);
        }
        page.setRecords(records);
        page.setTotal(dataDB.size());
        return page;
    }

    private void queryByParentId(JSONArray dataList, List<String> parentIds, JSONArray results) {
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject data = dataList.getJSONObject(i);
            JSONArray children = data.getJSONArray("children");
            // 找到了該父級
            if (parentIds.contains(data.getString("id"))) {
                if (children != null) {
                    // addAll 的目的是將多個子表的數據合并在一起
                    results.addAll(children);
                }
            } else {
                if (children != null) {
                    queryByParentId(children, parentIds, results);
                }
            }
        }
        results.addAll(new JSONArray());
    }

    private JSONArray readJsonData(String path) {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
            if (stream != null) {
                String json = IOUtils.toString(stream, "UTF-8");
                return JSON.parseArray(json);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
