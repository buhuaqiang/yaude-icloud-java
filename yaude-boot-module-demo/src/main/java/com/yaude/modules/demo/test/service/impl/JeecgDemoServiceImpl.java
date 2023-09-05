package com.yaude.modules.demo.test.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.modules.demo.test.entity.JeecgDemo;
import com.yaude.modules.demo.test.mapper.JeecgDemoMapper;
import com.yaude.modules.demo.test.service.IJeecgDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: jeecg 測試demo
 * @Author: jeecg-boot
 * @Date:  2018-12-29
 * @Version: V1.0
 */
@Service
public class JeecgDemoServiceImpl extends ServiceImpl<JeecgDemoMapper, JeecgDemo> implements IJeecgDemoService {
	@Autowired
	JeecgDemoMapper jeecgDemoMapper;
	
	/**
	 * 事務控制在service層面
	 * 加上注解：@Transactional，聲明的方法就是一個獨立的事務（有異常DB操作全部回滾）
	 */
	@Override
	@Transactional
	public void testTran() {
		JeecgDemo pp = new JeecgDemo();
		pp.setAge(1111);
		pp.setName("測試事務  小白兔 1");
		jeecgDemoMapper.insert(pp);
		
		JeecgDemo pp2 = new JeecgDemo();
		pp2.setAge(2222);
		pp2.setName("測試事務  小白兔 2");
		jeecgDemoMapper.insert(pp2);
		
		Integer.parseInt("hello");//自定義異常
		
		JeecgDemo pp3 = new JeecgDemo();
		pp3.setAge(3333);
		pp3.setName("測試事務  小白兔 3");
		jeecgDemoMapper.insert(pp3);
		return ;
	}


	/**
	 * 緩存注解測試： redis
	 */
	@Override
	@Cacheable(cacheNames = CacheConstant.TEST_DEMO_CACHE, key = "#id")
	public JeecgDemo getByIdCacheable(String id) {
		JeecgDemo t = jeecgDemoMapper.selectById(id);
		System.err.println("---未讀緩存，讀取數據庫---");
		System.err.println(t);
		return t;
	}


	@Override
	public IPage<JeecgDemo> queryListWithPermission(int pageSize,int pageNo) {
		Page<JeecgDemo> page = new Page<>(pageNo, pageSize);
		//編程方式，獲取當前請求的數據權限規則SQL片段
		String sql = QueryGenerator.installAuthJdbc(JeecgDemo.class);
		return this.baseMapper.queryListWithPermission(page, sql);
	}

	@Override
	public String getExportFields() {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		//權限配置列導出示例
		//1.配置前綴與菜單中配置的列前綴一致
		List<String> noAuthList = new ArrayList<>();
		List<String> exportFieldsList = new ArrayList<>();
		String permsPrefix = "testdemo:";
		//查詢配置菜單有效字段
		List<String> allAuth = this.jeecgDemoMapper.queryAllAuth(permsPrefix);
		//查詢已授權字段
		List<String> userAuth = this.jeecgDemoMapper.queryUserAuth(sysUser.getId(),permsPrefix);
		//列出未授權字段
		for(String perms : allAuth){
			if(!userAuth.contains(perms)){
				noAuthList.add(perms.substring(permsPrefix.length()));
			}
		}
		//實體類中字段與未授權字段比較，列出需導出字段
		Field[] fileds = JeecgDemo.class.getDeclaredFields();
		List<Field> list = new ArrayList(Arrays.asList(fileds));
		for(Field field : list){
			if(!noAuthList.contains(field.getName())){
				exportFieldsList.add(field.getName());
			}
		}
		return exportFieldsList != null && exportFieldsList.size()>0 ? String.join(",", exportFieldsList) : "";
	}

}
