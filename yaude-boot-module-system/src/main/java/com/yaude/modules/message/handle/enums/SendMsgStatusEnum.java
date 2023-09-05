package com.yaude.modules.message.handle.enums;

/**
 * 推送狀態枚舉
 */
public enum SendMsgStatusEnum {

//推送狀態 0未推送 1推送成功 2推送失敗
	WAIT("0"), SUCCESS("1"), FAIL("2");

	private String code;

	private SendMsgStatusEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setStatusCode(String code) {
		this.code = code;
	}

}