package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜單權限規則表
 * </p>
 *
 * @Author huangzhilin
 * @since 2019-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermissionDataRule implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	
	/**
	 * 對應的菜單id
	 */
	private String permissionId;
	
	/**
	 * 規則名稱
	 */
	private String ruleName;
	
	/**
	 * 字段
	 */
	private String ruleColumn;
	
	/**
	 * 條件
	 */
	private String ruleConditions;
	
	/**
	 * 規則值
	 */
	private String ruleValue;
	
	/**
	 * 狀態值 1有效 0無效
	 */
	private String status;
	
	/**
	 * 創建時間
	 */
	private Date createTime;
	
	/**
	 * 創建人
	 */
	private String createBy;
	
	/**
	 * 修改時間
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateBy;
}
