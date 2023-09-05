package com.yaude.modules.system.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysDepartPermission;
import com.yaude.modules.system.entity.SysDepartRolePermission;
import com.yaude.modules.system.entity.SysPermission;
import com.yaude.modules.system.entity.SysPermissionDataRule;
import com.yaude.modules.system.model.TreeModel;
import com.yaude.modules.system.service.ISysDepartPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.yaude.modules.system.service.ISysDepartRolePermissionService;
import com.yaude.modules.system.service.ISysPermissionDataRuleService;
import com.yaude.modules.system.service.ISysPermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 部門權限表
 * @Author: jeecg-boot
 * @Date:   2020-02-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags="部門權限表")
@RestController
@RequestMapping("/sys/sysDepartPermission")
public class SysDepartPermissionController extends JeecgController<SysDepartPermission, ISysDepartPermissionService> {
	@Autowired
	private ISysDepartPermissionService sysDepartPermissionService;

	 @Autowired
	 private ISysPermissionDataRuleService sysPermissionDataRuleService;

	 @Autowired
	 private ISysPermissionService sysPermissionService;

	 @Autowired
	 private ISysDepartRolePermissionService sysDepartRolePermissionService;

	/**
	 * 分頁列表查詢
	 *
	 * @param sysDepartPermission
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="部門權限表-分頁列表查詢", notes="部門權限表-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysDepartPermission sysDepartPermission,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysDepartPermission> queryWrapper = QueryGenerator.initQueryWrapper(sysDepartPermission, req.getParameterMap());
		Page<SysDepartPermission> page = new Page<SysDepartPermission>(pageNo, pageSize);
		IPage<SysDepartPermission> pageList = sysDepartPermissionService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param sysDepartPermission
	 * @return
	 */
	@ApiOperation(value="部門權限表-添加", notes="部門權限表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysDepartPermission sysDepartPermission) {
		sysDepartPermissionService.save(sysDepartPermission);
		return Result.ok("添加成功！");
	}
	
	/**
	 * 編輯
	 *
	 * @param sysDepartPermission
	 * @return
	 */
	@ApiOperation(value="部門權限表-編輯", notes="部門權限表-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysDepartPermission sysDepartPermission) {
		sysDepartPermissionService.updateById(sysDepartPermission);
		return Result.ok("編輯成功!");
	}
	
