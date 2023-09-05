package com.yaude.common.online.api.factory;

import com.yaude.common.online.api.fallback.OnlineBaseExtAPIFallback;
import feign.hystrix.FallbackFactory;
import com.yaude.common.online.api.IOnlineBaseExtAPI;
import org.springframework.stereotype.Component;

@Component
public class OnlineBaseExtAPIFallbackFactory implements FallbackFactory<IOnlineBaseExtAPI> {

    @Override
    public IOnlineBaseExtAPI create(Throwable throwable) {
        OnlineBaseExtAPIFallback fallback = new OnlineBaseExtAPIFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}