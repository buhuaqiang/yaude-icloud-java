package com.yaude.modules.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yaude.common.constant.CacheConstant;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.exception.JeecgBootException;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysPermission;
import com.yaude.modules.system.entity.SysPermissionDataRule;
import com.yaude.modules.system.mapper.SysDepartPermissionMapper;
import com.yaude.modules.system.mapper.SysDepartRolePermissionMapper;
import com.yaude.modules.system.mapper.SysPermissionMapper;
import com.yaude.modules.system.mapper.SysRolePermissionMapper;
import com.yaude.modules.system.model.TreeModel;
import com.yaude.modules.system.service.ISysPermissionDataRuleService;
import com.yaude.modules.system.service.ISysPermissionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 菜單權限表 服務實現類
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

	@Resource
	private SysPermissionMapper sysPermissionMapper;
	
	@Resource
	private ISysPermissionDataRuleService permissionDataRuleService;

	@Resource
	private SysRolePermissionMapper sysRolePermissionMapper;

	@Resource
	private SysDepartPermissionMapper sysDepartPermissionMapper;

	@Resource
	private SysDepartRolePermissionMapper sysDepartRolePermissionMapper;

	@Override
	public List<TreeModel> queryListByParentId(String parentId) {
		return sysPermissionMapper.queryListByParentId(parentId);
	}

	/**
	  * 真實刪除
	 */
	@Override
	@Transactional
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void deletePermission(String id) throws JeecgBootException {
		SysPermission sysPermission = this.getById(id);
		if(sysPermission==null) {
			throw new JeecgBootException("未找到菜單信息");
		}
		String pid = sysPermission.getParentId();
		if(oConvertUtils.isNotEmpty(pid)) {
			int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
			if(count==1) {
				//若父節點無其他子節點，則該父節點是葉子節點
				this.sysPermissionMapper.setMenuLeaf(pid, 1);
			}
		}
		sysPermissionMapper.deleteById(id);
		// 該節點可能是子節點但也可能是其它節點的父節點,所以需要級聯刪除
		this.removeChildrenBy(sysPermission.getId());
		//關聯刪除
		Map map = new HashMap<>();
		map.put("permission_id",id);
		//刪除數據規則
		this.deletePermRuleByPermId(id);
		//刪除角色授權表
		sysRolePermissionMapper.deleteByMap(map);
		//刪除部門權限表
		sysDepartPermissionMapper.deleteByMap(map);
		//刪除部門角色授權
		sysDepartRolePermissionMapper.deleteByMap(map);
	}
	
	/**
	 * 根據父id刪除其關聯的子節點數據
	 * 
	 * @return
	 */
	public void removeChildrenBy(String parentId) {
		LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
		// 封裝查詢條件parentId為主鍵,
		query.eq(SysPermission::getParentId, parentId);
		// 查出該主鍵下的所有子級
		List<SysPermission> permissionList = this.list(query);
		if (permissionList != null && permissionList.size() > 0) {
			String id = ""; // id
			int num = 0; // 查出的子級數量
			// 如果查出的集合不為空, 則先刪除所有
			this.remove(query);
			// 再遍歷剛才查出的集合, 根據每個對象,查找其是否仍有子級
			for (int i = 0, len = permissionList.size(); i < len; i++) {
				id = permissionList.get(i).getId();
				Map map = new HashMap<>();
				map.put("permission_id",id);
				//刪除數據規則
				this.deletePermRuleByPermId(id);
				//刪除角色授權表
				sysRolePermissionMapper.deleteByMap(map);
				//刪除部門權限表
				sysDepartPermissionMapper.deleteByMap(map);
				//刪除部門角色授權
				sysDepartRolePermissionMapper.deleteByMap(map);
				num = this.count(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
				// 如果有, 則遞歸
				if (num > 0) {
					this.removeChildrenBy(id);
				}
			}
		}
	}
	
	/**
	  * 邏輯刪除
	 */
	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	//@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true,condition="#sysPermission.menuType==2")
	public void deletePermissionLogical(String id) throws JeecgBootException {
		SysPermission sysPermission = this.getById(id);
		if(sysPermission==null) {
			throw new JeecgBootException("未找到菜單信息");
		}
		String pid = sysPermission.getParentId();
		int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
		if(count==1) {
			//若父節點無其他子節點，則該父節點是葉子節點
			this.sysPermissionMapper.setMenuLeaf(pid, 1);
		}
		sysPermission.setDelFlag(1);
		this.updateById(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void addPermission(SysPermission sysPermission) throws JeecgBootException {
		//----------------------------------------------------------------------
		//判斷是否是一級菜單，是的話清空父菜單
		if(CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
			sysPermission.setParentId(null);
		}
		//----------------------------------------------------------------------
		String pid = sysPermission.getParentId();
		if(oConvertUtils.isNotEmpty(pid)) {
			//設置父節點不為葉子節點
			this.sysPermissionMapper.setMenuLeaf(pid, 0);
		}
		sysPermission.setCreateTime(new Date());
		sysPermission.setDelFlag(0);
		sysPermission.setLeaf(true);
		this.save(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
	public void editPermission(SysPermission sysPermission) throws JeecgBootException {
		SysPermission p = this.getById(sysPermission.getId());
		//TODO 該節點判斷是否還有子節點
		if(p==null) {
			throw new JeecgBootException("未找到菜單信息");
		}else {
			sysPermission.setUpdateTime(new Date());
			//----------------------------------------------------------------------
			//Step1.判斷是否是一級菜單，是的話清空父菜單ID
			if(CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
				sysPermission.setParentId("");
			}
			//Step2.判斷菜單下級是否有菜單，無則設置為葉子節點
			int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, sysPermission.getId()));
			if(count==0) {
				sysPermission.setLeaf(true);
			}
			//----------------------------------------------------------------------
			this.updateById(sysPermission);
			
			//如果當前菜單的父菜單變了，則需要修改新父菜單和老父菜單的，葉子節點狀態
			String pid = sysPermission.getParentId();
			if((oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid)&&oConvertUtils.isNotEmpty(p.getParentId())) {
				//a.設置新的父菜單不為葉子節點
				this.sysPermissionMapper.setMenuLeaf(pid, 0);
				//b.判斷老的菜單下是否還有其他子菜單，沒有的話則設置為葉子節點
				int cc = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, p.getParentId()));
				if(cc==0) {
					if(oConvertUtils.isNotEmpty(p.getParentId())) {
						this.sysPermissionMapper.setMenuLeaf(p.getParentId(), 1);
					}
				}
				
			}
		}
		
	}

	@Override
	public List<SysPermission> queryByUser(String username) {
		return this.sysPermissionMapper.queryByUser(username);
	}

	/**
	 * 根據permissionId刪除其關聯的SysPermissionDataRule表中的數據
	 */
	@Override
	public void deletePermRuleByPermId(String id) {
		LambdaQueryWrapper<SysPermissionDataRule> query = new LambdaQueryWrapper<>();
		query.eq(SysPermissionDataRule::getPermissionId, id);
		int countValue = this.permissionDataRuleService.count(query);
		if(countValue > 0) {
			this.permissionDataRuleService.remove(query);	
		}
	}

	/**
	  *   獲取模糊匹配規則的數據權限URL
	 */
	@Override
	@Cacheable(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE)
	public List<String> queryPermissionUrlWithStar() {
		return this.baseMapper.queryPermissionUrlWithStar();
	}

	@Override
	public boolean hasPermission(String username, SysPermission sysPermission) {
		int count = baseMapper.queryCountByUsername(username,sysPermission);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean hasPermission(String username, String url) {
		SysPermission sysPermission = new SysPermission();
		sysPermission.setUrl(url);
		int count = baseMapper.queryCountByUsername(username,sysPermission);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

}
