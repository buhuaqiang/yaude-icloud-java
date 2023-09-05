package com.yaude.common.constant;
/**
 * 數據庫上下文常量
 */
public interface DataBaseConstant {
	//*********數據庫類型****************************************
	public static final String DB_TYPE_MYSQL = "MYSQL";
	public static final String DB_TYPE_ORACLE = "ORACLE";
	public static final String DB_TYPE_DM = "DM";//達夢數據庫
	public static final String DB_TYPE_POSTGRESQL = "POSTGRESQL";
	public static final String DB_TYPE_SQLSERVER = "SQLSERVER";
	public static final String DB_TYPE_MARIADB = "MARIADB";

//	// 數據庫類型，對應 database_type 字典
//	public static final String DB_TYPE_MYSQL_NUM = "1";
//	public static final String DB_TYPE_MYSQL7_NUM = "6";
//	public static final String DB_TYPE_ORACLE_NUM = "2";
//	public static final String DB_TYPE_SQLSERVER_NUM = "3";
//	public static final String DB_TYPE_POSTGRESQL_NUM = "4";
//	public static final String DB_TYPE_MARIADB_NUM = "5";

	//*********系統上下文變量****************************************
	/**
	 * 數據-所屬機構編碼
	 */
	public static final String SYS_ORG_CODE = "sysOrgCode";
	/**
	 * 數據-所屬機構編碼
	 */
	public static final String SYS_ORG_CODE_TABLE = "sys_org_code";
	/**
	 * 數據-所屬機構編碼
	 */
	public static final String SYS_MULTI_ORG_CODE = "sysMultiOrgCode";
	/**
	 * 數據-所屬機構編碼
	 */
	public static final String SYS_MULTI_ORG_CODE_TABLE = "sys_multi_org_code";
	/**
	 * 數據-系統用戶編碼（對應登錄用戶賬號）
	 */
	public static final String SYS_USER_CODE = "sysUserCode";
	/**
	 * 數據-系統用戶編碼（對應登錄用戶賬號）
	 */
	public static final String SYS_USER_CODE_TABLE = "sys_user_code";
	
	/**
	 * 登錄用戶真實姓名
	 */
	public static final String SYS_USER_NAME = "sysUserName";
	/**
	 * 登錄用戶真實姓名
	 */
	public static final String SYS_USER_NAME_TABLE = "sys_user_name";
	/**
	 * 系統日期"yyyy-MM-dd"
	 */
	public static final String SYS_DATE = "sysDate";
	/**
	 * 系統日期"yyyy-MM-dd"
	 */
	public static final String SYS_DATE_TABLE = "sys_date";
	/**
	 * 系統時間"yyyy-MM-dd HH:mm"
	 */
	public static final String SYS_TIME = "sysTime";
	/**
	 * 系統時間"yyyy-MM-dd HH:mm"
	 */
	public static final String SYS_TIME_TABLE = "sys_time";
	/**
	 * 數據-所屬機構編碼
	 */
	public static final String SYS_BASE_PATH = "sys_base_path";
	//*********系統上下文變量****************************************
	
	
	//*********系統建表標準字段****************************************
	/**
	 * 創建者登錄名稱
	 */
	public static final String CREATE_BY_TABLE = "create_by";
	/**
	 * 創建者登錄名稱
	 */
	public static final String CREATE_BY = "createBy";
	/**
	 * 創建日期時間
	 */
	public static final String CREATE_TIME_TABLE = "create_time";
	/**
	 * 創建日期時間
	 */
	public static final String CREATE_TIME = "createTime";
	/**
	 * 更新用戶登錄名稱
	 */
	public static final String UPDATE_BY_TABLE = "update_by";
	/**
	 * 更新用戶登錄名稱
	 */
	public static final String UPDATE_BY = "updateBy";
	/**
	 * 更新日期時間
	 */
	public static final String UPDATE_TIME = "updateTime";
	/**
	 * 更新日期時間
	 */
	public static final String UPDATE_TIME_TABLE = "update_time";
	
	/**
	 * 業務流程狀態
	 */
	public static final String BPM_STATUS = "bpmStatus";
	/**
	 * 業務流程狀態
	 */
	public static final String BPM_STATUS_TABLE = "bpm_status";
	//*********系統建表標準字段****************************************


	/**
	 * 租戶ID 實體字段名
	 */
	String TENANT_ID = "tenantId";
	/**
	 * 租戶ID 數據庫字段名
	 */
	String TENANT_ID_TABLE = "tenant_id";
}
