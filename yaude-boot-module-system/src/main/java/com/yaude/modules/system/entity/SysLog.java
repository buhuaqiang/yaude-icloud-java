package com.yaude.modules.system.entity;

import java.util.Date;

import com.yaude.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系統日志表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 創建人
	 */
	private String createBy;

	/**
	 * 創建時間
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 更新人
	 */
	private String updateBy;

	/**
	 * 更新時間
	 */
	private Date updateTime;

	/**
	 * 耗時
	 */
	private Long costTime;

	/**
	 * IP
	 */
	private String ip;

	/**
	 * 請求參數
	 */
	private String requestParam;

	/**
	 * 請求類型
	 */
	private String requestType;

	/**
	 * 請求路徑
	 */
	private String requestUrl;
	/**
	 * 請求方法
	 */
	private String method;

	/**
	 * 操作人用戶名稱
	 */
	private String username;
	/**
	 * 操作人用戶賬戶
	 */
	private String userid;
	/**
	 * 操作詳細日志
	 */
	private String logContent;

	/**
	 * 日志類型（1登錄日志，2操作日志）
	 */
	@Dict(dicCode = "log_type")
	private Integer logType;

	/**
	 * 操作類型（1查詢，2添加，3修改，4刪除,5導入，6導出）
	 */
	@Dict(dicCode = "operate_type")
	private Integer operateType;

}
