package com.yaude.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.api.dto.OnlineAuthDTO;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.vo.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.system.vo.*;
import com.yaude.common.system.vo.*;
import com.yaude.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 服務化 system模塊 對外接口請求類
 */
@RestController
@RequestMapping("/sys/api")
public class SystemAPIController {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ISysUserService sysUserService;


    /**
     * 發送系統消息
     * @param message 使用構造器賦值參數 如果不設置category(消息類型)則默認為2 發送系統消息
     */
    @PostMapping("/sendSysAnnouncement")
    public void sendSysAnnouncement(@RequestBody MessageDTO message){
        sysBaseAPI.sendSysAnnouncement(message);
    }

    /**
     * 發送消息 附帶業務參數
     * @param message 使用構造器賦值參數
     */
    @PostMapping("/sendBusAnnouncement")
    public void sendBusAnnouncement(@RequestBody BusMessageDTO message){
        sysBaseAPI.sendBusAnnouncement(message);
    }

    /**
     * 通過模板發送消息
     * @param message 使用構造器賦值參數
     */
    @PostMapping("/sendTemplateAnnouncement")
    public void sendTemplateAnnouncement(@RequestBody TemplateMessageDTO message){
        sysBaseAPI.sendTemplateAnnouncement(message);
    }

    /**
     * 通過模板發送消息 附帶業務參數
     * @param message 使用構造器賦值參數
     */
    @PostMapping("/sendBusTemplateAnnouncement")
    public void sendBusTemplateAnnouncement(@RequestBody BusTemplateMessageDTO message){
        sysBaseAPI.sendBusTemplateAnnouncement(message);
    }

    /**
     * 通過消息中心模板，生成推送內容
     * @param templateDTO 使用構造器賦值參數
     * @return
     */
    @PostMapping("/parseTemplateByCode")
    public String parseTemplateByCode(@RequestBody TemplateDTO templateDTO){
        return sysBaseAPI.parseTemplateByCode(templateDTO);
    }

    /**
     * 根據業務類型busType及業務busId修改消息已讀
     */
    @GetMapping("/updateSysAnnounReadFlag")
    public void updateSysAnnounReadFlag(@RequestParam("busType") String busType, @RequestParam("busId")String busId){
        sysBaseAPI.updateSysAnnounReadFlag(busType, busId);
    }

    /**
     * 根據用戶賬號查詢用戶信息
     * @param username
     * @return
     */
    @GetMapping("/getUserByName")
    public LoginUser getUserByName(@RequestParam("username") String username){
        return sysBaseAPI.getUserByName(username);
    }

    /**
     * 根據用戶id查詢用戶信息
     * @param id
     * @return
     */
    @GetMapping("/getUserById")
    LoginUser getUserById(@RequestParam("id") String id){
        return sysBaseAPI.getUserById(id);
    }

    /**
     * 通過用戶賬號查詢角色集合
     * @param username
     * @return
     */
    @GetMapping("/getRolesByUsername")
    List<String> getRolesByUsername(@RequestParam("username") String username){
        return sysBaseAPI.getRolesByUsername(username);
    }

    /**
     * 通過用戶賬號查詢部門集合
     * @param username
     * @return 部門 id
     */
    @GetMapping("/getDepartIdsByUsername")
    List<String> getDepartIdsByUsername(@RequestParam("username") String username){
        return sysBaseAPI.getDepartIdsByUsername(username);
    }

    /**
     * 通過用戶賬號查詢部門 name
     * @param username
     * @return 部門 name
     */
    @GetMapping("/getDepartNamesByUsername")
    List<String> getDepartNamesByUsername(@RequestParam("username") String username){
        return sysBaseAPI.getDepartNamesByUsername(username);
    }


    /**
     * 獲取數據字典
     * @param code
     * @return
     */
    @GetMapping("/queryDictItemsByCode")
    List<DictModel> queryDictItemsByCode(@RequestParam("code") String code){
        return sysBaseAPI.queryDictItemsByCode(code);
    }

    /**
     * 獲取有效的數據字典
     * @param code
     * @return
     */
    @GetMapping("/queryEnableDictItemsByCode")
    List<DictModel> queryEnableDictItemsByCode(@RequestParam("code") String code){
        return sysBaseAPI.queryEnableDictItemsByCode(code);
    }


    /** 查詢所有的父級字典，按照create_time排序 */
    @GetMapping("/queryAllDict")
    List<DictModel> queryAllDict(){
        return sysBaseAPI.queryAllDict();
    }

    /**
     * 查詢所有分類字典
     * @return
     */
    @GetMapping("/queryAllDSysCategory")
    List<SysCategoryModel> queryAllDSysCategory(){
        return sysBaseAPI.queryAllDSysCategory();
    }


