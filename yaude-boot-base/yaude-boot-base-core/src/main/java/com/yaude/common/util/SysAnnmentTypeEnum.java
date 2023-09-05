package com.yaude.common.util;

/**
 * 系統公告自定義跳轉方式
 */
public enum SysAnnmentTypeEnum {
    /**
     * 郵件跳轉組件
     */
    EMAIL("email", "component", "modules/eoa/email/modals/EoaEmailInForm"),
    /**
     * 工作流跳轉鏈接我的辦公
     */
    BPM("bpm", "url", "/bpm/task/MyTaskList");

    /**
     * 業務類型(email:郵件 bpm:流程)
     */
    private String type;
    /**
     * 打開方式 組件：component 路由：url
     */
    private String openType;
    /**
     * 組件/路由 地址
     */
    private String openPage;

    SysAnnmentTypeEnum(String type, String openType, String openPage) {
        this.type = type;
        this.openType = openType;
        this.openPage = openPage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getOpenPage() {
        return openPage;
    }

    public void setOpenPage(String openPage) {
        this.openPage = openPage;
    }

    public static SysAnnmentTypeEnum getByType(String type) {
        if (oConvertUtils.isEmpty(type)) {
            return null;
        }
        for (SysAnnmentTypeEnum val : values()) {
            if (val.getType().equals(type)) {
                return val;
            }
        }
        return null;
    }
}
