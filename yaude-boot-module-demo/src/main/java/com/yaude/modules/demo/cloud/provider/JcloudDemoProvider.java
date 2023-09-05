package com.yaude.modules.demo.cloud.provider;

import com.yaude.common.api.vo.Result;
import com.yaude.modules.demo.cloud.service.JcloudDemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * feign服務端接口
 */
@RestController
@RequestMapping("/test")
public class JcloudDemoProvider {

    @Resource
    private JcloudDemoService jcloudDemoService;

    @GetMapping("/getMessage")
    public Result<String> getMessage(@RequestParam String name) {
        return jcloudDemoService.getMessage(name);
    }

}
