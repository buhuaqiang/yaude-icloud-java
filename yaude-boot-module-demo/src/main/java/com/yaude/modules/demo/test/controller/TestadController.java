package com.yaude.modules.demo.test.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.modules.demo.test.entity.Testad;
import com.yaude.modules.demo.test.service.ITestadService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import com.yaude.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.yaude.common.aspect.annotation.AutoLog;

/**
 * @Description: 阿德測試
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
@Api(tags="阿德測試")
@RestController
@RequestMapping("/test/testad")
@Slf4j
public class TestadController extends JeecgController<Testad, ITestadService> {
	@Autowired
	private ITestadService testadService;

	/**
	 * 分頁列表查詢
	 *
	 * @param testad
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "阿德測試-分頁列表查詢")
	@ApiOperation(value="阿德測試-分頁列表查詢", notes="阿德測試-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(Testad testad,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Testad> queryWrapper = QueryGenerator.initQueryWrapper(testad, req.getParameterMap());
		Page<Testad> page = new Page<Testad>(pageNo, pageSize);
		IPage<Testad> pageList = testadService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param testad
	 * @return
	 */
	@AutoLog(value = "阿德測試-添加")
	@ApiOperation(value="阿德測試-添加", notes="阿德測試-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Testad testad) {
		testadService.save(testad);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param testad
	 * @return
	 */
	@AutoLog(value = "阿德測試-編輯")
	@ApiOperation(value="阿德測試-編輯", notes="阿德測試-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Testad testad) {
		testadService.updateById(testad);
		return Result.OK("編輯成功!");
	}

	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "阿德測試-通過id刪除")
	@ApiOperation(value="阿德測試-通過id刪除", notes="阿德測試-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		testadService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "阿德測試-批量刪除")
	@ApiOperation(value="阿德測試-批量刪除", notes="阿德測試-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.testadService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "阿德測試-通過id查詢")
	@ApiOperation(value="阿德測試-通過id查詢", notes="阿德測試-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Testad testad = testadService.getById(id);
		if(testad==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(testad);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param testad
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, Testad testad) {
		return super.exportXls(request, testad, Testad.class, "阿德測試");
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
		return super.importExcel(request, response, Testad.class);
	}

}
