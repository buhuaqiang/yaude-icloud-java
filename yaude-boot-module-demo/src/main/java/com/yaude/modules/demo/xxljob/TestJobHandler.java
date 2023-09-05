package org.jeecg.modules.demo.xxljob;//
//package org.jeecg.modules.demo.xxljob;
//
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.annotation.XxlJob;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//
///**
// * xxl-job定時任務測試
// */
//@Component
//@Slf4j
//public class TestJobHandler {
//
//
//    /**
//     * 簡單任務
//     *
//     * @param params
//     * @return
//     */
//
//    @XxlJob(value = "testJob")
//    public ReturnT<String> demoJobHandler(String params) {
//        log.info("我是demo服務里的定時任務testJob,我執行了...............................");
//        return ReturnT.SUCCESS;
//    }
//
//    public void init() {
//        log.info("init");
//    }
//
//    public void destroy() {
//        log.info("destory");
//    }
//
//}
//
