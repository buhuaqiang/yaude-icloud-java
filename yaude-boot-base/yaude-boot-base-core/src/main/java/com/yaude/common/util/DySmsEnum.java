package com.yaude.common.util;

import org.apache.commons.lang3.StringUtils;

public enum DySmsEnum {
	
	LOGIN_TEMPLATE_CODE("SMS_175435174","JEECG","code"),
	FORGET_PASSWORD_TEMPLATE_CODE("SMS_175435174","JEECG","code"),
	REGISTER_TEMPLATE_CODE("SMS_175430166","JEECG","code"),
	/**會議通知*/
	MEET_NOTICE_TEMPLATE_CODE("SMS_201480469","H5活動之家","username,title,minute,time"),
	/**我的計劃通知*/
	PLAN_NOTICE_TEMPLATE_CODE("SMS_201470515","H5活動之家","username,title,time");

	/**
	 * 短信模板編碼
	 */
	private String templateCode;
	/**
	 * 簽名
	 */
	private String signName;
	/**
	 * 短信模板必需的數據名稱，多個key以逗號分隔，此處配置作為校驗
	 */
	private String keys;
	
	private DySmsEnum(String templateCode,String signName,String keys) {
		this.templateCode = templateCode;
		this.signName = signName;
		this.keys = keys;
	}
	
	public String getTemplateCode() {
		return templateCode;
	}
	
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	
	public String getSignName() {
		return signName;
	}
	
	public void setSignName(String signName) {
		this.signName = signName;
	}
	
	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public static DySmsEnum toEnum(String templateCode) {
		if(StringUtils.isEmpty(templateCode)){
			return null;
		}
		for(DySmsEnum item : DySmsEnum.values()) {
			if(item.getTemplateCode().equals(templateCode)) {
				return item;
			}
		}
		return null;
	}
}

