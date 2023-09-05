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
import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.yaude.icloud.openstack.entity.OsApplyFloatip;
import com.yaude.icloud.openstack.service.IOsApplyFloatipService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.service.IOsOptionService;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyDiskVo;
import com.yaude.icloud.openstack.vo.OsApplyFloatipVo;
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
 * @Description: 浮動ip申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
@Api(tags="浮動ip申請明細檔")
@RestController
@RequestMapping("/os/osApplyFloatip")
@Slf4j
public class OsApplyFloatipController extends JeecgController<OsApplyFloatip, IOsApplyFloatipService> {
	@Autowired
	private IOsApplyFloatipService osApplyFloatipService;

	@Autowired
	private IOsOptionService osOptionService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osApplyFloatip
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-分頁列表查詢")
	@ApiOperation(value="浮動ip申請明細檔-分頁列表查詢", notes="浮動ip申請明細檔-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsApplyFloatip osApplyFloatip,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsApplyFloatip> queryWrapper = QueryGenerator.initQueryWrapper(osApplyFloatip, req.getParameterMap());
		Page<OsApplyFloatip> page = new Page<OsApplyFloatip>(pageNo, pageSize);
		//只顯示當前登錄人申請的數據
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String username = loginUser.getUsername();
		queryWrapper.eq("create_by",username);
		IPage<OsApplyFloatip> pageList = osApplyFloatipService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 根據id查詢數據
	 *
	 * @param osApplyFloatip
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "根據id查詢數據")
	@ApiOperation(value="根據id查詢數據", notes="根據id查詢數據")
	@GetMapping(value = "/floatipapplys")
	public Result<?> floatipapplys(OsApplyFloatip osApplyFloatip,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsApplyFloatip> queryWrapper = QueryGenerator.initQueryWrapper(new OsApplyFloatip(), req.getParameterMap());
		Page<OsApplyFloatip> page = new Page<OsApplyFloatip>(pageNo, pageSize);
		queryWrapper.eq("id",osApplyFloatip.getId());
		List<OsApplyFloatip> list = osApplyFloatipService.list(queryWrapper);
		List <OsApplyFloatipVo> osApplyFloatipVos = osApplyFloatipService.getOsApplyFloatList(list) ;
		return Result.OK(osApplyFloatipVos);
	}

	/**
	 *   添加
	 *
	 * @param osApplyFloatip
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-添加")
	@ApiOperation(value="浮動ip申請明細檔-添加", notes="浮動ip申請明細檔-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsApplyFloatip osApplyFloatip) {
		osApplyFloatip.setOptions("0");
		osApplyFloatipService.save(osApplyFloatip);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osApplyFloatip
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-編輯")
	@ApiOperation(value="浮動ip申請明細檔-編輯", notes="浮動ip申請明細檔-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsApplyFloatip osApplyFloatip) {
		osApplyFloatipService.updateById(osApplyFloatip);
		return Result.OK("編輯成功!");
	}


	/**
	 *   調整
	 *
	 * @param osApplyFloatip
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-調整")
	@ApiOperation(value="浮動ip申請明細檔-調整", notes="浮動ip申請明細檔-調整")
	@PostMapping(value = "/adjust")
	public Result<?> adjust(@RequestBody OsApplyFloatip osApplyFloatip) {
		osApplyFloatipService.save(osApplyFloatip);
		return Result.OK("添加成功！");
	}

	/**
	 *  提交
	 *
	 * @param osApplyFloatip
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-提交")
	@ApiOperation(value="浮動ip申請明細檔-提交", notes="浮動ip申請明細檔-提交")
	@PutMapping(value = "/submit")
	public Result<?> submit(@RequestBody OsApplyFloatip osApplyFloatip) {
		if(osApplyFloatip.getOptions().equals("0")){
			osApplyFloatip.setOptions("1");
		}
		osApplyFloatip.setStatus("0");
		osApplyFloatipService.updateById(osApplyFloatip);

		OsOptionVo osOptionVO = new OsOptionVo();
		osOptionVO.setOptions(osApplyFloatip.getOptions());
		osOptionVO.setStatus(osApplyFloatip.getStatus());
		osOptionVO.setApplyId(osApplyFloatip.getId());
		osOptionVO.setApplyName(osApplyFloatip.getFloatIp());
		osOptionVO.setApplyBy(osApplyFloatip.getCreateBy());
		osOptionVO.setApplyType("2");
		osOptionService.save(osOptionVO);
		return Result.OK("提交成功!");
	}


	/**
	 *   獲取網絡
	 *
	 * @param osApplyFloatipVo
	 * @return
	 */
	@AutoLog(value = "獲取網絡")
	@ApiOperation(value="獲取網絡", notes="獲取網絡")
	@PostMapping(value = "/getNetwork")
	public Result<?> getNetwork(@RequestBody OsApplyFloatipVo osApplyFloatipVo) {
		String projectId = osApplyFloatipVo.getProjectId();
		List<OsApplyFloatipVo> osApplyFloatips = osApplyFloatipService.getNetwork(projectId);
		return Result.OK(osApplyFloatips);
	}

