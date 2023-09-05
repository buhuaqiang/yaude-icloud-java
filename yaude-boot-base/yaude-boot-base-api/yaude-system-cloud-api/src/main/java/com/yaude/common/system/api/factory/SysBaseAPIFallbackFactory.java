package com.yaude.common.system.api.factory;

import com.yaude.common.system.api.fallback.SysBaseAPIFallback;
import feign.hystrix.FallbackFactory;
import com.yaude.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

@Component
public class SysBaseAPIFallbackFactory implements FallbackFactory<ISysBaseAPI> {

    @Override
    public ISysBaseAPI create(Throwable throwable) {
        SysBaseAPIFallback fallback = new SysBaseAPIFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}