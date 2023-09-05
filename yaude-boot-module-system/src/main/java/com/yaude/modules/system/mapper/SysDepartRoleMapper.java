package com.yaude.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.yaude.modules.system.entity.SysDepartRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 部門角色
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
public interface SysDepartRoleMapper extends BaseMapper<SysDepartRole> {
    /**
     * 根據用戶id，部門id查詢可授權所有部門角色
     * @param orgCode
     * @param userId
     * @return
     */
    public List<SysDepartRole> queryDeptRoleByDeptAndUser(@Param("orgCode") String orgCode, @Param("userId") String userId);
}
