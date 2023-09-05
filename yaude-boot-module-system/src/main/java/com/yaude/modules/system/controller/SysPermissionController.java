package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.constant.enums.RoleIndexConfigEnum;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.modules.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.MD5Util;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysDepartPermission;
import com.yaude.modules.system.entity.SysPermission;
import com.yaude.modules.system.entity.SysPermissionDataRule;
import com.yaude.modules.system.entity.SysRolePermission;
import com.yaude.modules.system.model.SysPermissionTree;
import com.yaude.modules.system.model.TreeModel;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.util.PermissionDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜單權限表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Slf4j
@RestController
@RequestMapping("/sys/permission")
public class SysPermissionController {

	@Autowired
	private ISysPermissionService sysPermissionService;

	@Autowired
	private ISysRolePermissionService sysRolePermissionService;

	@Autowired
	private ISysTranslateService sysTranslateService;

	@Autowired
	private ISysPermissionDataRuleService sysPermissionDataRuleService;

	@Autowired
	private ISysDepartPermissionService sysDepartPermissionService;

	@Autowired
	private ISysUserService sysUserService;

	/**
	 * 加載數據節點
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<List<SysPermissionTree>> list() {
        long start = System.currentTimeMillis();
		Result<List<SysPermissionTree>> result = new Result<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			List<SysPermissionTree> treeList = new ArrayList<>();
			getTreeList(treeList, list, null);
			result.setResult(treeList);
			result.setSuccess(true);
            log.info("======獲取全部菜單數據=====耗時:" + (System.currentTimeMillis() - start) + "毫秒");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/*update_begin author:wuxianquan date:20190908 for:先查詢一級菜單，當用戶點擊展開菜單時加載子菜單 */
	/**
	 * 系統菜單列表(一級菜單)
	 *
	 * @return
	 */
	@RequestMapping(value = "/getSystemMenuList", method = RequestMethod.GET)
	public Result<List<SysPermissionTree>> getSystemMenuList() {
        long start = System.currentTimeMillis();
		Result<List<SysPermissionTree>> result = new Result<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getMenuType,CommonConstant.MENU_TYPE_0);
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			List<SysPermissionTree> sysPermissionTreeList = new ArrayList<SysPermissionTree>();
			for(SysPermission sysPermission : list){
				SysPermissionTree sysPermissionTree = new SysPermissionTree(sysPermission);
				sysPermissionTreeList.add(sysPermissionTree);
			}
			result.setResult(sysPermissionTreeList);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        log.info("======獲取一級菜單數據=====耗時:" + (System.currentTimeMillis() - start) + "毫秒");
		return result;
	}

	/**
	 * 查詢子菜單
	 * @param parentId
	 * @return
	 */
	@RequestMapping(value = "/getSystemSubmenu", method = RequestMethod.GET)
	public Result<List<SysPermissionTree>> getSystemSubmenu(@RequestParam("parentId") String parentId){
		Result<List<SysPermissionTree>> result = new Result<>();
		try{
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getParentId,parentId);
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			List<SysPermissionTree> sysPermissionTreeList = new ArrayList<SysPermissionTree>();
			for(SysPermission sysPermission : list){
				SysPermissionTree sysPermissionTree = new SysPermissionTree(sysPermission);
				sysPermissionTreeList.add(sysPermissionTree);
			}
			result.setResult(sysPermissionTreeList);
			result.setSuccess(true);
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}
		return result;
	}
	/*update_end author:wuxianquan date:20190908 for:先查詢一級菜單，當用戶點擊展開菜單時加載子菜單 */

	// update_begin author:sunjianlei date:20200108 for: 新增批量根據父ID查詢子級菜單的接口 -------------
	/**
	 * 查詢子菜單
	 *
	 * @param parentIds 父ID（多個采用半角逗號分割）
	 * @return 返回 key-value 的 Map
	 */
	@GetMapping("/getSystemSubmenuBatch")
	public Result getSystemSubmenuBatch(@RequestParam("parentIds") String parentIds) {
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
			List<String> parentIdList = Arrays.asList(parentIds.split(","));
			query.in(SysPermission::getParentId, parentIdList);
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			Map<String, List<SysPermissionTree>> listMap = new HashMap<>();
			for (SysPermission item : list) {
				String pid = item.getParentId();
				if (parentIdList.contains(pid)) {
					List<SysPermissionTree> mapList = listMap.get(pid);
					if (mapList == null) {
						mapList = new ArrayList<>();
					}
					mapList.add(new SysPermissionTree(item));
					listMap.put(pid, mapList);
				}
			}
			return Result.ok(listMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("批量查詢子菜單失敗：" + e.getMessage());
		}
	}
	// update_end author:sunjianlei date:20200108 for: 新增批量根據父ID查詢子級菜單的接口 -------------