	/**
	 * 通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="部門權限表-通過id刪除", notes="部門權限表-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysDepartPermissionService.removeById(id);
		return Result.ok("刪除成功!");
	}
	
	/**
	 * 批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@ApiOperation(value="部門權限表-批量刪除", notes="部門權限表-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysDepartPermissionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量刪除成功！");
	}
	
	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="部門權限表-通過id查詢", notes="部門權限表-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysDepartPermission sysDepartPermission = sysDepartPermissionService.getById(id);
		return Result.ok(sysDepartPermission);
	}

	/**
	* 導出excel
	*
	* @param request
	* @param sysDepartPermission
	*/
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysDepartPermission sysDepartPermission) {
	  return super.exportXls(request, sysDepartPermission, SysDepartPermission.class, "部門權限表");
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
	  return super.importExcel(request, response, SysDepartPermission.class);
	}

	/**
	* 部門管理授權查詢數據規則數據
	*/
	@GetMapping(value = "/datarule/{permissionId}/{departId}")
	public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId,@PathVariable("departId") String departId) {
		List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
		if(list==null || list.size()==0) {
			return Result.error("未找到權限配置信息");
		}else {
			Map<String,Object> map = new HashMap<>();
			map.put("datarule", list);
			LambdaQueryWrapper<SysDepartPermission> query = new LambdaQueryWrapper<SysDepartPermission>()
				 .eq(SysDepartPermission::getPermissionId, permissionId)
				 .eq(SysDepartPermission::getDepartId,departId);
			SysDepartPermission sysDepartPermission = sysDepartPermissionService.getOne(query);
			if(sysDepartPermission==null) {
			 //return Result.error("未找到角色菜單配置信息");
			}else {
				String drChecked = sysDepartPermission.getDataRuleIds();
				if(oConvertUtils.isNotEmpty(drChecked)) {
					map.put("drChecked", drChecked.endsWith(",")?drChecked.substring(0, drChecked.length()-1):drChecked);
				}
			}
			return Result.ok(map);
			//TODO 以后按鈕權限的查詢也走這個請求 無非在map中多加兩個key
		}
	}

	/**
	* 保存數據規則至部門菜單關聯表
	*/
	@PostMapping(value = "/datarule")
	public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
		try {
			String permissionId = jsonObject.getString("permissionId");
			String departId = jsonObject.getString("departId");
			String dataRuleIds = jsonObject.getString("dataRuleIds");
			log.info("保存數據規則>>"+"菜單ID:"+permissionId+"部門ID:"+ departId+"數據權限ID:"+dataRuleIds);
			LambdaQueryWrapper<SysDepartPermission> query = new LambdaQueryWrapper<SysDepartPermission>()
				 .eq(SysDepartPermission::getPermissionId, permissionId)
				 .eq(SysDepartPermission::getDepartId,departId);
			SysDepartPermission sysDepartPermission = sysDepartPermissionService.getOne(query);
			if(sysDepartPermission==null) {
				return Result.error("請先保存部門菜單權限!");
			}else {
				sysDepartPermission.setDataRuleIds(dataRuleIds);
			 	this.sysDepartPermissionService.updateById(sysDepartPermission);
			}
		} catch (Exception e) {
		 	log.error("SysDepartPermissionController.saveDatarule()發生異常：" + e.getMessage(),e);
		 	return Result.error("保存失敗");
		}
		return Result.ok("保存成功!");
	}

	 /**
	  * 查詢角色授權
	  *
	  * @return
	  */
	 @RequestMapping(value = "/queryDeptRolePermission", method = RequestMethod.GET)
	 public Result<List<String>> queryDeptRolePermission(@RequestParam(name = "roleId", required = true) String roleId) {
		 Result<List<String>> result = new Result<>();
		 try {
			 List<SysDepartRolePermission> list = sysDepartRolePermissionService.list(new QueryWrapper<SysDepartRolePermission>().lambda().eq(SysDepartRolePermission::getRoleId, roleId));
			 result.setResult(list.stream().map(SysDepartRolePermission -> String.valueOf(SysDepartRolePermission.getPermissionId())).collect(Collectors.toList()));
			 result.setSuccess(true);
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
		 }
		 return result;
	 }

	 /**
	  * 保存角色授權
	  *
	  * @return
	  */
	 @RequestMapping(value = "/saveDeptRolePermission", method = RequestMethod.POST)
	 public Result<String> saveDeptRolePermission(@RequestBody JSONObject json) {
		 long start = System.currentTimeMillis();
		 Result<String> result = new Result<>();
		 try {
			 String roleId = json.getString("roleId");
			 String permissionIds = json.getString("permissionIds");
			 String lastPermissionIds = json.getString("lastpermissionIds");
			 this.sysDepartRolePermissionService.saveDeptRolePermission(roleId, permissionIds, lastPermissionIds);
			 result.success("保存成功！");
			 log.info("======部門角色授權成功=====耗時:" + (System.currentTimeMillis() - start) + "毫秒");
		 } catch (Exception e) {
			 result.error500("授權失敗！");
			 log.error(e.getMessage(), e);
		 }
		 return result;
	 }

	 /**
	  * 用戶角色授權功能，查詢菜單權限樹
	  * @param request
	  * @return
	  */
	 @RequestMapping(value = "/queryTreeListForDeptRole", method = RequestMethod.GET)
	 public Result<Map<String,Object>> queryTreeListForDeptRole(@RequestParam(name="departId",required=true) String departId,HttpServletRequest request) {
		 Result<Map<String,Object>> result = new Result<>();
		 //全部權限ids
		 List<String> ids = new ArrayList<>();
		 try {
			 LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			 query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			 query.orderByAsc(SysPermission::getSortNo);
			 query.inSql(SysPermission::getId,"select permission_id  from sys_depart_permission where depart_id='"+departId+"'");
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

	 private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
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
