package com.yaude.modules.system.service;

import com.yaude.common.api.dto.message.MessageDTO;
import com.yaude.modules.system.vo.thirdapp.SyncInfoVo;

import java.util.List;

/**
 * 第三方App對接
 */
public interface IThirdAppService {

    String getAccessToken();

    /**
     * 將本地部門同步到第三方App<br>
     * 同步方向：本地 --> 第三方APP
     * 同步邏輯：<br>
     * 1. 先判斷是否同步過，有則修改，無則創建；<br>
     * 2. 本地沒有但第三方App里有則刪除第三方App里的。
     *
     * @return 成功返回true
     */
    boolean syncLocalDepartmentToThirdApp(String ids);

    /**
     * 將第三方App部門同步到本地<br>
     * 同步方向：第三方APP --> 本地
     * 同步邏輯：<br>
     * 1. 先判斷是否同步過，有則修改，無則創建；<br>
     * 2. 本地沒有但第三方App里有則刪除第三方App里的。
     *
     * @return 成功返回true
     */
    SyncInfoVo syncThirdAppDepartmentToLocal(String ids);

    /**
     * 將本地用戶同步到第三方App<br>
     * 同步方向：本地 --> 第三方APP <br>
     * 同步邏輯：先判斷是否同步過，有則修改、無則創建<br>
     * 注意：同步人員的狀態，比如離職、禁用、邏輯刪除等。
     * (特殊點：1、目前邏輯特意做的不刪除用戶，防止企業微信提前上線，用戶已經存在，但是平臺無此用戶。
     *  企業微信支持禁用賬號；釘釘不支持
     *  2、企業微信里面是手機號激活，只能用戶自己改，不允許通過接口改)
     *
     * @return 成功返回空數組，失敗返回錯誤信息
     */
    SyncInfoVo syncLocalUserToThirdApp(String ids);

    /**
     * 將第三方App用戶同步到本地<br>
     * 同步方向：第三方APP --> 本地 <br>
     * 同步邏輯：先判斷是否同步過，有則修改、無則創建<br>
     * 注意：同步人員的狀態，比如離職、禁用、邏輯刪除等。
     *
     * @return 成功返回空數組，失敗返回錯誤信息
     */
    SyncInfoVo syncThirdAppUserToLocal();

    /**
     * 根據本地用戶ID，刪除第三方APP的用戶
     *
     * @param userIdList 本地用戶ID列表
     * @return 0表示成功，其他值表示失敗
     */
    int removeThirdAppUser(List<String> userIdList);

    /**
     * 發送消息
     *
     * @param message
     * @param verifyConfig 是否驗證配置（未啟用的APP會拒絕發送）
     * @return
     */
    boolean sendMessage(MessageDTO message, boolean verifyConfig);

    boolean sendMessage(MessageDTO message);

}
