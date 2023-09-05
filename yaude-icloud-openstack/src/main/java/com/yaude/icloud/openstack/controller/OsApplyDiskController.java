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
import com.yaude.icloud.openstack.service.IOsApplyDiskService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.service.IOsOptionService;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyDiskVo;
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
 * @Description: 磁盤申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
@Api(tags="磁盤申請明細檔")
@RestController
@RequestMapping("/os/osApplyDisk")
@Slf4j
public class OsApplyDiskController extends JeecgController<OsApplyDisk, IOsApplyDiskService> {
	@Autowired
	private IOsApplyDiskService osApplyDiskService;

	@Autowired
	private IOsOptionService osOptionService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osApplyDisk
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-分頁列表查詢")
	@ApiOperation(value="磁盤申請明細檔-分頁列表查詢", notes="磁盤申請明細檔-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsApplyDisk osApplyDisk,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsApplyDisk> queryWrapper = QueryGenerator.initQueryWrapper(new OsApplyDisk(), req.getParameterMap());
		Page<OsApplyDisk> page = new Page<OsApplyDisk>(pageNo, pageSize);
		//只顯示當前登錄人申請的數據
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String username = loginUser.getUsername();
		queryWrapper.eq("create_by",username);
		IPage<OsApplyDisk> pageList = osApplyDiskService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 根據id查詢所有數據
	 *
	 * @param osApplyDisk
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "根據id查詢所有數據")
	@ApiOperation(value="根據id查詢所有數據", notes="根據id查詢所有數據")
	@GetMapping(value = "/diskapplys")
	public Result<?> diskapplys(OsApplyDisk osApplyDisk,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) {
		QueryWrapper<OsApplyDisk> queryWrapper = QueryGenerator.initQueryWrapper(new OsApplyDisk(), req.getParameterMap());
		Page<OsApply> page = new Page<OsApply>(pageNo, pageSize);
		queryWrapper.eq("id",osApplyDisk.getId());
		List<OsApplyDisk> list = osApplyDiskService.list(queryWrapper);
		List <OsApplyDiskVo> osApplyDiskVos = osApplyDiskService.getOsApplyDiskList(list) ;
		return Result.OK(osApplyDiskVos);

	}

	/**
	 *   添加
	 *
	 * @param osApplyDisk
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-添加")
	@ApiOperation(value="磁盤申請明細檔-添加", notes="磁盤申請明細檔-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsApplyDisk osApplyDisk) {
		osApplyDisk.setOptions("0");
		osApplyDisk.setBoostatus("1");
		osApplyDiskService.save(osApplyDisk);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osApplyDisk
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-編輯")
	@ApiOperation(value="磁盤申請明細檔-編輯", notes="磁盤申請明細檔-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsApplyDisk osApplyDisk) {
		osApplyDiskService.updateById(osApplyDisk);
		return Result.OK("編輯成功!");
	}

	/**
	 *   調整
	 *
	 * @param osApplyDisk
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-調整")
	@ApiOperation(value="磁盤申請明細檔-調整", notes="磁盤申請明細檔-調整")
	@PostMapping(value = "/adjust")
	public Result<?> adjust(@RequestBody OsApplyDisk osApplyDisk) {
		osApplyDiskService.save(osApplyDisk);
		return Result.OK("添加成功！");
	}

	/**
	 *  提交
	 *
	 * @param osApplyDisk
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-提交")
	@ApiOperation(value="磁盤申請明細檔-提交", notes="磁盤申請明細檔-提交")
	@PutMapping(value = "/submit")
	public Result<?> submit(@RequestBody OsApplyDisk osApplyDisk) {
		if(osApplyDisk.getOptions().equals("0")){
			osApplyDisk.setOptions("1");
		}
		osApplyDisk.setStatus("0");
		osApplyDiskService.updateById(osApplyDisk);

		OsOptionVo osOptionVO = new OsOptionVo();
		osOptionVO.setOptions(osApplyDisk.getOptions());
		osOptionVO.setStatus(osApplyDisk.getStatus());
		osOptionVO.setApplyId(osApplyDisk.getId());
		osOptionVO.setApplyName(osApplyDisk.getDiskName());
		osOptionVO.setApplyBy(osApplyDisk.getCreateBy());
		osOptionVO.setApplyType("3");
		osOptionService.save(osOptionVO);
		return Result.OK("編輯成功!");
	}

	/**
	 *   獲取磁盤類型
	 *
	 * @param osApplyDisk
	 * @return
	 */
	@AutoLog(value = "獲取磁盤類型")
	@ApiOperation(value="獲取磁盤類型", notes="獲取磁盤類型")
	@PostMapping(value = "/getType")
	public Result<?> getType(@RequestBody OsApplyDisk osApplyDisk) {
		String projectId = osApplyDisk.getProjectId();
		List<OsApplyDisk> osApplyDisks = osApplyDiskService.getNetworkType(projectId);
		return Result.OK(osApplyDisks);
	}
	/**
	 *   獲取img
	 *
	 * @param osApplyDiskVo
	 * @return
	 */
	@AutoLog(value = "獲取img")
	@ApiOperation(value="獲取img", notes="獲取img")
	@PostMapping(value = "/getImg")
	public Result<?> getImg(@RequestBody OsApplyDiskVo osApplyDiskVo) {
		String projectId = osApplyDiskVo.getProjectId();
		List<OsApplyDiskVo> osApplyDiskVos = osApplyDiskService.getImg(projectId);
		return Result.OK(osApplyDiskVos);
	}
	/**
	 *   獲取快照
	 *
	 * @param osApplyDiskVo
	 * @return
	 */
	@AutoLog(value = "獲取快照")
	@ApiOperation(value="獲取快照", notes="獲取快照")
	@PostMapping(value = "/getSnapshot")
	public Result<?> getSnapshot(@RequestBody OsApplyDiskVo osApplyDiskVo) {
		String projectId = osApplyDiskVo.getProjectId();
		List<OsApplyDiskVo> osApplyDiskVos = osApplyDiskService.getSnapshot(projectId);
		return Result.OK(osApplyDiskVos);
	}