    /**
     * 查詢所有部門 作為字典信息 id -->value,departName -->text
     * @return
     */
    @GetMapping("/queryAllDepartBackDictModel")
    List<DictModel> queryAllDepartBackDictModel(){
        return sysBaseAPI.queryAllDepartBackDictModel();
    }

    /**
     * 獲取所有角色 帶參
     * roleIds 默認選中角色
     * @return
     */
    @GetMapping("/queryAllRole")
    public List<ComboModel> queryAllRole(@RequestParam(name = "roleIds",required = false)String[] roleIds){
        if(roleIds==null || roleIds.length==0){
            return sysBaseAPI.queryAllRole();
        }else{
            return sysBaseAPI.queryAllRole(roleIds);
        }
    }

    /**
     * 通過用戶賬號查詢角色Id集合
     * @param username
     * @return
     */
    @GetMapping("/getRoleIdsByUsername")
    public List<String> getRoleIdsByUsername(@RequestParam("username")String username){
        return sysBaseAPI.getRoleIdsByUsername(username);
    }

    /**
     * 通過部門編號查詢部門id
     * @param orgCode
     * @return
     */
    @GetMapping("/getDepartIdsByOrgCode")
    public String getDepartIdsByOrgCode(@RequestParam("orgCode")String orgCode){
        return sysBaseAPI.getDepartIdsByOrgCode(orgCode);
    }

    /**
     * 查詢所有部門
     * @return
     */
    @GetMapping("/getAllSysDepart")
    public List<SysDepartModel> getAllSysDepart(){
        return sysBaseAPI.getAllSysDepart();
    }

    /**
     * 根據 id 查詢數據庫中存儲的 DynamicDataSourceModel
     *
     * @param dbSourceId
     * @return
     */
    @GetMapping("/getDynamicDbSourceById")
    DynamicDataSourceModel getDynamicDbSourceById(@RequestParam("dbSourceId")String dbSourceId){
        return sysBaseAPI.getDynamicDbSourceById(dbSourceId);
    }



    /**
     * 根據部門Id獲取部門負責人
     * @param deptId
     * @return
     */
    @GetMapping("/getDeptHeadByDepId")
    public List<String> getDeptHeadByDepId(@RequestParam("deptId") String deptId){
        return sysBaseAPI.getDeptHeadByDepId(deptId);
    }

    /**
     * 查找父級部門
     * @param departId
     * @return
     */
    @GetMapping("/getParentDepartId")
    public DictModel getParentDepartId(@RequestParam("departId")String departId){
        return sysBaseAPI.getParentDepartId(departId);
    }

    /**
     * 根據 code 查詢數據庫中存儲的 DynamicDataSourceModel
     *
     * @param dbSourceCode
     * @return
     */
    @GetMapping("/getDynamicDbSourceByCode")
    public DynamicDataSourceModel getDynamicDbSourceByCode(@RequestParam("dbSourceCode") String dbSourceCode){
        return sysBaseAPI.getDynamicDbSourceByCode(dbSourceCode);
    }

    /**
     * 給指定用戶發消息
     * @param userIds
     * @param cmd
     */
    @GetMapping("/sendWebSocketMsg")
    public void sendWebSocketMsg(String[] userIds, String cmd){
        sysBaseAPI.sendWebSocketMsg(userIds, cmd);
    }


    /**
     * 根據id獲取所有參與用戶
     * userIds
     * @return
     */
    @GetMapping("/queryAllUserByIds")
    public List<LoginUser> queryAllUserByIds(@RequestParam("userIds") String[] userIds){
        return sysBaseAPI.queryAllUserByIds(userIds);
    }

    /**
     * 查詢所有用戶 返回ComboModel
     * @return
     */
    @GetMapping("/queryAllUserBackCombo")
    public List<ComboModel> queryAllUserBackCombo(){
        return sysBaseAPI.queryAllUserBackCombo();
    }

    /**
     * 分頁查詢用戶 返回JSONObject
     * @return
     */
    @GetMapping("/queryAllUser")
    public JSONObject queryAllUser(@RequestParam(name="userIds",required=false)String userIds, @RequestParam(name="pageNo",required=false) Integer pageNo,@RequestParam(name="pageSize",required=false) int pageSize){
        return sysBaseAPI.queryAllUser(userIds, pageNo, pageSize);
    }



    /**
     * 將會議簽到信息推動到預覽
     * userIds
     * @return
     * @param userId
     */
    @GetMapping("/meetingSignWebsocket")
    public void meetingSignWebsocket(@RequestParam("userId")String userId){
        sysBaseAPI.meetingSignWebsocket(userId);
    }

    /**
     * 根據name獲取所有參與用戶
     * userNames
     * @return
     */
    @GetMapping("/queryUserByNames")
    public List<LoginUser> queryUserByNames(@RequestParam("userNames")String[] userNames){
        return sysBaseAPI.queryUserByNames(userNames);
    }

