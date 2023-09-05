package com.yaude.modules.system.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysPermission;
import com.yaude.modules.system.entity.SysPermissionDataRule;
import com.yaude.modules.system.entity.SysRole;
import com.yaude.modules.system.entity.SysRolePermission;
import com.yaude.modules.system.model.TreeModel;
import com.yaude.modules.system.service.ISysPermissionDataRuleService;
import com.yaude.modules.system.service.ISysPermissionService;
import com.yaude.modules.system.service.ISysRolePermissionService;
import com.yaude.modules.system.service.ISysRoleService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@RestController
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {
	@Autowired
	private ISysRoleService sysRoleService;
	
	@Autowired
	private ISysPermissionDataRuleService sysPermissionDataRuleService;
	
	@Autowired
	private ISysRolePermissionService sysRolePermissionService;
	
	@Autowired
	private ISysPermissionService sysPermissionService;

	/**
	  * 分頁列表查詢
	 * @param role
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysRole>> queryPageList(SysRole role,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
		Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	//@RequiresRoles({"admin"})
	public Result<SysRole> add(@RequestBody SysRole role) {
		Result<SysRole> result = new Result<SysRole>();
		try {
			role.setCreateTime(new Date());
			sysRoleService.save(role);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}
	
	/**
	  *  編輯
	 * @param role
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<SysRole> edit(@RequestBody SysRole role) {
		Result<SysRole> result = new Result<SysRole>();
		SysRole sysrole = sysRoleService.getById(role.getId());
		if(sysrole==null) {
			result.error500("未找到對應實體");
		}else {
			role.setUpdateTime(new Date());
			boolean ok = sysRoleService.updateById(role);
			//TODO 返回false說明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *   通過id刪除
	 * @param id
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysRoleService.deleteRole(id);
		return Result.ok("刪除角色成功");
	}
	
	/**
	  *  批量刪除
	 * @param ids
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysRole> result = new Result<SysRole>();
		if(oConvertUtils.isEmpty(ids)) {
			result.error500("未選中角色！");
		}else {
			sysRoleService.deleteBatchRole(ids.split(","));
			result.success("刪除角色成功!");
		}
		return result;
	}
	
	/**
	  * 通過id查詢
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<SysRole> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysRole> result = new Result<SysRole>();
		SysRole sysrole = sysRoleService.getById(id);
		if(sysrole==null) {
			result.error500("未找到對應實體");
		}else {
			result.setResult(sysrole);
			result.setSuccess(true);
		}
		return result;
	}
	
	@RequestMapping(value = "/queryall", method = RequestMethod.GET)
	public Result<List<SysRole>> queryall() {
		Result<List<SysRole>> result = new Result<>();
		List<SysRole> list = sysRoleService.list();
		if(list==null||list.size()<=0) {
			result.error500("未找到角色信息");
		}else {
			result.setResult(list);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	  * 校驗角色編碼唯一
	 */
	@RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
	public Result<Boolean> checkUsername(String id,String roleCode) {
		Result<Boolean> result = new Result<>();
		result.setResult(true);//如果此參數為false則程序發生異常
		log.info("--驗證角色編碼是否唯一---id:"+id+"--roleCode:"+roleCode);
		try {
			SysRole role = null;
			if(oConvertUtils.isNotEmpty(id)) {
				role = sysRoleService.getById(id);
			}
			SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
			if(newRole!=null) {
				//如果根據傳入的roleCode查詢到信息了，那么就需要做校驗了。
				if(role==null) {
					//role為空=>新增模式=>只要roleCode存在則返回false
					result.setSuccess(false);
					result.setMessage("角色編碼已存在");
					return result;
				}else if(!id.equals(newRole.getId())) {
					//否則=>編輯模式=>判斷兩者ID是否一致-
					result.setSuccess(false);
					result.setMessage("角色編碼已存在");
					return result;
				}
			}
		} catch (Exception e) {
			result.setSuccess(false);
			result.setResult(false);
			result.setMessage(e.getMessage());
			return result;
		}
		result.setSuccess(true);
		return result;
	}

	/**
	 * 導出excel
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysRole sysRole,HttpServletRequest request) {
		// Step.1 組裝查詢條件
		QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(sysRole, request.getParameterMap());
		//Step.2 AutoPoi 導出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysRole> pageList = sysRoleService.list(queryWrapper);
		//導出文件名稱
		mv.addObject(NormalExcelConstants.FILE_NAME,"角色列表");
		mv.addObject(NormalExcelConstants.CLASS,SysRole.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("角色列表數據","導出人:"+user.getRealname(),"導出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
		return mv;
	}

	/**
	 * 通過excel導入數據
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 獲取上傳文件對象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				return sysRoleService.importExcelCheckRoleCode(file, params);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件導入失敗:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return Result.error("文件導入失敗！");
	}
	
	/**
	 * 查詢數據規則數據
	 */
	@GetMapping(value = "/datarule/{permissionId}/{roleId}")
	public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("roleId") String roleId) {
		List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
		if(list==null || list.size()==0) {
			return Result.error("未找到權限配置信息");
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("datarule", list);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.isNotNull(SysRolePermission::getDataRuleIds)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				//return Result.error("未找到角色菜單配置信息");
			}else {
				String drChecked = sysRolePermission.getDataRuleIds();
				if(oConvertUtils.isNotEmpty(drChecked)) {
					map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				}
			}
			return Result.ok(map);
			//TODO 以后按鈕權限的查詢也走這個請求 無非在map中多加兩個key
		}
	}
	
	/**
	 * 保存數據規則至角色菜單關聯表
	 */
	@PostMapping(value = "/datarule")
	public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
		try {
			String permissionId = jsonObject.getString("permissionId");
			String roleId = jsonObject.getString("roleId");
			String dataRuleIds = jsonObject.getString("dataRuleIds");
			log.info("保存數據規則>>"+"菜單ID:"+permissionId+"角色ID:"+ roleId+"數據權限ID:"+dataRuleIds);
			LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
					.eq(SysRolePermission::getPermissionId, permissionId)
					.eq(SysRolePermission::getRoleId,roleId);
			SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
			if(sysRolePermission==null) {
				return Result.error("請先保存角色菜單權限!");
			}else {
				sysRolePermission.setDataRuleIds(dataRuleIds);
				this.sysRolePermissionService.updateById(sysRolePermission);
			}
		} catch (Exception e) {
			log.error("SysRoleController.saveDatarule()發生異常：" + e.getMessage(),e);
			return Result.error("保存失敗");
		}
		return Result.ok("保存成功!");
	}
	
	
	/**
	 * 用戶角色授權功能，查詢菜單權限樹
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<Map<String,Object>> queryTreeList(HttpServletRequest request) {
		Result<Map<String,Object>> result = new Result<>();
		//全部權限ids
		List<String> ids = new ArrayList<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			for(SysPermission sysPer : list) {
				ids.add(sysPer.getId());
			}
			List<TreeModel> treeList = new ArrayList<>();
			getTreeModelList(treeList, list, null);
			Map<String,Object> resMap = new HashMap<String,Object>();
			resMap.put("treeList", treeList); //全部樹節點數據
			resMap.put("ids", ids);//全部樹ids
			result.setResult(resMap);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	private void getTreeModelList(List<TreeModel> treeList,List<SysPermission> metaList,TreeModel temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(),permission.getRuleFlag(), permission.isLeaf());
			if(temp==null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if(!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}else if(temp!=null && tempPid!=null && tempPid.equals(temp.getKey())){
				temp.getChildren().add(tree);
				if(!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}
			
		}
	}
	
	
}
