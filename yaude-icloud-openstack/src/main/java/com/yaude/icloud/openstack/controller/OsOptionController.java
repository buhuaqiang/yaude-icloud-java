package com.yaude.icloud.openstack.controller;

import java.util.ArrayList;
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
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.oConvertUtils;
import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsOption;
import com.yaude.icloud.openstack.service.IOsApplyService;
import com.yaude.icloud.openstack.service.IOsOptionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsOptionVo;
import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.yaude.common.system.base.controller.JeecgController;
import org.openstack4j.model.identity.v3.Project;
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
 * @Description: 審核意見細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Api(tags="審核意見細檔")
@RestController
@RequestMapping("/os/osOption")
@Slf4j
public class OsOptionController extends JeecgController<OsOption, IOsOptionService> {
	@Autowired
	private IOsOptionService osOptionService;
	@Autowired
	private IOsApplyService osApplyService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osOption
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "審核意見細檔-分頁列表查詢")
	@ApiOperation(value="審核意見細檔-分頁列表查詢", notes="審核意見細檔-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsOption osOption,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsOption> queryWrapper = QueryGenerator.initQueryWrapper(osOption, req.getParameterMap());
		Page<OsOption> page = new Page<OsOption>(pageNo, pageSize);
		//IPage<OsOption> pageList = osOptionService.page(page, queryWrapper);
		List<OsOption> pageList = osOptionService.list(queryWrapper);
		List <OsOptionVo> osOptionVos = osOptionService.getOsOptionVoListByOsApply(pageList) ;
		Page<OsOptionVo> pageRs =new Page<OsOptionVo>(pageNo, pageSize);
		BeanUtils.copyProperties(pageList,pageRs);
		pageRs.setRecords(osOptionVos);
		return Result.OK(pageRs);
	}

	/**
	 *   添加
	 *
	 * @param osOption
	 * @return
	 */
	@AutoLog(value = "審核意見細檔-添加")
	@ApiOperation(value="審核意見細檔-添加", notes="審核意見細檔-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsOption osOption) {
		osOptionService.save(osOption);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osOption
	 * @return
	 */
	@AutoLog(value = "審核意見細檔-編輯")
	@ApiOperation(value="審核意見細檔-編輯", notes="審核意見細檔-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsOption osOption) {
		osOptionService.updateById(osOption);
		return Result.OK("編輯成功!");
	}
	/**
	 *  審核同意
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "申請審核同意")
	@ApiOperation(value="申請審核同意", notes="申請審核同意")
	@PostMapping(value = "/agree")
	public Result<?> agree(@RequestBody OsApplyVo osApplyVo)throws InterruptedException {
		OsOption osOption = new OsOption();
		osApplyVo.setRunStatus("0");
		osOption.setId(osApplyVo.getOptionId());
		osOption.setOptionsText(osApplyVo.getOptionsText());
		osOption.setOptionsType("1");
		osOptionService.updateById(osOption);
		osOptionService.upStatus(osApplyVo);
		return Result.OK("編輯成功!");
	}
	/**
	 *  審核拒絕
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "申請審核拒絕")
	@ApiOperation(value="申請審核拒絕", notes="申請審核拒絕")
	@PostMapping(value = "/refuse")
	public Result<?> refuse(@RequestBody OsApplyVo osApplyVo)throws InterruptedException {
		OsOption osOption = new OsOption();
		osOption.setId(osApplyVo.getOptionId());
		osOption.setOptionsText(osApplyVo.getOptionsText());
		osOption.setOptionsType("0");
		osOptionService.updateById(osOption);
		osOptionService.upStatus(osApplyVo);
		return Result.OK("編輯成功!");
	}
	/**
	 *  審核狀態
	 *
	 * @param osOptionVo
	 * @return
	 */
	@AutoLog(value = "審核狀態")
	@ApiOperation(value="審核狀態", notes="審核狀態")
	@PostMapping(value = "/getStatus")
	public Result<?> getStatus(@RequestBody OsOptionVo osOptionVo) throws InterruptedException {
		osOptionService.getStatus(osOptionVo);
		return Result.OK("編輯成功!");
	}

	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "審核意見細檔-通過id刪除")
	@ApiOperation(value="審核意見細檔-通過id刪除", notes="審核意見細檔-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		osOptionService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "審核意見細檔-批量刪除")
	@ApiOperation(value="審核意見細檔-批量刪除", notes="審核意見細檔-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.osOptionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "審核意見細檔-通過id查詢")
	@ApiOperation(value="審核意見細檔-通過id查詢", notes="審核意見細檔-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsOption osOption = osOptionService.getById(id);
		if(osOption==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osOption);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osOption
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsOption osOption) {
		return super.exportXls(request, osOption, OsOption.class, "審核意見細檔");
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
		return super.importExcel(request, response, OsOption.class);
	}

}
