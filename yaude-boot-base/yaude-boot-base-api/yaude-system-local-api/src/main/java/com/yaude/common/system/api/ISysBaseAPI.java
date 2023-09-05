package com.yaude.common.system.api;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.api.dto.OnlineAuthDTO;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.vo.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.vo.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description  底層共通業務API，提供其他獨立模塊調用
 * @Author  scott
 * @Date 2019-4-20
 * @Version V1.0
 */
public interface ISysBaseAPI extends CommonAPI {


    /**
     * 1發送系統消息
     * @param message 使用構造器賦值參數 如果不設置category(消息類型)則默認為2 發送系統消息
     */
    void sendSysAnnouncement(MessageDTO message);

    /**
     * 2發送消息 附帶業務參數
     * @param message 使用構造器賦值參數
     */
    void sendBusAnnouncement(BusMessageDTO message);

    /**
     * 3通過模板發送消息
     * @param message 使用構造器賦值參數
     */
    void sendTemplateAnnouncement(TemplateMessageDTO message);

    /**
     * 4通過模板發送消息 附帶業務參數
     * @param message 使用構造器賦值參數
     */
    void sendBusTemplateAnnouncement(BusTemplateMessageDTO message);

    /**
     * 5通過消息中心模板，生成推送內容
     * @param templateDTO 使用構造器賦值參數
     * @return
     */
    String parseTemplateByCode(TemplateDTO templateDTO);

    /**
     * 6根據用戶id查詢用戶信息
     * @param id
     * @return
     */
    LoginUser getUserById(String id);

    /**
     * 7通過用戶賬號查詢角色集合
     * @param username
     * @return
     */
    List<String> getRolesByUsername(String username);

    /**
     * 8通過用戶賬號查詢部門集合
     * @param username
     * @return 部門 id
     */
    List<String> getDepartIdsByUsername(String username);

    /**
     * 9通過用戶賬號查詢部門 name
     * @param username
     * @return 部門 name
     */
    List<String> getDepartNamesByUsername(String username);



    /** 11查詢所有的父級字典，按照create_time排序 */
    public List<DictModel> queryAllDict();

    /**
     * 12查詢所有分類字典
     * @return
     */
    public List<SysCategoryModel> queryAllDSysCategory();


    /**
     * 14查詢所有部門 作為字典信息 id -->value,departName -->text
     * @return
     */
    public List<DictModel> queryAllDepartBackDictModel();

    /**
     * 15根據業務類型及業務id修改消息已讀
     * @param busType
     * @param busId
     */
    public void updateSysAnnounReadFlag(String busType, String busId);

