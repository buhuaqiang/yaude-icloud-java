package com.yaude.config.thirdapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 第三方App對接配置
 */
@Configuration
public class ThirdAppConfig {

    /**
     * 釘釘
     */
    public final static String DINGTALK = "DINGTALK";
    /**
     * 企業微信
     */
    public final static String WECHAT_ENTERPRISE = "WECHAT_ENTERPRISE";

    /**
     * 是否啟用 第三方App對接
     */
    @Value("${third-app.enabled:false}")
    private boolean enabled;

    /**
     * 系統類型，目前支持：WECHAT_ENTERPRISE（企業微信）；DINGTALK （釘釘）
     */
    @Autowired
    private ThirdAppTypeConfig type;

    public boolean isEnabled() {
        return enabled;
    }

    public ThirdAppConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * 獲取企業微信配置
     */
    public ThirdAppTypeItemVo getWechatEnterprise() {
        return this.type.getWECHAT_ENTERPRISE();
    }

    /**
     * 獲取釘釘配置
     */
    public ThirdAppTypeItemVo getDingtalk() {
        return this.type.getDINGTALK();
    }

    /**
     * 獲取企業微信是否啟用
     */
    public boolean isWechatEnterpriseEnabled() {
        try {
            return this.enabled && this.getWechatEnterprise().isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 獲取釘釘是否啟用
     */
    public boolean isDingtalkEnabled() {
        try {
            return this.enabled && this.getDingtalk().isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

}
