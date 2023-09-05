package com.yaude.common.constant;

/**
 * 	系統通告 - 發布狀態
 * @Author LeeShaoQing
 *
 */
public interface CommonSendStatus {
	
	public static final String UNPUBLISHED_STATUS_0 = "0";	//未發布
	
	public static final String PUBLISHED_STATUS_1 = "1";		//已發布
	
	public static final String REVOKE_STATUS_2 = "2";			//撤銷
	//app端推送會話標識后綴
	public static final String  APP_SESSION_SUFFIX = "_app";	//app端推送會話標識后綴



	/**流程催辦——系統通知消息模板*/
	public static final String TZMB_BPM_CUIBAN = "bpm_cuiban";
	/**標準模板—系統消息通知*/
	public static final String TZMB_SYS_TS_NOTE = "sys_ts_note";
	/**流程超時提醒——系統通知消息模板*/
	public static final String TZMB_BPM_CHAOSHI_TIP = "bpm_chaoshi_tip";
}
