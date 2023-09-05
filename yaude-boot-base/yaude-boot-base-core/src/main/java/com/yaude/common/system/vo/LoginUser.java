package com.yaude.common.system.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 在線用戶信息
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginUser {

	/**
	 * 登錄人id
	 */
	private String id;

	/**
	 * 登錄人賬號
	 */
	private String username;

	/**
	 * 登錄人名字
	 */
	private String realname;

	/**
	 * 登錄人密碼
	 */
	private String password;

     /**
      * 當前登錄部門code
      */
    private String orgCode;
	/**
	 * 頭像
	 */
	private String avatar;

	/**
	 * 生日
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	/**
	 * 性別（1：男 2：女）
	 */
	private Integer sex;

	/**
	 * 電子郵件
	 */
	private String email;

	/**
	 * 電話
	 */
	private String phone;

	/**
	 * 狀態(1：正常 2：凍結 ）
	 */
	private Integer status;
	
	private Integer delFlag;
	/**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

	/**
	 * 創建時間
	 */
	private Date createTime;

	/**
	 *  身份（1 普通員工 2 上級）
	 */
	private Integer userIdentity;

	/**
	 * 管理部門ids
	 */
	private String departIds;

	/**
	 * 職務，關聯職務表
	 */
	private String post;

	/**
	 * 座機號
	 */
	private String telephone;

	/**多租戶id配置，編輯用戶的時候設置*/
	private String relTenantIds;

	/**設備id uniapp推送用*/
	private String clientId;

}
