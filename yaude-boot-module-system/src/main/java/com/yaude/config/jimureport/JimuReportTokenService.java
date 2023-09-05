package com.yaude.config.jimureport;

import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.SysUserCacheInfo;
import com.yaude.common.util.RedisUtil;
import com.yaude.common.util.TokenUtils;
import org.jeecg.modules.jmreport.api.JmReportTokenServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定義積木報表鑒權(如果不進行自定義，則所有請求不做權限控制)
 *  * 1.自定義獲取登錄token
 *  * 2.自定義獲取登錄用戶
 */
@Component
public class JimuReportTokenService implements JmReportTokenServiceI {
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    @Lazy
    private RedisUtil redisUtil;

    @Override
    public String getToken(HttpServletRequest request) {
        return TokenUtils.getTokenByRequest(request);
    }

    @Override
    public String getUsername(String token) {
        return JwtUtil.getUsername(token);
    }

    @Override
    public Boolean verifyToken(String token) {
        return TokenUtils.verifyToken(token, sysBaseAPI, redisUtil);
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        String username = JwtUtil.getUsername(token);
        //此處通過token只能拿到一個信息 用戶賬號  后面的就是根據賬號獲取其他信息 查詢數據或是走redis 用戶根據自身業務可自定義
        SysUserCacheInfo userInfo = sysBaseAPI.getCacheUser(username);
        Map<String, Object> map = new HashMap<String, Object>();
        //設置賬號名
        map.put(SYS_USER_CODE, userInfo.getSysUserCode());
        //設置部門編碼
        map.put(SYS_ORG_CODE, userInfo.getSysOrgCode());
        // 將所有信息存放至map 解析sql/api會根據map的鍵值解析
        return map;
    }
}
