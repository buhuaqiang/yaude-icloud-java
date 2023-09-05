package com.yaude.modules.demo.cloud.service;

import com.yaude.common.api.vo.Result;

public interface JcloudDemoService {
    Result<String> getMessage(String name);
}
