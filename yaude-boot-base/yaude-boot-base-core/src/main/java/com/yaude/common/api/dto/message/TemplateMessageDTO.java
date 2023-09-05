package com.yaude.common.api.dto.message;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 模板消息
 */
@Data
public class TemplateMessageDTO extends TemplateDTO implements Serializable {

    private static final long serialVersionUID = 411137565170647585L;


    /**
     * 發送人(用戶登錄賬戶)
     */
    protected String fromUser;

    /**
     * 發送給(用戶登錄賬戶)
     */
    protected String toUser;

    /**
     * 消息主題
     */
    protected String title;


    public TemplateMessageDTO(){

    }

    /**
     * 構造器1 發模板消息用
     */
    public TemplateMessageDTO(String fromUser, String toUser,String title, Map<String, String> templateParam, String templateCode){
        super(templateCode, templateParam);
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.title = title;
    }



}
