package com.yaude.common.bpm.api;

import com.yaude.common.api.vo.Result;
import com.yaude.common.constant.ServiceNameConstants;
import com.yaude.common.online.api.factory.OnlineBaseExtAPIFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 流程接口
 *
 * @author scott
 */
@Component
@FeignClient(contextId = "bpmBaseRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE,
    fallbackFactory = OnlineBaseExtAPIFallbackFactory.class)
public interface IBpmBaseExtAPI {
    /**
     * 23. 流程提交接口（online，自定義開發）
     *
     * @param flowCode
     *            流程業務關聯 例如：joa_leave_01
     * @param id
     *            表單業務數據data id
     * @param formUrl
     *            流程審批時附件頁面默認展示的PC端表單組件（地址）
     * @param formUrlMobile
     *            流程審批時附件頁面默認展示的移動端表單組件（地址）
     * @param username
     *            流程發起人賬號
     * @param jsonData
     *            Json串，額外擴展的流程變量值 【非必填】
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/act/process/extActProcess/startMutilProcess")
    Result<String> startMutilProcess(@RequestParam("flowCode") String flowCode, @RequestParam("id") String id,
                                     @RequestParam("formUrl") String formUrl, @RequestParam("formUrlMobile") String formUrlMobile,
                                     @RequestParam("username") String username, @RequestParam("jsonData") String jsonData) throws Exception;

    /**
     * 24. 流程提交接口（自定義表單設計器）
     *
     * @param flowCode
     *            流程業務關聯 例如：joa_leave_01
     * @param id
     *            表單業務數據data id
     * @param formUrl
     *            流程審批時附件頁面默認展示的PC端表單組件（地址）
     * @param formUrlMobile
     *            流程審批時附件頁面默認展示的移動端表單組件（地址）
     * @param username
     *            流程發起人賬號
     * @param jsonData
     *            Json串，額外擴展的流程變量值 【非必填】
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/act/process/extActProcess/startDesFormMutilProcess")
    Result<String> startDesFormMutilProcess(@RequestParam("flowCode") String flowCode, @RequestParam("id") String id,
        @RequestParam("formUrl") String formUrl, @RequestParam("formUrlMobile") String formUrlMobile,
        @RequestParam("username") String username, @RequestParam("jsonData") String jsonData) throws Exception;

    /**
     * 25. 保存流程草稿箱接口（自定義開發表單、online表單）
     *
     * @param flowCode
     *            流程業務關聯 例如：joa_leave_01
     * @param id
     *            表單業務數據data id
     * @param formUrl
     *            流程審批時附件頁面默認展示的PC端表單組件（地址） 【非必填】
     * @param formUrlMobile
     *            流程審批時附件頁面默認展示的移動端表單組件（地址） 【非必填】
     * @param username
     *            流程發起人賬號
     * @param jsonData
     *            Json串，額外擴展的流程變量值 【非必填】
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/act/process/extActProcess/saveMutilProcessDraft")
    Result<String> saveMutilProcessDraft(@RequestParam("flowCode") String flowCode, @RequestParam("id") String id,
        @RequestParam("formUrl") String formUrl, @RequestParam("formUrlMobile") String formUrlMobile,
        @RequestParam("username") String username, @RequestParam("jsonData") String jsonData) throws Exception;

}
