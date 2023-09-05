package com.yaude.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.system.vo.DictModel;
import com.yaude.common.system.vo.DictModelMany;
import com.yaude.common.system.vo.DictQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.yaude.modules.system.entity.SysDict;
import com.yaude.modules.system.model.DuplicateCheckVo;
import com.yaude.modules.system.model.TreeSelectModel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface SysDictMapper extends BaseMapper<SysDict> {
	
	/**
	  *  重復檢查SQL
	 * @return
	 */
	@Deprecated
	public Long duplicateCheckCountSql(DuplicateCheckVo duplicateCheckVo);
	@Deprecated
	public Long duplicateCheckCountSqlNoDataId(DuplicateCheckVo duplicateCheckVo);
	
	public List<DictModel> queryDictItemsByCode(@Param("code") String code);

	/**
	 * 查詢有效的數據字典項
	 * @param code
	 * @return
	 */
	List<DictModel> queryEnableDictItemsByCode(@Param("code") String code);


	/**
	 * 通過多個字典code獲取字典數據
	 *
	 * @param dictCodeList
	 * @return
	 */
	public List<DictModelMany> queryDictItemsByCodeList(@Param("dictCodeList") List<String> dictCodeList);

	@Deprecated
	public List<DictModel> queryTableDictItemsByCode(@Param("table") String table,@Param("text") String text,@Param("code") String code);

	@Deprecated
	public List<DictModel> queryTableDictItemsByCodeAndFilter(@Param("table") String table,@Param("text") String text,@Param("code") String code,@Param("filterSql") String filterSql);

	@Deprecated
	@Select("select ${key} as \"label\",${value} as \"value\" from ${table}")
	public List<Map<String,String>> getDictByTableNgAlain(@Param("table") String table, @Param("key") String key, @Param("value") String value);

	public String queryDictTextByKey(@Param("code") String code,@Param("key") String key);

	/**
	 * 可通過多個字典code查詢翻譯文本
	 * @param dictCodeList 多個字典code
	 * @param keys 數據列表
	 * @return
	 */
	List<DictModelMany> queryManyDictByKeys(@Param("dictCodeList") List<String> dictCodeList, @Param("keys") List<String> keys);

	@Deprecated
	public String queryTableDictTextByKey(@Param("table") String table,@Param("text") String text,@Param("code") String code,@Param("key") String key);

	/**
	 * 通過查詢指定table的 text code key 獲取字典值，可批量查詢
	 *
	 * @param table
	 * @param text
	 * @param code
	 * @param keys
	 * @return
	 */
	@Deprecated
	List<DictModel> queryTableDictTextByKeys(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("keys") List<String> keys);

	@Deprecated
	public List<DictModel> queryTableDictByKeys(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("keyArray") String[] keyArray);

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
	 * 通過關鍵字查詢出字典表
	 * @param table
	 * @param text
	 * @param code
	 * @param keyword
	 * @return
	 */
	@Deprecated
	public List<DictModel> queryTableDictItems(@Param("table") String table,@Param("text") String text,@Param("code") String code,@Param("keyword") String keyword);


	/**
	 * 通過關鍵字查詢出字典表
	 * @param page
	 * @param table
	 * @param text
	 * @param code
	 * @param keyword
	 * @return
	 */
	IPage<DictModel> queryTableDictItems(Page<DictModel> page, @Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("keyword") String keyword);

	/**
	  * 根據表名、顯示字段名、存儲字段名 查詢樹
	 * @param table
	 * @param text
	 * @param code
	 * @param pid
	 * @param hasChildField
	 * @return
	 */
	@Deprecated
	List<TreeSelectModel> queryTreeList(@Param("query") Map<String, String> query,@Param("table") String table,@Param("text") String text,@Param("code") String code,@Param("pidField") String pidField,@Param("pid") String pid,@Param("hasChildField") String hasChildField);

	/**
	 * 刪除
	 * @param id
	 */
	@Select("delete from sys_dict where id = #{id}")
	public void deleteOneById(@Param("id") String id);

	/**
	 * 查詢被邏輯刪除的數據
	 * @return
	 */
	@Select("select * from sys_dict where del_flag = 1")
	public List<SysDict> queryDeleteList();

	/**
	 * 修改狀態值
	 * @param delFlag
	 * @param id
	 */
	@Update("update sys_dict set del_flag = #{flag,jdbcType=INTEGER} where id = #{id,jdbcType=VARCHAR}")
	public void updateDictDelFlag(@Param("flag") int delFlag, @Param("id") String id);


	/**
	 * 分頁查詢字典表數據
	 * @param page
	 * @param query
	 * @return
	 */
	@Deprecated
	public Page<DictModel> queryDictTablePageList(Page page, @Param("query") DictQuery query);


	/**
	 * 查詢 字典表數據 支持查詢條件 分頁
	 * @param page
	 * @param table
	 * @param text
	 * @param code
	 * @param filterSql
	 * @return
	 */
	@Deprecated
	IPage<DictModel> queryTableDictWithFilter(Page<DictModel> page, @Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("filterSql") String filterSql);

	/**
	 * 查詢 字典表數據 支持查詢條件 查詢所有
	 * @param table
	 * @param text
	 * @param code
	 * @param filterSql
	 * @return
	 */
	@Deprecated
	List<DictModel> queryAllTableDictItems(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("filterSql") String filterSql);
}
