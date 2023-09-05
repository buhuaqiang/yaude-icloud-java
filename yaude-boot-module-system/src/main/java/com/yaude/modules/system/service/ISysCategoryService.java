package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.common.exception.JeecgBootException;
import com.yaude.modules.system.entity.SysCategory;
import com.yaude.modules.system.model.TreeSelectModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: 分類字典
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
public interface ISysCategoryService extends IService<SysCategory> {

	/**根節點父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	void addSysCategory(SysCategory sysCategory);
	
	void updateSysCategory(SysCategory sysCategory);
	
	/**
	  * 根據父級編碼加載分類字典的數據
	 * @param pcode
	 * @return
	 */
	public List<TreeSelectModel> queryListByCode(String pcode) throws JeecgBootException;
	
	/**
	  * 根據pid查詢子節點集合
	 * @param pid
	 * @return
	 */
	public List<TreeSelectModel> queryListByPid(String pid);

	/**
	 * 根據pid查詢子節點集合,支持查詢條件
	 * @param pid
	 * @param condition
	 * @return
	 */
	public List<TreeSelectModel> queryListByPid(String pid, Map<String,String> condition);

	/**
	 * 根據code查詢id
	 * @param code
	 * @return
	 */
	public String queryIdByCode(String code);

	/**
	 * 刪除節點時同時刪除子節點及修改父級節點
	 * @param ids
	 */
	void deleteSysCategory(String ids);

	/**
	 * 分類字典控件數據回顯[表單頁面]
	 *
	 * @param ids
	 * @return
	 */
	List<String> loadDictItem(String ids);

	/**
	 * 分類字典控件數據回顯[表單頁面]
	 *
	 * @param ids
	 * @param delNotExist 是否移除不存在的項，設為false如果某個key不存在數據庫中，則直接返回key本身
	 * @return
	 */
	List<String> loadDictItem(String ids, boolean delNotExist);

}
