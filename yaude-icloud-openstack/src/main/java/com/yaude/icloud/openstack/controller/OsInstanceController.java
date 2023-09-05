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

import com.google.common.collect.ImmutableList;
import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.icloud.openstack.entity.OsInstance;
import com.yaude.icloud.openstack.service.IOsInstanceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
import lombok.extern.slf4j.Slf4j;


import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.storage.block.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
@Api(tags="申請明細檔")
@RestController
@RequestMapping("/openstack/osInstance")
@Slf4j
public class OsInstanceController extends JeecgController<OsInstance, IOsInstanceService> {
	@Autowired
	private IOsInstanceService osInstanceService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osInstanceVo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "申請明細檔-分頁列表查詢")
	@ApiOperation(value="申請明細檔-分頁列表查詢", notes="申請明細檔-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsInstanceVo osInstanceVo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		//QueryWrapper<OsInstance> queryWrapper = QueryGenerator.initQueryWrapper(osInstanceVo, req.getParameterMap());
		//Page<OsInstance> page = new Page<OsInstance>(pageNo, pageSize);
		//IPage<OsInstance> pageList = osInstanceService.page(page, queryWrapper);
		List<OsInstanceVo> osInstanceVos = osInstanceService.getInstanceList(osInstanceVo);
		return Result.OK(osInstanceVos);
	}


	/**
	 * 獲取單個實例信息
	 *
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "獲取單個實例信息")
	@ApiOperation(value="獲取單個實例信息", notes="獲取單個實例信息")
	@GetMapping(value = "/getServerDetailById")
	public Result<?> getServerDetailById(OsInstanceVo osInstanceVo) {
		OsInstanceVo res = osInstanceService.getServerDetailById(osInstanceVo.getId(),osInstanceVo.getProjectId());
		return Result.OK(res);
	}

	/**
	 *   添加
	 *
	 * @param osInstance
	 * @return
	 */
	@AutoLog(value = "申請明細檔-添加")
	@ApiOperation(value="申請明細檔-添加", notes="申請明細檔-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsInstance osInstance) {
		osInstanceService.save(osInstance);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osInstance
	 * @return
	 */
	@AutoLog(value = "申請明細檔-編輯")
	@ApiOperation(value="申請明細檔-編輯", notes="申請明細檔-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsInstance osInstance) {
		osInstanceService.updateById(osInstance);
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
	public Result<?> delete(@RequestParam(name="id",required=true) String id,@RequestParam(name="projectId",required=true) String projectId) throws InterruptedException {
		osInstanceService.delete(id,projectId);
		return Result.OK("刪除成功!");
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
		this.osInstanceService.deleteBatch(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
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
		OsInstance osInstance = osInstanceService.getById(id);
		if(osInstance==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osInstance);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osInstance
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsInstance osInstance) {
		return super.exportXls(request, osInstance, OsInstance.class, "申請明細檔");
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
		return super.importExcel(request, response, OsInstance.class);
	}


	/**
	 *   通過id開啟實例
	 *
	 * @param osInstance
	 * @return
	 */
	@AutoLog(value = "實例-開啟")
	@ApiOperation(value="實例-開啟", notes="實例-開啟")
	@GetMapping(value = "/powerOn")
	public Result<?> powerOn(OsInstance osInstance) throws InterruptedException {
		osInstanceService.startInstance(osInstance);
		return Result.OK("實例開啟成功!");
	}

	/**
	 *   通過id關閉實例
	 *
	 * @param osInstance
	 * @return
	 */
	@AutoLog(value = "實例-關閉")
	@ApiOperation(value="實例-關閉", notes="實例-關閉")
	@GetMapping(value = "/shutDown")
	public Result<?> shutDown(OsInstance osInstance) throws InterruptedException {
		osInstanceService.stopInstance(osInstance);
		return Result.OK("實例關閉成功!");
	}


	/**
	 *   通過id硬重啟實例
	 *
	 * @param osInstance
	 * @return
	 */
	@AutoLog(value = "實例-硬重啟")
	@ApiOperation(value="實例-硬重啟", notes="實例-硬重啟")
	@GetMapping(value = "/rebootByHARD")
	public Result<?> rebootByHARD(OsInstance osInstance) throws InterruptedException {
		osInstanceService.rebootInstanceByHARD(osInstance);
		return Result.OK("實例硬重啟成功!");
	}




	/**
	 *   通過id軟重啟實例
	 *
	 * @param osInstance
	 * @return
	 */
	@AutoLog(value = "實例-軟重啟")
	@ApiOperation(value="實例-軟重啟", notes="實例-軟重啟")
	@GetMapping(value = "/rebootBySOFT")
	public Result<?> rebootBySOFT(OsInstance osInstance) throws InterruptedException {
		osInstanceService.rebootInstanceBySOFT(osInstance);
		return Result.OK("實例軟重啟成功!");
	}


	/**
	 *   通過id獲取控制臺鏈接
	 *
	 * @param instanceID
	 * @return
	 */
	@AutoLog(value = "實例-通過id獲取控制臺鏈接")
	@ApiOperation(value="實例-通過id獲取控制臺鏈接", notes="實例-通過id獲取控制臺鏈接")
	@GetMapping(value = "/getConsoleUrl")
	public Result<?> getConsoleUrl( String instanceID,String projectId)  {
		String res = osInstanceService.getConsoleUrl(instanceID,projectId);
		return Result.OK(res);
	}


	/**
	 *   通過id,imgName 創建快照
	 *
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "實例-通過id,imgName 創建快照")
	@ApiOperation(value="實例-通過id,imgName 創建快照", notes="實例-通過id,imgName 創建快照")
	@GetMapping(value = "/createSnapshot")
	public Result<?> createSnapshot(OsInstanceVo osInstanceVo) {
		String res = osInstanceService.createSnapshot(osInstanceVo);
		return Result.OK(res);
	}


	/**
	 *   獲取可用的卷列表
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "獲取可用的卷列表")
	@ApiOperation(value="獲取可用的卷列表", notes="獲取可用的卷列表")
	@GetMapping(value = "/getAvailableVolumes")
	public Result<?> getAvailableVolumes(OsInstanceVo osInstanceVo) {
		List<Volume> res = osInstanceService.getAvailableVolumes(osInstanceVo);
		return Result.OK(res);
	}

	/**
	 *   獲取可用的卷列表
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "獲取已連接卷列表")
	@ApiOperation(value="獲取已連接卷列表", notes="獲取已連接卷列表")
	@GetMapping(value = "/getInUseVolumes")
	public Result<?> getInUseVolumes(OsInstanceVo osInstanceVo) {
		List<Volume> res = osInstanceService.getInUseVolumes(osInstanceVo);
		return Result.OK(res);
	}


	/**
	 *   獲取可綁定的浮動Ip列表
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "獲取可綁定的浮動Ip列表")
	@ApiOperation(value="獲取可綁定的浮動Ip列表", notes="獲取可綁定的浮動Ip列表")
	@GetMapping(value = "/getFloatingIps")
	public Result<?> getFloatingIps(OsInstanceVo osInstanceVo) {
		List<NetFloatingIP> res = osInstanceService.getFloatingIps(osInstanceVo);
		if(StringUtils.isNotEmpty(osInstanceVo.getFloatingIpStatus())){
			res = res.stream().filter(r -> r.getStatus().equals(osInstanceVo.getFloatingIpStatus())).collect(Collectors.toList());
		}
		return Result.OK(res);
	}


	/**
	 *   獲取待解除的浮動Ip
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "獲取待解除的浮動Ip")
	@ApiOperation(value="獲取待解除的浮動Ip", notes="獲取待解除的浮動Ip")
	@GetMapping(value = "/getFloatingIpById")
	public Result<?> getFloatingIpById(OsInstanceVo osInstanceVo) {
		OsInstanceVo serverDetailById = osInstanceService.getServerDetailById(osInstanceVo.getId(),osInstanceVo.getProjectId());
		return Result.OK(serverDetailById);
	}





	/**
	 *   通過id,volumeId 連接卷
	 *
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "通過id,volumeId 連接卷")
	@ApiOperation(value="通過id,volumeId 連接卷", notes="通過id,volumeId 連接卷")
	@GetMapping(value = "/connectVolume")
	public Result<?> connectVolume(OsInstanceVo osInstanceVo) {
		osInstanceService.connectVolume(osInstanceVo);
		return Result.OK("連接成功");
	}

	/**
	 *   通過id,volumeId 分離卷
	 *
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "通過id,volumeId 分離卷")
	@ApiOperation(value="通過id,volumeId 分離卷", notes="通過id,volumeId 分離卷")
	@GetMapping(value = "/detachVolume")
	public Result<?> detachVolume(OsInstanceVo osInstanceVo) {
		ActionResponse actionResponse = osInstanceService.detachVolume(osInstanceVo);
		if(actionResponse.isSuccess()){//正常

		}else if(actionResponse.getCode()==400){
			return  Result.error(actionResponse.getFault());
		}
		return Result.OK("分離成功");
	}

	/**
	 *   實例-綁定浮動IP
	 *
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "實例-綁定浮動IP")
	@ApiOperation(value="實例-綁定浮動IP", notes="實例-綁定浮動IP")
	@GetMapping(value = "/addFloatingIp")
	public Result<?> addFloatingIp(OsInstanceVo osInstanceVo) {
		ActionResponse actionResponse = osInstanceService.addFloatingIp(osInstanceVo);
		if(actionResponse.isSuccess()){//正常

		}else if(actionResponse.getCode()==400){
			return  Result.error(actionResponse.getFault());
		}
		return Result.OK("綁定成功");
	}


	/**
	 *   實例-解除浮動IP的綁定
	 *
	 * @param osInstanceVo
	 * @return
	 */
	@AutoLog(value = "實例-解除浮動IP的綁定")
	@ApiOperation(value="實例-解除浮動IP的綁定", notes="實例-解除浮動IP的綁定")
	@GetMapping(value = "/removeFloatingIP")
	public Result<?> removeFloatingIP(OsInstanceVo osInstanceVo) {
		ActionResponse actionResponse = osInstanceService.removeFloatingIP(osInstanceVo);
		if(actionResponse.isSuccess()){//正常

		}else if(actionResponse.getCode()==400){
			return  Result.error(actionResponse.getFault());
		}
		return Result.OK("解除成功");
	}




}
