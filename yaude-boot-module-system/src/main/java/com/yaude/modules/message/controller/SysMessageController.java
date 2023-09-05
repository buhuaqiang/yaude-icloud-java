package com.yaude.modules.message.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yaude.common.api.vo.Result;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.modules.message.entity.SysMessage;
import com.yaude.modules.message.service.ISysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 消息
 * @author: jeecg-boot
 * @date: 2019-04-09
 * @version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/message/sysMessage")
public class SysMessageController extends JeecgController<SysMessage, ISysMessageService> {
	@Autowired
	private ISysMessageService sysMessageService;

	/**
	 * 分頁列表查詢
	 * 
	 * @param sysMessage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysMessage sysMessage, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<SysMessage> queryWrapper = QueryGenerator.initQueryWrapper(sysMessage, req.getParameterMap());
		Page<SysMessage> page = new Page<SysMessage>(pageNo, pageSize);
		IPage<SysMessage> pageList = sysMessageService.page(page, queryWrapper);
        return Result.ok(pageList);
	}

	/**
	 * 添加
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysMessage sysMessage) {
		sysMessageService.save(sysMessage);
		return Result.ok("添加成功！");
	}

	/**
	 * 編輯
	 * 
	 * @param sysMessage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysMessage sysMessage) {	
		sysMessageService.updateById(sysMessage);
        return Result.ok("修改成功!");

	}

	/**
	 * 通過id刪除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageService.removeById(id);
        return Result.ok("刪除成功!");
	}

	/**
	 * 批量刪除
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		this.sysMessageService.removeByIds(Arrays.asList(ids.split(",")));
	    return Result.ok("批量刪除成功！");
	}

	/**
	 * 通過id查詢
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		SysMessage sysMessage = sysMessageService.getById(id);
		return Result.ok(sysMessage);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysMessage sysMessage) {
		return super.exportXls(request,sysMessage,SysMessage.class, "推送消息模板");
	}

	/**
	 * excel導入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/importExcel")
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysMessage.class);
	}

}
