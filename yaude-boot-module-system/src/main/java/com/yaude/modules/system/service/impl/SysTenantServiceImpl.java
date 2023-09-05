package com.yaude.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.exception.JeecgBootException;
import lombok.extern.slf4j.Slf4j;
import com.yaude.modules.system.entity.SysTenant;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.mapper.SysTenantMapper;
import com.yaude.modules.system.service.ISysTenantService;
import com.yaude.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("sysTenantServiceImpl")
@Slf4j
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {

    @Autowired
    ISysUserService userService;

    @Override
    public List<SysTenant> queryEffectiveTenant(Collection<String> idList) {
        LambdaQueryWrapper<SysTenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysTenant::getId, idList);
        queryWrapper.eq(SysTenant::getStatus, CommonConstant.STATUS_1);
        //此處查詢忽略時間條件
        return super.list(queryWrapper);
    }

    @Override
    public int countUserLinkTenant(String id) {
        LambdaQueryWrapper<SysUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(SysUser::getRelTenantIds, id);
        userQueryWrapper.or().like(SysUser::getRelTenantIds, "%," + id);
        userQueryWrapper.or().like(SysUser::getRelTenantIds, id + ",%");
        userQueryWrapper.or().like(SysUser::getRelTenantIds, "%," + id + ",%");
        // 查找出已被關聯的用戶數量
        return userService.count(userQueryWrapper);
    }

    @Override
    public boolean removeTenantById(String id) {
        // 查找出已被關聯的用戶數量
        int userCount = this.countUserLinkTenant(id);
        if (userCount > 0) {
            throw new JeecgBootException("該租戶已被引用，無法刪除！");
        }
        return super.removeById(id);
    }

}
