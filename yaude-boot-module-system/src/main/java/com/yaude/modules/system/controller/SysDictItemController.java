package com.yaude.modules.system.controller;


import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.system.query.QueryGenerator;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.modules.system.entity.SysDictItem;
import com.yaude.modules.system.service.ISysDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.RequestBody;
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
 *  前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/sys/dictItem")
@Slf4j
public class SysDictItemController {

	@Autowired
	private ISysDictItemService sysDictItemService;
	
	/**
	 * @功能：查詢字典數據
	 * @param sysDictItem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDictItem>> queryPageList(SysDictItem sysDictItem,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysDictItem>> result = new Result<IPage<SysDictItem>>();
		QueryWrapper<SysDictItem> queryWrapper = QueryGenerator.initQueryWrapper(sysDictItem, req.getParameterMap());
		queryWrapper.orderByAsc("sort_order");
		Page<SysDictItem> page = new Page<SysDictItem>(pageNo, pageSize);
		IPage<SysDictItem> pageList = sysDictItemService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * @功能：新增
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> add(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		try {
			sysDictItem.setCreateTime(new Date());
			sysDictItemService.save(sysDictItem);
			result.success("保存成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
		}
		return result;
	}
	
	/**
	 * @功能：編輯
	 * @param sysDictItem
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> edit(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem sysdict = sysDictItemService.getById(sysDictItem.getId());
		if(sysdict==null) {
			result.error500("未找到對應實體");
		}else {
			sysDictItem.setUpdateTime(new Date());
			boolean ok = sysDictItemService.updateById(sysDictItem);
			//TODO 返回false說明什么？
			if(ok) {
				result.success("編輯成功!");
			}
		}
		return result;
	}
	
	/**
	 * @功能：刪除字典數據
	 * @param id
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem joinSystem = sysDictItemService.getById(id);
		if(joinSystem==null) {
			result.error500("未找到對應實體");
		}else {
			boolean ok = sysDictItemService.removeById(id);
			if(ok) {
				result.success("刪除成功!");
			}
		}
		return result;
	}
	
	/**
	 * @功能：批量刪除字典數據
	 * @param ids
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("參數不識別！");
		}else {
			this.sysDictItemService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("刪除成功!");
		}
		return result;
	}

	/**
	 * 字典值重復校驗
	 * @param sysDictItem
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dictItemCheck", method = RequestMethod.GET)
	@ApiOperation("字典重復校驗接口")
	public Result<Object> doDictItemCheck(SysDictItem sysDictItem, HttpServletRequest request) {
		int num = 0;
		LambdaQueryWrapper<SysDictItem> queryWrapper = new LambdaQueryWrapper<SysDictItem>();
		queryWrapper.eq(SysDictItem::getItemValue,sysDictItem.getItemValue());
		queryWrapper.eq(SysDictItem::getDictId,sysDictItem.getDictId());
		if (StringUtils.isNotBlank(sysDictItem.getId())) {
			// 編輯頁面校驗
			queryWrapper.ne(SysDictItem::getId,sysDictItem.getId());
		}
		num = sysDictItemService.count(queryWrapper);
		if (num == 0) {
			// 該值可用
			return Result.ok("該值可用！");
		} else {
			// 該值不可用
			log.info("該值不可用，系統中已存在！");
			return Result.error("該值不可用，系統中已存在！");
		}
	}
	
}