    /**
     * 獲取用戶的角色集合
     * @param username
     * @return
     */
    @GetMapping("/getUserRoleSet")
    public Set<String> getUserRoleSet(@RequestParam("username")String username){
        return sysBaseAPI.getUserRoleSet(username);
    }

    /**
     * 獲取用戶的權限集合
     * @param username
     * @return
     */
    @GetMapping("/getUserPermissionSet")
    public Set<String> getUserPermissionSet(@RequestParam("username") String username){
        return sysBaseAPI.getUserPermissionSet(username);
    }

    //-----

    /**
     * 判斷是否有online訪問的權限
     * @param onlineAuthDTO
     * @return
     */
    @PostMapping("/hasOnlineAuth")
    public boolean hasOnlineAuth(@RequestBody OnlineAuthDTO onlineAuthDTO){
        return sysBaseAPI.hasOnlineAuth(onlineAuthDTO);
    }

    /**
     * 查詢用戶角色信息
     * @param username
     * @return
     */
    @GetMapping("/queryUserRoles")
    public Set<String> queryUserRoles(@RequestParam("username") String username){
        return sysUserService.getUserRolesSet(username);
    }


    /**
     * 查詢用戶權限信息
     * @param username
     * @return
     */
    @GetMapping("/queryUserAuths")
    public Set<String> queryUserAuths(@RequestParam("username") String username){
        return sysUserService.getUserPermissionsSet(username);
    }

    /**
     * 通過部門id獲取部門全部信息
     */
    @GetMapping("/selectAllById")
    public SysDepartModel selectAllById(@RequestParam("id") String id){
        return sysBaseAPI.selectAllById(id);
    }

    /**
     * 根據用戶id查詢用戶所屬公司下所有用戶ids
     * @param userId
     * @return
     */
    @GetMapping("/queryDeptUsersByUserId")
    public List<String> queryDeptUsersByUserId(@RequestParam("userId") String userId){
        return sysBaseAPI.queryDeptUsersByUserId(userId);
    }


    /**
     * 查詢數據權限
     * @return
     */
    @GetMapping("/queryPermissionDataRule")
    public List<SysPermissionDataRuleModel> queryPermissionDataRule(@RequestParam("component") String component, @RequestParam("requestPath")String requestPath, @RequestParam("username") String username){
        return sysBaseAPI.queryPermissionDataRule(component, requestPath, username);
    }

    /**
     * 查詢用戶信息
     * @param username
     * @return
     */
    @GetMapping("/getCacheUser")
    public SysUserCacheInfo getCacheUser(@RequestParam("username") String username){
        return sysBaseAPI.getCacheUser(username);
    }

    /**
     * 普通字典的翻譯
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/translateDict")
    public String translateDict(@RequestParam("code") String code, @RequestParam("key") String key){
        return sysBaseAPI.translateDict(code, key);
    }


    /**
     * 36根據多個用戶賬號(逗號分隔)，查詢返回多個用戶信息
     * @param usernames
     * @return
     */
    @RequestMapping("/queryUsersByUsernames")
    List<JSONObject> queryUsersByUsernames(@RequestParam("usernames") String usernames){
        return this.sysBaseAPI.queryUsersByUsernames(usernames);
    }

    /**
     * 37根據多個用戶id(逗號分隔)，查詢返回多個用戶信息
     * @param ids
     * @return
     */
    @RequestMapping("/queryUsersByIds")
    List<JSONObject> queryUsersByIds(@RequestParam("ids") String ids){
        return this.sysBaseAPI.queryUsersByIds(ids);
    }

    /**
     * 38根據多個部門編碼(逗號分隔)，查詢返回多個部門信息
     * @param orgCodes
     * @return
     */
    @GetMapping("/queryDepartsByOrgcodes")
    List<JSONObject> queryDepartsByOrgcodes(@RequestParam("orgCodes") String orgCodes){
        return this.sysBaseAPI.queryDepartsByOrgcodes(orgCodes);
    }

    /**
     * 39根據多個部門ID(逗號分隔)，查詢返回多個部門信息
     * @param ids
     * @return
     */
    @GetMapping("/queryDepartsByIds")
    List<JSONObject> queryDepartsByIds(@RequestParam("ids") String ids){
        return this.sysBaseAPI.queryDepartsByIds(ids);
    }

    /**
     * 40發送郵件消息
     * @param email
     * @param title
     * @param content
     */
    @GetMapping("/sendEmailMsg")
    public void sendEmailMsg(@RequestParam("email")String email,@RequestParam("title")String title,@RequestParam("content")String content){
         this.sysBaseAPI.sendEmailMsg(email,title,content);
    };
    /**
     * 41 獲取公司下級部門和公司下所有用戶信息
     * @param orgCode
     */
    @GetMapping("/getDeptUserByOrgCode")
    List<Map> getDeptUserByOrgCode(@RequestParam("orgCode")String orgCode){
       return this.sysBaseAPI.getDeptUserByOrgCode(orgCode);
    }

