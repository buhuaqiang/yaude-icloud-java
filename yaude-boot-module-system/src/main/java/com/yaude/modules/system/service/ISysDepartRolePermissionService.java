package com.yaude.modules.system.service;

import com.yaude.modules.system.entity.SysDepartRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 部門角色權限
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
public interface ISysDepartRolePermissionService extends IService<SysDepartRolePermission> {
    /**
     * 保存授權 將上次的權限和這次作比較 差異處理提高效率
     * @param roleId
     * @param permissionIds
     * @param lastPermissionIds
     */
    public void saveDeptRolePermission(String roleId,String permissionIds,String lastPermissionIds);
}
