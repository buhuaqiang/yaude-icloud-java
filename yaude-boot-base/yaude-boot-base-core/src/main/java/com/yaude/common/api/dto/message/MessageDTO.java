package com.yaude.common.api.dto.message;

import com.yaude.common.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 普通消息
 */
@Data
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = -5690444483968058442L;

    /**
     * 發送人(用戶登錄賬戶)
     */
    protected String fromUser;

    /**
     * 發送給(用戶登錄賬戶)
     */
    protected String toUser;

    /**
     * 發送給所有人
     */
    protected boolean toAll;

    /**
     * 消息主題
     */
    protected String title;

    /**
     * 消息內容
     */
    protected String content;

    /**
     * 消息類型 1:消息  2:系統消息
     */
    protected String category;


    public MessageDTO(){

    }

    /**
     * 構造器1 系統消息
     */
    public MessageDTO(String fromUser,String toUser,String title, String content){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.title = title;
        this.content = content;
        //默認 都是2系統消息
        this.category = CommonConstant.MSG_CATEGORY_2;
    }

    /**
     * 構造器2 支持設置category 1:消息  2:系統消息
     */
    public MessageDTO(String fromUser,String toUser,String title, String content, String category){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.title = title;
        this.content = content;
        this.category = category;
    }

}
