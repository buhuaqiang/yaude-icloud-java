package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 用戶代理人設置
 * @Author: jeecg-boot
 * @Date:  2019-04-17
 * @Version: V1.0
 */
@Data
@TableName("sys_user_agent")
public class SysUserAgent implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**序號*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**用戶名*/
	@Excel(name = "用戶名", width = 15)
	private java.lang.String userName;
	/**代理人用戶名*/
	@Excel(name = "代理人用戶名", width = 15)
	private java.lang.String agentUserName;
	/**代理開始時間*/
	@Excel(name = "代理開始時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date startTime;
	/**代理結束時間*/
	@Excel(name = "代理結束時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date endTime;
	/**狀態0無效1有效*/
	@Excel(name = "狀態0無效1有效", width = 15)
	private java.lang.String status;
	/**創建人名稱*/
	@Excel(name = "創建人名稱", width = 15)
	private java.lang.String createName;
	/**創建人登錄名稱*/
	@Excel(name = "創建人登錄名稱", width = 15)
	private java.lang.String createBy;
	/**創建日期*/
	@Excel(name = "創建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**更新人名稱*/
	@Excel(name = "更新人名稱", width = 15)
	private java.lang.String updateName;
	/**更新人登錄名稱*/
	@Excel(name = "更新人登錄名稱", width = 15)
	private java.lang.String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**所屬部門*/
	@Excel(name = "所屬部門", width = 15)
	private java.lang.String sysOrgCode;
	/**所屬公司*/
	@Excel(name = "所屬公司", width = 15)
	private java.lang.String sysCompanyCode;
}
