package com.yaude.common.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * online 攔截器權限判斷
 * cloud api 用到的接口傳輸對象
 */
@Data
public class OnlineAuthDTO implements Serializable {
    private static final long serialVersionUID = 1771827545416418203L;


    /**
     * 用戶名
     */
    private String username;

    /**
     * 可能的請求地址
     */
    private List<String> possibleUrl;

    /**
     * online開發的菜單地址
     */
    private String onlineFormUrl;

    public OnlineAuthDTO(){

    }

    public OnlineAuthDTO(String username, List<String> possibleUrl, String onlineFormUrl){
        this.username = username;
        this.possibleUrl = possibleUrl;
        this.onlineFormUrl = onlineFormUrl;
    }
}