//	/**
//	 * 查詢用戶擁有的菜單權限和按鈕權限（根據用戶賬號）
//	 * 
//	 * @return
//	 */
//	@RequestMapping(value = "/queryByUser", method = RequestMethod.GET)
//	public Result<JSONArray> queryByUser(HttpServletRequest req) {
//		Result<JSONArray> result = new Result<>();
//		try {
//			String username = req.getParameter("username");
//			List<SysPermission> metaList = sysPermissionService.queryByUser(username);
//			JSONArray jsonArray = new JSONArray();
//			this.getPermissionJsonArray(jsonArray, metaList, null);
//			result.setResult(jsonArray);
//			result.success("查詢成功");
//		} catch (Exception e) {
//			result.error500("查詢失敗:" + e.getMessage());
//			log.error(e.getMessage(), e);
//		}
//		return result;
//	}

	/**
	 * 查詢用戶擁有的菜單權限和按鈕權限
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getUserPermissionByToken", method = RequestMethod.GET)
	public Result<?> getUserPermissionByToken() {
		Result<JSONObject> result = new Result<JSONObject>();
		try {
			//直接獲取當前用戶不適用前端token
			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			if (oConvertUtils.isEmpty(loginUser)) {
				return Result.error("請登錄系統！");
			}
			List<SysPermission> metaList = sysPermissionService.queryByUser(loginUser.getUsername());
			//添加首頁路由
			//update-begin-author:taoyan date:20200211 for: TASK #3368 【路由緩存】首頁的緩存設置有問題，需要根據后臺的路由配置來實現是否緩存
			if(!PermissionDataUtil.hasIndexPage(metaList)){
				SysPermission indexMenu = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName,"首頁")).get(0);
				//update-begin--Author:liusq  Date:20210624  for:自定義首頁地址LOWCOD-1578
				List<String> roles = sysUserService.getRole(loginUser.getUsername());
				if(roles.size()>0){
					for (String code:roles) {
						String componentUrl = RoleIndexConfigEnum.getIndexByCode(code);
						if(StringUtils.isNotBlank(componentUrl)){
							indexMenu.setComponent(componentUrl);
							break;
						}
					}
				}
				//update-end--Author:liusq  Date:20210624  for：自定義首頁地址LOWCOD-1578
				metaList.add(0,indexMenu);
			}
			//update-end-author:taoyan date:20200211 for: TASK #3368 【路由緩存】990752a9-d60a-409b-bb80-64af31a08c8a的緩存設置有問題，需要根據后臺的路由配置來實現是否緩存
			JSONObject json = new JSONObject();
			JSONArray menujsonArray = new JSONArray();
			this.getPermissionJsonArray(menujsonArray, metaList, null);
			JSONArray authjsonArray = new JSONArray();
			this.getAuthJsonArray(authjsonArray, metaList);
			//查詢所有的權限
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.eq(SysPermission::getMenuType, CommonConstant.MENU_TYPE_2);
			//query.eq(SysPermission::getStatus, "1");
			List<SysPermission> allAuthList = sysPermissionService.list(query);
			JSONArray allauthjsonArray = new JSONArray();
			this.getAllAuthJsonArray(allauthjsonArray, allAuthList);
			//路由菜單
			json.put("menu", menujsonArray);
			//按鈕權限（用戶擁有的權限集合）
			json.put("auth", authjsonArray);
			//全部權限配置集合（按鈕權限，訪問權限）
			json.put("allAuth", allauthjsonArray);
			//獲取多語言配置
			json.put("sysTranslateItems",sysTranslateService.queryAllSysTranslateItems());
			result.setResult(json);
			result.success("查詢成功");
		} catch (Exception e) {
			result.error500("查詢失敗:" + e.getMessage());  
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	  * 添加菜單
	 * @param permission
	 * @return
	 */
	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysPermission> add(@RequestBody SysPermission permission) {
		Result<SysPermission> result = new Result<SysPermission>();
		try {
			permission = PermissionDataUtil.intelligentProcessData(permission);
			sysPermissionService.addPermission(permission);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	  * 編輯菜單
	 * @param permission
	 * @return
	 */
	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
	public Result<SysPermission> edit(@RequestBody SysPermission permission) {
		Result<SysPermission> result = new Result<>();
		try {
			permission = PermissionDataUtil.intelligentProcessData(permission);
			sysPermissionService.editPermission(permission);
			result.success("修改成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	  * 刪除菜單
	 * @param id
	 * @return
	 */
	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysPermission> delete(@RequestParam(name = "id", required = true) String id) {
		Result<SysPermission> result = new Result<>();
		try {
			sysPermissionService.deletePermission(id);
			result.success("刪除成功!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500(e.getMessage());
		}
		return result;
	}

	/**
	  * 批量刪除菜單
	 * @param ids
	 * @return
	 */
	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysPermission> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<SysPermission> result = new Result<>();
		try {
            String[] arr = ids.split(",");
			for (String id : arr) {
				if (oConvertUtils.isNotEmpty(id)) {
					sysPermissionService.deletePermission(id);
				}
			}
			result.success("刪除成功!");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("刪除成功!");
		}
		return result;
	}

	/**
	 * 獲取全部的權限樹
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<Map<String, Object>> queryTreeList() {
		Result<Map<String, Object>> result = new Result<>();
		// 全部權限ids
		List<String> ids = new ArrayList<>();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = sysPermissionService.list(query);
			for (SysPermission sysPer : list) {
				ids.add(sysPer.getId());
			}
			List<TreeModel> treeList = new ArrayList<>();
			getTreeModelList(treeList, list, null);

			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("treeList", treeList); // 全部樹節點數據
			resMap.put("ids", ids);// 全部樹ids
			result.setResult(resMap);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 異步加載數據節點
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryListAsync", method = RequestMethod.GET)
	public Result<List<TreeModel>> queryAsync(@RequestParam(name = "pid", required = false) String parentId) {
		Result<List<TreeModel>> result = new Result<>();
		try {
			List<TreeModel> list = sysPermissionService.queryListByParentId(parentId);
			if (list == null || list.size() <= 0) {
				result.error500("未找到角色信息");
			} else {
				result.setResult(list);
				result.setSuccess(true);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * 查詢角色授權
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryRolePermission", method = RequestMethod.GET)
	public Result<List<String>> queryRolePermission(@RequestParam(name = "roleId", required = true) String roleId) {
		Result<List<String>> result = new Result<>();
		try {
			List<SysRolePermission> list = sysRolePermissionService.list(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId));
			result.setResult(list.stream().map(SysRolePermission -> String.valueOf(SysRolePermission.getPermissionId())).collect(Collectors.toList()));
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
	@RequestMapping(value = "/saveRolePermission", method = RequestMethod.POST)
	//@RequiresRoles({ "admin" })
	public Result<String> saveRolePermission(@RequestBody JSONObject json) {
		long start = System.currentTimeMillis();
		Result<String> result = new Result<>();
		try {
			String roleId = json.getString("roleId");
			String permissionIds = json.getString("permissionIds");
			String lastPermissionIds = json.getString("lastpermissionIds");
			this.sysRolePermissionService.saveRolePermission(roleId, permissionIds, lastPermissionIds);
			result.success("保存成功！");
			log.info("======角色授權成功=====耗時:" + (System.currentTimeMillis() - start) + "毫秒");
		} catch (Exception e) {
			result.error500("授權失敗！");
			log.error(e.getMessage(), e);
		}
		return result;
	}

	private void getTreeList(List<SysPermissionTree> treeList, List<SysPermission> metaList, SysPermissionTree temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			SysPermissionTree tree = new SysPermissionTree(permission);
			if (temp == null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if (!tree.getIsLeaf()) {
					getTreeList(treeList, metaList, tree);
				}
			} else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
				temp.getChildren().add(tree);
				if (!tree.getIsLeaf()) {
					getTreeList(treeList, metaList, tree);
				}
			}

		}
	}

	private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
		for (SysPermission permission : metaList) {
			String tempPid = permission.getParentId();
			TreeModel tree = new TreeModel(permission);
			if (temp == null && oConvertUtils.isEmpty(tempPid)) {
				treeList.add(tree);
				if (!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			} else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
				temp.getChildren().add(tree);
				if (!tree.getIsLeaf()) {
					getTreeModelList(treeList, metaList, tree);
				}
			}

		}
	}
	
	/**
	  *  獲取權限JSON數組
	 * @param jsonArray
	 * @param allList
	 */
	private void getAllAuthJsonArray(JSONArray jsonArray,List<SysPermission> allList) {
		JSONObject json = null;
		for (SysPermission permission : allList) {
			json = new JSONObject();
			json.put("action", permission.getPerms());
			json.put("status", permission.getStatus());
			//1顯示2禁用
			json.put("type", permission.getPermsType());
			json.put("describe", permission.getName());
			jsonArray.add(json);
		}
	}

	/**
	  *  獲取權限JSON數組
	 * @param jsonArray
	 * @param metaList
	 */
	private void getAuthJsonArray(JSONArray jsonArray,List<SysPermission> metaList) {
		for (SysPermission permission : metaList) {
			if(permission.getMenuType()==null) {
				continue;
			}
			JSONObject json = null;
			if(permission.getMenuType().equals(CommonConstant.MENU_TYPE_2) &&CommonConstant.STATUS_1.equals(permission.getStatus())) {
				json = new JSONObject();
				json.put("action", permission.getPerms());
				json.put("type", permission.getPermsType());
				json.put("describe", permission.getName());
				jsonArray.add(json);
			}
		}
	}
	/**
	  *  獲取菜單JSON數組
	 * @param jsonArray
	 * @param metaList
	 * @param parentJson
	 */
	private void getPermissionJsonArray(JSONArray jsonArray, List<SysPermission> metaList, JSONObject parentJson) {
		for (SysPermission permission : metaList) {
			if (permission.getMenuType() == null) {
				continue;
			}
			String tempPid = permission.getParentId();
			JSONObject json = getPermissionJsonObject(permission);
			if(json==null) {
				continue;
			}
			if (parentJson == null && oConvertUtils.isEmpty(tempPid)) {
				jsonArray.add(json);
				if (!permission.isLeaf()) {
					getPermissionJsonArray(jsonArray, metaList, json);
				}
			} else if (parentJson != null && oConvertUtils.isNotEmpty(tempPid) && tempPid.equals(parentJson.getString("id"))) {
				// 類型( 0：一級菜單 1：子菜單 2：按鈕 )
				if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
					JSONObject metaJson = parentJson.getJSONObject("meta");
					if (metaJson.containsKey("permissionList")) {
						metaJson.getJSONArray("permissionList").add(json);
					} else {
						JSONArray permissionList = new JSONArray();
						permissionList.add(json);
						metaJson.put("permissionList", permissionList);
					}
					// 類型( 0：一級菜單 1：子菜單 2：按鈕 )
				} else if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_1) || permission.getMenuType().equals(CommonConstant.MENU_TYPE_0)) {
					if (parentJson.containsKey("children")) {
						parentJson.getJSONArray("children").add(json);
					} else {
						JSONArray children = new JSONArray();
						children.add(json);
						parentJson.put("children", children);
					}

					if (!permission.isLeaf()) {
						getPermissionJsonArray(jsonArray, metaList, json);
					}
				}
			}

		}
	}

	/**
	 * 根據菜單配置生成路由json
	 * @param permission
	 * @return
	 */
		private JSONObject getPermissionJsonObject(SysPermission permission) {
		JSONObject json = new JSONObject();
		// 類型(0：一級菜單 1：子菜單 2：按鈕)
		if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
			//json.put("action", permission.getPerms());
			//json.put("type", permission.getPermsType());
			//json.put("describe", permission.getName());
			return null;
		} else if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_0) || permission.getMenuType().equals(CommonConstant.MENU_TYPE_1)) {
			json.put("id", permission.getId());
			if (permission.isRoute()) {
				json.put("route", "1");// 表示生成路由
			} else {
				json.put("route", "0");// 表示不生成路由
			}

			if (isWWWHttpUrl(permission.getUrl())) {
				json.put("path", MD5Util.MD5Encode(permission.getUrl(), "utf-8"));
			} else {
				json.put("path", permission.getUrl());
			}

			// 重要規則：路由name (通過URL生成路由name,路由name供前端開發，頁面跳轉使用)
			if (oConvertUtils.isNotEmpty(permission.getComponentName())) {
				json.put("name", permission.getComponentName());
			} else {
				json.put("name", urlToRouteName(permission.getUrl()));
			}

			// 是否隱藏路由，默認都是顯示的
			if (permission.isHidden()) {
				json.put("hidden", true);
			}
			// 聚合路由
			if (permission.isAlwaysShow()) {
				json.put("alwaysShow", true);
			}
			json.put("component", permission.getComponent());
			JSONObject meta = new JSONObject();
			// 由用戶設置是否緩存頁面 用布爾值
			if (permission.isKeepAlive()) {
				meta.put("keepAlive", true);
			} else {
				meta.put("keepAlive", false);
			}

			/*update_begin author:wuxianquan date:20190908 for:往菜單信息里添加外鏈菜單打開方式 */
			//外鏈菜單打開方式
			if (permission.isInternalOrExternal()) {
				meta.put("internalOrExternal", true);
			} else {
				meta.put("internalOrExternal", false);
			}
			/* update_end author:wuxianquan date:20190908 for: 往菜單信息里添加外鏈菜單打開方式*/

			meta.put("title", permission.getName());

			//update-begin--Author:scott  Date:20201015 for：路由緩存問題，關閉了tab頁時再打開就不刷新 #842
			String component = permission.getComponent();
			if(oConvertUtils.isNotEmpty(permission.getComponentName()) || oConvertUtils.isNotEmpty(component)){
				meta.put("componentName", oConvertUtils.getString(permission.getComponentName(),component.substring(component.lastIndexOf("/")+1)));
			}
			//update-end--Author:scott  Date:20201015 for：路由緩存問題，關閉了tab頁時再打開就不刷新 #842

			if (oConvertUtils.isEmpty(permission.getParentId())) {
				// 一級菜單跳轉地址
				json.put("redirect", permission.getRedirect());
				if (oConvertUtils.isNotEmpty(permission.getIcon())) {
					meta.put("icon", permission.getIcon());
				}
			} else {
				if (oConvertUtils.isNotEmpty(permission.getIcon())) {
					meta.put("icon", permission.getIcon());
				}
			}
			if (isWWWHttpUrl(permission.getUrl())) {
				meta.put("url", permission.getUrl());
			}
			json.put("meta", meta);
		}

		return json;
	}

	/**
	 * 判斷是否外網URL 例如： http://localhost:8080/jeecg-boot/swagger-ui.html#/ 支持特殊格式： {{
	 * window._CONFIG['domianURL'] }}/druid/ {{ JS代碼片段 }}，前臺解析會自動執行JS代碼片段
	 * 
	 * @return
	 */
	private boolean isWWWHttpUrl(String url) {
		if (url != null && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("{{"))) {
			return true;
		}
		return false;
	}

	/**
	 * 通過URL生成路由name（去掉URL前綴斜杠，替換內容中的斜杠‘/’為-） 舉例： URL = /isystem/role RouteName =
	 * isystem-role
	 * 
	 * @return
	 */
	private String urlToRouteName(String url) {
		if (oConvertUtils.isNotEmpty(url)) {
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
			url = url.replace("/", "-");

			// 特殊標記
			url = url.replace(":", "@");
			return url;
		} else {
			return null;
		}
	}

	/**
	 * 根據菜單id來獲取其對應的權限數據
	 * 
	 * @param sysPermissionDataRule
	 * @return
	 */
	@RequestMapping(value = "/getPermRuleListByPermId", method = RequestMethod.GET)
	public Result<List<SysPermissionDataRule>> getPermRuleListByPermId(SysPermissionDataRule sysPermissionDataRule) {
		List<SysPermissionDataRule> permRuleList = sysPermissionDataRuleService.getPermRuleListByPermId(sysPermissionDataRule.getPermissionId());
		Result<List<SysPermissionDataRule>> result = new Result<>();
		result.setSuccess(true);
		result.setResult(permRuleList);
		return result;
	}

	/**
	 * 添加菜單權限數據
	 * 
	 * @param sysPermissionDataRule
	 * @return
	 */
	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/addPermissionRule", method = RequestMethod.POST)
	public Result<SysPermissionDataRule> addPermissionRule(@RequestBody SysPermissionDataRule sysPermissionDataRule) {
		Result<SysPermissionDataRule> result = new Result<SysPermissionDataRule>();
		try {
			sysPermissionDataRule.setCreateTime(new Date());
			sysPermissionDataRuleService.savePermissionDataRule(sysPermissionDataRule);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/editPermissionRule", method = { RequestMethod.PUT, RequestMethod.POST })
	public Result<SysPermissionDataRule> editPermissionRule(@RequestBody SysPermissionDataRule sysPermissionDataRule) {
		Result<SysPermissionDataRule> result = new Result<SysPermissionDataRule>();
		try {
			sysPermissionDataRuleService.saveOrUpdate(sysPermissionDataRule);
			result.success("更新成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	 * 刪除菜單權限數據
	 * 
	 * @param id
	 * @return
	 */
	//@RequiresRoles({ "admin" })
	@RequestMapping(value = "/deletePermissionRule", method = RequestMethod.DELETE)
	public Result<SysPermissionDataRule> deletePermissionRule(@RequestParam(name = "id", required = true) String id) {
		Result<SysPermissionDataRule> result = new Result<SysPermissionDataRule>();
		try {
			sysPermissionDataRuleService.deletePermissionDataRule(id);
			result.success("刪除成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	 * 查詢菜單權限數據
	 * 
	 * @param sysPermissionDataRule
	 * @return
	 */
	@RequestMapping(value = "/queryPermissionRule", method = RequestMethod.GET)
	public Result<List<SysPermissionDataRule>> queryPermissionRule(SysPermissionDataRule sysPermissionDataRule) {
		Result<List<SysPermissionDataRule>> result = new Result<>();
		try {
			List<SysPermissionDataRule> permRuleList = sysPermissionDataRuleService.queryPermissionRule(sysPermissionDataRule);
			result.setResult(permRuleList);
			result.success("查詢成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	 * 部門權限表
	 * @param departId
	 * @return
	 */
	@RequestMapping(value = "/queryDepartPermission", method = RequestMethod.GET)
	public Result<List<String>> queryDepartPermission(@RequestParam(name = "departId", required = true) String departId) {
		Result<List<String>> result = new Result<>();
		try {
			List<SysDepartPermission> list = sysDepartPermissionService.list(new QueryWrapper<SysDepartPermission>().lambda().eq(SysDepartPermission::getDepartId, departId));
			result.setResult(list.stream().map(SysDepartPermission -> String.valueOf(SysDepartPermission.getPermissionId())).collect(Collectors.toList()));
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 保存部門授權
	 *
	 * @return
	 */
	@RequestMapping(value = "/saveDepartPermission", method = RequestMethod.POST)
	//@RequiresRoles({ "admin" })
	public Result<String> saveDepartPermission(@RequestBody JSONObject json) {
		long start = System.currentTimeMillis();
		Result<String> result = new Result<>();
		try {
			String departId = json.getString("departId");
			String permissionIds = json.getString("permissionIds");
			String lastPermissionIds = json.getString("lastpermissionIds");
			this.sysDepartPermissionService.saveDepartPermission(departId, permissionIds, lastPermissionIds);
			result.success("保存成功！");
			log.info("======部門授權成功=====耗時:" + (System.currentTimeMillis() - start) + "毫秒");
		} catch (Exception e) {
			result.error500("授權失敗！");
			log.error(e.getMessage(), e);
		}
		return result;
	}
}
