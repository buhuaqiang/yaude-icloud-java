package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.common.system.vo.DictModel;
import com.yaude.common.system.vo.DictQuery;
import com.yaude.modules.system.entity.SysDict;
import com.yaude.modules.system.entity.SysDictItem;
import com.yaude.modules.system.model.TreeSelectModel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服務類
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictService extends IService<SysDict> {

    public List<DictModel> queryDictItemsByCode(String code);

	/**
	 * 查詢有效的數據字典項
	 * @param code
	 * @return
	 */
	List<DictModel> queryEnableDictItemsByCode(String code);

	/**
	 * 通過多個字典code獲取字典數據
	 *
	 * @param dictCodeList
	 * @return key = 字典code，value=對應的字典選項
	 */
	Map<String, List<DictModel>> queryDictItemsByCodeList(List<String> dictCodeList);

    public Map<String,List<DictModel>> queryAllDictItems();

    @Deprecated
    List<DictModel> queryTableDictItemsByCode(String table, String text, String code);

    @Deprecated
	public List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql);

    public String queryDictTextByKey(String code, String key);

	/**
	 * 可通過多個字典code查詢翻譯文本
	 * @param dictCodeList 多個字典code
	 * @param keys 數據列表
	 * @return
	 */
	Map<String, List<DictModel>> queryManyDictByKeys(List<String> dictCodeList, List<String> keys);

    @Deprecated
	String queryTableDictTextByKey(String table, String text, String code, String key);

	/**
	 * 通過查詢指定table的 text code key 獲取字典值，可批量查詢
	 *
	 * @param table
	 * @param text
	 * @param code
	 * @param keys
	 * @return
	 */
	List<DictModel> queryTableDictTextByKeys(String table, String text, String code, List<String> keys);

	@Deprecated
	List<String> queryTableDictByKeys(String table, String text, String code, String keys);
	@Deprecated
	List<String> queryTableDictByKeys(String table, String text, String code, String keys,boolean delNotExist);

    /**
     * 根據字典類型刪除關聯表中其對應的數據
     *
     * @param sysDict
     * @return
     */
    boolean deleteByDictId(SysDict sysDict);

    /**
     * 添加一對多
     */
    public Integer saveMain(SysDict sysDict, List<SysDictItem> sysDictItemList);

    /**
	 * 查詢所有部門 作為字典信息 id -->value,departName -->text
	 * @return
	 */
	public List<DictModel> queryAllDepartBackDictModel();

	/**
	 * 查詢所有用戶  作為字典信息 username -->value,realname -->text
	 * @return
	 */
	public List<DictModel> queryAllUserBackDictModel();

	/**
	 * 通過關鍵字查詢字典表
	 * @param table
	 * @param text
	 * @param code
	 * @param keyword
	 * @return
	 */
	@Deprecated
	public List<DictModel> queryTableDictItems(String table, String text, String code,String keyword);

	/**
	 * 查詢字典表數據 只查詢前10條
	 * @param table
	 * @param text
	 * @param code
	 * @param keyword
	 * @return
	 */
	public List<DictModel> queryLittleTableDictItems(String table, String text, String code, String condition, String keyword, int pageSize);

	/**
	 * 查詢字典表所有數據
	 * @param table
	 * @param text
	 * @param code
	 * @param condition
	 * @param keyword
	 * @return
	 */
	public List<DictModel> queryAllTableDictItems(String table, String text, String code, String condition, String keyword);
	/**
	  * 根據表名、顯示字段名、存儲字段名 查詢樹
	 * @param table
	 * @param text
	 * @param code
	 * @param pidField
	 * @param pid
	 * @param hasChildField
	 * @return
	 */
	@Deprecated
	List<TreeSelectModel> queryTreeList(Map<String, String> query,String table, String text, String code, String pidField,String pid,String hasChildField);

	/**
	 * 真實刪除
	 * @param id
	 */
	public void deleteOneDictPhysically(String id);

	/**
	 * 修改delFlag
	 * @param delFlag
	 * @param id
	 */
	public void updateDictDelFlag(int delFlag,String id);

	/**
	 * 查詢被邏輯刪除的數據
	 * @return
	 */
	public List<SysDict> queryDeleteList();

	/**
	 * 分頁查詢
	 * @param query
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@Deprecated
	public List<DictModel> queryDictTablePageList(DictQuery query, int pageSize, int pageNo);

    /**
     * 獲取字典數據
     * @param dictCode 字典code
     * @param dictCode 表名,文本字段,code字段  | 舉例：sys_user,realname,id
     * @return
     */
    List<DictModel> getDictItems(String dictCode);

    /**
     * 【JSearchSelectTag下拉搜索組件專用接口】
     * 大數據量的字典表 走異步加載  即前端輸入內容過濾數據
     *
     * @param dictCode 字典code格式：table,text,code
     * @return
     */
    List<DictModel> loadDict(String dictCode, String keyword, Integer pageSize);

}
