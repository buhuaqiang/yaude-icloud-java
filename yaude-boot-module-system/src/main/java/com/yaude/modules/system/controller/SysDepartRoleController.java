package com.yaude.modules.system.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.modules.system.service.*;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.yaude.modules.system.entity.SysDepartRole;
import com.yaude.modules.system.entity.SysDepartRolePermission;
import com.yaude.modules.system.entity.SysDepartRoleUser;
import com.yaude.modules.system.entity.SysPermissionDataRule;
import com.yaude.modules.system.service.*;

/**
 * @Description: 部門角色
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
@Slf4j
@Api(tags="部門角色")
@RestController
@RequestMapping("/sys/sysDepartRole")
public class SysDepartRoleController extends JeecgController<SysDepartRole, ISysDepartRoleService> {
	@Autowired
	private ISysDepartRoleService sysDepartRoleService;

	@Autowired
	private ISysDepartRoleUserService departRoleUserService;

	@Autowired
	private ISysDepartPermissionService sysDepartPermissionService;

	 @Autowired
	 private ISysDepartRolePermissionService sysDepartRolePermissionService;

	 @Autowired
	 private ISysDepartService sysDepartService;
	
	/**
	 * 分頁列表查詢
	 *
	 * @param sysDepartRole
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="部門角色-分頁列表查詢", notes="部門角色-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysDepartRole sysDepartRole,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   @RequestParam(name="deptId",required=false) String deptId,
								   HttpServletRequest req) {
		QueryWrapper<SysDepartRole> queryWrapper = QueryGenerator.initQueryWrapper(sysDepartRole, req.getParameterMap());
		Page<SysDepartRole> page = new Page<SysDepartRole>(pageNo, pageSize);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<String> deptIds = null;
//		if(oConvertUtils.isEmpty(deptId)){
//			if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2) ){
//				deptIds = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
//			}else{
//				return Result.ok(null);
//			}
//		}else{
//			deptIds = sysDepartService.getSubDepIdsByDepId(deptId);
//		}
//		queryWrapper.in("depart_id",deptIds);

		//我的部門，選中部門只能看當前部門下的角色
		queryWrapper.eq("depart_id",deptId);
		IPage<SysDepartRole> pageList = sysDepartRoleService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param sysDepartRole
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@ApiOperation(value="部門角色-添加", notes="部門角色-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysDepartRole sysDepartRole) {
		sysDepartRoleService.save(sysDepartRole);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 編輯
	 *
	 * @param sysDepartRole
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@ApiOperation(value="部門角色-編輯", notes="部門角色-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysDepartRole sysDepartRole) {
		sysDepartRoleService.updateById(sysDepartRole);
		return Result.ok("編輯成功!");
	}
	
	/**
	 * 通過id刪除
	 *
	 * @param id
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@AutoLog(value = "部門角色-通過id刪除")
	@ApiOperation(value="部門角色-通過id刪除", notes="部門角色-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysDepartRoleService.removeById(id);
		return Result.ok("刪除成功!");
	}
	
	/**
	 * 批量刪除
	 *
	 * @param ids
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@AutoLog(value = "部門角色-批量刪除")
	@ApiOperation(value="部門角色-批量刪除", notes="部門角色-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysDepartRoleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量刪除成功！");
	}
	
	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="部門角色-通過id查詢", notes="部門角色-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysDepartRole sysDepartRole = sysDepartRoleService.getById(id);
		return Result.ok(sysDepartRole);
	}

	 /**
	  * 獲取部門下角色
	  * @param departId
	  * @return
	  */
	@RequestMapping(value = "/getDeptRoleList", method = RequestMethod.GET)
	public Result<List<SysDepartRole>> getDeptRoleList(@RequestParam(value = "departId") String departId,@RequestParam(value = "userId") String userId){
		Result<List<SysDepartRole>> result = new Result<>();
		//查詢選中部門的角色
		List<SysDepartRole> deptRoleList = sysDepartRoleService.list(new LambdaQueryWrapper<SysDepartRole>().eq(SysDepartRole::getDepartId,departId));
		result.setSuccess(true);
		result.setResult(deptRoleList);
		return result;
	}

	 /**
	  * 設置
	  * @param json
	  * @return
	  */
	 //@RequiresRoles({"admin"})
	 @RequestMapping(value = "/deptRoleUserAdd", method = RequestMethod.POST)
	 public Result<?> deptRoleAdd(@RequestBody JSONObject json) {
		 String newRoleId = json.getString("newRoleId");
		 String oldRoleId = json.getString("oldRoleId");
		 String userId = json.getString("userId");
		 departRoleUserService.deptRoleUserAdd(userId,newRoleId,oldRoleId);
		 return Result.ok("添加成功！");
	 }

	 /**
	  * 根據用戶id獲取已設置部門角色
	  * @param userId
	  * @return
	  */
	 @RequestMapping(value = "/getDeptRoleByUserId", method = RequestMethod.GET)
	 public Result<List<SysDepartRoleUser>> getDeptRoleByUserId(@RequestParam(value = "userId") String userId, @RequestParam(value = "departId") String departId){
		 Result<List<SysDepartRoleUser>> result = new Result<>();
		 //查詢部門下角色
		 List<SysDepartRole> roleList = sysDepartRoleService.list(new QueryWrapper<SysDepartRole>().eq("depart_id",departId));
		 List<String> roleIds = roleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
		 //根據角色id,用戶id查詢已授權角色
		 List<SysDepartRoleUser> roleUserList = departRoleUserService.list(new QueryWrapper<SysDepartRoleUser>().eq("user_id",userId).in("drole_id",roleIds));
		 result.setSuccess(true);
		 result.setResult(roleUserList);
		 return result;
	 }

	 /**
	  * 查詢數據規則數據
	  */
	 @GetMapping(value = "/datarule/{permissionId}/{departId}/{roleId}")
	 public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("departId") String departId,@PathVariable("roleId") String roleId) {
		//查詢已授權的部門規則
	 	List<SysPermissionDataRule> list = sysDepartPermissionService.getPermRuleListByDeptIdAndPermId(departId,permissionId);
		 if(list==null || list.size()==0) {
			 return Result.error("未找到權限配置信息");
		 }else {
			 Map<String,Object> map = new HashMap<>();
			 map.put("datarule", list);
			 LambdaQueryWrapper<SysDepartRolePermission> query = new LambdaQueryWrapper<SysDepartRolePermission>()
					 .eq(SysDepartRolePermission::getPermissionId, permissionId)
					 .eq(SysDepartRolePermission::getRoleId,roleId);
			 SysDepartRolePermission sysRolePermission = sysDepartRolePermissionService.getOne(query);
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
			 LambdaQueryWrapper<SysDepartRolePermission> query = new LambdaQueryWrapper<SysDepartRolePermission>()
					 .eq(SysDepartRolePermission::getPermissionId, permissionId)
					 .eq(SysDepartRolePermission::getRoleId,roleId);
			 SysDepartRolePermission sysRolePermission = sysDepartRolePermissionService.getOne(query);
			 if(sysRolePermission==null) {
				 return Result.error("請先保存角色菜單權限!");
			 }else {
				 sysRolePermission.setDataRuleIds(dataRuleIds);
				 this.sysDepartRolePermissionService.updateById(sysRolePermission);
			 }
		 } catch (Exception e) {
			 log.error("SysRoleController.saveDatarule()發生異常：" + e.getMessage(),e);
			 return Result.error("保存失敗");
		 }
		 return Result.ok("保存成功!");
	 }

  /**
   * 導出excel
   *
   * @param request
   * @param sysDepartRole
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SysDepartRole sysDepartRole) {
      return super.exportXls(request, sysDepartRole, SysDepartRole.class, "部門角色");
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
      return super.importExcel(request, response, SysDepartRole.class);
  }

}
