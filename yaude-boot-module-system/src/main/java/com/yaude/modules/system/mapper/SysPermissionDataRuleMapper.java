package com.yaude.modules.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.yaude.modules.system.entity.SysPermissionDataRule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 權限規則 Mapper 接口
 * </p>
 *
 * @Author huangzhilin
 * @since 2019-04-01
 */
public interface SysPermissionDataRuleMapper extends BaseMapper<SysPermissionDataRule> {
	
	/**
	  * 根據用戶名和權限id查詢
	 * @param username
	 * @param permissionId
	 * @return
	 */
	public List<String> queryDataRuleIds(@Param("username") String username,@Param("permissionId") String permissionId);

}
