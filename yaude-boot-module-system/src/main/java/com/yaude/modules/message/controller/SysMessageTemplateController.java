package com.yaude.modules.message.controller;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yaude.modules.message.service.ISysMessageTemplateService;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.modules.message.entity.MsgParams;
import com.yaude.modules.message.entity.SysMessageTemplate;
import com.yaude.modules.message.util.PushMsgUtil;
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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Sate: 2019-04-09
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/message/sysMessageTemplate")
public class SysMessageTemplateController extends JeecgController<SysMessageTemplate, ISysMessageTemplateService> {
	@Autowired
	private ISysMessageTemplateService sysMessageTemplateService;
	@Autowired
	private PushMsgUtil pushMsgUtil;

	/**
	 * 分頁列表查詢
	 * 
	 * @param sysMessageTemplate
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysMessageTemplate sysMessageTemplate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<SysMessageTemplate> queryWrapper = QueryGenerator.initQueryWrapper(sysMessageTemplate, req.getParameterMap());
		Page<SysMessageTemplate> page = new Page<SysMessageTemplate>(pageNo, pageSize);
		IPage<SysMessageTemplate> pageList = sysMessageTemplateService.page(page, queryWrapper);
        return Result.ok(pageList);
	}

	/**
	 * 添加
	 * 
	 * @param sysMessageTemplate
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysMessageTemplate sysMessageTemplate) {
		sysMessageTemplateService.save(sysMessageTemplate);
        return Result.ok("添加成功！");
	}

	/**
	 * 編輯
	 * 
	 * @param sysMessageTemplate
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysMessageTemplate sysMessageTemplate) {
		sysMessageTemplateService.updateById(sysMessageTemplate);
        return Result.ok("更新成功！");
	}

	/**
	 * 通過id刪除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysMessageTemplateService.removeById(id);
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
		this.sysMessageTemplateService.removeByIds(Arrays.asList(ids.split(",")));
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
		SysMessageTemplate sysMessageTemplate = sysMessageTemplateService.getById(id);
        return Result.ok(sysMessageTemplate);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 */
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request,SysMessageTemplate sysMessageTemplate) {
		return super.exportXls(request, sysMessageTemplate, SysMessageTemplate.class,"推送消息模板");
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
		return super.importExcel(request, response, SysMessageTemplate.class);
	}

	/**
	 * 發送消息
	 */
	@PostMapping(value = "/sendMsg")
	public Result<SysMessageTemplate> sendMessage(@RequestBody MsgParams msgParams) {
		Result<SysMessageTemplate> result = new Result<SysMessageTemplate>();
		Map<String, String> map = null;
		try {
			map = (Map<String, String>) JSON.parse(msgParams.getTestData());
		} catch (Exception e) {
			result.error500("解析Json出錯！");
			return result;
		}
		boolean is_sendSuccess = pushMsgUtil.sendMessage(msgParams.getMsgType(), msgParams.getTemplateCode(), map, msgParams.getReceiver());
		if (is_sendSuccess) {
			result.success("發送消息任務添加成功！");
		} else {
			result.error500("發送消息任務添加失敗！");
		}
		return result;
	}
}
