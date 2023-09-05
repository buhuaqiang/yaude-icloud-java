package com.yaude.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeecg.dingtalk.api.base.JdtBaseAPI;
import com.jeecg.dingtalk.api.core.response.Response;
import com.jeecg.dingtalk.api.core.vo.AccessToken;
import com.jeecg.dingtalk.api.core.vo.PageResult;
import com.jeecg.dingtalk.api.department.JdtDepartmentAPI;
import com.jeecg.dingtalk.api.department.vo.Department;
import com.jeecg.dingtalk.api.message.JdtMessageAPI;
import com.jeecg.dingtalk.api.message.vo.ActionCardMessage;
import com.jeecg.dingtalk.api.message.vo.Message;
import com.jeecg.dingtalk.api.message.vo.TextMessage;
import com.jeecg.dingtalk.api.oauth2.JdtOauth2API;
import com.jeecg.dingtalk.api.oauth2.vo.ContactUser;
import com.jeecg.dingtalk.api.user.JdtUserAPI;
import com.jeecg.dingtalk.api.user.body.GetUserListBody;
import com.jeecg.dingtalk.api.user.vo.User;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.util.RestUtil;
import com.yaude.config.thirdapp.ThirdAppConfig;
import com.yaude.config.thirdapp.ThirdAppTypeItemVo;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import com.yaude.common.api.dto.message.MessageDTO;
import com.yaude.common.util.PasswordUtil;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.mapper.SysAnnouncementSendMapper;
import com.yaude.modules.system.model.SysDepartTreeModel;
import com.yaude.modules.system.model.ThirdLoginModel;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.vo.thirdapp.JdtDepartmentTreeVo;
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
 * 第三方App對接：釘釘實現類
 */
@Slf4j
@Service
public class ThirdAppDingtalkServiceImpl implements IThirdAppService {

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

    // 第三方APP類型，當前固定為 dingtalk
    public final String THIRD_TYPE = ThirdAppConfig.DINGTALK.toLowerCase();

