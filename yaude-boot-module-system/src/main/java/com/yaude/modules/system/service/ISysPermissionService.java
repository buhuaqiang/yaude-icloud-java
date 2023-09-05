package com.yaude.modules.system.service;

import java.util.List;

import com.yaude.common.exception.JeecgBootException;
import com.yaude.modules.system.entity.SysPermission;
import com.yaude.modules.system.model.TreeModel;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜單權限表 服務類
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysPermissionService extends IService<SysPermission> {
	
	public List<TreeModel> queryListByParentId(String parentId);
	
	/**真實刪除*/
	public void deletePermission(String id) throws JeecgBootException;
	/**邏輯刪除*/
	public void deletePermissionLogical(String id) throws JeecgBootException;
	
	public void addPermission(SysPermission sysPermission) throws JeecgBootException;
	
	public void editPermission(SysPermission sysPermission) throws JeecgBootException;
	
	public List<SysPermission> queryByUser(String username);
	
	/**
	 * 根據permissionId刪除其關聯的SysPermissionDataRule表中的數據
	 * 
	 * @param id
	 * @return
	 */
	public void deletePermRuleByPermId(String id);
	
	/**
	  * 查詢出帶有特殊符號的菜單地址的集合
	 * @return
	 */
	public List<String> queryPermissionUrlWithStar();

	/**
	 * 判斷用戶否擁有權限
	 * @param username
	 * @param sysPermission
	 * @return
	 */
	public boolean hasPermission(String username, SysPermission sysPermission);

	/**
	 * 根據用戶和請求地址判斷是否有此權限
	 * @param username
	 * @param url
	 * @return
	 */
	public boolean hasPermission(String username, String url);
}
