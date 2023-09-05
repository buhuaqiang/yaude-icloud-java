package com.yaude.modules.demo.cloud.controller;//package org.jeecg.modules.demo.cloud.controller;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import Result;
//import org.jeecg.common.system.api.ISysBaseAPI;
//import DictModel;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// *
// */
//@Slf4j
//@Api(tags = "Cloud示例")
//@RestController
//@RequestMapping("/test")
//public class JcloudDemoController {
//
//
//    @Resource
//    private ISysBaseAPI sysBaseAPI;
//
//    /**
//     * 測試
//     *
//     * @return
//     */
//    @GetMapping("/remote")
//    @ApiOperation(value = "測試feign", notes = "測試feign")
//    public Result remoteDict() {
////        try{
////            //睡5秒，網關Hystrix3秒超時，會觸發熔斷降級操作
////            Thread.sleep(5000);
////        }catch (Exception e){
////            e.printStackTrace();
////        }
//        List<DictModel> list = sysBaseAPI.queryAllDict();
//        return Result.OK(list);
//    }
//
//
//}
