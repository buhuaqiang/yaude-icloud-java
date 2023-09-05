package com.yaude.common.system.api;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.api.dto.OnlineAuthDTO;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.constant.ServiceNameConstants;
import com.yaude.common.system.api.factory.SysBaseAPIFallbackFactory;
import com.yaude.common.system.vo.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 相比較local版
 * 去掉了一些方法：
 * addLog getDatabaseType queryAllDepart
 * queryAllUser(Wrapper wrapper) queryAllUser(String[] userIds, int pageNo, int pageSize)
 * 修改了一些方法：
 * createLog
 * sendSysAnnouncement 只保留了一個，其余全部干掉
 *
 * cloud接口數量43  local：35 common：9  額外一個特殊queryAllRole一個當兩個用
 */
@Component
@FeignClient(contextId = "sysBaseRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SysBaseAPIFallbackFactory.class)
public interface ISysBaseAPI extends CommonAPI {

    /**
     * 1發送系統消息
     * @param message 使用構造器賦值參數 如果不設置category(消息類型)則默認為2 發送系統消息
     */
    @PostMapping("/sys/api/sendSysAnnouncement")
    void sendSysAnnouncement(@RequestBody MessageDTO message);

    /**
     * 2發送消息 附帶業務參數
     * @param message 使用構造器賦值參數
     */
    @PostMapping("/sys/api/sendBusAnnouncement")
    void sendBusAnnouncement(@RequestBody BusMessageDTO message);

    /**
     * 3通過模板發送消息
     * @param message 使用構造器賦值參數
     */
    @PostMapping("/sys/api/sendTemplateAnnouncement")
    void sendTemplateAnnouncement(@RequestBody TemplateMessageDTO message);

    /**
     * 4通過模板發送消息 附帶業務參數
     * @param message 使用構造器賦值參數
     */
    @PostMapping("/sys/api/sendBusTemplateAnnouncement")
    void sendBusTemplateAnnouncement(@RequestBody BusTemplateMessageDTO message);

    /**
     * 5通過消息中心模板，生成推送內容
     * @param templateDTO 使用構造器賦值參數
     * @return
     */
    @PostMapping("/sys/api/parseTemplateByCode")
    String parseTemplateByCode(@RequestBody TemplateDTO templateDTO);

    /**
     * 6根據用戶id查詢用戶信息
     * @param id
     * @return
     */
    @GetMapping("/sys/api/getUserById")
    LoginUser getUserById(@RequestParam("id") String id);

    /**
     * 7通過用戶賬號查詢角色集合
     * @param username
     * @return
     */
    @GetMapping("/sys/api/getRolesByUsername")
    List<String> getRolesByUsername(@RequestParam("username") String username);

    /**
     * 8通過用戶賬號查詢部門集合
     * @param username
     * @return 部門 id
     */
    @GetMapping("/sys/api/getDepartIdsByUsername")
    List<String> getDepartIdsByUsername(@RequestParam("username") String username);

    /**
     * 9通過用戶賬號查詢部門 name
     * @param username
     * @return 部門 name
     */
    @GetMapping("/sys/api/getDepartNamesByUsername")
    List<String> getDepartNamesByUsername(@RequestParam("username") String username);

    /**
     * 10獲取數據字典
     * @param code
     * @return
     */
    @GetMapping("/sys/api/queryDictItemsByCode")
    List<DictModel> queryDictItemsByCode(@RequestParam("code") String code);

    /**
     * 獲取有效的數據字典項
     * @param code
     * @return
     */
    @GetMapping("/sys/api/queryEnableDictItemsByCode")
    public List<DictModel> queryEnableDictItemsByCode(@RequestParam("code") String code);

    /** 11查詢所有的父級字典，按照create_time排序 */
    @GetMapping("/sys/api/queryAllDict")
    List<DictModel> queryAllDict();

    /**
     * 12查詢所有分類字典
     * @return
     */
    @GetMapping("/sys/api/queryAllDSysCategory")
    List<SysCategoryModel> queryAllDSysCategory();

    /**
     * 13獲取表數據字典
     * @param table
     * @param text
     * @param code
     * @return
     */
    @GetMapping("/sys/api/queryTableDictItemsByCode")
    List<DictModel> queryTableDictItemsByCode(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code);

    /**
     * 14查詢所有部門 作為字典信息 id -->value,departName -->text
     * @return
     */
    @GetMapping("/sys/api/queryAllDepartBackDictModel")
    List<DictModel> queryAllDepartBackDictModel();

    /**
     * 15根據業務類型 busType 及業務 busId 修改消息已讀
     */
    @GetMapping("/sys/api/updateSysAnnounReadFlag")
    public void updateSysAnnounReadFlag(@RequestParam("busType") String busType, @RequestParam("busId")String busId);

    /**
     * 16查詢表字典 支持過濾數據
     * @param table
     * @param text
     * @param code
     * @param filterSql
     * @return
     */
    @GetMapping("/sys/api/queryFilterTableDictInfo")
    List<DictModel> queryFilterTableDictInfo(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("filterSql") String filterSql);

    /**
     * 17查詢指定table的 text code 獲取字典，包含text和value
     * @param table
     * @param text
     * @param code
     * @param keyArray
     * @return
     */
    @Deprecated
    @GetMapping("/sys/api/queryTableDictByKeys")
    public List<String> queryTableDictByKeys(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("keyArray") String[] keyArray);

    /**
     * 18查詢所有用戶 返回ComboModel
     * @return
     */
    @GetMapping("/sys/api/queryAllUserBackCombo")
    public List<ComboModel> queryAllUserBackCombo();

    /**
     * 19分頁查詢用戶 返回JSONObject
     * @return
     */
    @GetMapping("/sys/api/queryAllUser")
    public JSONObject queryAllUser(@RequestParam(name="userIds",required=false)String userIds, @RequestParam(name="pageNo",required=false) Integer pageNo,@RequestParam(name="pageSize",required=false) int pageSize);


    /**
     * 20獲取所有角色 帶參
     * roleIds 默認選中角色
     * @return
     */
    @GetMapping("/sys/api/queryAllRole")
    public List<ComboModel> queryAllRole(@RequestParam(name = "roleIds",required = false)String[] roleIds);

    /**
     * 21通過用戶賬號查詢角色Id集合
     * @param username
     * @return
     */
    @GetMapping("/sys/api/getRoleIdsByUsername")
    public List<String> getRoleIdsByUsername(@RequestParam("username")String username);

    /**
     * 22通過部門編號查詢部門id
     * @param orgCode
     * @return
     */
    @GetMapping("/sys/api/getDepartIdsByOrgCode")
    public String getDepartIdsByOrgCode(@RequestParam("orgCode")String orgCode);

    /**
     * 23查詢所有部門
     * @return
     */
    @GetMapping("/sys/api/getAllSysDepart")
    public List<SysDepartModel> getAllSysDepart();

    /**
     * 24查找父級部門
     * @param departId
     * @return
     */
    @GetMapping("/sys/api/getParentDepartId")
    DictModel getParentDepartId(@RequestParam("departId")String departId);

    /**
     * 25根據部門Id獲取部門負責人
     * @param deptId
     * @return
     */
    @GetMapping("/sys/api/getDeptHeadByDepId")
    public List<String> getDeptHeadByDepId(@RequestParam("deptId") String deptId);

    /**
     * 26給指定用戶發消息
     * @param userIds
     * @param cmd
     */
    @GetMapping("/sys/api/sendWebSocketMsg")
    public void sendWebSocketMsg(@RequestParam("userIds")String[] userIds, @RequestParam("cmd") String cmd);

    /**
     * 27根據id獲取所有參與用戶
     * userIds
     * @return
     */
    @GetMapping("/sys/api/queryAllUserByIds")
    public List<LoginUser> queryAllUserByIds(@RequestParam("userIds") String[] userIds);

    /**
     * 28將會議簽到信息推動到預覽
     * userIds
     * @return
     * @param userId
     */
    @GetMapping("/sys/api/meetingSignWebsocket")
    void meetingSignWebsocket(@RequestParam("userId")String userId);

    /**
     * 29根據name獲取所有參與用戶
     * userNames
     * @return
     */
    @GetMapping("/sys/api/queryUserByNames")
    List<LoginUser> queryUserByNames(@RequestParam("userNames")String[] userNames);


    /**
     * 30獲取用戶的角色集合
     * @param username
     * @return
     */
    @GetMapping("/sys/api/getUserRoleSet")
    Set<String> getUserRoleSet(@RequestParam("username")String username);

    /**
     * 31獲取用戶的權限集合
     * @param username
     * @return
     */
    @GetMapping("/sys/api/getUserPermissionSet")
    Set<String> getUserPermissionSet(@RequestParam("username") String username);

    /**
     * 32判斷是否有online訪問的權限
     * @param onlineAuthDTO
     * @return
     */
    @PostMapping("/sys/api/hasOnlineAuth")
    boolean hasOnlineAuth(@RequestBody OnlineAuthDTO onlineAuthDTO);

    /**
     * 33通過部門id獲取部門全部信息
     */
    @GetMapping("/sys/api/selectAllById")
    SysDepartModel selectAllById(@RequestParam("id") String id);

    /**
     * 34根據用戶id查詢用戶所屬公司下所有用戶ids
     * @param userId
     * @return
     */
    @GetMapping("/sys/api/queryDeptUsersByUserId")
    List<String> queryDeptUsersByUserId(@RequestParam("userId") String userId);


    //---

    /**
     * 35查詢用戶角色信息
     * @param username
     * @return
     */
    @GetMapping("/sys/api/queryUserRoles")
    Set<String> queryUserRoles(@RequestParam("username")String username);

    /**
     * 36查詢用戶權限信息
     * @param username
     * @return
     */
    @GetMapping("/sys/api/queryUserAuths")
    Set<String> queryUserAuths(@RequestParam("username")String username);

    /**
     * 37根據 id 查詢數據庫中存儲的 DynamicDataSourceModel
     *
     * @param dbSourceId
     * @return
     */
    @GetMapping("/sys/api/getDynamicDbSourceById")
    DynamicDataSourceModel getDynamicDbSourceById(@RequestParam("dbSourceId") String dbSourceId);

    /**
     * 38根據 code 查詢數據庫中存儲的 DynamicDataSourceModel
     *
     * @param dbSourceCode
     * @return
     */
    @GetMapping("/sys/api/getDynamicDbSourceByCode")
    DynamicDataSourceModel getDynamicDbSourceByCode(@RequestParam("dbSourceCode") String dbSourceCode);

    /**
     * 39根據用戶賬號查詢用戶信息 CommonAPI中定義
     * @param username
     */
    @GetMapping("/sys/api/getUserByName")
    LoginUser getUserByName(@RequestParam("username") String username);

    /**
     * 40字典表的 翻譯
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/sys/api/translateDictFromTable")
    String translateDictFromTable(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("key") String key);

    /**
     * 41普通字典的翻譯
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/sys/api/translateDict")
    String translateDict(@RequestParam("code") String code, @RequestParam("key") String key);

    /**
     * 42查詢數據權限
     * @return
     */
    @GetMapping("/sys/api/queryPermissionDataRule")
    List<SysPermissionDataRuleModel> queryPermissionDataRule(@RequestParam("component") String component, @RequestParam("requestPath")String requestPath, @RequestParam("username") String username);

    /**
     * 43查詢用戶信息
     * @param username
     * @return
     */
    @GetMapping("/sys/api/getCacheUser")
    SysUserCacheInfo getCacheUser(@RequestParam("username") String username);

    /**
     * 36根據多個用戶賬號(逗號分隔)，查詢返回多個用戶信息
     * @param usernames
     * @return
     */
    @GetMapping("/sys/api/queryUsersByUsernames")
    List<JSONObject> queryUsersByUsernames(@RequestParam("usernames") String usernames);

    /**
     * 37根據多個用戶ID(逗號分隔)，查詢返回多個用戶信息
     * @param ids
     * @return
     */
    @RequestMapping("/sys/api/queryUsersByIds")
    List<JSONObject> queryUsersByIds(@RequestParam("ids") String ids);

    /**
     * 38根據多個部門編碼(逗號分隔)，查詢返回多個部門信息
     * @param orgCodes
     * @return
     */
    @RequestMapping("/sys/api/queryDepartsByOrgcodes")
    List<JSONObject> queryDepartsByOrgcodes(@RequestParam("orgCodes") String orgCodes);

    /**
     * 39根據多個部門編碼(逗號分隔)，查詢返回多個部門信息
     * @param ids
     * @return
     */
    @GetMapping("/sys/api/queryDepartsByOrgIds")
    List<JSONObject> queryDepartsByOrgIds(@RequestParam("ids") String ids);
    
    /**
     * 40發送郵件消息
     * @param email
     * @param title
     * @param content
     */
    @GetMapping("/sys/api/sendEmailMsg")
    void sendEmailMsg(@RequestParam("email")String email,@RequestParam("title")String title,@RequestParam("content")String content);
    /**
     * 41 獲取公司下級部門和公司下所有用戶id
     * @param orgCode
     */
    @GetMapping("/sys/api/getDeptUserByOrgCode")
    List<Map> getDeptUserByOrgCode(@RequestParam("orgCode")String orgCode);

    /**
     * 42 查詢分類字典翻譯
     */
    @GetMapping("/sys/api/loadCategoryDictItem")
    List<String> loadCategoryDictItem(@RequestParam("ids") String ids);

    /**
     * 43 根據字典code加載字典text
     *
     * @param dictCode 順序：tableName,text,code
     * @param keys     要查詢的key
     * @return
     */
    @GetMapping("/sys/api/loadDictItem")
    List<String> loadDictItem(@RequestParam("dictCode") String dictCode, @RequestParam("keys") String keys);

    /**
     * 44 根據字典code查詢字典項
     *
     * @param dictCode 順序：tableName,text,code
     * @param dictCode 要查詢的key
     * @return
     */
    @GetMapping("/sys/api/getDictItems")
    List<DictModel> getDictItems(@RequestParam("dictCode") String dictCode);

    /**
     * 45 根據多個字典code查詢多個字典項
     *
     * @param dictCodeList
     * @return key = dictCode ； value=對應的字典項
     */
    @RequestMapping("/sys/api/getManyDictItems")
    Map<String, List<DictModel>> getManyDictItems(@RequestParam("dictCodeList") List<String> dictCodeList);

    /**
     * 46 【JSearchSelectTag下拉搜索組件專用接口】
     * 大數據量的字典表 走異步加載  即前端輸入內容過濾數據
     *
     * @param dictCode 字典code格式：table,text,code
     * @param keyword  過濾關鍵字
     * @return
     */
    @GetMapping("/sys/api/loadDictItemByKeyword")
    List<DictModel> loadDictItemByKeyword(@RequestParam("dictCode") String dictCode, @RequestParam("keyword") String keyword, @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 47 根據多個部門id(逗號分隔)，查詢返回多個部門信息
     * @param ids
     * @return
     */
    @GetMapping("/sys/api/queryDepartsByIds")
    List<JSONObject> queryDepartsByIds(@RequestParam("ids") String ids);

    /**
     * 48 普通字典的翻譯，根據多個dictCode和多條數據，多個以逗號分割
     * @param dictCodes
     * @param keys
     * @return
     */
    @Override
    @GetMapping("/sys/api/translateManyDict")
    Map<String, List<DictModel>> translateManyDict(@RequestParam("dictCodes") String dictCodes, @RequestParam("keys") String keys);

    /**
     * 49 字典表的 翻譯，可批量
     * @param table
     * @param text
     * @param code
     * @param keys 多個用逗號分割
     * @return
     */
    @Override
    @GetMapping("/sys/api/translateDictFromTableByKeys")
    List<DictModel> translateDictFromTableByKeys(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("keys") String keys);

}
