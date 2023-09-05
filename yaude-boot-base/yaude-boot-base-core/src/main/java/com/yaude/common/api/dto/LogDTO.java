package com.yaude.common.api.dto;
import com.yaude.common.system.vo.LoginUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 日志對象
 * cloud api 用到的接口傳輸對象
 */
@Data
public class LogDTO implements Serializable {

    private static final long serialVersionUID = 8482720462943906924L;

    /**內容*/
    private String logContent;

    /**日志類型(0:操作日志;1:登錄日志;2:定時任務)  */
    private Integer logType;

    /**操作類型(1:添加;2:修改;3:刪除;) */
    private Integer operateType;

    /**登錄用戶 */
    private LoginUser loginUser;

    private String id;
    private String createBy;
    private Date createTime;
    private Long costTime;
    private String ip;

    /**請求參數 */
    private String requestParam;

    /**請求類型*/
    private String requestType;

    /**請求路徑*/
    private String requestUrl;

    /**請求方法 */
    private String method;

    /**操作人用戶名稱*/
    private String username;

    /**操作人用戶賬戶*/
    private String userid;

    public LogDTO(){

    }

    public LogDTO(String logContent, Integer logType, Integer operatetype){
        this.logContent = logContent;
        this.logType = logType;
        this.operateType = operatetype;
    }

    public LogDTO(String logContent, Integer logType, Integer operatetype, LoginUser loginUser){
        this.logContent = logContent;
        this.logType = logType;
        this.operateType = operatetype;
        this.loginUser = loginUser;
    }
}
