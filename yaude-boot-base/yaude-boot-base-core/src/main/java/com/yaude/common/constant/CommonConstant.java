package com.yaude.common.constant;

public interface CommonConstant {

	/**
	 * 正常狀態
	 */
	public static final Integer STATUS_NORMAL = 0;

	/**
	 * 禁用狀態
	 */
	public static final Integer STATUS_DISABLE = -1;

	/**
	 * 刪除標志
	 */
	public static final Integer DEL_FLAG_1 = 1;

	/**
	 * 未刪除
	 */
	public static final Integer DEL_FLAG_0 = 0;

	/**
	 * 系統日志類型： 登錄
	 */
	public static final int LOG_TYPE_1 = 1;
	
	/**
	 * 系統日志類型： 操作
	 */
	public static final int LOG_TYPE_2 = 2;

	/**
	 * 操作日志類型： 查詢
	 */
	public static final int OPERATE_TYPE_1 = 1;
	
	/**
	 * 操作日志類型： 添加
	 */
	public static final int OPERATE_TYPE_2 = 2;
	
	/**
	 * 操作日志類型： 更新
	 */
	public static final int OPERATE_TYPE_3 = 3;
	
	/**
	 * 操作日志類型： 刪除
	 */
	public static final int OPERATE_TYPE_4 = 4;
	
	/**
	 * 操作日志類型： 倒入
	 */
	public static final int OPERATE_TYPE_5 = 5;
	
	/**
	 * 操作日志類型： 導出
	 */
	public static final int OPERATE_TYPE_6 = 6;
	
	
	/** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_OK_200 = 200;
    
    /**訪問權限認證未通過 510*/
    public static final Integer SC_JEECG_NO_AUTHZ=510;

    /** 登錄用戶Shiro權限緩存KEY前綴 */
    public static String PREFIX_USER_SHIRO_CACHE  = "shiro:cache:ShiroRealm.authorizationCache:";
    /** 登錄用戶Token令牌緩存KEY前綴 */
    public static final String PREFIX_USER_TOKEN  = "prefix_user_token_";
    /** Token緩存時間：3600秒即一小時 */
    public static final int  TOKEN_EXPIRE_TIME  = 3600;
    

    /**
     *  0：一級菜單
     */
    public static final Integer MENU_TYPE_0  = 0;
   /**
    *  1：子菜單 
    */
    public static final Integer MENU_TYPE_1  = 1;
    /**
     *  2：按鈕權限
     */
    public static final Integer MENU_TYPE_2  = 2;
    
    /**通告對象類型（USER:指定用戶，ALL:全體用戶）*/
    public static final String MSG_TYPE_UESR  = "USER";
    public static final String MSG_TYPE_ALL  = "ALL";
    
    /**發布狀態（0未發布，1已發布，2已撤銷）*/
    public static final String NO_SEND  = "0";
    public static final String HAS_SEND  = "1";
    public static final String HAS_CANCLE  = "2";
    
    /**閱讀狀態（0未讀，1已讀）*/
    public static final String HAS_READ_FLAG  = "1";
    public static final String NO_READ_FLAG  = "0";
    
    /**優先級（L低，M中，H高）*/
    public static final String PRIORITY_L  = "L";
    public static final String PRIORITY_M  = "M";
    public static final String PRIORITY_H  = "H";
    
    /**
     * 短信模板方式  0 .登錄模板、1.注冊模板、2.忘記密碼模板
     */
    public static final String SMS_TPL_TYPE_0  = "0";
    public static final String SMS_TPL_TYPE_1  = "1";
    public static final String SMS_TPL_TYPE_2  = "2";
    
    /**
     * 狀態(0無效1有效)
     */
    public static final String STATUS_0 = "0";
    public static final String STATUS_1 = "1";
    
    /**
     * 同步工作流引擎1同步0不同步
     */
    public static final Integer ACT_SYNC_1 = 1;
    public static final Integer ACT_SYNC_0 = 0;

    /**
     * 消息類型1:通知公告2:系統消息
     */
    public static final String MSG_CATEGORY_1 = "1";
    public static final String MSG_CATEGORY_2 = "2";
    
    /**
     * 是否配置菜單的數據權限 1是0否
     */
    public static final Integer RULE_FLAG_0 = 0;
    public static final Integer RULE_FLAG_1 = 1;

    /**
     * 是否用戶已被凍結 1正常(解凍) 2凍結
     */
    public static final Integer USER_UNFREEZE = 1;
    public static final Integer USER_FREEZE = 2;
    
    /**字典翻譯文本后綴*/
    public static final String DICT_TEXT_SUFFIX = "_dictText";

    /**
     * 表單設計器主表類型
     */
    public static final Integer DESIGN_FORM_TYPE_MAIN = 1;

    /**
     * 表單設計器子表表類型
     */
    public static final Integer DESIGN_FORM_TYPE_SUB = 2;

    /**
     * 表單設計器URL授權通過
     */
    public static final Integer DESIGN_FORM_URL_STATUS_PASSED = 1;

    /**
     * 表單設計器URL授權未通過
     */
    public static final Integer DESIGN_FORM_URL_STATUS_NOT_PASSED = 2;

