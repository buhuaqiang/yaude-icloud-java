package com.yaude.common.bpm.api;

import com.yaude.common.api.vo.Result;

/**
 * 流程接口
 *
 * @author scott
 */
public interface IBpmBaseExtAPI {
    /**
     *  23. 流程提交接口（online，自定義開發）
     * @param flowCode 流程業務關聯 例如：joa_leave_01
     * @param id 表單業務數據data id
     * @param formUrl 	流程審批時附件頁面默認展示的PC端表單組件（地址）
     * @param formUrlMobile  流程審批時附件頁面默認展示的移動端表單組件（地址）
     * @param username  流程發起人賬號
     * @param jsonData  Json串，額外擴展的流程變量值  【非必填】
     * @return
     * @throws Exception
     */
    Result<String> startMutilProcess(String flowCode, String id, String formUrl, String formUrlMobile, String username, String jsonData) throws Exception;

    /**
     *  24. 流程提交接口（自定義表單設計器）
     * @param flowCode 流程業務關聯 例如：joa_leave_01
     * @param id 表單業務數據data id
     * @param formUrl 	流程審批時附件頁面默認展示的PC端表單組件（地址）
     * @param formUrlMobile  流程審批時附件頁面默認展示的移動端表單組件（地址）
     * @param username  流程發起人賬號
     * @param jsonData  Json串，額外擴展的流程變量值  【非必填】
     * @return
     * @throws Exception
     */
    Result<String> startDesFormMutilProcess(String flowCode, String id, String formUrl, String formUrlMobile,String username,String jsonData) throws Exception;
    /**
     * 25. 保存流程草稿箱接口（自定義開發表單、online表單）
     * @param flowCode 流程業務關聯 例如：joa_leave_01
     * @param id 表單業務數據data id
     * @param formUrl 	流程審批時附件頁面默認展示的PC端表單組件（地址） 【非必填】
     * @param formUrlMobile  流程審批時附件頁面默認展示的移動端表單組件（地址）  【非必填】
     * @param username  流程發起人賬號
     * @param jsonData  Json串，額外擴展的流程變量值  【非必填】
     * @return
     * @throws Exception
     */
    Result<String> saveMutilProcessDraft(String flowCode, String id, String formUrl, String formUrlMobile,String username,String jsonData) throws Exception;

}
