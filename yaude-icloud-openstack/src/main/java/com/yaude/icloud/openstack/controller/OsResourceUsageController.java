package com.yaude.icloud.openstack.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.util.DateUtils;
import com.yaude.common.util.oConvertUtils;
import com.yaude.icloud.openstack.entity.OsResourceUsage;
import com.yaude.icloud.openstack.service.IOsResourceUsageService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.vo.OsResourceUsageVo;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.yaude.common.system.base.controller.JeecgController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.yaude.common.aspect.annotation.AutoLog;

/**
 * @Description: 資源用量表
 * @Author: jeecg-boot
 * @Date:   2021-10-21
 * @Version: V1.0
 */
@Api(tags="資源用量表")
@RestController
@RequestMapping("/openstack/osResourceUsage")
@Slf4j
public class OsResourceUsageController extends JeecgController<OsResourceUsage, IOsResourceUsageService> {
	@Autowired
	private IOsResourceUsageService osResourceUsageService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osResourceUsage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "資源用量表-分頁列表查詢")
	@ApiOperation(value="資源用量表-分頁列表查詢", notes="資源用量表-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsResourceUsage osResourceUsage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsResourceUsage> queryWrapper = QueryGenerator.initQueryWrapper(osResourceUsage, req.getParameterMap());
		Page<OsResourceUsage> page = new Page<OsResourceUsage>(pageNo, pageSize);
		IPage<OsResourceUsage> pageList = osResourceUsageService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	/**
	 * 列表按日查詢
	 *
	 * @param osResourceUsageVo
	 * @param req
	 * @return
	 */
	@AutoLog(value = "資源用量表-列表按日查詢")
	@ApiOperation(value="資源用量表-列表按日查詢", notes="資源用量表-列表按日查詢")
	@GetMapping(value = "/getDateCountInfo")
	public Result<?> getDateCountInfo(OsResourceUsageVo osResourceUsageVo,
									  HttpServletRequest req) {
		OsResourceUsage osResourceUsage = new OsResourceUsage();
		BeanUtils.copyProperties(osResourceUsageVo,osResourceUsage);
		QueryWrapper<OsResourceUsage> queryWrapper = QueryGenerator.initQueryWrapper(osResourceUsage, req.getParameterMap());
		queryWrapper.orderByDesc("usage_date");
		if(StringUtils.isNotEmpty(osResourceUsageVo.getEndTime())){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			queryWrapper.le("usage_date",DateUtils.str2Date(osResourceUsageVo.getEndTime(),sdf));
		}

		queryWrapper.last("limit 12");
		List<OsResourceUsage> list = osResourceUsageService.list(queryWrapper);
		Collections.reverse(list);
		return Result.OK(list);
	}

	/**
	 * 列表按月查詢
	 *
	 * @param osResourceUsageVo
	 * @param req
	 * @return
	 */
	@AutoLog(value = "資源用量表-列表按月查詢")
	@ApiOperation(value="資源用量表-列表按月查詢", notes="資源用量表-列表按月查詢")
	@GetMapping(value = "/getMonthCountInfo")
	public Result<?> getMonthCountInfo(OsResourceUsageVo osResourceUsageVo,
									   HttpServletRequest req) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
		String startMonth = "";
		String endMonth = "";
		if(StringUtils.isNotEmpty(osResourceUsageVo.getStartTime())){}
		startMonth = DateUtils.date2Str(DateUtils.str2Date(osResourceUsageVo.getStartTime(),sdf),sdf1);
		if(StringUtils.isNotEmpty(osResourceUsageVo.getEndTime()))
			endMonth = DateUtils.date2Str(DateUtils.str2Date(osResourceUsageVo.getEndTime(),sdf),sdf1);

		List<OsResourceUsageVo> list = osResourceUsageService.getMonthCountInfo(osResourceUsageVo.getProjectId(),startMonth,endMonth);
		Collections.reverse(list);
		return Result.OK(list);
	}

	/**
	 * 列表按年查詢
	 *
	 * @param osResourceUsageVo
	 * @param req
	 * @return
	 */
	@AutoLog(value = "資源用量表-列表按年查詢")
	@ApiOperation(value="資源用量表-列表按年查詢", notes="資源用量表-列表按年查詢")
	@GetMapping(value = "/getYearCountInfo")
	public Result<?> getYearCountInfo(OsResourceUsageVo osResourceUsageVo,
									  HttpServletRequest req) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
		String startYear = "";
		String endYear = "";
		if(StringUtils.isNotEmpty(osResourceUsageVo.getStartTime())){}
		startYear = DateUtils.date2Str(DateUtils.str2Date(osResourceUsageVo.getStartTime(),sdf),sdf1);
		if(StringUtils.isNotEmpty(osResourceUsageVo.getEndTime()))
			endYear = DateUtils.date2Str(DateUtils.str2Date(osResourceUsageVo.getEndTime(),sdf),sdf1);

		List<OsResourceUsageVo> list = osResourceUsageService.getYearCountInfo(osResourceUsageVo.getProjectId(),startYear,endYear);
		Collections.reverse(list);
		return Result.OK(list);
	}




	/**
	 *   添加
	 *
	 * @param osResourceUsage
	 * @return
	 */
	@AutoLog(value = "資源用量表-添加")
	@ApiOperation(value="資源用量表-添加", notes="資源用量表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsResourceUsage osResourceUsage) {
		osResourceUsageService.save(osResourceUsage);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osResourceUsage
	 * @return
	 */
	@AutoLog(value = "資源用量表-編輯")
	@ApiOperation(value="資源用量表-編輯", notes="資源用量表-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsResourceUsage osResourceUsage) {
		osResourceUsageService.updateById(osResourceUsage);
		return Result.OK("編輯成功!");
	}

	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "資源用量表-通過id刪除")
	@ApiOperation(value="資源用量表-通過id刪除", notes="資源用量表-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		osResourceUsageService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "資源用量表-批量刪除")
	@ApiOperation(value="資源用量表-批量刪除", notes="資源用量表-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.osResourceUsageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "資源用量表-通過id查詢")
	@ApiOperation(value="資源用量表-通過id查詢", notes="資源用量表-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsResourceUsage osResourceUsage = osResourceUsageService.getById(id);
		if(osResourceUsage==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osResourceUsage);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osResourceUsage
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsResourceUsage osResourceUsage) {
		return super.exportXls(request, osResourceUsage, OsResourceUsage.class, "資源用量表");
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
		return super.importExcel(request, response, OsResourceUsage.class);
	}

}
