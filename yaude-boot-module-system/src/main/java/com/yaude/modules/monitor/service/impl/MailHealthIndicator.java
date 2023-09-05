package com.yaude.modules.monitor.service.impl;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 功能說明:自定義郵件檢測
 *
 * @author: 李波
 * @email: 503378406@qq.com
 * @date: 2019-06-29
 */
@Component
public class MailHealthIndicator implements HealthIndicator {


    @Override public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode) .build();
        }
        return Health.up().build();
    }
    int check(){
        //可以實現自定義的數據庫檢測邏輯
        return 0;
    }
}