	/**
	 *   獲取卷
	 *
	 * @param osApplyDiskVo
	 * @return
	 */
	@AutoLog(value = "獲取卷")
	@ApiOperation(value="獲取卷", notes="獲取卷")
	@PostMapping(value = "/getVolume")
	public Result<?> getVolume(@RequestBody OsApplyDiskVo osApplyDiskVo) {
		String projectId = osApplyDiskVo.getProjectId();
		List<OsApplyDiskVo> osApplyDiskVos = osApplyDiskService.getVolume(projectId);
		return Result.OK(osApplyDiskVos);
	}



	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-通過id刪除")
	@ApiOperation(value="磁盤申請明細檔-通過id刪除", notes="磁盤申請明細檔-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		Result<?> result = new Result();
		String message = "";
		int counts = osApplyDiskService.getStatus(id);
		if(counts>0){
			message = "審核已通過，不能刪除";
		}else{
			osApplyDiskService.removeById(id);
			osOptionService.deleteOption(id,"3");
			message = "刪除成功!";
		}
		result.setMessage(message);
		return result;

		/*osApplyDiskService.removeById(id);
		osOptionService.deleteOption(id,"3");
		return Result.OK("刪除成功!");*/
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-批量刪除")
	@ApiOperation(value="磁盤申請明細檔-批量刪除", notes="磁盤申請明細檔-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<?> result = new Result();
		String message = "";
		ids = ids.substring(0,ids.length()-1);
		int counts = osApplyDiskService.getCountStatus(Arrays.asList(ids.split(",")));
		if(counts>0){
			message = "審核已通過，不能刪除";
		}else{
			this.osApplyDiskService.removeByIds(Arrays.asList(ids.split(",")));
			message = "批量刪除成功!";
		}
		result.setMessage(message);
		return result;

		//this.osApplyDiskService.removeByIds(Arrays.asList(ids.split(",")));
		//return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "磁盤申請明細檔-通過id查詢")
	@ApiOperation(value="磁盤申請明細檔-通過id查詢", notes="磁盤申請明細檔-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsApplyDisk osApplyDisk = osApplyDiskService.getById(id);
		if(osApplyDisk==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osApplyDisk);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osApplyDisk
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsApplyDisk osApplyDisk) {
		return super.exportXls(request, osApplyDisk, OsApplyDisk.class, "磁盤申請明細檔");
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
		return super.importExcel(request, response, OsApplyDisk.class);
	}

}