	/**
	 *   獲取網絡
	 *
	 * @param osApplyFloatipVo
	 * @return
	 */
	@AutoLog(value = "獲取子網")
	@ApiOperation(value="獲取子網", notes="獲取子網")
	@PostMapping(value = "/getSubnets")
	public Result<?> getSubnets(@RequestBody OsApplyFloatipVo osApplyFloatipVo) {
		String networkId = osApplyFloatipVo.getNetworkId();
		String projectId = osApplyFloatipVo.getProjectId();
		List<OsApplyFloatipVo> osApplyFloatips = osApplyFloatipService.getSubnets(networkId,projectId);
		return Result.OK(osApplyFloatips);
	}

	/**
	 *   獲取浮動ip
	 *
	 * @param osApplyFloatipVo
	 * @return
	 */
	/* @AutoLog(value = "獲取浮動ip")
	 @ApiOperation(value="獲取浮動ip", notes="獲取浮動ip")
	 @PostMapping(value = "/getFloatip")
	 public Result<?> getFloatip(@RequestBody OsApplyFloatipVo osApplyFloatipVo) {
	 	String projectId = osApplyFloatipVo.getProjectId();
		 List<OsApplyFloatipVo> osApplyFloatips = osApplyFloatipService.getFloatip(projectId);
		 return Result.OK(osApplyFloatips);
	 }*/


	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-通過id刪除")
	@ApiOperation(value="浮動ip申請明細檔-通過id刪除", notes="浮動ip申請明細檔-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		Result<?> result = new Result();
		String message = "";
		int counts = osApplyFloatipService.getStatus(id);
		if(counts>0){
			message = "審核已通過，不能刪除";
		}else{
			osApplyFloatipService.removeById(id);
			osOptionService.deleteOption(id,"2");
			message = "刪除成功!";
		}
		result.setMessage(message);
		return result;
		/*osApplyFloatipService.removeById(id);
		osOptionService.deleteOption(id,"2");
		return Result.OK("刪除成功!");*/
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-批量刪除")
	@ApiOperation(value="浮動ip申請明細檔-批量刪除", notes="浮動ip申請明細檔-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<?> result = new Result();
		String message = "";
		ids = ids.substring(0,ids.length()-1);
		int counts = osApplyFloatipService.getCountStatus(Arrays.asList(ids.split(",")));
		if(counts>0){
			message = "審核已通過，不能刪除";
		}else{
			this.osApplyFloatipService.removeByIds(Arrays.asList(ids.split(",")));
			message = "批量刪除成功!";
		}
		result.setMessage(message);
		return result;
		//this.osApplyFloatipService.removeByIds(Arrays.asList(ids.split(",")));
		//return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "浮動ip申請明細檔-通過id查詢")
	@ApiOperation(value="浮動ip申請明細檔-通過id查詢", notes="浮動ip申請明細檔-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsApplyFloatip osApplyFloatip = osApplyFloatipService.getById(id);
		if(osApplyFloatip==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osApplyFloatip);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osApplyFloatip
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsApplyFloatip osApplyFloatip) {
		return super.exportXls(request, osApplyFloatip, OsApplyFloatip.class, "浮動ip申請明細檔");
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
		return super.importExcel(request, response, OsApplyFloatip.class);
	}

}
