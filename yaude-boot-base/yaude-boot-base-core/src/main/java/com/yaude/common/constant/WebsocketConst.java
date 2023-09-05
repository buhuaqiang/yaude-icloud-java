package com.yaude.common.constant;

/**
 * @Description: Websocket常量類
 * @author: taoyan
 * @date: 2020年03月23日
 */
public class WebsocketConst {


    /**
     * 消息json key:cmd
     */
    public static final String MSG_CMD = "cmd";

    /**
     * 消息json key:msgId
     */
    public static final String MSG_ID = "msgId";

    /**
     * 消息json key:msgTxt
     */
    public static final String MSG_TXT = "msgTxt";

    /**
     * 消息json key:userId
     */
    public static final String MSG_USER_ID = "userId";

    /**
     * 消息類型 heartcheck
     */
    public static final String CMD_CHECK = "heartcheck";

    /**
     * 消息類型 user 用戶消息
     */
    public static final String CMD_USER = "user";

    /**
     * 消息類型 topic 系統通知
     */
    public static final String CMD_TOPIC = "topic";

    /**
     * 消息類型 email
     */
    public static final String CMD_EMAIL = "email";

    /**
     * 消息類型 meetingsign 會議簽到
     */
    public static final String CMD_SIGN = "sign";

    /**
     * 消息類型 新聞發布/取消
     */
    public static final String NEWS_PUBLISH = "publish";

}
