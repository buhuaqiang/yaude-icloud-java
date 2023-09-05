package com.yaude.icloud.openstack.controller;

import java.util.Arrays;
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
import com.yaude.common.util.oConvertUtils;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.yaude.icloud.openstack.service.IOsUserProjectService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.yaude.common.system.base.controller.JeecgController;
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
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Api(tags="用戶 項目關聯表")
@RestController
@RequestMapping("/openstack/osUserProject")
@Slf4j
public class OsUserProjectController extends JeecgController<OsUserProject, IOsUserProjectService> {
	@Autowired
	private IOsUserProjectService osUserProjectService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osUserProject
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用戶 項目關聯表-分頁列表查詢")
	@ApiOperation(value="用戶 項目關聯表-分頁列表查詢", notes="用戶 項目關聯表-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsUserProject osUserProject,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsUserProject> queryWrapper = QueryGenerator.initQueryWrapper(osUserProject, req.getParameterMap());
		Page<OsUserProject> page = new Page<OsUserProject>(pageNo, pageSize);
		IPage<OsUserProject> pageList = osUserProjectService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	/**
	 * 分頁列表查詢
	 *
	 * @param osUserProjectVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "項目列表查詢")
	@ApiOperation(value="項目列表查詢", notes="項目列表查詢")
	@GetMapping(value = "/listProject")
	public Result<?> queryProjectList(OsUserProjectVo osUserProjectVo,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		//QueryWrapper<OsUserProject> queryWrapper = QueryGenerator.initQueryWrapper(osUserProject, req.getParameterMap());
		//Page<OsUserProject> page = new Page<OsUserProject>(pageNo, pageSize);
		//IPage<OsUserProject> pageList = osUserProjectService.page(page, queryWrapper);
		List<OsUserProjectVo> projectList = osUserProjectService.getProjectList(osUserProjectVo);
		return Result.OK(projectList);
	}

	/**
	 *   添加
	 *
	 * @param osUserProject
	 * @return
	 */
	@AutoLog(value = "用戶 項目關聯表-添加")
	@ApiOperation(value="用戶 項目關聯表-添加", notes="用戶 項目關聯表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsUserProject osUserProject) {
		osUserProjectService.save(osUserProject);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osUserProject
	 * @return
	 */
	@AutoLog(value = "用戶 項目關聯表-編輯")
	@ApiOperation(value="用戶 項目關聯表-編輯", notes="用戶 項目關聯表-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsUserProject osUserProject) {
		osUserProjectService.updateById(osUserProject);
		return Result.OK("編輯成功!");
	}


	/**
	 *  管理項目成員
	 *
	 * @param osUserProjectVo
	 * @return
	 */
	@AutoLog(value = "管理項目成員")
	@ApiOperation(value="管理項目成員", notes="管理項目成員")
	@PutMapping(value = "/updateUserProject")
	public Result<?> updateUserProject(@RequestBody OsUserProjectVo osUserProjectVo) {
		osUserProjectService.updateUserProject(osUserProjectVo);
		return Result.OK("編輯成功!");
	}


	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用戶 項目關聯表-通過id刪除")
	@ApiOperation(value="用戶 項目關聯表-通過id刪除", notes="用戶 項目關聯表-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		osUserProjectService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用戶 項目關聯表-批量刪除")
	@ApiOperation(value="用戶 項目關聯表-批量刪除", notes="用戶 項目關聯表-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.osUserProjectService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用戶 項目關聯表-通過id查詢")
	@ApiOperation(value="用戶 項目關聯表-通過id查詢", notes="用戶 項目關聯表-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsUserProject osUserProject = osUserProjectService.getById(id);
		if(osUserProject==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osUserProject);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osUserProject
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsUserProject osUserProject) {
		return super.exportXls(request, osUserProject, OsUserProject.class, "用戶 項目關聯表");
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
		return super.importExcel(request, response, OsUserProject.class);
	}

}
