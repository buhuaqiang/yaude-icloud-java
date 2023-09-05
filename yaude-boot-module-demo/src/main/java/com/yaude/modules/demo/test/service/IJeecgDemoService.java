package com.yaude.modules.demo.test.service;

import com.yaude.common.system.base.service.JeecgService;
import com.yaude.modules.demo.test.entity.JeecgDemo;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Description: jeecg 測試demo
 * @Author: jeecg-boot
 * @Date:  2018-12-29
 * @Version: V1.0
 */
public interface IJeecgDemoService extends JeecgService<JeecgDemo> {
	
	public void testTran();
	
	public JeecgDemo getByIdCacheable(String id);
	
	/**
	 * 查詢列表數據 在service中獲取數據權限sql信息
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	IPage<JeecgDemo> queryListWithPermission(int pageSize, int pageNo);

	/**
	 * 根據用戶權限獲取導出字段
	 * @return
	 */
	String getExportFields();
}
