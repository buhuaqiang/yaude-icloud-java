package com.yaude.common.bpm.api.factory;

import com.yaude.common.bpm.api.fallback.BpmBaseExtAPIFallback;
import com.yaude.common.bpm.api.IBpmBaseExtAPI;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

@Component
public class BpmBaseExtAPIFallbackFactory implements FallbackFactory<IBpmBaseExtAPI> {

    @Override
    public IBpmBaseExtAPI create(Throwable throwable) {
        BpmBaseExtAPIFallback fallback = new BpmBaseExtAPIFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}