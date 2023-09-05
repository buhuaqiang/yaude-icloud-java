package com.yaude.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeecg.qywx.api.base.JwAccessTokenAPI;
import com.jeecg.qywx.api.core.common.AccessToken;
import com.jeecg.qywx.api.department.JwDepartmentAPI;
import com.jeecg.qywx.api.department.vo.DepartMsgResponse;
import com.jeecg.qywx.api.department.vo.Department;
import com.jeecg.qywx.api.message.JwMessageAPI;
import com.jeecg.qywx.api.message.vo.Text;
import com.jeecg.qywx.api.message.vo.TextCard;
import com.jeecg.qywx.api.message.vo.TextCardEntity;
import com.jeecg.qywx.api.message.vo.TextEntity;
import com.jeecg.qywx.api.user.JwUserAPI;
import com.jeecg.qywx.api.user.vo.User;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.config.thirdapp.ThirdAppConfig;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import com.yaude.common.api.dto.message.MessageDTO;
import com.yaude.common.util.PasswordUtil;
import com.yaude.common.util.RestUtil;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.mapper.SysAnnouncementSendMapper;
import com.yaude.modules.system.model.SysDepartTreeModel;
import com.yaude.modules.system.model.ThirdLoginModel;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.vo.thirdapp.JwDepartmentTreeVo;
import com.yaude.modules.system.vo.thirdapp.SyncInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 第三方App對接：企業微信實現類
 */
@Slf4j
@Service
public class ThirdAppWechatEnterpriseServiceImpl implements IThirdAppService {

    @Autowired
    ThirdAppConfig thirdAppConfig;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysThirdAccountService sysThirdAccountService;
    @Autowired
    private ISysUserDepartService sysUserDepartService;
    @Autowired
    private ISysPositionService sysPositionService;
    @Autowired
    private SysAnnouncementSendMapper sysAnnouncementSendMapper;

    // 第三方APP類型，當前固定為 wechat_enterprise
    public final String THIRD_TYPE = ThirdAppConfig.WECHAT_ENTERPRISE.toLowerCase();

    @Override
    public String getAccessToken() {
        String CORP_ID = thirdAppConfig.getWechatEnterprise().getClientId();
        String SECRET = thirdAppConfig.getWechatEnterprise().getClientSecret();
        AccessToken accessToken = JwAccessTokenAPI.getAccessToken(CORP_ID, SECRET);
        if (accessToken != null) {
            return accessToken.getAccesstoken();
        }
        log.warn("獲取AccessToken失敗");
        return null;
    }

    /** 獲取APPToken，新版企業微信的秘鑰是分開的 */
    public String getAppAccessToken() {
        String CORP_ID = thirdAppConfig.getWechatEnterprise().getClientId();
        String SECRET = thirdAppConfig.getWechatEnterprise().getAgentAppSecret();
        // 如果沒有配置APP秘鑰，就說明是老企業，可以通用秘鑰
        if (oConvertUtils.isEmpty(SECRET)) {
            SECRET = thirdAppConfig.getWechatEnterprise().getClientSecret();
        }

        AccessToken accessToken = JwAccessTokenAPI.getAccessToken(CORP_ID, SECRET);
        if (accessToken != null) {
            return accessToken.getAccesstoken();
        }
        log.warn("獲取AccessToken失敗");
        return null;
    }

