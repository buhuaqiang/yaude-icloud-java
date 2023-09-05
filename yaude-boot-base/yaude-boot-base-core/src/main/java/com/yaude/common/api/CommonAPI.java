package com.yaude.common.api;

import com.yaude.common.system.vo.*;
import com.yaude.common.system.vo.*;
import com.yaude.common.system.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommonAPI {

    /**
     * 1查詢用戶角色信息
     * @param username
     * @return
     */
    Set<String> queryUserRoles(String username);


    /**
     * 2查詢用戶權限信息
     * @param username
     * @return
     */
    Set<String> queryUserAuths(String username);

    /**
     * 3根據 id 查詢數據庫中存儲的 DynamicDataSourceModel
     *
     * @param dbSourceId
     * @return
     */
    DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId);

    /**
     * 4根據 code 查詢數據庫中存儲的 DynamicDataSourceModel
     *
     * @param dbSourceCode
     * @return
     */
    DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode);

    /**
     * 5根據用戶賬號查詢用戶信息
     * @param username
     * @return
     */
    public LoginUser getUserByName(String username);


    /**
     * 6字典表的 翻譯
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    String translateDictFromTable(String table, String text, String code, String key);

    /**
     * 7普通字典的翻譯
     * @param code
     * @param key
     * @return
     */
    String translateDict(String code, String key);

    /**
     * 8查詢數據權限
     * @return
     */
    List<SysPermissionDataRuleModel> queryPermissionDataRule(String component, String requestPath, String username);


    /**
     * 9查詢用戶信息
     * @param username
     * @return
     */
    SysUserCacheInfo getCacheUser(String username);

    /**
     * 10獲取數據字典
     * @param code
     * @return
     */
    public List<DictModel> queryDictItemsByCode(String code);

    /**
     * 獲取有效的數據字典項
     * @param code
     * @return
     */
    public List<DictModel> queryEnableDictItemsByCode(String code);

    /**
     * 13獲取表數據字典
     * @param table
     * @param text
     * @param code
     * @return
     */
    List<DictModel> queryTableDictItemsByCode(String table, String text, String code);

    /**
     * 14 普通字典的翻譯，根據多個dictCode和多條數據，多個以逗號分割
     * @param dictCodes 例如：user_status,sex
     * @param keys 例如：1,2,0
     * @return
     */
    Map<String, List<DictModel>> translateManyDict(String dictCodes, String keys);

    /**
     * 15 字典表的 翻譯，可批量
     * @param table
     * @param text
     * @param code
     * @param keys 多個用逗號分割
     * @return
     */
    List<DictModel> translateDictFromTableByKeys(String table, String text, String code, String keys);

}
