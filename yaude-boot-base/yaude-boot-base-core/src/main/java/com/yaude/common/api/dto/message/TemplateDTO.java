package com.yaude.common.api.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息模板dto
 */
@Data
public class TemplateDTO implements Serializable {

    private static final long serialVersionUID = 5848247133907528650L;

    /**
     * 模板編碼
     */
    protected String templateCode;

    /**
     * 模板參數
     */
    protected Map<String, String> templateParam;

    /**
     * 構造器 通過設置模板參數和模板編碼 作為參數獲取消息內容
     */
    public TemplateDTO(String templateCode, Map<String, String> templateParam){
        this.templateCode = templateCode;
        this.templateParam = templateParam;
    }

    public TemplateDTO(){

    }
}