    @Override
    public String getAccessToken() {
        String appKey = thirdAppConfig.getDingtalk().getClientId();
        String appSecret = thirdAppConfig.getDingtalk().getClientSecret();
        AccessToken accessToken = JdtBaseAPI.getAccessToken(appKey, appSecret);
        if (accessToken != null) {
            return accessToken.getAccessToken();
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
        // 獲取【釘釘】所有的部門
        List<Department> departments = JdtDepartmentAPI.listAll(accessToken);
        // 刪除釘釘有但本地沒有的部門（以本地部門數據為主）（釘釘不能創建同名部門，只能先刪除）
        List<SysDepart> sysDepartList = sysDepartService.list();
        for1:
        for (Department department : departments) {
            for (SysDepart depart : sysDepartList) {
                // id相同，代表已存在，不刪除
                String sourceIdentifier = department.getSource_identifier();
                if (sourceIdentifier != null && sourceIdentifier.equals(depart.getId())) {
                    continue for1;
                }
            }
            // 循環到此說明本地沒有，刪除
            int deptId = department.getDept_id();
            // 釘釘不允許刪除帶有用戶的部門，所以需要判斷下，將有用戶的部門的用戶移動至根部門
            Response<List<String>> userIdRes = JdtUserAPI.getUserListIdByDeptId(deptId, accessToken);
            if (userIdRes.isSuccess() && userIdRes.getResult().size() > 0) {
                for (String userId : userIdRes.getResult()) {
                    User updateUser = new User();
                    updateUser.setUserid(userId);
                    updateUser.setDept_id_list(1);
                    JdtUserAPI.update(updateUser, accessToken);
                }
            }
            JdtDepartmentAPI.delete(deptId, accessToken);
        }
        // 獲取本地所有部門樹結構
        List<SysDepartTreeModel> sysDepartsTree = sysDepartService.queryTreeList();
        // -- 釘釘不能創建新的頂級部門，所以新的頂級部門的parentId就為1
        Department parent = new Department();
        parent.setDept_id(1);
        // 遞歸同步部門
        departments = JdtDepartmentAPI.listAll(accessToken);
        this.syncDepartmentRecursion(sysDepartsTree, departments, parent, accessToken);
        return true;
    }

    // 遞歸同步部門到本地
    public void syncDepartmentRecursion(List<SysDepartTreeModel> sysDepartsTree, List<Department> departments, Department parent, String accessToken) {
        if (sysDepartsTree != null && sysDepartsTree.size() != 0) {
            for1:
            for (SysDepartTreeModel depart : sysDepartsTree) {
                for (Department department : departments) {
                    // id相同，代表已存在，執行修改操作
                    String sourceIdentifier = department.getSource_identifier();
                    if (sourceIdentifier != null && sourceIdentifier.equals(depart.getId())) {
                        this.sysDepartToDtDepartment(depart, department, parent.getDept_id());
                        JdtDepartmentAPI.update(department, accessToken);
                        // 緊接著同步子級
                        this.syncDepartmentRecursion(depart.getChildren(), departments, department, accessToken);
                        // 跳出外部循環
                        continue for1;
                    }
                }
                // 循環到此說明是新部門，直接調接口創建
                Department newDepartment = this.sysDepartToDtDepartment(depart, parent.getDept_id());
                Response<Integer> response = JdtDepartmentAPI.create(newDepartment, accessToken);
                // 創建成功，將返回的id綁定到本地
                if (response.getResult() != null) {
                    Department newParent = new Department();
                    newParent.setDept_id(response.getResult());
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
        // 獲取【釘釘】所有的部門
        List<Department> departments = JdtDepartmentAPI.listAll(accessToken);
        String username = JwtUtil.getUserNameByToken(SpringContextUtils.getHttpServletRequest());
        List<JdtDepartmentTreeVo> departmentTreeList = JdtDepartmentTreeVo.listToTree(departments);
        // 遞歸同步部門
        this.syncDepartmentToLocalRecursion(departmentTreeList, null, username, syncInfo, accessToken);
        return syncInfo;
    }

    public void syncDepartmentToLocalRecursion(List<JdtDepartmentTreeVo> departmentTreeList, String sysParentId, String username, SyncInfoVo syncInfo, String accessToken) {

        if (departmentTreeList != null && departmentTreeList.size() != 0) {
            for (JdtDepartmentTreeVo departmentTree : departmentTreeList) {
                LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
                // 根據 source_identifier 字段查詢
                queryWrapper.eq(SysDepart::getId, departmentTree.getSource_identifier());
                SysDepart sysDepart = sysDepartService.getOne(queryWrapper);
                if (sysDepart != null) {
                    //  執行更新操作
                    SysDepart updateSysDepart = this.dtDepartmentToSysDepart(departmentTree, sysDepart);
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
                        this.syncDepartmentToLocalRecursion(departmentTree.getChildren(), updateSysDepart.getId(), username, syncInfo, accessToken);
                    }
                } else {
                    //  執行新增操作
                    SysDepart newSysDepart = this.dtDepartmentToSysDepart(departmentTree, null);
                    if (sysParentId != null) {
                        newSysDepart.setParentId(sysParentId);
                    }
                    try {
                        sysDepartService.saveDepartData(newSysDepart, username);
                        // 更新釘釘 source_identifier
                        Department updateDtDepart = new Department();
                        updateDtDepart.setDept_id(departmentTree.getDept_id());
                        updateDtDepart.setSource_identifier(newSysDepart.getId());
                        Response response = JdtDepartmentAPI.update(updateDtDepart, accessToken);
                        if (!response.isSuccess()) {
                            throw new RuntimeException(response.getErrmsg());
                        }
                        String str = String.format("部門 %s 創建成功！", newSysDepart.getDepartName());
                        syncInfo.addSuccessInfo(str);
                    } catch (Exception e) {
                        this.syncDepartCollectErrInfo(e, departmentTree, syncInfo);
                    }
                    // 緊接著同步子級
                    if (departmentTree.hasChildren()) {
                        this.syncDepartmentToLocalRecursion(departmentTree.getChildren(), newSysDepart.getId(), username, syncInfo, accessToken);
                    }
                }
            }
        }
    }

    private boolean syncDepartCollectErrInfo(Exception e, Department department, SyncInfoVo syncInfo) {
        String msg;
        if (e instanceof DuplicateKeyException) {
            msg = e.getCause().getMessage();
        } else {
            msg = e.getMessage();
        }
        String str = String.format("部門 %s(%s) 同步失敗！錯誤信息：%s", department.getName(), department.getDept_id(), msg);
        syncInfo.addFailInfo(str);
        return false;
    }


    @Override
    public SyncInfoVo syncLocalUserToThirdApp(String ids) {
        SyncInfoVo syncInfo = new SyncInfoVo();
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            syncInfo.addFailInfo("accessToken獲取失敗！");
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
        // 查詢釘釘所有的部門，用于同步用戶和部門的關系
        List<Department> allDepartment = JdtDepartmentAPI.listAll(accessToken);

        for (SysUser sysUser : sysUsers) {
            // 外部模擬登陸臨時賬號，不同步
            if ("_reserve_user_external".equals(sysUser.getUsername())) {
                continue;
            }
            // 釘釘用戶信息，不為null代表已同步過
            Response<User> dtUserInfo;
            /*
             * 判斷是否同步過的邏輯：
             * 1. 查詢 sys_third_account（第三方賬號表）是否有數據，如果有代表已同步
             * 2. 本地表里沒有，就先用手機號判斷，不通過再用username判斷。
             */
            SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneBySysUserId(sysUser.getId(), THIRD_TYPE);
            if (sysThirdAccount != null && oConvertUtils.isNotEmpty(sysThirdAccount.getThirdUserId())) {
                // sys_third_account 表匹配成功，通過第三方userId查詢出第三方userInfo
                dtUserInfo = JdtUserAPI.getUserById(sysThirdAccount.getThirdUserId(), accessToken);
            } else {
                // 手機號匹配
                Response<String> thirdUserId = JdtUserAPI.getUseridByMobile(sysUser.getPhone(), accessToken);
                // 手機號匹配成功
                if (thirdUserId.isSuccess() && oConvertUtils.isNotEmpty(thirdUserId.getResult())) {
                    // 通過查詢到的userId查詢用戶詳情
                    dtUserInfo = JdtUserAPI.getUserById(thirdUserId.getResult(), accessToken);
                } else {
                    // 手機號匹配失敗，嘗試使用username匹配
                    dtUserInfo = JdtUserAPI.getUserById(sysUser.getUsername(), accessToken);
                }
            }
            String dtUserId;
            // api 接口是否執行成功
            boolean apiSuccess;
            // 已同步就更新，否則就創建
            if (dtUserInfo != null && dtUserInfo.isSuccess() && dtUserInfo.getResult() != null) {
                User dtUser = dtUserInfo.getResult();
                dtUserId = dtUser.getUserid();
                User updateQwUser = this.sysUserToDtUser(sysUser, dtUser, allDepartment);
                Response<JSONObject> updateRes = JdtUserAPI.update(updateQwUser, accessToken);
                // 收集成功/失敗信息
                apiSuccess = this.syncUserCollectErrInfo(updateRes, sysUser, syncInfo);
            } else {
                User newQwUser = this.sysUserToDtUser(sysUser, allDepartment);
                Response<String> createRes = JdtUserAPI.create(newQwUser, accessToken);
                dtUserId = createRes.getResult();
                // 收集成功/失敗信息
                apiSuccess = this.syncUserCollectErrInfo(createRes, sysUser, syncInfo);
            }

            // api 接口執行成功，并且 sys_third_account 表匹配失敗，就向 sys_third_account 里插入一條數據
            if (apiSuccess && (sysThirdAccount == null || oConvertUtils.isEmpty(sysThirdAccount.getThirdUserId()))) {
                if (sysThirdAccount == null) {
                    sysThirdAccount = new SysThirdAccount();
                    sysThirdAccount.setSysUserId(sysUser.getId());
                    sysThirdAccount.setStatus(1);
                    sysThirdAccount.setDelFlag(0);
                    sysThirdAccount.setThirdType(THIRD_TYPE);
                }
                // 設置第三方app用戶ID
                sysThirdAccount.setThirdUserId(dtUserId);
                sysThirdAccountService.saveOrUpdate(sysThirdAccount);
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

        // 獲取本地用戶
        List<SysUser> sysUsersList = sysUserService.list();

        // 查詢釘釘所有的部門，用于同步用戶和部門的關系
        List<Department> allDepartment = JdtDepartmentAPI.listAll(accessToken);
        // 根據釘釘部門查詢所有釘釘用戶，用于反向同步到本地
        List<User> ddUserList = this.getDtAllUserByDepartment(allDepartment, accessToken);

        for (User dtUserInfo : ddUserList) {
            SysThirdAccount sysThirdAccount = sysThirdAccountService.getOneByThirdUserId(dtUserInfo.getUserid(), THIRD_TYPE);
            List<SysUser> collect = sysUsersList.stream().filter(user -> (dtUserInfo.getMobile().equals(user.getPhone()) || dtUserInfo.getUserid().equals(user.getUsername()))
                                                                 ).collect(Collectors.toList());
            if (collect != null && collect.size() > 0) {
                SysUser sysUserTemp = collect.get(0);
                // 循環到此說明用戶匹配成功，進行更新操作
                SysUser updateSysUser = this.dtUserToSysUser(dtUserInfo, sysUserTemp);
                try {
                    sysUserService.updateById(updateSysUser);
                    String str = String.format("用戶 %s(%s) 更新成功！", updateSysUser.getRealname(), updateSysUser.getUsername());
                    syncInfo.addSuccessInfo(str);
                } catch (Exception e) {
                    this.syncUserCollectErrInfo(e, dtUserInfo, syncInfo);
                }
                //第三方賬號關系表
                this.thirdAccountSaveOrUpdate(sysThirdAccount, updateSysUser.getId(), dtUserInfo.getUserid());
            }else{
                // 如果沒有匹配到用戶，則走創建邏輯
                SysUser newSysUser = this.dtUserToSysUser(dtUserInfo);
                try {
                    sysUserService.save(newSysUser);
                    String str = String.format("用戶 %s(%s) 創建成功！", newSysUser.getRealname(), newSysUser.getUsername());
                    syncInfo.addSuccessInfo(str);
                } catch (Exception e) {
                    this.syncUserCollectErrInfo(e, dtUserInfo, syncInfo);
                }
                //第三方賬號關系表
                this.thirdAccountSaveOrUpdate(null, newSysUser.getId(), dtUserInfo.getUserid());
            }
        }
        return syncInfo;
    }

    private List<User> getDtAllUserByDepartment(List<Department> allDepartment, String accessToken) {
        // 根據釘釘部門查詢所有釘釘用戶，用于反向同步到本地
        List<User> userList = new ArrayList<>();
        for (Department department : allDepartment) {
            this.getUserListByDeptIdRecursion(department.getDept_id(), 0, userList, accessToken);
        }
        return userList;
    }

    /**
     * 遞歸查詢所有用戶
     */
    private void getUserListByDeptIdRecursion(int deptId, int cursor, List<User> userList, String accessToken) {
        // 根據釘釘部門查詢所有釘釘用戶，用于反向同步到本地
        GetUserListBody getUserListBody = new GetUserListBody(deptId, cursor, 100);
        Response<PageResult<User>> response = JdtUserAPI.getUserListByDeptId(getUserListBody, accessToken);
        if (response.isSuccess()) {
            PageResult<User> page = response.getResult();
            userList.addAll(page.getList());
            if (page.getHas_more()) {
                this.getUserListByDeptIdRecursion(deptId, page.getNext_cursor(), userList, accessToken);
            }
        }
    }

    /**
     * 保存或修改第三方登錄表
     *
     * @param sysThirdAccount 第三方賬戶表對象，為null就新增數據，否則就修改
     * @param sysUserId       本地系統用戶ID
     * @param dtUserId        釘釘用戶ID
     */
    private void thirdAccountSaveOrUpdate(SysThirdAccount sysThirdAccount, String sysUserId, String dtUserId) {
        if (sysThirdAccount == null) {
            sysThirdAccount = new SysThirdAccount();
            sysThirdAccount.setSysUserId(sysUserId);
            sysThirdAccount.setStatus(1);
            sysThirdAccount.setDelFlag(0);
            sysThirdAccount.setThirdType(THIRD_TYPE);
        }
        sysThirdAccount.setThirdUserId(dtUserId);
        sysThirdAccountService.saveOrUpdate(sysThirdAccount);
    }

    /**
     * 【同步用戶】收集同步過程中的錯誤信息
     */
    private boolean syncUserCollectErrInfo(Response<?> response, SysUser sysUser, SyncInfoVo syncInfo) {
        if (!response.isSuccess()) {
            String str = String.format("用戶 %s(%s) 同步失敗！錯誤碼：%s——%s", sysUser.getUsername(), sysUser.getRealname(), response.getErrcode(), response.getErrmsg());
            syncInfo.addFailInfo(str);
            return false;
        } else {
            String str = String.format("用戶 %s(%s) 同步成功！", sysUser.getUsername(), sysUser.getRealname());
            syncInfo.addSuccessInfo(str);
            return true;
        }
    }

    /**
     * 【同步用戶】收集同步過程中的錯誤信息
     */
    private boolean syncUserCollectErrInfo(Exception e, User dtUser, SyncInfoVo syncInfo) {
        String msg;
        if (e instanceof DuplicateKeyException) {
            msg = e.getCause().getMessage();
        } else {
            msg = e.getMessage();
        }
        String str = String.format("用戶 %s(%s) 同步失敗！錯誤信息：%s", dtUser.getUserid(), dtUser.getName(), msg);
        syncInfo.addFailInfo(str);
        return false;
    }


    /**
     * 【同步用戶】將SysUser轉為【釘釘】的User對象（創建新用戶）
     */
    private User sysUserToDtUser(SysUser sysUser, List<Department> allDepartment) {
        User user = new User();
        // 通過 username 來關聯
        user.setUserid(sysUser.getUsername());
        return this.sysUserToDtUser(sysUser, user, allDepartment);
    }

    /**
     * 【同步用戶】將SysUser轉為【釘釘】的User對象（更新舊用戶）
     */
    private User sysUserToDtUser(SysUser sysUser, User user, List<Department> allDepartment) {
        user.setName(sysUser.getRealname());
        user.setMobile(sysUser.getPhone());
        user.setTelephone(sysUser.getTelephone());
        user.setJob_number(sysUser.getWorkNo());
        // 職務翻譯
        if (oConvertUtils.isNotEmpty(sysUser.getPost())) {
            SysPosition position = sysPositionService.getByCode(sysUser.getPost());
            if (position != null) {
                user.setTitle(position.getName());
            }
        }
        user.setEmail(sysUser.getEmail());
        // 查詢并同步用戶部門關系
        List<SysDepart> departList = this.getUserDepart(sysUser);
        if (departList != null) {
            List<Integer> departmentIdList = new ArrayList<>();
            for (SysDepart sysDepart : departList) {
                // 企業微信的部門id
                Department department = this.getDepartmentByDepartId(sysDepart.getId(), allDepartment);
                if (department != null) {
                    departmentIdList.add(department.getDept_id());
                }
            }
            user.setDept_id_list(departmentIdList.toArray(new Integer[]{}));
            user.setDept_order_list(null);
        }
        if (oConvertUtils.isEmpty(user.getDept_id_list())) {
            // 沒有找到匹配部門，同步到根部門下
            user.setDept_id_list(1);
            user.setDept_order_list(null);
        }
        // --- 釘釘沒有邏輯刪除功能
        // sysUser.getDelFlag()
        // --- 釘釘沒有凍結、啟用禁用功能
        // sysUser.getStatus()
        return user;
    }


    /**
     * 【同步用戶】將【釘釘】的User對象轉為SysUser（創建新用戶）
     */
    private SysUser dtUserToSysUser(User dtUser) {
        SysUser sysUser = new SysUser();
        sysUser.setDelFlag(0);
        // 通過 username 來關聯
        sysUser.setUsername(dtUser.getUserid());
        // 密碼默認為 “123456”，隨機加鹽
        String password = "123456", salt = oConvertUtils.randomGen(8);
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
        sysUser.setSalt(salt);
        sysUser.setPassword(passwordEncode);
        // update-begin--Author:liusq Date:20210713 for：釘釘同步到本地的人員沒有狀態，導致同步之后無法登錄 #I3ZC2L
        sysUser.setStatus(1);
        // update-end--Author:liusq Date:20210713 for：釘釘同步到本地的人員沒有狀態，導致同步之后無法登錄 #I3ZC2L
        return this.dtUserToSysUser(dtUser, sysUser);
    }

    /**
     * 【同步用戶】將【釘釘】的User對象轉為SysUser（更新舊用戶）
     */
    private SysUser dtUserToSysUser(User dtUser, SysUser oldSysUser) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(oldSysUser, sysUser);
        sysUser.setRealname(dtUser.getName());
        sysUser.setPhone(dtUser.getMobile());
        sysUser.setTelephone(dtUser.getTelephone());

        // 因為唯一鍵約束的原因，如果原數據和舊數據相同，就不更新
        if (oConvertUtils.isNotEmpty(dtUser.getEmail()) && !dtUser.getEmail().equals(sysUser.getEmail())) {
            sysUser.setEmail(dtUser.getEmail());
        } else {
            sysUser.setEmail(null);
        }
        // 因為唯一鍵約束的原因，如果原數據和舊數據相同，就不更新
        if (oConvertUtils.isNotEmpty(dtUser.getMobile()) && !dtUser.getMobile().equals(sysUser.getPhone())) {
            sysUser.setPhone(dtUser.getMobile());
        } else {
            sysUser.setPhone(null);
        }
        sysUser.setWorkNo(null);
        // --- 釘釘沒有邏輯刪除功能
        // sysUser.getDelFlag()
        // --- 釘釘沒有凍結、啟用禁用功能
        // sysUser.getStatus()
        return sysUser;
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
     * 根據sysDepartId查詢釘釘的部門
     */
    private Department getDepartmentByDepartId(String departId, List<Department> allDepartment) {
        for (Department department : allDepartment) {
            if (departId.equals(department.getSource_identifier())) {
                return department;
            }
        }
        return null;
    }


    /**
     * 【同步部門】將SysDepartTreeModel轉為【釘釘】的Department對象（創建新部門）
     */
    private Department sysDepartToDtDepartment(SysDepartTreeModel departTree, Integer parentId) {
        Department department = new Department();
        department.setSource_identifier(departTree.getId());
        return this.sysDepartToDtDepartment(departTree, department, parentId);
    }

    /**
     * 【同步部門】將SysDepartTreeModel轉為【釘釘】的Department對象
     */
    private Department sysDepartToDtDepartment(SysDepartTreeModel departTree, Department department, Integer parentId) {
        department.setName(departTree.getDepartName());
        department.setParent_id(parentId);
        department.setOrder(departTree.getDepartOrder());
        return department;
    }


    /**
     * 【同步部門】將【釘釘】的Department對象轉為SysDepartTreeModel
     */
    private SysDepart dtDepartmentToSysDepart(Department department, SysDepart departTree) {
        SysDepart sysDepart = new SysDepart();
        if (departTree != null) {
            BeanUtils.copyProperties(departTree, sysDepart);
        }
        sysDepart.setDepartName(department.getName());
        sysDepart.setDepartOrder(department.getOrder());
        return sysDepart;
    }

    @Override
    public int removeThirdAppUser(List<String> userIdList) {
        // 判斷啟用狀態
        if (!thirdAppConfig.isDingtalkEnabled()) {
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
                    Response<JSONObject> response = JdtUserAPI.delete(thirdUserId, accessToken);
                    if (response.getErrcode() == 0) {
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

    /**
     * 發送消息
     *
     * @param message
     * @param verifyConfig
     * @return
     */
    public boolean sendMessage(MessageDTO message, boolean verifyConfig) {
        Response<String> response = this.sendMessageResponse(message, verifyConfig);
        if (response != null) {
            return response.isSuccess();
        }
        return false;
    }

    public Response<String> sendMessageResponse(MessageDTO message, boolean verifyConfig) {
        if (verifyConfig && !thirdAppConfig.isDingtalkEnabled()) {
            return null;
        }
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            return null;
        }
        // 封裝釘釘消息
        String content = message.getContent();
        int agentId = thirdAppConfig.getDingtalk().getAgentIdInt();
        Message<TextMessage> textMessage = new Message<>(agentId, new TextMessage(content));
        if (message.isToAll()) {
            textMessage.setTo_all_user(true);
        } else {
            String[] toUsers = message.getToUser().split(",");
            // 通過第三方賬號表查詢出第三方userId
            List<SysThirdAccount> thirdAccountList = sysThirdAccountService.listThirdUserIdByUsername(toUsers, THIRD_TYPE);
            List<String> dtUserIds = thirdAccountList.stream().map(SysThirdAccount::getThirdUserId).collect(Collectors.toList());
            textMessage.setUserid_list(dtUserIds);
        }
        return JdtMessageAPI.sendTextMessage(textMessage, accessToken);
    }

    public boolean recallMessage(String msg_task_id) {
        Response<JSONObject> response = this.recallMessageResponse(msg_task_id);
        if (response == null) {
            return false;
        }
        return response.isSuccess();
    }

    /**
     * 撤回消息
     *
     * @param msg_task_id
     * @return
     */
    public Response<JSONObject> recallMessageResponse(String msg_task_id) {
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            return null;
        }
        int agentId = thirdAppConfig.getDingtalk().getAgentIdInt();
        return JdtMessageAPI.recallMessage(agentId, msg_task_id, getAccessToken());
    }

    /**
     * 發送卡片消息（SysAnnouncement定制）
     *
     * @param announcement
     * @param verifyConfig 是否驗證配置（未啟用的APP會拒絕發送）
     * @return
     */
    public Response<String> sendActionCardMessage(SysAnnouncement announcement, boolean verifyConfig) {
        if (verifyConfig && !thirdAppConfig.isDingtalkEnabled()) {
            return null;
        }
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            return null;
        }
        int agentId = thirdAppConfig.getDingtalk().getAgentIdInt();
        String markdown = "### " + announcement.getTitile() + "\n" + oConvertUtils.getString(announcement.getMsgAbstract(),"空");
        ActionCardMessage actionCard = new ActionCardMessage(markdown);
        actionCard.setTitle(announcement.getTitile());
        actionCard.setSingle_title("詳情");
        actionCard.setSingle_url(RestUtil.getBaseUrl() + "/sys/annountCement/show/" + announcement.getId());
        Message<ActionCardMessage> actionCardMessage = new Message<>(agentId, actionCard);
        if (CommonConstant.MSG_TYPE_ALL.equals(announcement.getMsgType())) {
            actionCardMessage.setTo_all_user(true);
            return JdtMessageAPI.sendActionCardMessage(actionCardMessage, accessToken);
        } else {
            // 將userId轉為username
            String[] userIds = null;
            String userId = announcement.getUserIds();
            if(oConvertUtils.isNotEmpty(userId)){
                userIds = userId.substring(0, (userId.length() - 1)).split(",");
            }else{
                LambdaQueryWrapper<SysAnnouncementSend> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysAnnouncementSend::getAnntId, announcement.getId());
                SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendMapper.selectOne(queryWrapper);
                userIds = new String[] {sysAnnouncementSend.getUserId()};
            }

            if(userIds!=null){
                String[] usernameList = sysUserService.userIdToUsername(Arrays.asList(userIds)).toArray(new String[]{});
                // 通過第三方賬號表查詢出第三方userId
                List<SysThirdAccount> thirdAccountList = sysThirdAccountService.listThirdUserIdByUsername(usernameList, THIRD_TYPE);
                List<String> dtUserIds = thirdAccountList.stream().map(SysThirdAccount::getThirdUserId).collect(Collectors.toList());
                actionCardMessage.setUserid_list(dtUserIds);
                return JdtMessageAPI.sendActionCardMessage(actionCardMessage, accessToken);
            }
        }
        return null;
    }

    /**
     * OAuth2登錄，成功返回登錄的SysUser，失敗返回null
     */
    public SysUser oauth2Login(String authCode) {
        ThirdAppTypeItemVo dtConfig = thirdAppConfig.getDingtalk();
        // 1. 根據免登授權碼獲取用戶 AccessToken
        String userAccessToken = JdtOauth2API.getUserAccessToken(dtConfig.getClientId(), dtConfig.getClientSecret(), authCode);
        if (userAccessToken == null) {
            log.error("oauth2Login userAccessToken is null");
            return null;
        }
        // 2. 根據用戶 AccessToken 獲取當前用戶的基本信息（不包括userId）
        ContactUser contactUser = JdtOauth2API.getContactUsers("me", userAccessToken);
        if (contactUser == null) {
            log.error("oauth2Login contactUser is null");
            return null;
        }
        String unionId = contactUser.getUnionId();
        // 3. 根據獲取到的 unionId 換取用戶 userId
        String accessToken = this.getAccessToken();
        if (accessToken == null) {
            log.error("oauth2Login accessToken is null");
            return null;
        }
        Response<String> getUserIdRes = JdtUserAPI.getUseridByUnionid(unionId, accessToken);
        if (!getUserIdRes.isSuccess()) {
            log.error("oauth2Login getUseridByUnionid failed: " + JSON.toJSONString(getUserIdRes));
            return null;
        }
        String appUserId = getUserIdRes.getResult();
        log.info("appUserId: " + appUserId);
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
                User appUser = JdtUserAPI.getUserById(appUserId, accessToken).getResult();
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
                appUser = JdtUserAPI.getUserById(appUserId, accessToken).getResult();
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