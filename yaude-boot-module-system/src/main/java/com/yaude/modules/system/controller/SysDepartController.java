package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.ImportExcelUtil;
import com.yaude.common.util.YouBianCodeUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysDepart;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.model.DepartIdModel;
import com.yaude.modules.system.model.SysDepartTreeModel;
import com.yaude.modules.system.service.ISysDepartService;
import com.yaude.modules.system.service.ISysUserDepartService;
import com.yaude.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 部門表 前端控制器
 * <p>
 * 
 * @Author: Steve @Since： 2019-01-22
 */
@RestController
@RequestMapping("/sys/sysDepart")
@Slf4j
public class SysDepartController {

	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	/**
	 * 查詢數據 查出我的部門,并以樹結構數據格式響應給前端
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryMyDeptTreeList", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryMyDeptTreeList() {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		try {
			if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals( CommonConstant.USER_IDENTITY_2 )){
				//update-begin--Author:liusq  Date:20210624  for:部門查詢ids為空后的前端顯示問題 issues/I3UD06
				String departIds = user.getDepartIds();
				if(StringUtils.isNotBlank(departIds)){
					List<SysDepartTreeModel> list = sysDepartService.queryMyDeptTreeList(departIds);
					result.setResult(list);
				}
				//update-end--Author:liusq  Date:20210624  for:部門查詢ids為空后的前端顯示問題 issues/I3UD06
				result.setMessage(CommonConstant.USER_IDENTITY_2.toString());
				result.setSuccess(true);
			}else{
				result.setMessage(CommonConstant.USER_IDENTITY_1.toString());
				result.setSuccess(true);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 查詢數據 查出所有部門,并以樹結構數據格式響應給前端
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryTreeList() {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		try {
			// 從內存中讀取
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}
			List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 異步查詢部門list
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryDepartTreeSync", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> queryDepartTreeSync(@RequestParam(name = "pid", required = false) String parentId) {
		Result<List<SysDepartTreeModel>> result = new Result<>();
		try {
			List<SysDepartTreeModel> list = sysDepartService.queryTreeListByPid(parentId);
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 獲取某個部門的所有父級部門的ID
	 *
	 * @param departId 根據departId查
	 * @param orgCode  根據orgCode查，departId和orgCode必須有一個不為空
	 */
	@GetMapping("/queryAllParentId")
	public Result queryParentIds(
			@RequestParam(name = "departId", required = false) String departId,
			@RequestParam(name = "orgCode", required = false) String orgCode
	) {
		try {
			JSONObject data;
			if (oConvertUtils.isNotEmpty(departId)) {
				data = sysDepartService.queryAllParentIdByDepartId(departId);
			} else if (oConvertUtils.isNotEmpty(orgCode)) {
				data = sysDepartService.queryAllParentIdByOrgCode(orgCode);
			} else {
				return Result.error("departId 和 orgCode 不能都為空！");
			}
			return Result.OK(data);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 添加新數據 添加用戶新建的部門對象數據,并保存到數據庫
	 * 
	 * @param sysDepart
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<SysDepart> add(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
		Result<SysDepart> result = new Result<SysDepart>();
		String username = JwtUtil.getUserNameByToken(request);
		try {
			sysDepart.setCreateBy(username);
			sysDepartService.saveDepartData(sysDepart, username);
			//清除部門樹內存
			// FindsDepartsChildrenUtil.clearSysDepartTreeList();
			// FindsDepartsChildrenUtil.clearDepartIdModel();
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	 * 編輯數據 編輯部門的部分數據,并保存到數據庫
	 * 
	 * @param sysDepart
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<SysDepart> edit(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
		String username = JwtUtil.getUserNameByToken(request);
		sysDepart.setUpdateBy(username);
		Result<SysDepart> result = new Result<SysDepart>();
		SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
		if (sysDepartEntity == null) {
			result.error500("未找到對應實體");
		} else {
			boolean ok = sysDepartService.updateDepartDataById(sysDepart, username);
			// TODO 返回false說明什么？
			if (ok) {
				//清除部門樹內存
				//FindsDepartsChildrenUtil.clearSysDepartTreeList();
				//FindsDepartsChildrenUtil.clearDepartIdModel();
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
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
   public Result<SysDepart> delete(@RequestParam(name="id",required=true) String id) {

       Result<SysDepart> result = new Result<SysDepart>();
       SysDepart sysDepart = sysDepartService.getById(id);
       if(sysDepart==null) {
           result.error500("未找到對應實體");
       }else {
           boolean ok = sysDepartService.delete(id);
           if(ok) {
	            //清除部門樹內存
	   		   //FindsDepartsChildrenUtil.clearSysDepartTreeList();
	   		   // FindsDepartsChildrenUtil.clearDepartIdModel();
               result.success("刪除成功!");
           }
       }
       return result;
   }


	/**
	 * 批量刪除 根據前端請求的多個ID,對數據庫執行刪除相關部門數據的操作
	 * 
	 * @param ids
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
	public Result<SysDepart> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {

		Result<SysDepart> result = new Result<SysDepart>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("參數不識別！");
		} else {
			this.sysDepartService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
			result.success("刪除成功!");
		}
		return result;
	}

	/**
	 * 查詢數據 添加或編輯頁面對該方法發起請求,以樹結構形式加載所有部門的名稱,方便用戶的操作
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
	public Result<List<DepartIdModel>> queryIdTree() {
//		Result<List<DepartIdModel>> result = new Result<List<DepartIdModel>>();
//		List<DepartIdModel> idList;
//		try {
//			idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//			if (idList != null && idList.size() > 0) {
//				result.setResult(idList);
//				result.setSuccess(true);
//			} else {
//				sysDepartService.queryTreeList();
//				idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//				result.setResult(idList);
//				result.setSuccess(true);
//			}
//			return result;
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			result.setSuccess(false);
//			return result;
//		}
		Result<List<DepartIdModel>> result = new Result<>();
		try {
			List<DepartIdModel> list = sysDepartService.queryDepartIdTreeList();
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}
	 
	/**
	 * <p>
	 * 部門搜索功能方法,根據關鍵字模糊搜索相關部門
	 * </p>
	 * 
	 * @param keyWord
	 * @return
	 */
	@RequestMapping(value = "/searchBy", method = RequestMethod.GET)
	public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord,@RequestParam(name = "myDeptSearch", required = false) String myDeptSearch) {
		Result<List<SysDepartTreeModel>> result = new Result<List<SysDepartTreeModel>>();
		//部門查詢，myDeptSearch為1時為我的部門查詢，登錄用戶為上級時查只查負責部門下數據
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String departIds = null;
		if(oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals( CommonConstant.USER_IDENTITY_2 )){
			departIds = user.getDepartIds();
		}
		List<SysDepartTreeModel> treeList = this.sysDepartService.searhBy(keyWord,myDeptSearch,departIds);
		if (treeList == null || treeList.size() == 0) {
			result.setSuccess(false);
			result.setMessage("未查詢匹配數據！");
			return result;
		}
		result.setResult(treeList);
		return result;
	}


	/**
     * 導出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDepart sysDepart,HttpServletRequest request) {
        // Step.1 組裝查詢條件
        QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart, request.getParameterMap());
        //Step.2 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDepart> pageList = sysDepartService.list(queryWrapper);
        //按字典排序
        Collections.sort(pageList, new Comparator<SysDepart>() {
            @Override
			public int compare(SysDepart arg0, SysDepart arg1) {
            	return arg0.getOrgCode().compareTo(arg1.getOrgCode());
            }
        });
        //導出文件名稱
        mv.addObject(NormalExcelConstants.FILE_NAME, "部門列表");
        mv.addObject(NormalExcelConstants.CLASS, SysDepart.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("部門列表數據", "導出人:"+user.getRealname(), "導出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通過excel導入數據
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List<String> errorMessageList = new ArrayList<>();
		List<SysDepart> listSysDeparts = null;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 獲取上傳文件對象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
            	// orgCode編碼長度
            	int codeLength = YouBianCodeUtil.zhanweiLength;
                listSysDeparts = ExcelImportUtil.importExcel(file.getInputStream(), SysDepart.class, params);
                //按長度排序
                Collections.sort(listSysDeparts, new Comparator<SysDepart>() {
                    @Override
					public int compare(SysDepart arg0, SysDepart arg1) {
                    	return arg0.getOrgCode().length() - arg1.getOrgCode().length();
                    }
                });

                int num = 0;
                for (SysDepart sysDepart : listSysDeparts) {
                	String orgCode = sysDepart.getOrgCode();
                	if(orgCode.length() > codeLength) {
                		String parentCode = orgCode.substring(0, orgCode.length()-codeLength);
                		QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<SysDepart>();
                		queryWrapper.eq("org_code", parentCode);
                		try {
                		SysDepart parentDept = sysDepartService.getOne(queryWrapper);
                		if(!parentDept.equals(null)) {
							sysDepart.setParentId(parentDept.getId());
						} else {
							sysDepart.setParentId("");
						}
                		}catch (Exception e) {
                			//沒有查找到parentDept
                		}
                	}else{
                		sysDepart.setParentId("");
					}
                    //update-begin---author:liusq   Date:20210223  for：批量導入部門以后，不能追加下一級部門 #2245------------
					sysDepart.setOrgType(sysDepart.getOrgCode().length()/codeLength+"");
                    //update-end---author:liusq   Date:20210223  for：批量導入部門以后，不能追加下一級部門 #2245------------
					sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
					ImportExcelUtil.importDateSaveOne(sysDepart, ISysDepartService.class, errorMessageList, num, CommonConstant.SQL_INDEX_UNIQ_DEPART_ORG_CODE);
					num++;
                }
				//清空部門緩存
				Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
				Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
				redisTemplate.delete(keys3);
				redisTemplate.delete(keys4);
				return ImportExcelUtil.imporReturnRes(errorMessageList.size(), listSysDeparts.size() - errorMessageList.size(), errorMessageList);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("文件導入失敗:"+e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件導入失敗！");
    }


	/**
	 * 查詢所有部門信息
	 * @return
	 */
	@GetMapping("listAll")
	public Result<List<SysDepart>> listAll(@RequestParam(name = "id", required = false) String id) {
		Result<List<SysDepart>> result = new Result<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<SysDepart>();
		query.orderByAsc(SysDepart::getOrgCode);
		if(oConvertUtils.isNotEmpty(id)){
			String arr[] = id.split(",");
			query.in(SysDepart::getId,arr);
		}
		List<SysDepart> ls = this.sysDepartService.list(query);
		result.setSuccess(true);
		result.setResult(ls);
		return result;
	}
	/**
	 * 查詢數據 查出所有部門,并以樹結構數據格式響應給前端
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryTreeByKeyWord", method = RequestMethod.GET)
	public Result<Map<String,Object>> queryTreeByKeyWord(@RequestParam(name = "keyWord", required = false) String keyWord) {
		Result<Map<String,Object>> result = new Result<>();
		try {
			Map<String,Object> map=new HashMap<String,Object>();
			List<SysDepartTreeModel> list = sysDepartService.queryTreeByKeyWord(keyWord);
			//根據keyWord獲取用戶信息
			LambdaQueryWrapper<SysUser> queryUser = new LambdaQueryWrapper<SysUser>();
			queryUser.eq(SysUser::getDelFlag,CommonConstant.DEL_FLAG_0);
			queryUser.and(i -> i.like(SysUser::getUsername, keyWord).or().like(SysUser::getRealname, keyWord));
			List<SysUser> sysUsers = this.sysUserService.list(queryUser);
			map.put("userList",sysUsers);
			map.put("departList",list);
			result.setResult(map);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 根據部門編碼獲取部門信息
	 *
	 * @param orgCode
	 * @return
	 */
	@GetMapping("/getDepartName")
	public Result<SysDepart> getDepartName(@RequestParam(name = "orgCode") String orgCode) {
		Result<SysDepart> result = new Result<>();
		LambdaQueryWrapper<SysDepart> query = new LambdaQueryWrapper<>();
		query.eq(SysDepart::getOrgCode, orgCode);
		SysDepart sysDepart = sysDepartService.getOne(query);
		result.setSuccess(true);
		result.setResult(sysDepart);
		return result;
	}

	/**
	 * 根據部門id獲取用戶信息
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/getUsersByDepartId")
	public Result<List<SysUser>> getUsersByDepartId(@RequestParam(name = "id") String id) {
		Result<List<SysUser>> result = new Result<>();
		List<SysUser> sysUsers = sysUserDepartService.queryUserByDepId(id);
		result.setSuccess(true);
		result.setResult(sysUsers);
		return result;
	}
}
