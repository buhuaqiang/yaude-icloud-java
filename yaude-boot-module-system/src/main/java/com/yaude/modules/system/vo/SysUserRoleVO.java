package com.yaude.modules.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysUserRoleVO implements Serializable{
	private static final long serialVersionUID = 1L;

	/**部門id*/
	private String roleId;
	/**對應的用戶id集合*/
	private List<String> userIdList;

	public SysUserRoleVO() {
		super();
	}

	public SysUserRoleVO(String roleId, List<String> userIdList) {
		super();
		this.roleId = roleId;
		this.userIdList = userIdList;
	}

}
