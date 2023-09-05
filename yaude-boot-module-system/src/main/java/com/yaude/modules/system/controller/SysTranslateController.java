package com.yaude.modules.system.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.api.vo.Result;
import com.yaude.modules.system.entity.SysDict;
import com.yaude.modules.system.entity.SysTranslate;
import com.yaude.modules.system.service.ISysTranslateService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.yaude.modules.system.vo.SysTranslateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 多語言
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
@Api(tags="多語言")
@RestController
@RequestMapping("/sys/sysTranslate")
@Slf4j
public class SysTranslateController extends JeecgController<SysTranslate, ISysTranslateService> {
	@Autowired
	private ISysTranslateService sysTranslateService;

	@Autowired
	public RedisTemplate<String, Object> redisTemplate;

	/**
	 * 分頁列表查詢
	 *
	 * @param sysTranslate
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "多語言-分頁列表查詢")
	@ApiOperation(value="多語言-分頁列表查詢", notes="多語言-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysTranslate sysTranslate,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysTranslate> queryWrapper = QueryGenerator.initQueryWrapper(sysTranslate, req.getParameterMap());
		Page<SysTranslate> page = new Page<SysTranslate>(pageNo, pageSize);
		IPage<SysTranslate> pageList = sysTranslateService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	/**
	 * 分頁列表查詢
	 *
	 * @param sysTranslateVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "多語言全局-分頁列表查詢")
	@ApiOperation(value="多語言全局-分頁列表查詢", notes="多語言全局-分頁列表查詢")
	@GetMapping(value = "/alllist")
	public Result<?> queryPageAllList(SysTranslateVO sysTranslateVo,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		QueryWrapper<SysTranslate> queryWrapper = QueryGenerator.initQueryWrapper(sysTranslateVo, req.getParameterMap());
		Page<SysTranslate> page = new Page<SysTranslate>(pageNo, pageSize);
		List<Map<String, String>> list = sysTranslateService.queryAllList(sysTranslateVo);
		return Result.OK(list);
	}

	/**
	 *   添加
	 *
	 * @param sysTranslate
	 * @return
	 */
	@AutoLog(value = "多語言-添加")
	@ApiOperation(value="多語言-添加", notes="多語言-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysTranslate sysTranslate) {
		sysTranslateService.save(sysTranslate);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param sysTranslate
	 * @return
	 */
	@AutoLog(value = "多語言-編輯")
	@ApiOperation(value="多語言-編輯", notes="多語言-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysTranslate sysTranslate) {
		sysTranslateService.updateById(sysTranslate);
		return Result.OK("編輯成功!");
	}

	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "多語言-通過id刪除")
	@ApiOperation(value="多語言-通過id刪除", notes="多語言-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysTranslateService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "多語言-批量刪除")
	@ApiOperation(value="多語言-批量刪除", notes="多語言-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysTranslateService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "多語言-通過id查詢")
	@ApiOperation(value="多語言-通過id查詢", notes="多語言-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysTranslate sysTranslate = sysTranslateService.getById(id);
		if(sysTranslate==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(sysTranslate);
	}

	/**
	 * 通過條件查詢
	 *
	 * @param sysTranslate
	 * @return
	 */
	@AutoLog(value = "多語言-通過條件查詢")
	@ApiOperation(value="多語言-通過條件查詢", notes="多語言-通過條件查詢")
	@GetMapping(value = "/queryByParams")
	public Result<?> queryByParams(SysTranslate sysTranslate) {
		LambdaQueryWrapper<SysTranslate> query = new LambdaQueryWrapper<SysTranslate>();
		query.eq(SysTranslate::getRelateTable,sysTranslate.getRelateTable());
		query.eq(SysTranslate::getRelateId, sysTranslate.getRelateId());
		SysTranslate sysTranslateServiceOne = sysTranslateService.getOne(query);
		if(sysTranslateServiceOne==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(sysTranslateServiceOne);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param sysTranslate
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysTranslate sysTranslate) {
		return super.exportXls(request, sysTranslate, SysTranslate.class, "多語言");
	}

	/**
	 * 通過excel導入數據
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysTranslate.class);
	}

	/**
	 * @功能：刷新緩存
	 * @return
	 */
	@RequestMapping(value = "/refleshCache")
	public Result<?> refleshCache() {
		Result<?> result = new Result<SysDict>();
		//清空字典緩存
		Set keys = redisTemplate.keys(CacheConstant.SYS_TRANSLATE_ALLTABLE_CACHE + "*");

		redisTemplate.delete(keys);

		return result;
	}
	/**
	 * 獲取全部字典數據
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryAllDictItems", method = RequestMethod.GET)
	public Result<?> queryAllDictItems(HttpServletRequest request) {
		List<SysTranslate> list = sysTranslateService.queryAllSysTranslateItems();
		return Result.ok(list);
	}

}
