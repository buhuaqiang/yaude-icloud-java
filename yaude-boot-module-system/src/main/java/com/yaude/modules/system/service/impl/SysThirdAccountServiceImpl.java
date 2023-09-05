package com.yaude.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.util.DateUtils;
import com.yaude.common.util.PasswordUtil;
import com.yaude.common.util.UUIDGenerator;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysRole;
import com.yaude.modules.system.entity.SysThirdAccount;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.entity.SysUserRole;
import com.yaude.modules.system.mapper.SysRoleMapper;
import com.yaude.modules.system.mapper.SysThirdAccountMapper;
import com.yaude.modules.system.mapper.SysUserMapper;
import com.yaude.modules.system.mapper.SysUserRoleMapper;
import com.yaude.modules.system.model.ThirdLoginModel;
import com.yaude.modules.system.service.ISysThirdAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 第三方登錄賬號表
 * @Author: jeecg-boot
 * @Date:   2020-11-17
 * @Version: V1.0
 */
@Service
public class SysThirdAccountServiceImpl extends ServiceImpl<SysThirdAccountMapper, SysThirdAccount> implements ISysThirdAccountService {
    
    @Autowired
    private  SysThirdAccountMapper sysThirdAccountMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    
    @Override
    public void updateThirdUserId(SysUser sysUser,String thirdUserUuid) {
        //修改第三方登錄賬戶表使其進行添加用戶id
        LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
        query.eq(SysThirdAccount::getThirdUserUuid,thirdUserUuid);
        SysThirdAccount account = sysThirdAccountMapper.selectOne(query);
        SysThirdAccount sysThirdAccount = new SysThirdAccount();
        sysThirdAccount.setSysUserId(sysUser.getId());
        //根據當前用戶id和登錄方式查詢第三方登錄表
        LambdaQueryWrapper<SysThirdAccount> thirdQuery = new LambdaQueryWrapper<>();
        thirdQuery.eq(SysThirdAccount::getSysUserId,sysUser.getId());
        thirdQuery.eq(SysThirdAccount::getThirdType,account.getThirdType());
        SysThirdAccount sysThirdAccounts = sysThirdAccountMapper.selectOne(thirdQuery);
        if(sysThirdAccounts!=null){
            sysThirdAccount.setThirdUserId(sysThirdAccounts.getThirdUserId());
            sysThirdAccountMapper.deleteById(sysThirdAccounts.getId());
        }
        //更新用戶賬戶表sys_user_id
        sysThirdAccountMapper.update(sysThirdAccount,query);
    }
    
    @Override
    public SysUser createUser(String phone, String thirdUserUuid) {
       //先查詢第三方，獲取登錄方式
        LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
        query.eq(SysThirdAccount::getThirdUserUuid,thirdUserUuid);
        SysThirdAccount account = sysThirdAccountMapper.selectOne(query);
        //通過用戶名查詢數據庫是否已存在
        SysUser userByName = sysUserMapper.getUserByName(thirdUserUuid);
        if(null!=userByName){
            //如果賬號存在的話，則自動加上一個時間戳
            String format = DateUtils.yyyymmddhhmmss.get().format(new Date());
            thirdUserUuid = thirdUserUuid + format;
        }
        //添加用戶
        SysUser user = new SysUser();
        user.setActivitiSync(CommonConstant.ACT_SYNC_0);
        user.setDelFlag(CommonConstant.DEL_FLAG_0);
        user.setStatus(1);
        user.setUsername(thirdUserUuid);
        user.setPhone(phone);
        //設置初始密碼
        String salt = oConvertUtils.randomGen(8);
        user.setSalt(salt);
        String passwordEncode = PasswordUtil.encrypt(user.getUsername(), "123456", salt);
        user.setPassword(passwordEncode);
        user.setRealname(account.getRealname());
        user.setAvatar(account.getAvatar());
        String s = this.saveThirdUser(user);
        //更新用戶第三方賬戶表的userId
        SysThirdAccount sysThirdAccount = new SysThirdAccount();
        sysThirdAccount.setSysUserId(s);
        sysThirdAccountMapper.update(sysThirdAccount,query);
        return user;
    }
    
    public String saveThirdUser(SysUser sysUser) {
        //保存用戶
        String userid = UUIDGenerator.generate();
        sysUser.setId(userid);
        sysUserMapper.insert(sysUser);
        //獲取第三方角色
        SysRole sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, "third_role"));
        //保存用戶角色
        SysUserRole userRole = new SysUserRole();
        userRole.setRoleId(sysRole.getId());
        userRole.setUserId(userid);
        sysUserRoleMapper.insert(userRole);
        return userid;
    }

    @Override
    public SysThirdAccount getOneBySysUserId(String sysUserId, String thirdType) {
        LambdaQueryWrapper<SysThirdAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysThirdAccount::getSysUserId, sysUserId);
        queryWrapper.eq(SysThirdAccount::getThirdType, thirdType);
        return super.getOne(queryWrapper);
    }

    @Override
    public SysThirdAccount getOneByThirdUserId(String thirdUserId, String thirdType) {
        LambdaQueryWrapper<SysThirdAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysThirdAccount::getThirdUserId, thirdUserId);
        queryWrapper.eq(SysThirdAccount::getThirdType, thirdType);
        return super.getOne(queryWrapper);
    }

    @Override
    public List<SysThirdAccount> listThirdUserIdByUsername(String[] sysUsernameArr, String thirdType) {
        return sysThirdAccountMapper.selectThirdIdsByUsername(sysUsernameArr, thirdType);
    }

    @Override
    public SysThirdAccount saveThirdUser(ThirdLoginModel tlm) {
        SysThirdAccount user = new SysThirdAccount();
        user.setDelFlag(CommonConstant.DEL_FLAG_0);
        user.setStatus(1);
        user.setThirdType(tlm.getSource());
        user.setAvatar(tlm.getAvatar());
        user.setRealname(tlm.getUsername());
        user.setThirdUserUuid(tlm.getUuid());
        user.setThirdUserId(tlm.getUuid());
        super.save(user);
        return user;
    }

}
