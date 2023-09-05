package com.yaude.modules.message.handle.impl;

import com.yaude.modules.message.handle.ISendMsgHandle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsSendMsgHandle implements ISendMsgHandle {

	@Override
	public void SendMsg(String es_receiver, String es_title, String es_content) {
		// TODO Auto-generated method stub
		log.info("發短信");
	}

}
