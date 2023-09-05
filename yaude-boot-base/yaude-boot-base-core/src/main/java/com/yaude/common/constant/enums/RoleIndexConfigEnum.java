package com.yaude.common.constant.enums;

/**
 * 首頁自定義
 * 通過角色編碼與首頁組件路徑配置
 */
public enum RoleIndexConfigEnum {
    /**
     * 管理員
     */
    ADMIN("admin1", "dashboard/Analysis2"),
    /**
     * 測試
     */
    TEST("test",  "dashboard/Analysis"),
    /**
     * hr
     */
    HR("hr", "dashboard/Analysis1");

    /**
     * 角色編碼
     */
    String roleCode;
    /**
     * 路由index
     */
    String componentUrl;

    /**
     * 構造器
     *
     * @param roleCode 角色編碼
     * @param componentUrl 首頁組件路徑（規則跟菜單配置一樣）
     */
    RoleIndexConfigEnum(String roleCode, String componentUrl) {
        this.roleCode = roleCode;
        this.componentUrl = componentUrl;
    }
    /**
     * 根據code找枚舉
     * @param roleCode 角色編碼
     * @return
     */
    public static RoleIndexConfigEnum getEnumByCode(String roleCode) {
        for (RoleIndexConfigEnum e : RoleIndexConfigEnum.values()) {
            if (e.roleCode.equals(roleCode)) {
                return e;
            }
        }
        return null;
    }
    /**
     * 根據code找index
     * @param roleCode 角色編碼
     * @return
     */
    public static String getIndexByCode(String roleCode) {
        for (RoleIndexConfigEnum e : RoleIndexConfigEnum.values()) {
            if (e.roleCode.equals(roleCode)) {
                return e.componentUrl;
            }
        }
        return null;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getComponentUrl() {
        return componentUrl;
    }

    public void setComponentUrl(String componentUrl) {
        this.componentUrl = componentUrl;
    }
}
