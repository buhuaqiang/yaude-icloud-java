package com.yaude.modules.system.controller;


import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysLog;
import com.yaude.modules.system.entity.SysRole;
import com.yaude.modules.system.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 系統日志表 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@RestController
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController {
	
	@Autowired
	private ISysLogService sysLogService;
	
	/**
	 * @功能：查詢日志記錄
	 * @param syslog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysLog>> queryPageList(SysLog syslog,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysLog>> result = new Result<IPage<SysLog>>();
		QueryWrapper<SysLog> queryWrapper = QueryGenerator.initQueryWrapper(syslog, req.getParameterMap());
		Page<SysLog> page = new Page<SysLog>(pageNo, pageSize);
		//日志關鍵詞
		String keyWord = req.getParameter("keyWord");
		if(oConvertUtils.isNotEmpty(keyWord)) {
			queryWrapper.like("log_content",keyWord);
		}
		//TODO 過濾邏輯處理
		//TODO begin、end邏輯處理
		//TODO 一個強大的功能，前端傳一個字段字符串，后臺只返回這些字符串對應的字段
		//創建時間/創建人的賦值
		IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
		log.info("查詢當前頁："+pageList.getCurrent());
		log.info("查詢當前頁數量："+pageList.getSize());
		log.info("查詢結果數量："+pageList.getRecords().size());
		log.info("數據總數："+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * @功能：刪除單個日志記錄
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysLog> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysLog> result = new Result<SysLog>();
		SysLog sysLog = sysLogService.getById(id);
		if(sysLog==null) {
			result.error500("未找到對應實體");
		}else {
			boolean ok = sysLogService.removeById(id);
			if(ok) {
				result.success("刪除成功!");
			}
		}
		return result;
	}
	
	/**
	 * @功能：批量，全部清空日志記錄
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysRole> result = new Result<SysRole>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("參數不識別！");
		}else {
			if("allclear".equals(ids)) {
				this.sysLogService.removeAll();
				result.success("清除成功!");
			}
			this.sysLogService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("刪除成功!");
		}
		return result;
	}
	
	
}
