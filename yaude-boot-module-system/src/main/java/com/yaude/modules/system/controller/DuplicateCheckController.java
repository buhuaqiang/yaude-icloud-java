package com.yaude.modules.system.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.SqlInjectionUtil;
import com.yaude.modules.system.mapper.SysDictMapper;
import com.yaude.modules.system.model.DuplicateCheckVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: DuplicateCheckAction
 * @Description: 重復校驗工具
 * @Author 張代浩
 * @Date 2019-03-25
 * @Version V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/duplicate")
@Api(tags="重復校驗")
public class DuplicateCheckController {

	@Autowired
	SysDictMapper sysDictMapper;

	/**
	 * 校驗數據是否在系統中是否存在
	 * 
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	@ApiOperation("重復校驗接口")
	public Result<Object> doDuplicateCheck(DuplicateCheckVo duplicateCheckVo, HttpServletRequest request) {
		Long num = null;

		log.info("----duplicate check------："+ duplicateCheckVo.toString());
		//關聯表字典（舉例：sys_user,realname,id）
		//SQL注入校驗（只限制非法串改數據庫）
		final String[] sqlInjCheck = {duplicateCheckVo.getTableName(),duplicateCheckVo.getFieldName()};
		SqlInjectionUtil.filterContent(sqlInjCheck);
		if (StringUtils.isNotBlank(duplicateCheckVo.getDataId())) {
			// [2].編輯頁面校驗
			num = sysDictMapper.duplicateCheckCountSql(duplicateCheckVo);
		} else {
			// [1].添加頁面校驗
			num = sysDictMapper.duplicateCheckCountSqlNoDataId(duplicateCheckVo);
		}

		if (num == null || num == 0) {
			// 該值可用
			return Result.ok("該值可用！");
		} else {
			// 該值不可用
			log.info("該值不可用，系統中已存在！");
			return Result.error("該值不可用，系統中已存在！");
		}
	}
}
