package com.yaude.config.thirdapp;

import lombok.Data;

/**
 * 第三方App對接
 */
@Data
public class ThirdAppTypeItemVo {

    /**
     * 是否啟用
     */
    private boolean enabled;
    /**
     * 應用Key
     */
    private String clientId;
    /**
     * 應用Secret
     */
    private String clientSecret;
    /**
     * 應用ID
     */
    private String agentId;
    /**
     * 目前僅企業微信用到：自建應用Secret
     */
    private String agentAppSecret;

    public int getAgentIdInt() {
        return Integer.parseInt(agentId);
    }

}
