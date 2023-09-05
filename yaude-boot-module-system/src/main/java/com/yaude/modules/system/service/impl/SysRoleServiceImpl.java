package com.yaude.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.ImportExcelUtil;
import com.yaude.modules.system.entity.SysRole;
import com.yaude.modules.system.mapper.SysRoleMapper;
import com.yaude.modules.system.mapper.SysUserMapper;
import com.yaude.modules.system.service.ISysRoleService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 角色表 服務實現類
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    @Autowired
    SysRoleMapper sysRoleMapper;
    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception {
        List<Object> listSysRoles = ExcelImportUtil.importExcel(file.getInputStream(), SysRole.class, params);
        int totalCount = listSysRoles.size();
        List<String> errorStrs = new ArrayList<>();

        // 去除 listSysRoles 中重復的數據
        for (int i = 0; i < listSysRoles.size(); i++) {
            String roleCodeI =((SysRole)listSysRoles.get(i)).getRoleCode();
            for (int j = i + 1; j < listSysRoles.size(); j++) {
                String roleCodeJ =((SysRole)listSysRoles.get(j)).getRoleCode();
                // 發現重復數據
                if (roleCodeI.equals(roleCodeJ)) {
                    errorStrs.add("第 " + (j + 1) + " 行的 roleCode 值：" + roleCodeI + " 已存在，忽略導入");
                    listSysRoles.remove(j);
                    break;
                }
            }
        }
        // 去掉 sql 中的重復數據
        Integer errorLines=0;
        Integer successLines=0;
        List<String> list = ImportExcelUtil.importDateSave(listSysRoles, ISysRoleService.class, errorStrs, CommonConstant.SQL_INDEX_UNIQ_SYS_ROLE_CODE);
         errorLines+=list.size();
         successLines+=(listSysRoles.size()-errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String roleid) {
        //1.刪除角色和用戶關系
        sysRoleMapper.deleteRoleUserRelation(roleid);
        //2.刪除角色和權限關系
        sysRoleMapper.deleteRolePermissionRelation(roleid);
        //3.刪除角色
        this.removeById(roleid);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchRole(String[] roleIds) {
        //1.刪除角色和用戶關系
        sysUserMapper.deleteBathRoleUserRelation(roleIds);
        //2.刪除角色和權限關系
        sysUserMapper.deleteBathRolePermissionRelation(roleIds);
        //3.刪除角色
        this.removeByIds(Arrays.asList(roleIds));
        return true;
    }
}
