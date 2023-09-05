package com.yaude.modules.message.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 發送消息實體
 */
@Data
public class MsgParams implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	/*消息類型*/
	private String msgType;
	/*消息接收方*/
	private String receiver;
	/*消息模板碼*/
	private String templateCode;
	/*測試數據*/
	private String testData;
	
}
