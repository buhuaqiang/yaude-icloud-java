package com.yaude.modules.system.service;


import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.entity.SysUserDepart;
import com.yaude.modules.system.model.DepartIdModel;


import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * SysUserDpeart用戶組織機構service
 * </p>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<SysUserDepart> {
	

	/**
	 * 根據指定用戶id查詢部門信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);
	

	/**
	 * 根據部門id查詢用戶信息
	 * @param depId
	 * @return
	 */
	List<SysUser> queryUserByDepId(String depId);
  	/**
	 * 根據部門code，查詢當前部門和下級部門的用戶信息
	 */
	List<SysUser> queryUserByDepCode(String depCode,String realname);

	/**
	 * 用戶組件數據查詢
	 * @param departId
	 * @param username
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	IPage<SysUser> queryDepartUserPageList(String departId, String username, String realname, int pageSize, int pageNo);

}
