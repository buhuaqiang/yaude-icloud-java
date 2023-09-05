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
import com.yaude.icloud.openstack.entity.OsOption;
import com.yaude.icloud.openstack.service.IOsApplyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.service.IOsOptionService;
import com.yaude.icloud.openstack.utils.JCloudsNeutron;
import com.yaude.icloud.openstack.utils.JCloudsNova;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
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
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
@Api(tags="申請明細檔")
@RestController
@RequestMapping("/os/osApply")
@Slf4j
public class OsApplyController extends JeecgController<OsApply, IOsApplyService> {
	@Autowired
	private IOsApplyService osApplyService;

	@Autowired
	private IOsOptionService osOptionService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "申請明細檔-分頁列表查詢")
	@ApiOperation(value="申請明細檔-分頁列表查詢", notes="申請明細檔-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<IPage> queryPageList(OsApply osApply,
									   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									   HttpServletRequest req) {
		QueryWrapper<OsApply> queryWrapper = QueryGenerator.initQueryWrapper(osApply, req.getParameterMap());
		Page<OsApply> page = new Page<OsApply>(pageNo, pageSize);
		//只顯示當前登錄人申請的數據
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String username = loginUser.getUsername();
		queryWrapper.eq("create_by",username);
		IPage<OsApply> pageList = osApplyService.page(page, queryWrapper);
		List <OsApplyVo> osApplyVos = osApplyService.getOsApplyVoListByOsApply(pageList.getRecords()) ;
		Page<OsApplyVo> pageRs =new Page<OsApplyVo>(pageNo, pageSize);
		BeanUtils.copyProperties(pageList,pageRs);
		pageRs.setRecords(osApplyVos);
		return Result.OK(pageRs);
	}

	/**
	 * 根據id查詢所有數據
	 *
	 * @param osApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "根據id查詢所有數據")
	@ApiOperation(value="根據id查詢所有數據", notes="根據id查詢所有數據")
	@GetMapping(value = "/applys")
	public Result<?> applys(OsApply osApply,
							@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
							@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
							HttpServletRequest req) {
		QueryWrapper<OsApply> queryWrapper = QueryGenerator.initQueryWrapper(new OsApply(), req.getParameterMap());
		Page<OsApply> page = new Page<OsApply>(pageNo, pageSize);
		queryWrapper.eq("id",osApply.getId());
		List<OsApply> list = osApplyService.list(queryWrapper);
		List <OsApplyVo> osApplyVos = osApplyService.getOsApplyVoListByOsApply(list) ;
		return Result.OK(osApplyVos);
	}




	/**
	 *   獲取項目
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "申請明細檔-獲取項目")
	@ApiOperation(value="申請明細檔-獲取項目", notes="申請明細檔-獲取項目")
	@PostMapping(value = "/getProject")
	public Result<?> getProject(@RequestBody OsApplyVo osApplyVo) {
		List<OsApplyVo> osApplies = osApplyService.getProject();
		return Result.OK(osApplies);
	}

	/**
	 *   獲取img
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "申請明細檔-獲取img")
	@ApiOperation(value="申請明細檔-獲取img", notes="申請明細檔-獲取img")
	@PostMapping(value = "/getImg")
	public Result<?> getImg(@RequestBody OsApplyVo osApplyVo) {
		String projectId = osApplyVo.getProjectId();
		List<OsApplyVo> osApplies = osApplyService.getImg(projectId);
		return Result.OK(osApplies);
	}
	/**
	 *   獲取實例類型
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "申請明細檔-獲取實例類型")
	@ApiOperation(value="申請明細檔-獲取實例類型", notes="申請明細檔-獲取實例類型")
	@PostMapping(value = "/getFlavor")
	public Result<?> getFlavor(@RequestBody OsApplyVo osApplyVo) {
		String projectId = osApplyVo.getProjectId();
		List<OsApplyVo> osApplies = osApplyService.getFlavor(projectId);
		return Result.OK(osApplies);
	}
	/**
	 *   獲取安全組
	 *
	 * @param osApply
	 * @return
	 */
	@AutoLog(value = "申請明細檔-獲取安全組")
	@ApiOperation(value="申請明細檔-獲取安全組", notes="申請明細檔-獲取安全組")
	@PostMapping(value = "/getSecurity")
	public Result<?> getSecurity(@RequestBody OsApply osApply) {
		String projectId = osApply.getProjectId();
		List<OsApply> osApplies = osApplyService.getSecurity(projectId);
		return Result.OK(osApplies);
	}
	/**
	 *   獲取網絡
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "申請明細檔-獲取網絡")
	@ApiOperation(value="申請明細檔-獲取網絡", notes="申請明細檔-獲取網絡")
	@PostMapping(value = "/getNetwork")
	public Result<?> getNetwork(@RequestBody OsApplyVo osApplyVo) {
		String projectId = osApplyVo.getProjectId();
		List<OsApplyVo> osApplies = osApplyService.getNetwork(projectId);
		return Result.OK(osApplies);
	}
	/**
	 *   獲取秘鑰
	 *
	 * @param osApplyVo
	 * @return
	 */
	@AutoLog(value = "獲取秘鑰")
	@ApiOperation(value="獲取秘鑰", notes="獲取秘鑰")
	@PostMapping(value = "/getKeyPairs")
	public Result<?> getKeyPairs(@RequestBody OsApplyVo osApplyVo) {
		String projectId = osApplyVo.getProjectId();
		List<OsApplyVo> osApplies = osApplyService.getPrivateKey(projectId);
		return Result.OK(osApplies);
	}



	/**
	 *   添加
	 *
	 * @param osApply
	 * @return
	 */
	@AutoLog(value = "申請明細檔-添加")
	@ApiOperation(value="申請明細檔-添加", notes="申請明細檔-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsApply osApply) {
		osApply.setOptions("0");
		osApplyService.save(osApply);

		return Result.OK("添加成功！");
	}


	/**
	 *  編輯
	 *
	 * @param osApply
	 * @return
	 */
	@AutoLog(value = "申請明細檔-編輯")
	@ApiOperation(value="申請明細檔-編輯", notes="申請明細檔-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsApply osApply) {
		osApplyService.updateById(osApply);
		return Result.OK("編輯成功!");
	}

	/**
	 *   調整
	 *
	 * @param osApply
	 * @return
	 */
	@AutoLog(value = "申請明細檔-調整")
	@ApiOperation(value="申請明細檔-調整", notes="申請明細檔-調整")
	@PostMapping(value = "/adjust")
	public Result<?> adjust(@RequestBody OsApply osApply) {
		osApplyService.save(osApply);
		return Result.OK("添加成功！");
	}

	/**
	 *  提交
	 *
	 * @param osApply
	 * @return
	 */
	@AutoLog(value = "申請明細檔-提交")
	@ApiOperation(value="申請明細檔-提交", notes="申請明細檔-提交")
	@PutMapping(value = "/submit")
	public Result<?> submit(@RequestBody OsApply osApply) {
		if(osApply.getOptions().equals("0")){
			osApply.setOptions("1");
		}
		osApply.setStatus("0");
		osApplyService.updateById(osApply);

		OsOptionVo osOptionVO = new OsOptionVo();
		osOptionVO.setOptions(osApply.getOptions());
		osOptionVO.setStatus(osApply.getStatus());
		osOptionVO.setApplyId(osApply.getId());
		osOptionVO.setApplyName(osApply.getInstanceName());
		osOptionVO.setApplyBy(osApply.getCreateBy());
		osOptionVO.setApplyType("1");
		osOptionService.save(osOptionVO);
		return Result.OK("編輯成功!");
	}

	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "申請明細檔-通過id刪除")
	@ApiOperation(value="申請明細檔-通過id刪除", notes="申請明細檔-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		Result<?> result = new Result();
		String message = "";
		int counts = osApplyService.getStatus(id);
		if(counts>0){
			message = "審核已通過，不能刪除";
		}else{
			osApplyService.removeById(id);
			osOptionService.deleteOption(id,"1");
			message = "刪除成功!";
		}
		result.setMessage(message);
		return result;
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "申請明細檔-批量刪除")
	@ApiOperation(value="申請明細檔-批量刪除", notes="申請明細檔-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<?> result = new Result();
		String message = "";
		ids = ids.substring(0,ids.length()-1);
		int counts = osApplyService.getCountStatus(Arrays.asList(ids.split(",")));
		if(counts>0){
			message = "審核已通過，不能刪除";
		}else{
			this.osApplyService.removeByIds(Arrays.asList(ids.split(",")));
			message = "批量刪除成功!";
		}
		result.setMessage(message);
		return result;
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "申請明細檔-通過id查詢")
	@ApiOperation(value="申請明細檔-通過id查詢", notes="申請明細檔-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsApply osApply = osApplyService.getById(id);
		if(osApply==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osApply);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osApply
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsApply osApply) {
		return super.exportXls(request, osApply, OsApply.class, "申請明細檔");
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
		return super.importExcel(request, response, OsApply.class);
	}

}
