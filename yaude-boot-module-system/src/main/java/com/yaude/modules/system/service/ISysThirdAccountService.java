package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.entity.SysThirdAccount;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.model.ThirdLoginModel;

import java.util.List;

/**
 * @Description: 第三方登錄賬號表
 * @Author: jeecg-boot
 * @Date:   2020-11-17
 * @Version: V1.0
 */
public interface ISysThirdAccountService extends IService<SysThirdAccount> {
    /**更新第三方賬戶信息*/
    void updateThirdUserId(SysUser sysUser,String thirdUserUuid);
    /**創建第三方用戶*/
    SysUser createUser(String phone, String thirdUserUuid);

    /** 根據本地userId查詢數據 */
    SysThirdAccount getOneBySysUserId(String sysUserId, String thirdType);
    /** 根據第三方userId查詢數據 */
    SysThirdAccount getOneByThirdUserId(String thirdUserId, String thirdType);

    /**
     * 通過 sysUsername 集合批量查詢
     *
     * @param sysUsernameArr username集合
     * @param thirdType      第三方類型
     * @return
     */
    List<SysThirdAccount> listThirdUserIdByUsername(String[] sysUsernameArr, String thirdType);

    /**
     * 創建新用戶
     *
     * @param tlm 第三方登錄信息
     */
    SysThirdAccount saveThirdUser(ThirdLoginModel tlm);

}
