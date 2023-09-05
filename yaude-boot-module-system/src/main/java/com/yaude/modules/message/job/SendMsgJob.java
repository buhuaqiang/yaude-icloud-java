package com.yaude.modules.message.job;

import java.util.List;

import com.yaude.modules.message.entity.SysMessage;
import com.yaude.modules.message.handle.ISendMsgHandle;
import com.yaude.modules.message.handle.enums.SendMsgStatusEnum;
import com.yaude.modules.message.handle.enums.SendMsgTypeEnum;
import com.yaude.modules.message.service.ISysMessageService;
import com.yaude.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 發送消息任務
 */

@Slf4j
public class SendMsgJob implements Job {

	@Autowired
	private ISysMessageService sysMessageService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		log.info(String.format(" Jeecg-Boot 發送消息任務 SendMsgJob !  時間:" + DateUtils.getTimestamp()));

		// 1.讀取消息中心數據，只查詢未發送的和發送失敗不超過次數的
		QueryWrapper<SysMessage> queryWrapper = new QueryWrapper<SysMessage>();
		queryWrapper.eq("es_send_status", SendMsgStatusEnum.WAIT.getCode())
				.or(i -> i.eq("es_send_status", SendMsgStatusEnum.FAIL.getCode()).lt("es_send_num", 6));
		List<SysMessage> sysMessages = sysMessageService.list(queryWrapper);
		System.out.println(sysMessages);
		// 2.根據不同的類型走不通的發送實現類
		for (SysMessage sysMessage : sysMessages) {
			ISendMsgHandle sendMsgHandle = null;
			try {
				if (sysMessage.getEsType().equals(SendMsgTypeEnum.EMAIL.getType())) {
					sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.EMAIL.getImplClass()).newInstance();
				} else if (sysMessage.getEsType().equals(SendMsgTypeEnum.SMS.getType())) {
					sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.SMS.getImplClass()).newInstance();
				} else if (sysMessage.getEsType().equals(SendMsgTypeEnum.WX.getType())) {
					sendMsgHandle = (ISendMsgHandle) Class.forName(SendMsgTypeEnum.WX.getImplClass()).newInstance();
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			Integer sendNum = sysMessage.getEsSendNum();
			try {
				sendMsgHandle.SendMsg(sysMessage.getEsReceiver(), sysMessage.getEsTitle(),
						sysMessage.getEsContent().toString());
				// 發送消息成功
				sysMessage.setEsSendStatus(SendMsgStatusEnum.SUCCESS.getCode());
			} catch (Exception e) {
				e.printStackTrace();
				// 發送消息出現異常
				sysMessage.setEsSendStatus(SendMsgStatusEnum.FAIL.getCode());
			}
			sysMessage.setEsSendNum(++sendNum);
			// 發送結果回寫到數據庫
			sysMessageService.updateById(sysMessage);
		}

	}

}
