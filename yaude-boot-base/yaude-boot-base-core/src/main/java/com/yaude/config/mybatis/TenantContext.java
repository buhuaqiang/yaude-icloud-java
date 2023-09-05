package com.yaude.config.mybatis;

import lombok.extern.slf4j.Slf4j;

/**
 * 多租戶 tenant_id存儲器
 */
@Slf4j
public class TenantContext {

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenant(String tenant) {
        log.debug(" setting tenant to " + tenant);
        currentTenant.set(tenant);
    }

    public static String getTenant() {
        return currentTenant.get();
    }

    public static void clear(){
        currentTenant.remove();
    }
}