package com.yaude.modules.system.service;

import com.yaude.modules.system.entity.SysDepartPermission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.entity.SysPermissionDataRule;

import java.util.List;

/**
 * @Description: 部門權限表
 * @Author: jeecg-boot
 * @Date:   2020-02-11
 * @Version: V1.0
 */
public interface ISysDepartPermissionService extends IService<SysDepartPermission> {
    /**
     * 保存授權 將上次的權限和這次作比較 差異處理提高效率
     * @param departId
     * @param permissionIds
     * @param lastPermissionIds
     */
    public void saveDepartPermission(String departId,String permissionIds,String lastPermissionIds);

    /**
     * 根據部門id，菜單id獲取數據規則
     * @param permissionId
     * @return
     */
    List<SysPermissionDataRule> getPermRuleListByDeptIdAndPermId(String departId,String permissionId);
}
