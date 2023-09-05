package com.yaude.common.api.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 帶業務參數的模板消息
 */
@Data
public class BusTemplateMessageDTO extends TemplateMessageDTO implements Serializable {

    private static final long serialVersionUID = -4277810906346929459L;

    /**
     * 業務類型
     */
    private String busType;

    /**
     * 業務id
     */
    private String busId;

    public BusTemplateMessageDTO(){

    }

    /**
     * 構造 帶業務參數的模板消息
     * @param fromUser
     * @param toUser
     * @param title
     * @param templateParam
     * @param templateCode
     * @param busType
     * @param busId
     */
    public BusTemplateMessageDTO(String fromUser, String toUser, String title, Map<String, String> templateParam, String templateCode, String busType, String busId){
        super(fromUser, toUser, title, templateParam, templateCode);
        this.busId = busId;
        this.busType = busType;
    }
}