    /**
     * 16查詢表字典 支持過濾數據
     * @param table
     * @param text
     * @param code
     * @param filterSql
     * @return
     */
    public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql);

    /**
     * 17查詢指定table的 text code 獲取字典，包含text和value
     * @param table
     * @param text
     * @param code
     * @param keyArray
     * @return
     */
    @Deprecated
    public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray);

    /**
     * 18查詢所有用戶 返回ComboModel
     * @return
     */
    public List<ComboModel> queryAllUserBackCombo();

    /**
     * 19分頁查詢用戶 返回JSONObject
     * @return
     */
    public JSONObject queryAllUser(String userIds, Integer pageNo, Integer pageSize);

    /**
     * 20獲取所有角色
     * @return
     */
    public List<ComboModel> queryAllRole();

    /**
     * 21獲取所有角色 帶參
     * roleIds 默認選中角色
     * @return
     */
    public List<ComboModel> queryAllRole(String[] roleIds );

    /**
     * 22通過用戶賬號查詢角色Id集合
     * @param username
     * @return
     */
    public List<String> getRoleIdsByUsername(String username);

    /**
     * 23通過部門編號查詢部門id
     * @param orgCode
     * @return
     */
    public String getDepartIdsByOrgCode(String orgCode);

    /**
     * 24查詢所有部門
     * @return
     */
    public List<SysDepartModel> getAllSysDepart();

    /**
     * 25查找父級部門
     * @param departId
     * @return
     */
    DictModel getParentDepartId(String departId);

    /**
     * 26根據部門Id獲取部門負責人
     * @param deptId
     * @return
     */
    public List<String> getDeptHeadByDepId(String deptId);

    /**
     * 27給指定用戶發消息
     * @param userIds
     * @param cmd
     */
    public void sendWebSocketMsg(String[] userIds, String cmd);

    /**
     * 28根據id獲取所有參與用戶
     * userIds
     * @return
     */
    public List<LoginUser> queryAllUserByIds(String[] userIds);

    /**
     * 29將會議簽到信息推動到預覽
     * userIds
     * @return
     * @param userId
     */
    void meetingSignWebsocket(String userId);

    /**
     * 30根據name獲取所有參與用戶
     * userNames
     * @return
     */
    List<LoginUser> queryUserByNames(String[] userNames);


    /**
     * 31獲取用戶的角色集合
     * @param username
     * @return
     */
    Set<String> getUserRoleSet(String username);

    /**
     * 32獲取用戶的權限集合
     * @param username
     * @return
     */
    Set<String> getUserPermissionSet(String username);

    /**
     * 33判斷是否有online訪問的權限
     * @param onlineAuthDTO
     * @return
     */
    boolean hasOnlineAuth(OnlineAuthDTO onlineAuthDTO);

    /**
     * 34通過部門id獲取部門全部信息
     */
    SysDepartModel selectAllById(String id);

    /**
     * 35根據用戶id查詢用戶所屬公司下所有用戶ids
     * @param userId
     * @return
     */
    List<String> queryDeptUsersByUserId(String userId);

    /**
     * 36根據多個用戶賬號(逗號分隔)，查詢返回多個用戶信息
     * @param usernames
     * @return
     */
    List<JSONObject> queryUsersByUsernames(String usernames);

    /**
     * 37根據多個用戶ID(逗號分隔)，查詢返回多個用戶信息
     * @param ids
     * @return
     */
    List<JSONObject> queryUsersByIds(String ids);

    /**
     * 38根據多個部門編碼(逗號分隔)，查詢返回多個部門信息
     * @param orgCodes
     * @return
     */
    List<JSONObject> queryDepartsByOrgcodes(String orgCodes);

    /**
     * 39根據多個部門id(逗號分隔)，查詢返回多個部門信息
     * @param ids
     * @return
     */
    List<JSONObject> queryDepartsByIds(String ids);

    /**
     * 40發送郵件消息
     * @param email
     * @param title
     * @param content
     */
    void sendEmailMsg(String email,String title,String content);
    /**
     * 41 獲取公司下級部門和公司下所有用戶信息
     * @param orgCode
     */
    List<Map> getDeptUserByOrgCode(String orgCode);

    /**
     * 查詢分類字典翻譯
     */
    List<String> loadCategoryDictItem(String ids);

    /**
     * 根據字典code加載字典text
     *
     * @param dictCode 順序：tableName,text,code
     * @param keys     要查詢的key
     * @return
     */
    List<String> loadDictItem(String dictCode, String keys);

    /**
     * 根據字典code查詢字典項
     *
     * @param dictCode 順序：tableName,text,code
     * @param dictCode 要查詢的key
     * @return
     */
    List<DictModel> getDictItems(String dictCode);

    /**
     *  根據多個字典code查詢多個字典項
     * @param dictCodeList
     * @return key = dictCode ； value=對應的字典項
     */
    Map<String, List<DictModel>> getManyDictItems(List<String> dictCodeList);

    /**
     * 【JSearchSelectTag下拉搜索組件專用接口】
     * 大數據量的字典表 走異步加載  即前端輸入內容過濾數據
     *
     * @param dictCode 字典code格式：table,text,code
     * @param keyword 過濾關鍵字
     * @return
     */
    List<DictModel> loadDictItemByKeyword(String dictCode, String keyword, Integer pageSize);

}
