package com.yaude;

import com.alibaba.fastjson.JSONObject;
import com.yaude.common.util.security.SecurityTools;
import com.yaude.common.util.security.entity.*;
import com.yaude.common.util.security.entity.*;
import org.junit.Test;
import com.yaude.common.util.security.entity.*;

public class SecurityToolsTest {
    @Test
    public void Test(){
        MyKeyPair mkeyPair = SecurityTools.generateKeyPair();

        JSONObject msg = new JSONObject();
        msg.put("name", "黨政輝");
        msg.put("age", 50);
        JSONObject identity = new JSONObject();
        identity.put("type", "01");
        identity.put("no", "210882165896524512");
        msg.put("identity", identity);

        // 簽名加密部分
        SecuritySignReq signReq = new SecuritySignReq();
        // data為要加密的報文字符串
        signReq.setData(msg.toString());
        // 為rsa私鑰
        signReq.setPrikey(mkeyPair.getPriKey());
        // 調用簽名方法
        SecuritySignResp sign = SecurityTools.sign(signReq);
        // 打印出來加密數據
        // signData為簽名數據
        // data為aes加密數據
        // asekey為ras加密過的aeskey
        System.out.println(JSONObject.toJSON(sign));

        // 驗簽解密部分
        SecurityReq req = new SecurityReq();
        //對方傳過來的數據一一對應
        req.setAesKey(sign.getAesKey());
        req.setData(sign.getData());
        req.setSignData(sign.getSignData());
        //我們的公鑰
        req.setPubKey(mkeyPair.getPubKey());
        //驗簽方法調用
        SecurityResp securityResp = SecurityTools.valid(req);
        //解密報文data為解密報文
        //sucess 為驗簽成功失敗標志 true代碼驗簽成功，false代表失敗
        System.out.println(JSONObject.toJSON(securityResp));
    }
}
