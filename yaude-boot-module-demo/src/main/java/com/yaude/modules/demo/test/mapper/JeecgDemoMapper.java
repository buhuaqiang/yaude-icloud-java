package com.yaude.modules.demo.test.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.yaude.modules.demo.test.entity.JeecgDemo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: jeecg 測試demo
 * @Author: jeecg-boot
 * @Date:  2018-12-29
 * @Version: V1.0
 */
public interface JeecgDemoMapper extends BaseMapper<JeecgDemo> {

	public List<JeecgDemo> getDemoByName(@Param("name") String name);
	
	/**
	 * 查詢列表數據 直接傳數據權限的sql進行數據過濾
	 * @param page
	 * @param permissionSql
	 * @return
	 */
	public IPage<JeecgDemo> queryListWithPermission(Page<JeecgDemo> page, @Param("permissionSql") String permissionSql);

	/**
	 * 根據前綴獲取所有有效權限
	 * @param permsPrefix
	 * @return
	 */
	public List<String> queryAllAuth(@Param("permsPrefix") String permsPrefix);

	/**
	 * 查詢用戶已授權字段
	 * @param userId
	 * @param permsPrefix
	 * @return
	 */
	public List<String> queryUserAuth(@Param("userId") String userId, @Param("permsPrefix") String permsPrefix);

}
