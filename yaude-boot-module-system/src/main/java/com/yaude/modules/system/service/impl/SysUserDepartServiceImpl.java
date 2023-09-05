package com.yaude.modules.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysDepart;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.entity.SysUserDepart;
import com.yaude.modules.system.mapper.SysUserDepartMapper;
import com.yaude.modules.system.model.DepartIdModel;
import com.yaude.modules.system.service.ISysDepartService;
import com.yaude.modules.system.service.ISysUserDepartService;
import com.yaude.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <P>
 * 用戶部門表實現類
 * <p/>
 * @Author ZhiLin
 *@since 2019-02-22
 */
@Service
public class SysUserDepartServiceImpl extends ServiceImpl<SysUserDepartMapper, SysUserDepart> implements ISysUserDepartService {
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysUserService sysUserService;
	

	/**
	 * 根據用戶id查詢部門信息
	 */
	@Override
	public List<DepartIdModel> queryDepartIdsOfUser(String userId) {
		LambdaQueryWrapper<SysUserDepart> queryUDep = new LambdaQueryWrapper<SysUserDepart>();
		LambdaQueryWrapper<SysDepart> queryDep = new LambdaQueryWrapper<SysDepart>();
		try {
			queryUDep.eq(SysUserDepart::getUserId, userId);
			List<String> depIdList = new ArrayList<>();
			List<DepartIdModel> depIdModelList = new ArrayList<>();
			List<SysUserDepart> userDepList = this.list(queryUDep);
			if(userDepList != null && userDepList.size() > 0) {
			for(SysUserDepart userDepart : userDepList) {
					depIdList.add(userDepart.getDepId());
				}
			queryDep.in(SysDepart::getId, depIdList);
			List<SysDepart> depList = sysDepartService.list(queryDep);
			if(depList != null || depList.size() > 0) {
				for(SysDepart depart : depList) {
					depIdModelList.add(new DepartIdModel().convertByUserDepart(depart));
				}
			}
			return depIdModelList;
			}
		}catch(Exception e) {
			e.fillInStackTrace();
		}
		return null;
		
		
	}


	/**
	 * 根據部門id查詢用戶信息
	 */
	@Override
	public List<SysUser> queryUserByDepId(String depId) {
		LambdaQueryWrapper<SysUserDepart> queryUDep = new LambdaQueryWrapper<SysUserDepart>();
		queryUDep.eq(SysUserDepart::getDepId, depId);
		List<String> userIdList = new ArrayList<>();
		List<SysUserDepart> uDepList = this.list(queryUDep);
		if(uDepList != null && uDepList.size() > 0) {
			for(SysUserDepart uDep : uDepList) {
				userIdList.add(uDep.getUserId());
			}
			List<SysUser> userList = (List<SysUser>) sysUserService.listByIds(userIdList);
			//update-begin-author:taoyan date:201905047 for:接口調用查詢返回結果不能返回密碼相關信息
			for (SysUser sysUser : userList) {
				sysUser.setSalt("");
				sysUser.setPassword("");
			}
			//update-end-author:taoyan date:201905047 for:接口調用查詢返回結果不能返回密碼相關信息
			return userList;
		}
		return new ArrayList<SysUser>();
	}

	/**
	 * 根據部門code，查詢當前部門和下級部門的 用戶信息
	 */
	@Override
	public List<SysUser> queryUserByDepCode(String depCode,String realname) {
		//update-begin-author:taoyan date:20210422 for: 根據部門選擇用戶接口代碼優化
		if(oConvertUtils.isNotEmpty(realname)){
			realname = realname.trim();
		}
		List<SysUser> userList = this.baseMapper.queryDepartUserList(depCode, realname);
		Map<String, SysUser> map = new HashMap<String, SysUser>();
		for (SysUser sysUser : userList) {
			// 返回的用戶數據去掉密碼信息
			sysUser.setSalt("");
			sysUser.setPassword("");
			map.put(sysUser.getId(), sysUser);
		}
		return new ArrayList<SysUser>(map.values());
		//update-end-author:taoyan date:20210422 for: 根據部門選擇用戶接口代碼優化

	}

	@Override
	public IPage<SysUser> queryDepartUserPageList(String departId, String username, String realname, int pageSize, int pageNo) {
		IPage<SysUser> pageList = null;
		// 部門ID不存在 直接查詢用戶表即可
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		if(oConvertUtils.isEmpty(departId)){
			LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
			if(oConvertUtils.isNotEmpty(username)){
				query.like(SysUser::getUsername, username);
			}
			pageList = sysUserService.page(page, query);
		}else{
			// 有部門ID 需要走自定義sql
			SysDepart sysDepart = sysDepartService.getById(departId);
			pageList = this.baseMapper.queryDepartUserPageList(page, sysDepart.getOrgCode(), username, realname);
		}
		List<SysUser> userList = pageList.getRecords();
		if(userList!=null && userList.size()>0){
			List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
			Map<String, SysUser> map = new HashMap<String, SysUser>();
			if(userIds!=null && userIds.size()>0){
				// 查部門名稱
				Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
				userList.forEach(item->{
					//TODO 臨時借用這個字段用于頁面展示
					item.setOrgCodeTxt(useDepNames.get(item.getId()));
					item.setSalt("");
					item.setPassword("");
					// 去重
					map.put(item.getId(), item);
				});
			}
			pageList.setRecords(new ArrayList<SysUser>(map.values()));
		}
		return pageList;
	}

}
