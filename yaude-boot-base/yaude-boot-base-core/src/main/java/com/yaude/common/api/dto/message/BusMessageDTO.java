package com.yaude.common.api.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 帶業務參數的消息
 */
@Data
public class BusMessageDTO extends MessageDTO implements Serializable {

    private static final long serialVersionUID = 9104793287983367669L;
    /**
     * 業務類型
     */
    private String busType;

    /**
     * 業務id
     */
    private String busId;

    public BusMessageDTO(){

    }

    /**
     * 構造 帶業務參數的消息
     * @param fromUser
     * @param toUser
     * @param title
     * @param msgContent
     * @param msgCategory
     * @param busType
     * @param busId
     */
    public BusMessageDTO(String fromUser, String toUser, String title, String msgContent, String msgCategory, String busType, String busId){
        super(fromUser, toUser, title, msgContent, msgCategory);
        this.busId = busId;
        this.busType = busType;
    }
}
