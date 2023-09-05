package com.yaude.modules.message.entity;

import com.yaude.common.aspect.annotation.Dict;
import com.yaude.common.system.base.entity.JeecgEntity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_sms")
public class SysMessage extends JeecgEntity {
	/**推送內容*/
	@Excel(name = "推送內容", width = 15)
	private java.lang.String esContent;
	/**推送所需參數Json格式*/
	@Excel(name = "推送所需參數Json格式", width = 15)
	private java.lang.String esParam;
	/**接收人*/
	@Excel(name = "接收人", width = 15)
	private java.lang.String esReceiver;
	/**推送失敗原因*/
	@Excel(name = "推送失敗原因", width = 15)
	private java.lang.String esResult;
	/**發送次數*/
	@Excel(name = "發送次數", width = 15)
	private java.lang.Integer esSendNum;
	/**推送狀態 0未推送 1推送成功 2推送失敗*/
	@Excel(name = "推送狀態 0未推送 1推送成功 2推送失敗", width = 15)
	@Dict(dicCode = "msgSendStatus")
	private java.lang.String esSendStatus;
	/**推送時間*/
	@Excel(name = "推送時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date esSendTime;
	/**消息標題*/
	@Excel(name = "消息標題", width = 15)
	private java.lang.String esTitle;
	/**推送方式：1短信 2郵件 3微信*/
	@Excel(name = "推送方式：1短信 2郵件 3微信", width = 15)
	@Dict(dicCode = "msgType")
	private java.lang.String esType;
	/**備注*/
	@Excel(name = "備注", width = 15)
	private java.lang.String remark;
}