    @Override
    public boolean syncLocalDepartmentToThirdApp(String ids) {
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            return false;
        }
        // 獲取企業微信所有的部門
        List<Department> departments = JwDepartmentAPI.getAllDepartment(accessToken);
        if (departments == null) {
            return false;
        }
        // 刪除企業微信有但本地沒有的部門（以本地部門數據為主）(以為企業微信不能創建同名部門，所以只能先刪除）
        List<JwDepartmentTreeVo> departmentTreeList = JwDepartmentTreeVo.listToTree(departments);
        this.deleteDepartRecursion(departmentTreeList, accessToken, true);
        // 獲取本地所有部門樹結構
        List<SysDepartTreeModel> sysDepartsTree = sysDepartService.queryTreeList();
        // -- 企業微信不能創建新的頂級部門，所以新的頂級部門的parentId就為1
        Department parent = new Department();
        parent.setId("1");
        // 遞歸同步部門
        departments = JwDepartmentAPI.getAllDepartment(accessToken);
        this.syncDepartmentRecursion(sysDepartsTree, departments, parent, accessToken);
        return true;
    }

    // 遞歸刪除部門以及子部門，由于企業微信不允許刪除帶有成員和子部門的部門，所以需要遞歸刪除下子部門，然后把部門成員移動端根部門下
    private void deleteDepartRecursion(List<JwDepartmentTreeVo> children, String accessToken, boolean ifLocal) {
        for (JwDepartmentTreeVo departmentTree : children) {
            String depId = departmentTree.getId();
            // 過濾根部門
            if (!"1".equals(depId)) {
                // 判斷本地是否有該部門
                if (ifLocal) {
                    LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(SysDepart::getQywxIdentifier, depId);
                    SysDepart sysDepart = sysDepartService.getOne(queryWrapper);
                    // 本地有該部門，不刪除
                    if (sysDepart != null) {
                        if (departmentTree.hasChildren()) {
                            this.deleteDepartRecursion(departmentTree.getChildren(), accessToken, true);
                        }
                        continue;
                    }
                }
                // 判斷是否有成員，有就移動到根部門
                List<User> departUserList = JwUserAPI.getUsersByDepartid(depId, "1", null, accessToken);
                if (departUserList != null && departUserList.size() > 0) {
                    for (User user : departUserList) {
                        User updateUser = new User();
                        updateUser.setUserid(user.getUserid());
                        updateUser.setDepartment(new Integer[]{1});
                        JwUserAPI.updateUser(updateUser, accessToken);
                    }
                }
                // 有子部門優先刪除子部門
                if (departmentTree.hasChildren()) {
                    this.deleteDepartRecursion(departmentTree.getChildren(), accessToken, false);
                }
                // 執行刪除操作
                JwDepartmentAPI.deleteDepart(depId, accessToken);
            }
        }
    }

    // 遞歸同步部門到第三方APP
    private void syncDepartmentRecursion(List<SysDepartTreeModel> sysDepartsTree, List<Department> departments, Department parent, String accessToken) {
        if (sysDepartsTree != null && sysDepartsTree.size() != 0) {
            for1:
            for (SysDepartTreeModel depart : sysDepartsTree) {
                for (Department department : departments) {
                    // id相同，代表已存在，執行修改操作
                    if (department.getId().equals(depart.getQywxIdentifier())) {
                        this.sysDepartToQwDepartment(depart, department, parent.getId());
                        JwDepartmentAPI.updateDepart(department, accessToken);
                        // 緊接著同步子級
                        this.syncDepartmentRecursion(depart.getChildren(), departments, department, accessToken);
                        // 跳出外部循環
                        continue for1;
                    }
                }
                // 循環到此說明是新部門，直接調接口創建
                Department newDepartment = this.sysDepartToQwDepartment(depart, parent.getId());
                DepartMsgResponse response = JwDepartmentAPI.createDepartment(newDepartment, accessToken);
                // 創建成功，將返回的id綁定到本地
                if (response != null && response.getId() != null) {
                    SysDepart sysDepart = new SysDepart();
                    sysDepart.setId(depart.getId());
                    sysDepart.setQywxIdentifier(response.getId().toString());
                    sysDepartService.updateById(sysDepart);
                    Department newParent = new Department();
                    newParent.setId(response.getId().toString());
                    // 緊接著同步子級
                    this.syncDepartmentRecursion(depart.getChildren(), departments, newParent, accessToken);
                }
                // 收集錯誤信息
//                this.syncUserCollectErrInfo(errCode, sysUser, errInfo);
            }
        }
    }

    @Override
    public SyncInfoVo syncThirdAppDepartmentToLocal(String ids) {
        SyncInfoVo syncInfo = new SyncInfoVo();
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            syncInfo.addFailInfo("accessToken獲取失敗！");
            return syncInfo;
        }
        // 獲取企業微信所有的部門
        List<Department> departments = JwDepartmentAPI.getAllDepartment(accessToken);
        if (departments == null) {
            syncInfo.addFailInfo("企業微信部門信息獲取失敗！");
            return syncInfo;
        }
        String username = JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest());
        // 將list轉為tree
        List<JwDepartmentTreeVo> departmentTreeList = JwDepartmentTreeVo.listToTree(departments);
        // 遞歸同步部門
        this.syncDepartmentToLocalRecursion(departmentTreeList, null, username, syncInfo);
        return syncInfo;
    }

    /**
     * 遞歸同步部門到本地
     */
    private void syncDepartmentToLocalRecursion(List<JwDepartmentTreeVo> departmentTreeList, String sysParentId, String username, SyncInfoVo syncInfo) {
        if (departmentTreeList != null && departmentTreeList.size() != 0) {
            for (JwDepartmentTreeVo departmentTree : departmentTreeList) {
                String depId = departmentTree.getId();
                LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
                // 根據 qywxIdentifier 字段查詢
                queryWrapper.eq(SysDepart::getQywxIdentifier, depId);
                SysDepart sysDepart = sysDepartService.getOne(queryWrapper);
                if (sysDepart != null) {
                    //  執行更新操作
                    SysDepart updateSysDepart = this.qwDepartmentToSysDepart(departmentTree, sysDepart);
                    if (sysParentId != null) {
                        updateSysDepart.setParentId(sysParentId);
                    }
                    try {
                        sysDepartService.updateDepartDataById(updateSysDepart, username);
                        String str = String.format("部門 %s 更新成功！", updateSysDepart.getDepartName());
                        syncInfo.addSuccessInfo(str);
                    } catch (Exception e) {
                        this.syncDepartCollectErrInfo(e, departmentTree, syncInfo);
                    }
                    if (departmentTree.hasChildren()) {
                        // 緊接著同步子級
                        this.syncDepartmentToLocalRecursion(departmentTree.getChildren(), updateSysDepart.getId(), username, syncInfo);
                    }
                } else {
                    // 執行新增操作
                    SysDepart newSysDepart = this.qwDepartmentToSysDepart(departmentTree, null);
                    if (sysParentId != null) {
                        newSysDepart.setParentId(sysParentId);
                    }
                    try {
                        sysDepartService.saveDepartData(newSysDepart, username);
                        String str = String.format("部門 %s 創建成功！", newSysDepart.getDepartName());
                        syncInfo.addSuccessInfo(str);
                    } catch (Exception e) {
                        this.syncDepartCollectErrInfo(e, departmentTree, syncInfo);
                    }
                    // 緊接著同步子級
                    if (departmentTree.hasChildren()) {
                        this.syncDepartmentToLocalRecursion(departmentTree.getChildren(), newSysDepart.getId(), username, syncInfo);
                    }
                }
            }
        }
    }

    @Override
    public SyncInfoVo syncLocalUserToThirdApp(String ids) {
        SyncInfoVo syncInfo = new SyncInfoVo();
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            syncInfo.addFailInfo("accessToken獲取失敗！");
            return syncInfo;
        }
        // 獲取企業微信所有的用戶
        List<User> qwUsers = JwUserAPI.getDetailUsersByDepartid("1", null, null, accessToken);
        if (qwUsers == null) {
            syncInfo.addFailInfo("企業微信用戶列表查詢失敗！");
            return syncInfo;
        }
        List<SysUser> sysUsers;
        if (StringUtils.isNotBlank(ids)) {
            String[] idList = ids.split(",");
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysUser::getId, (Object[]) idList);
            // 獲取本地指定用戶
            sysUsers = sysUserService.list(queryWrapper);
        } else {
            // 獲取本地所有用戶
            sysUsers = sysUserService.list();
        }

        // 循環判斷新用戶和需要更新的用戶
        for1:
        for (SysUser sysUser : sysUsers) {
            // 外部模擬登陸臨時賬號，不同步
            if ("_reserve_user_external".equals(sysUser.getUsername())) {
                continue;
            }
            /*
             * 判斷是否同步過的邏輯：
             * 1. 查詢 sys_third_account（第三方賬號表）是否有數據，如果有代表已同步
             * 2. 本地表里沒有，就先用手機號判斷，不通過再用username判斷。
             */
            User qwUser;
            SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneBySysUserId(sysUser.getId(), THIRD_TYPE);
            for (User qwUserTemp : qwUsers) {
                if (sysThirdAccount == null || oConvertUtils.isEmpty(sysThirdAccount.getThirdUserId()) || !sysThirdAccount.getThirdUserId().equals(qwUserTemp.getUserid())) {
                    // sys_third_account 表匹配失敗，嘗試用手機號匹配
                    String phone = sysUser.getPhone();
                    if (!(oConvertUtils.isEmpty(phone) || phone.equals(qwUserTemp.getMobile()))) {
                        // 手機號匹配失敗，再嘗試用username匹配
                        String username = sysUser.getUsername();
                        if (!(oConvertUtils.isEmpty(username) || username.equals(qwUserTemp.getUserid()))) {
                            // username 匹配失敗，直接跳到下一次循環繼續
                            continue;
                        }
                    }
                }
                // 循環到此說明用戶匹配成功，進行更新操作
                qwUser = this.sysUserToQwUser(sysUser, qwUserTemp);
                int errCode = JwUserAPI.updateUser(qwUser, accessToken);
                // 收集錯誤信息
                this.syncUserCollectErrInfo(errCode, sysUser, syncInfo);
                this.thirdAccountSaveOrUpdate(sysThirdAccount, sysUser.getId(), qwUser.getUserid());
                // 更新完成，直接跳到下一次外部循環繼續
                continue for1;
            }
            // 循環到此說明是新用戶，直接調接口創建
            qwUser = this.sysUserToQwUser(sysUser);
            int errCode = JwUserAPI.createUser(qwUser, accessToken);
            // 收集錯誤信息
            boolean apiSuccess = this.syncUserCollectErrInfo(errCode, sysUser, syncInfo);
            if (apiSuccess) {
                this.thirdAccountSaveOrUpdate(sysThirdAccount, sysUser.getId(), qwUser.getUserid());
            }
        }
        return syncInfo;
    }

    @Override
    public SyncInfoVo syncThirdAppUserToLocal() {
        SyncInfoVo syncInfo = new SyncInfoVo();
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            syncInfo.addFailInfo("accessToken獲取失敗！");
            return syncInfo;
        }
        // 獲取企業微信所有的用戶
        List<User> qwUsersList = JwUserAPI.getDetailUsersByDepartid("1", null, null, accessToken);
        if (qwUsersList == null) {
            syncInfo.addFailInfo("企業微信用戶列表查詢失敗！");
            return syncInfo;
        }
        //查詢本地用戶
        List<SysUser> sysUsersList = sysUserService.list();
        // 循環判斷新用戶和需要更新的用戶
        for (User qwUser : qwUsersList) {
            /*
             * 判斷是否同步過的邏輯：
             * 1. 查詢 sys_third_account（第三方賬號表）是否有數據，如果有代表已同步
             * 2. 本地表里沒有，就先用手機號判斷，不通過再用username判斷。
             */
            SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneByThirdUserId(qwUser.getUserid(), THIRD_TYPE);
            List<SysUser> collect = sysUsersList.stream().filter(user -> (qwUser.getMobile().equals(user.getPhone()) || qwUser.getUserid().equals(user.getUsername()))
                                                                ).collect(Collectors.toList());

            if (collect != null && collect.size() > 0) {
                SysUser sysUserTemp = collect.get(0);
                // 循環到此說明用戶匹配成功，進行更新操作
                SysUser updateSysUser = this.qwUserToSysUser(qwUser, sysUserTemp);
                try {
                    sysUserService.updateById(updateSysUser);
                    String str = String.format("用戶 %s(%s) 更新成功！", updateSysUser.getRealname(), updateSysUser.getUsername());
                    syncInfo.addSuccessInfo(str);
                } catch (Exception e) {
                    this.syncUserCollectErrInfo(e, qwUser, syncInfo);
                }

                this.thirdAccountSaveOrUpdate(sysThirdAccount, updateSysUser.getId(), qwUser.getUserid());
                // 更新完成，直接跳到下一次外部循環繼續
            }else{
                // 沒匹配到用戶則走新增邏輯
                SysUser newSysUser = this.qwUserToSysUser(qwUser);
                try {
                    sysUserService.save(newSysUser);
                    String str = String.format("用戶 %s(%s) 創建成功！", newSysUser.getRealname(), newSysUser.getUsername());
                    syncInfo.addSuccessInfo(str);
                } catch (Exception e) {
                    this.syncUserCollectErrInfo(e, qwUser, syncInfo);
                }
                this.thirdAccountSaveOrUpdate(sysThirdAccount, newSysUser.getId(), qwUser.getUserid());
            }
        }
        return syncInfo;
    }

    /**
     * 保存或修改第三方登錄表
     *
     * @param sysThirdAccount 第三方賬戶表對象，為null就新增數據，否則就修改
     * @param sysUserId       本地系統用戶ID
     * @param qwUserId        企業微信用戶ID
     */
    private void thirdAccountSaveOrUpdate(SysThirdAccount sysThirdAccount, String sysUserId, String qwUserId) {
        if (sysThirdAccount == null) {
            sysThirdAccount = new SysThirdAccount();
            sysThirdAccount.setSysUserId(sysUserId);
            sysThirdAccount.setStatus(1);
            sysThirdAccount.setDelFlag(0);
            sysThirdAccount.setThirdType(THIRD_TYPE);
        }
        sysThirdAccount.setThirdUserId(qwUserId);
        sysThirdAccountService.saveOrUpdate(sysThirdAccount);
    }

    /**
     * 【同步用戶】收集同步過程中的錯誤信息
     */
    private boolean syncUserCollectErrInfo(int errCode, SysUser sysUser, SyncInfoVo syncInfo) {
        if (errCode != 0) {
            String msg = "";
            // https://open.work.weixin.qq.com/api/doc/90000/90139/90313
            switch (errCode) {
                case 40003:
                    msg = "無效的UserID";
                    break;
                case 60129:
                    msg = "手機和郵箱不能都為空";
                    break;
                case 60102:
                    msg = "UserID已存在";
                    break;
                case 60103:
                    msg = "手機號碼不合法";
                    break;
                case 60104:
                    msg = "手機號碼已存在";
                    break;
            }
            String str = String.format("用戶 %s(%s) 同步失敗！錯誤碼：%s——%s", sysUser.getUsername(), sysUser.getRealname(), errCode, msg);
            syncInfo.addFailInfo(str);
            return false;
        } else {
            String str = String.format("用戶 %s(%s) 同步成功！", sysUser.getUsername(), sysUser.getRealname());
            syncInfo.addSuccessInfo(str);
            return true;
        }
    }

    private boolean syncUserCollectErrInfo(Exception e, User qwUser, SyncInfoVo syncInfo) {
        String msg;
        if (e instanceof DuplicateKeyException) {
            msg = e.getCause().getMessage();
        } else {
            msg = e.getMessage();
        }
        String str = String.format("用戶 %s(%s) 同步失敗！錯誤信息：%s", qwUser.getUserid(), qwUser.getName(), msg);
        syncInfo.addFailInfo(str);
        return false;
    }

    private boolean syncDepartCollectErrInfo(Exception e, Department department, SyncInfoVo syncInfo) {
        String msg;
        if (e instanceof DuplicateKeyException) {
            msg = e.getCause().getMessage();
        } else {
            msg = e.getMessage();
        }
        String str = String.format("部門 %s(%s) 同步失敗！錯誤信息：%s", department.getName(), department.getId(), msg);
        syncInfo.addFailInfo(str);
        return false;
    }

    /**
     * 【同步用戶】將SysUser轉為企業微信的User對象（創建新用戶）
     */
    private User sysUserToQwUser(SysUser sysUser) {
        User user = new User();
        // 通過 username 來關聯
        user.setUserid(sysUser.getUsername());
        return this.sysUserToQwUser(sysUser, user);
    }

    /**
     * 【同步用戶】將SysUser轉為企業微信的User對象（更新舊用戶）
     */
    private User sysUserToQwUser(SysUser sysUser, User user) {
        user.setName(sysUser.getRealname());
        user.setMobile(sysUser.getPhone());
        // 查詢并同步用戶部門關系
        List<SysDepart> departList = this.getUserDepart(sysUser);
        if (departList != null) {
            List<Integer> departmentIdList = new ArrayList<>();
            // 企業微信 1表示為上級，0表示非上級
            List<Integer> isLeaderInDept = new ArrayList<>();
            // 當前用戶管理的部門
            List<String> manageDepartIdList = new ArrayList<>();
            if (oConvertUtils.isNotEmpty(sysUser.getDepartIds())) {
                manageDepartIdList = Arrays.asList(sysUser.getDepartIds().split(","));
            }
            for (SysDepart sysDepart : departList) {
                // 企業微信的部門id
                if (oConvertUtils.isNotEmpty(sysDepart.getQywxIdentifier())) {
                    try {
                        departmentIdList.add(Integer.parseInt(sysDepart.getQywxIdentifier()));
                    } catch (NumberFormatException ignored) {
                        continue;
                    }
                    // 判斷用戶身份，是否為上級
                    if (CommonConstant.USER_IDENTITY_2.equals(sysUser.getUserIdentity())) {
                        // 判斷當前部門是否為該用戶管理的部門
                        isLeaderInDept.add(manageDepartIdList.contains(sysDepart.getId()) ? 1 : 0);
                    } else {
                        isLeaderInDept.add(0);
                    }
                }
            }
            user.setDepartment(departmentIdList.toArray(new Integer[]{}));
            // 個數必須和參數department的個數一致，表示在所在的部門內是否為上級。1表示為上級，0表示非上級。在審批等應用里可以用來標識上級審批人
            user.setIs_leader_in_dept(isLeaderInDept.toArray(new Integer[]{}));
        }
        if (user.getDepartment() == null || user.getDepartment().length == 0) {
            // 沒有找到匹配部門，同步到根部門下
            user.setDepartment(new Integer[]{1});
            user.setIs_leader_in_dept(new Integer[]{0});
        }
        // 職務翻譯
        if (oConvertUtils.isNotEmpty(sysUser.getPost())) {
            SysPosition position = sysPositionService.getByCode(sysUser.getPost());
            if (position != null) {
                user.setPosition(position.getName());
            }
        }
        if (sysUser.getSex() != null) {
            user.setGender(sysUser.getSex().toString());
        }
        user.setEmail(sysUser.getEmail());
        // 啟用/禁用成員（狀態），規則不同，需要轉換
        // 企業微信規則：1表示啟用成員，0表示禁用成員
        // JEECG規則：1正常，2凍結
        if (sysUser.getStatus() != null) {
            if (sysUser.getStatus() == 1 || sysUser.getStatus() == 2) {
                user.setEnable(sysUser.getStatus() == 1 ? 1 : 0);
            } else {
                user.setEnable(1);
            }
        }
        user.setTelephone(sysUser.getTelephone());// 座機號
        // --- 企業微信沒有邏輯刪除的功能
        // update-begin--Author:sunjianlei Date:20210520 for：本地邏輯刪除的用戶，在企業微信里禁用 -----
        if (CommonConstant.DEL_FLAG_1.equals(sysUser.getDelFlag())) {
            user.setEnable(0);
        }
        // update-end--Author:sunjianlei Date:20210520 for：本地邏輯刪除的用戶，在企業微信里凍結 -----

        return user;
    }

    /**
     * 查詢用戶和部門的關系
     */
    private List<SysDepart> getUserDepart(SysUser sysUser) {
        // 根據用戶部門關系表查詢出用戶的部門
        LambdaQueryWrapper<SysUserDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserDepart::getUserId, sysUser.getId());
        List<SysUserDepart> sysUserDepartList = sysUserDepartService.list(queryWrapper);
        if (sysUserDepartList.size() == 0) {
            return null;
        }
        // 根據用戶部門
        LambdaQueryWrapper<SysDepart> departQueryWrapper = new LambdaQueryWrapper<>();
        List<String> departIdList = sysUserDepartList.stream().map(SysUserDepart::getDepId).collect(Collectors.toList());
        departQueryWrapper.in(SysDepart::getId, departIdList);
        List<SysDepart> departList = sysDepartService.list(departQueryWrapper);
        return departList.size() == 0 ? null : departList;
    }

    /**
     * 【同步用戶】將企業微信的User對象轉為SysUser（創建新用戶）
     */
    private SysUser qwUserToSysUser(User user) {
        SysUser sysUser = new SysUser();
        sysUser.setDelFlag(0);
        sysUser.setStatus(1);
        // 通過 username 來關聯
        sysUser.setUsername(user.getUserid());
        // 密碼默認為 “123456”，隨機加鹽
        String password = "123456", salt = oConvertUtils.randomGen(8);
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
        sysUser.setSalt(salt);
        sysUser.setPassword(passwordEncode);
        return this.qwUserToSysUser(user, sysUser);
    }

    /**
     * 【同步用戶】將企業微信的User對象轉為SysUser（更新舊用戶）
     */
    private SysUser qwUserToSysUser(User qwUser, SysUser oldSysUser) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(oldSysUser, sysUser);
        sysUser.setRealname(qwUser.getName());
        sysUser.setPost(qwUser.getPosition());
        try {
            sysUser.setSex(Integer.parseInt(qwUser.getGender()));
        } catch (NumberFormatException ignored) {
        }
        // 因為唯一鍵約束的原因，如果原數據和舊數據相同，就不更新
        if (oConvertUtils.isNotEmpty(qwUser.getEmail()) && !qwUser.getEmail().equals(sysUser.getEmail())) {
            sysUser.setEmail(qwUser.getEmail());
        } else {
            sysUser.setEmail(null);
        }
        // 因為唯一鍵約束的原因，如果原數據和舊數據相同，就不更新
        if (oConvertUtils.isNotEmpty(qwUser.getMobile()) && !qwUser.getMobile().equals(sysUser.getPhone())) {
            sysUser.setPhone(qwUser.getMobile());
        } else {
            sysUser.setPhone(null);
        }

        // 啟用/禁用成員（狀態），規則不同，需要轉換
        // 企業微信規則：1表示啟用成員，0表示禁用成員
        // JEECG規則：1正常，2凍結
        if (qwUser.getEnable() != null) {
            sysUser.setStatus(qwUser.getEnable() == 1 ? 1 : 2);
        }
        sysUser.setTelephone(qwUser.getTelephone());// 座機號

        // --- 企業微信沒有邏輯刪除的功能
        // sysUser.setDelFlag()
        return sysUser;
    }

    /**
     * 【同步部門】將SysDepartTreeModel轉為企業微信的Department對象（創建新部門）
     */
    private Department sysDepartToQwDepartment(SysDepartTreeModel departTree, String parentId) {
        Department department = new Department();
        return this.sysDepartToQwDepartment(departTree, department, parentId);
    }

    /**
     * 【同步部門】將SysDepartTreeModel轉為企業微信的Department對象
     */
    private Department sysDepartToQwDepartment(SysDepartTreeModel departTree, Department department, String parentId) {
        department.setName(departTree.getDepartName());
        department.setParentid(parentId);
        if (departTree.getDepartOrder() != null) {
            department.setOrder(departTree.getDepartOrder().toString());
        }
        return department;
    }


    /**
     * 【同步部門】將企業微信的Department對象轉為SysDepart
     */
    private SysDepart qwDepartmentToSysDepart(Department department, SysDepart oldSysDepart) {
        SysDepart sysDepart = new SysDepart();
        if (oldSysDepart != null) {
            BeanUtils.copyProperties(oldSysDepart, sysDepart);
        }
        sysDepart.setQywxIdentifier(department.getId());
        sysDepart.setDepartName(department.getName());
        try {
            sysDepart.setDepartOrder(Integer.parseInt(department.getOrder()));
        } catch (NumberFormatException ignored) {
        }
        return sysDepart;
    }

    @Override
    public int removeThirdAppUser(List<String> userIdList) {
        // 判斷啟用狀態
        if (!thirdAppConfig.isWechatEnterpriseEnabled()) {
            return -1;
        }
        int count = 0;
        if (userIdList != null && userIdList.size() > 0) {
            String accessToken = this.getAccessToken();
            if (accessToken == null) {
                return count;
            }
            LambdaQueryWrapper<SysThirdAccount> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysThirdAccount::getThirdType, THIRD_TYPE);
            queryWrapper.in(SysThirdAccount::getSysUserId, userIdList);
            // 根據userId，獲取第三方用戶的id
            List<SysThirdAccount> thirdAccountList = sysThirdAccountService.list(queryWrapper);
            List<String> thirdUserIdList = thirdAccountList.stream().map(SysThirdAccount::getThirdUserId).collect(Collectors.toList());

            for (String thirdUserId : thirdUserIdList) {
                if (oConvertUtils.isNotEmpty(thirdUserId)) {
                    // 沒有批量刪除的接口
                    int err = JwUserAPI.deleteUser(thirdUserId, accessToken);
                    if (err == 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean sendMessage(MessageDTO message) {
        return this.sendMessage(message, false);
    }

    @Override
    public boolean sendMessage(MessageDTO message, boolean verifyConfig) {
        JSONObject response = this.sendMessageResponse(message, verifyConfig);
        if (response != null) {
            return response.getIntValue("errcode") == 0;
        }
        return false;
    }

    public JSONObject sendMessageResponse(MessageDTO message, boolean verifyConfig) {
        if (verifyConfig && !thirdAppConfig.isWechatEnterpriseEnabled()) {
            return null;
        }
        String accessToken = this.getAppAccessToken();
        if (accessToken == null) {
            return null;
        }
        Text text = new Text();
        text.setMsgtype("text");
        text.setTouser(this.getTouser(message.getToUser(), message.isToAll()));
        TextEntity entity = new TextEntity();
        entity.setContent(message.getContent());
        text.setText(entity);
        text.setAgentid(thirdAppConfig.getWechatEnterprise().getAgentIdInt());
        return JwMessageAPI.sendTextMessage(text, accessToken);
    }

    /**
     * 發送文本卡片消息（SysAnnouncement定制）
     *
     * @param announcement
     * @param verifyConfig 是否驗證配置（未啟用的APP會拒絕發送）
     * @return
     */
    public JSONObject sendTextCardMessage(SysAnnouncement announcement, boolean verifyConfig) {
        if (verifyConfig && !thirdAppConfig.isWechatEnterpriseEnabled()) {
            return null;
        }
        String accessToken = this.getAppAccessToken();
        if (accessToken == null) {
            return null;
        }
        TextCard textCard = new TextCard();
        textCard.setAgentid(thirdAppConfig.getWechatEnterprise().getAgentIdInt());
        boolean isToAll = CommonConstant.MSG_TYPE_ALL.equals(announcement.getMsgType());
        String usernameString = "";
        if (!isToAll) {
            // 將userId轉為username
            String userId = announcement.getUserIds();
            String[] userIds = null;
            if(oConvertUtils.isNotEmpty(userId)){
                userIds = userId.substring(0, (userId.length() - 1)).split(",");
            }else{
                LambdaQueryWrapper<SysAnnouncementSend> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysAnnouncementSend::getAnntId, announcement.getId());
                SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendMapper.selectOne(queryWrapper);
                userIds = new String[] {sysAnnouncementSend.getUserId()};
            }
            List<String> usernameList = sysUserService.userIdToUsername(Arrays.asList(userIds));
            usernameString = String.join(",", usernameList);
        }

        textCard.setTouser(this.getTouser(usernameString, isToAll));
        TextCardEntity entity = new TextCardEntity();
        entity.setTitle(announcement.getTitile());
        entity.setDescription(oConvertUtils.getString(announcement.getMsgAbstract(),"空"));
        entity.setUrl(RestUtil.getBaseUrl() + "/sys/annountCement/show/" + announcement.getId());
        textCard.setTextcard(entity);
        return JwMessageAPI.sendTextCardMessage(textCard, accessToken);
    }

    private String getTouser(String origin, boolean toAll) {
        if (toAll) {
            return "@all";
        } else {
            String[] toUsers = origin.split(",");
            // 通過第三方賬號表查詢出第三方userId
            List<SysThirdAccount> thirdAccountList = sysThirdAccountService.listThirdUserIdByUsername(toUsers, THIRD_TYPE);
            List<String> toUserList = thirdAccountList.stream().map(SysThirdAccount::getThirdUserId).collect(Collectors.toList());
            // 多個接收者用‘|’分隔
            return String.join("|", toUserList);
        }
    }

    /**
     * 根據第三方登錄獲取到的code來獲取第三方app的用戶ID
     *
     * @param code
     * @return
     */
    public String getUserIdByThirdCode(String code, String accessToken) {
        JSONObject response = JwUserAPI.getUserInfoByCode(code, accessToken);
        if (response != null) {
            log.info("response: " + response.toJSONString());
            if (response.getIntValue("errcode") == 0) {
                return response.getString("UserId");
            }
        }
        return null;
    }

    /**
     * OAuth2登錄，成功返回登錄的SysUser，失敗返回null
     */
    public SysUser oauth2Login(String code) {
        String accessToken = this.getAppAccessToken();
        if (accessToken == null) {
            return null;
        }
        String appUserId = this.getUserIdByThirdCode(code, accessToken);
        if (appUserId != null) {
            // 判斷第三方用戶表有沒有這個人
            LambdaQueryWrapper<SysThirdAccount> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysThirdAccount::getThirdUserUuid, appUserId);
            queryWrapper.or().eq(SysThirdAccount::getThirdUserId, appUserId);
            queryWrapper.eq(SysThirdAccount::getThirdType, THIRD_TYPE);
            SysThirdAccount thirdAccount = sysThirdAccountService.getOne(queryWrapper);
            if (thirdAccount != null) {
                return this.getSysUserByThird(thirdAccount, null, appUserId, accessToken);
            } else {
                // 直接創建新賬號
                User appUser = JwUserAPI.getUserByUserid(appUserId, accessToken);
                ThirdLoginModel tlm = new ThirdLoginModel(THIRD_TYPE, appUser.getUserid(), appUser.getName(), appUser.getAvatar());
                thirdAccount = sysThirdAccountService.saveThirdUser(tlm);
                return this.getSysUserByThird(thirdAccount, appUser, null, null);
            }
        }
        return null;
    }

    /**
     * 根據第三方賬號獲取本地賬號，如果不存在就創建
     *
     * @param thirdAccount
     * @param appUser
     * @param appUserId
     * @param accessToken
     * @return
     */
    private SysUser getSysUserByThird(SysThirdAccount thirdAccount, User appUser, String appUserId, String accessToken) {
        String sysUserId = thirdAccount.getSysUserId();
        if (oConvertUtils.isNotEmpty(sysUserId)) {
            return sysUserService.getById(sysUserId);
        } else {
            // 如果沒有 sysUserId ，說明沒有綁定賬號，獲取到手機號之后進行綁定
            if (appUser == null) {
                appUser = JwUserAPI.getUserByUserid(appUserId, accessToken);
            }
            // 判斷系統里是否有這個手機號的用戶
            SysUser sysUser = sysUserService.getUserByPhone(appUser.getMobile());
            if (sysUser != null) {
                thirdAccount.setAvatar(appUser.getAvatar());
                thirdAccount.setRealname(appUser.getName());
                thirdAccount.setThirdUserId(appUser.getUserid());
                thirdAccount.setThirdUserUuid(appUser.getUserid());
                thirdAccount.setSysUserId(sysUser.getId());
                sysThirdAccountService.updateById(thirdAccount);
                return sysUser;
            } else {
                // 沒有就走創建邏輯
                return sysThirdAccountService.createUser(appUser.getMobile(), appUser.getUserid());
            }

        }
    }

}
