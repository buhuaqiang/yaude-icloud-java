package com.yaude.modules.system.service;

import java.util.List;

import com.yaude.modules.system.entity.SysPermissionDataRule;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜單權限規則 服務類
 * </p>
 *
 * @Author huangzhilin
 * @since 2019-04-01
 */
public interface ISysPermissionDataRuleService extends IService<SysPermissionDataRule> {

	/**
	 * 根據菜單id查詢其對應的權限數據
	 * 
	 * @param permRule
	 */
	List<SysPermissionDataRule> getPermRuleListByPermId(String permissionId);

	/**
	 * 根據頁面傳遞的參數查詢菜單權限數據
	 * 
	 * @return
	 */
	List<SysPermissionDataRule> queryPermissionRule(SysPermissionDataRule permRule);
	
	
	/**
	  * 根據菜單ID和用戶名查找數據權限配置信息
	 * @param permission
	 * @param username
	 * @return
	 */
	List<SysPermissionDataRule> queryPermissionDataRules(String username,String permissionId);
	
	/**
	 * 新增菜單權限配置 修改菜單rule_flag
	 * @param sysPermissionDataRule
	 */
	public void savePermissionDataRule(SysPermissionDataRule sysPermissionDataRule);
	
	/**
	 * 刪除菜單權限配置 判斷菜單還有無權限
	 * @param dataRuleId
	 */
	public void deletePermissionDataRule(String dataRuleId);
	
	
}