    /**
     * 查詢分類字典翻譯
     *
     * @param ids 分類字典表id
     * @return
     */
    @GetMapping("/loadCategoryDictItem")
    public List<String> loadCategoryDictItem(@RequestParam("ids") String ids) {
        return sysBaseAPI.loadCategoryDictItem(ids);
    }

    /**
     * 根據字典code加載字典text
     *
     * @param dictCode 順序：tableName,text,code
     * @param keys     要查詢的key
     * @return
     */
    @GetMapping("/loadDictItem")
    public List<String> loadDictItem(@RequestParam("dictCode") String dictCode, @RequestParam("keys") String keys) {
        return sysBaseAPI.loadDictItem(dictCode, keys);
    }

    /**
     * 根據字典code查詢字典項
     *
     * @param dictCode 順序：tableName,text,code
     * @param dictCode 要查詢的key
     * @return
     */
    @GetMapping("/getDictItems")
    public List<DictModel> getDictItems(@RequestParam("dictCode") String dictCode) {
        return sysBaseAPI.getDictItems(dictCode);
    }

    /**
     * 根據多個字典code查詢多個字典項
     *
     * @param dictCodeList
     * @return key = dictCode ； value=對應的字典項
     */
    @RequestMapping("/getManyDictItems")
    public Map<String, List<DictModel>> getManyDictItems(@RequestParam("dictCodeList") List<String> dictCodeList) {
        return sysBaseAPI.getManyDictItems(dictCodeList);
    }

    /**
     * 【下拉搜索】
     * 大數據量的字典表 走異步加載，即前端輸入內容過濾數據
     *
     * @param dictCode 字典code格式：table,text,code
     * @param keyword  過濾關鍵字
     * @return
     */
    @GetMapping("/loadDictItemByKeyword")
    public List<DictModel> loadDictItemByKeyword(@RequestParam("dictCode") String dictCode, @RequestParam("keyword") String keyword, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return sysBaseAPI.loadDictItemByKeyword(dictCode, keyword, pageSize);
    }

    /**
     * 48 普通字典的翻譯，根據多個dictCode和多條數據，多個以逗號分割
     * @param dictCodes
     * @param keys
     * @return
     */
    @GetMapping("/translateManyDict")
    public Map<String, List<DictModel>> translateManyDict(@RequestParam("dictCodes") String dictCodes, @RequestParam("keys") String keys){
        return this.sysBaseAPI.translateManyDict(dictCodes, keys);
    }


    /**
     * 獲取表數據字典 【接口簽名驗證】
     * @param table
     * @param text
     * @param code
     * @return
     */
    @GetMapping("/queryTableDictItemsByCode")
    List<DictModel> queryTableDictItemsByCode(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code){
        return sysBaseAPI.queryTableDictItemsByCode(table, text, code);
    }

    /**
     * 查詢表字典 支持過濾數據 【接口簽名驗證】
     * @param table
     * @param text
     * @param code
     * @param filterSql
     * @return
     */
    @GetMapping("/queryFilterTableDictInfo")
    List<DictModel> queryFilterTableDictInfo(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("filterSql") String filterSql){
        return sysBaseAPI.queryFilterTableDictInfo(table, text, code, filterSql);
    }

    /**
     * 【接口簽名驗證】
     * 查詢指定table的 text code 獲取字典，包含text和value
     * @param table
     * @param text
     * @param code
     * @param keyArray
     * @return
     */
    @Deprecated
    @GetMapping("/queryTableDictByKeys")
    public List<String> queryTableDictByKeys(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("keyArray") String[] keyArray){
        return sysBaseAPI.queryTableDictByKeys(table, text, code, keyArray);
    }


    /**
     * 字典表的 翻譯【接口簽名驗證】
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    @GetMapping("/translateDictFromTable")
    public String translateDictFromTable(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("key") String key){
        return sysBaseAPI.translateDictFromTable(table, text, code, key);
    }


    /**
     * 【接口簽名驗證】
     * 49 字典表的 翻譯，可批量
     *
     * @param table
     * @param text
     * @param code
     * @param keys  多個用逗號分割
     * @return
     */
    @GetMapping("/translateDictFromTableByKeys")
    public List<DictModel> translateDictFromTableByKeys(@RequestParam("table") String table, @RequestParam("text") String text, @RequestParam("code") String code, @RequestParam("keys") String keys) {
        return this.sysBaseAPI.translateDictFromTableByKeys(table, text, code, keys);
    }

}
