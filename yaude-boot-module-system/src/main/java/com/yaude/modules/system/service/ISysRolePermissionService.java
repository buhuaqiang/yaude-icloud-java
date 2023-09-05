package com.yaude.modules.system.service;

import com.yaude.modules.system.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色權限表 服務類
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {
	
	/**
	 * 保存授權/先刪后增
	 * @param roleId
	 * @param permissionIds
	 */
	public void saveRolePermission(String roleId,String permissionIds);
	
	/**
	 * 保存授權 將上次的權限和這次作比較 差異處理提高效率 
	 * @param roleId
	 * @param permissionIds
	 * @param lastPermissionIds
	 */
	public void saveRolePermission(String roleId,String permissionIds,String lastPermissionIds);

}