    /**
     * 表單設計器新增 Flag
     */
    public static final String DESIGN_FORM_URL_TYPE_ADD = "add";
    /**
     * 表單設計器修改 Flag
     */
    public static final String DESIGN_FORM_URL_TYPE_EDIT = "edit";
    /**
     * 表單設計器詳情 Flag
     */
    public static final String DESIGN_FORM_URL_TYPE_DETAIL = "detail";
    /**
     * 表單設計器復用數據 Flag
     */
    public static final String DESIGN_FORM_URL_TYPE_REUSE = "reuse";
    /**
     * 表單設計器編輯 Flag （已棄用）
     */
    public static final String DESIGN_FORM_URL_TYPE_VIEW = "view";

    /**
     * online參數值設置（是：Y, 否：N）
     */
    public static final String ONLINE_PARAM_VAL_IS_TURE = "Y";
    public static final String ONLINE_PARAM_VAL_IS_FALSE = "N";

    /**
     * 文件上傳類型（本地：local，Minio：minio，阿里云：alioss）
     */
    public static final String UPLOAD_TYPE_LOCAL = "local";
    public static final String UPLOAD_TYPE_MINIO = "minio";
    public static final String UPLOAD_TYPE_OSS = "alioss";

    /**
     * 文檔上傳自定義桶名稱
     */
    public static final String UPLOAD_CUSTOM_BUCKET = "eoafile";
    /**
     * 文檔上傳自定義路徑
     */
    public static final String UPLOAD_CUSTOM_PATH = "eoafile";
    /**
     * 文件外鏈接有效天數
     */
    public static final Integer UPLOAD_EFFECTIVE_DAYS = 1;

    /**
     * 員工身份 （1:普通員工  2:上級）
     */
    public static final Integer USER_IDENTITY_1 = 1;
    public static final Integer USER_IDENTITY_2 = 2;

    /** sys_user 表 username 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_SYS_USER_USERNAME = "uniq_sys_user_username";
    /** sys_user 表 work_no 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_SYS_USER_WORK_NO = "uniq_sys_user_work_no";
    /** sys_user 表 phone 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_SYS_USER_PHONE = "uniq_sys_user_phone";
    /** sys_user 表 email 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_SYS_USER_EMAIL = "uniq_sys_user_email";
    /** sys_quartz_job 表 job_class_name 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_JOB_CLASS_NAME = "uniq_job_class_name";
    /** sys_position 表 code 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_CODE = "uniq_code";
    /** sys_role 表 code 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_SYS_ROLE_CODE = "uniq_sys_role_role_code";
    /** sys_depart 表 code 唯一鍵索引 */
    public static final String SQL_INDEX_UNIQ_DEPART_ORG_CODE = "uniq_depart_org_code";
    /**
     * 在線聊天 是否為默認分組
     */
    public static final String IM_DEFAULT_GROUP = "1";
    /**
     * 在線聊天 圖片文件保存路徑
     */
    public static final String IM_UPLOAD_CUSTOM_PATH = "imfile";
    /**
     * 在線聊天 用戶狀態
     */
    public static final String IM_STATUS_ONLINE = "online";

    /**
     * 在線聊天 SOCKET消息類型
     */
    public static final String IM_SOCKET_TYPE = "chatMessage";

    /**
     * 在線聊天 是否開啟默認添加好友 1是 0否
     */
    public static final String IM_DEFAULT_ADD_FRIEND = "1";

    /**
     * 在線聊天 用戶好友緩存前綴
     */
    public static final String IM_PREFIX_USER_FRIEND_CACHE = "sys:cache:im:im_prefix_user_friend_";

    /**
     * 考勤補卡業務狀態 （1：同意  2：不同意）
     */
    public static final String SIGN_PATCH_BIZ_STATUS_1 = "1";
    public static final String SIGN_PATCH_BIZ_STATUS_2 = "2";

    /**
     * 公文文檔上傳自定義路徑
     */
    public static final String UPLOAD_CUSTOM_PATH_OFFICIAL = "officialdoc";
     /**
     * 公文文檔下載自定義路徑
     */
    public static final String DOWNLOAD_CUSTOM_PATH_OFFICIAL = "officaldown";

    /**
     * WPS存儲值類別(1 code文號 2 text（WPS模板還是公文發文模板）)
     */
    public static final String WPS_TYPE_1="1";
    public static final String WPS_TYPE_2="2";


    public final static String X_ACCESS_TOKEN = "X-Access-Token";
    public final static String X_SIGN = "X-Sign";
    public final static String X_TIMESTAMP = "X-TIMESTAMP";

    /**
     * 多租戶 請求頭
     */
    public final static String TENANT_ID = "tenant-id";

    /**
     * 微服務讀取配置文件屬性 服務地址
     */
    public final static String CLOUD_SERVER_KEY = "spring.cloud.nacos.discovery.server-addr";

    /**
     * 第三方登錄 驗證密碼/創建用戶 都需要設置一個操作碼 防止被惡意調用
     */
    public final static String THIRD_LOGIN_CODE = "third_login_code";

    /**
     * 第三方APP同步方向：本地 --> 第三方APP
     */
    String THIRD_SYNC_TO_APP = "SYNC_TO_APP";
    /**
     * 第三方APP同步方向：第三方APP --> 本地
     */
    String THIRD_SYNC_TO_LOCAL = "SYNC_TO_LOCAL";

    /** 系統通告消息狀態：0=未發布 */
    String ANNOUNCEMENT_SEND_STATUS_0 = "0";
    /** 系統通告消息狀態：1=已發布 */
    String ANNOUNCEMENT_SEND_STATUS_1 = "1";
    /** 系統通告消息狀態：2=已撤銷 */
    String ANNOUNCEMENT_SEND_STATUS_2 = "2";

}
