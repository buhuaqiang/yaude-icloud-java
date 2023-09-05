package com.yaude.modules.system.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方登錄 信息存儲
 */
@Data
public class ThirdLoginModel implements Serializable {
    private static final long serialVersionUID = 4098628709290780891L;

    /**
     * 第三方登錄 來源
     */
    private String source;

    /**
     * 第三方登錄 uuid
     */
    private String uuid;

    /**
     * 第三方登錄 username
     */
    private String username;

    /**
     * 第三方登錄 頭像
     */
    private String avatar;

    /**
     * 賬號 后綴第三方登錄 防止賬號重復
     */
    private String suffix;

    /**
     * 操作碼 防止被攻擊
     */
    private String operateCode;

    public ThirdLoginModel(){

    }

    /**
     * 構造器
     * @param source
     * @param uuid
     * @param username
     * @param avatar
     */
    public ThirdLoginModel(String source,String uuid,String username,String avatar){
        this.source = source;
        this.uuid = uuid;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * 獲取登錄賬號名
     * @return
     */
    public String getUserLoginAccount(){
        if(suffix==null){
            return this.uuid;
        }
        return this.uuid + this.suffix;
    }
}
