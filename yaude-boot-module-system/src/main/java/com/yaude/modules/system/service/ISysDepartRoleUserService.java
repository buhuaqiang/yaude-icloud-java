package com.yaude.modules.system.service;

import com.yaude.modules.system.entity.SysDepartRoleUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 部門角色人員信息
 * @Author: jeecg-boot
 * @Date:   2020-02-13
 * @Version: V1.0
 */
public interface ISysDepartRoleUserService extends IService<SysDepartRoleUser> {

    void deptRoleUserAdd(String userId,String newRoleId,String oldRoleId);

    /**
     * 取消用戶與部門關聯，刪除關聯關系
     * @param userIds
     * @param depId
     */
    void removeDeptRoleUser(List<String> userIds,String depId);
}
