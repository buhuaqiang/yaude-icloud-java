package com.yaude.modules.system.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SysDepartUsersVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**部門id*/
	private String depId;
	/**對應的用戶id集合*/
	private List<String> userIdList;
	public SysDepartUsersVO(String depId, List<String> userIdList) {
		super();
		this.depId = depId;
		this.userIdList = userIdList;
	}
    //update-begin--Author:kangxiaolin  Date:20190908 for：[512][部門管理]點擊添加已有用戶失敗修復--------------------

	public SysDepartUsersVO(){

	}
    //update-begin--Author:kangxiaolin  Date:20190908 for：[512][部門管理]點擊添加已有用戶失敗修復--------------------

}
