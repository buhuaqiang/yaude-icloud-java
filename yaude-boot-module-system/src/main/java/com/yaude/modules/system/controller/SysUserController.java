package com.yaude.modules.system.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.RedisUtil;
import com.yaude.modules.base.service.BaseCommonService;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.PermissionData;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.util.ImportExcelUtil;
import com.yaude.common.util.PasswordUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.model.DepartIdModel;
import com.yaude.modules.system.model.SysUserSysDepartModel;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.vo.SysDepartUsersVO;
import com.yaude.modules.system.vo.SysUserRoleVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用戶表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	
	@Autowired
	private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@Autowired
	private ISysUserDepartService sysUserDepartService;

	@Autowired
	private ISysUserRoleService userRoleService;

    @Autowired
    private ISysDepartRoleUserService departRoleUserService;

    @Autowired
    private ISysDepartRoleService departRoleService;

	@Autowired
	private RedisUtil redisUtil;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Resource
    private BaseCommonService baseCommonService;

    /**
     * 獲取用戶列表數據
     * @param user
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/UserList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysUser>> queryPageList(SysUser user, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
		Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
		QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
    	//TODO 外部模擬登陸臨時賬號，列表不顯示
        queryWrapper.ne("username","_reserve_user_external");
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);

		//批量查詢用戶的所屬部門
        //step.1 先拿到全部的 useids
        //step.2 通過 useids，一次性查詢用戶的所屬部門名字
        List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            pageList.getRecords().forEach(item->{
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
		result.setSuccess(true);
		result.setResult(pageList);
		log.info(pageList.toString());
		return result;
	}

    //@RequiresRoles({"admin"})
    //@RequiresPermissions("user:add")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		String selectedRoles = jsonObject.getString("selectedroles");
		String selectedDeparts = jsonObject.getString("selecteddeparts");
		try {
			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//設置創建時間
			String salt = oConvertUtils.randomGen(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(1);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			// 保存用戶走一個service 保證事務
			sysUserService.saveUser(user, selectedRoles, selectedDeparts);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

    //@RequiresRoles({"admin"})
    //@RequiresPermissions("user:edit")
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			baseCommonService.addLog("編輯用戶，id： " +jsonObject.getString("id") ,CommonConstant.LOG_TYPE_2, 2);
			if(sysUser==null) {
				result.error500("未找到對應實體");
			}else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				//String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
                // 修改用戶走一個service 保證事務
				sysUserService.editUser(user, roles, departs);
				result.success("修改成功!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	 * 刪除用戶
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		baseCommonService.addLog("刪除用戶，id： " +id ,CommonConstant.LOG_TYPE_2, 3);
		this.sysUserService.deleteUser(id);
		return Result.ok("刪除用戶成功");
	}

	/**
	 * 批量刪除用戶
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		baseCommonService.addLog("批量刪除用戶， ids： " +ids ,CommonConstant.LOG_TYPE_2, 3);
		this.sysUserService.deleteBatchUsers(ids);
		return Result.ok("批量刪除用戶成功");
	}

	/**
	  * 凍結&解凍用戶
	 * @param jsonObject
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			String ids = jsonObject.getString("ids");
			String status = jsonObject.getString("status");
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
							new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId,id));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗"+e.getMessage());
		}
		result.success("操作成功!");
		return result;

    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("未找到對應實體");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用戶相關角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
	  *  校驗用戶賬號是否唯一<br>
	  *  可以校驗其他 需要檢驗什么就傳什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此參數為false則程序發生異常
        result.setResult(true);
        try {
            //通過傳入信息查詢新的用戶信息
            sysUser.setPassword(null);
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用戶賬號已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密碼
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public Result<?> changePassword(@RequestBody SysUser sysUser) {
        SysUser u = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (u == null) {
            return Result.error("用戶不存在！");
        }
        sysUser.setId(u.getId());
        return sysUserService.changePassword(sysUser);
    }

    /**
     * 查詢指定用戶和部門關聯的數據
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("查找成功");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("查找失敗");
            }
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("查找過程中出現了異常: " + e.getMessage());
            return result;
        }

    }

    /**
     * 生成在添加用戶情況下沒有主鍵的問題,返回給前端,根據該id綁定部門數據
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("我執行了,生成用戶ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * 根據部門id查詢用戶信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id,@RequestParam(name="realname",required=false) String realname) {
        Result<List<SysUser>> result = new Result<>();
        //List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        SysDepart sysDepart = sysDepartService.getById(id);
        List<SysUser> userList = sysUserDepartService.queryUserByDepCode(sysDepart.getOrgCode(),realname);

        //批量查詢用戶的所屬部門
        //step.1 先拿到全部的 useids
        //step.2 通過 useids，一次性查詢用戶的所屬部門名字
        List<String> userIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            userList.forEach(item->{
                //TODO 臨時借用這個字段用于頁面展示
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }

        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 用戶選擇組件 專用  根據用戶賬號或部門分頁查詢
     * @param departId
     * @param username
     * @return
     */
    @RequestMapping(value = "/queryUserComponentData", method = RequestMethod.GET)
    public Result<IPage<SysUser>> queryUserComponentData(
            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "departId", required = false) String departId,
            @RequestParam(name="realname",required=false) String realname,
            @RequestParam(name="username",required=false) String username) {
        IPage<SysUser> pageList = sysUserDepartService.queryDepartUserPageList(departId, username, realname, pageSize, pageNo);
        return Result.OK(pageList);
    }

    /**
     * 導出excel
     *
     * @param request
     * @param sysUser
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser,HttpServletRequest request) {
        // Step.1 組裝查詢條件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用戶導出，如果選擇數據則只導出相關數據--------------------
        String selections = request.getParameter("selections");
       if(!oConvertUtils.isEmpty(selections)){
           queryWrapper.in("id",selections.split(","));
       }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用戶導出，如果選擇數據則只導出相關數據----------------------
        List<SysUser> pageList = sysUserService.list(queryWrapper);

        //導出文件名稱
        mv.addObject(NormalExcelConstants.FILE_NAME, "用戶列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("用戶列表數據", "導出人:"+user.getRealname(), "導出信息");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
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
    //@RequiresPermissions("user:import")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        // 錯誤信息
        List<String> errorMessage = new ArrayList<>();
        int successLines = 0, errorLines = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 獲取上傳文件對象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (int i = 0; i < listSysUsers.size(); i++) {
                    SysUser sysUserExcel = listSysUsers.get(i);
                    if (StringUtils.isBlank(sysUserExcel.getPassword())) {
                        // 密碼默認為 “123456”
                        sysUserExcel.setPassword("123456");
                    }
                    // 密碼加密加鹽
                    String salt = oConvertUtils.randomGen(8);
                    sysUserExcel.setSalt(salt);
                    String passwordEncode = PasswordUtil.encrypt(sysUserExcel.getUsername(), sysUserExcel.getPassword(), salt);
                    sysUserExcel.setPassword(passwordEncode);
                    try {
                        sysUserService.save(sysUserExcel);
                        successLines++;
                    } catch (Exception e) {
                        errorLines++;
                        String message = e.getMessage().toLowerCase();
                        int lineNumber = i + 1;
                        // 通過索引名判斷出錯信息
                        if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_USERNAME)) {
                            errorMessage.add("第 " + lineNumber + " 行：用戶名已經存在，忽略導入。");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_WORK_NO)) {
                            errorMessage.add("第 " + lineNumber + " 行：工號已經存在，忽略導入。");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_PHONE)) {
                            errorMessage.add("第 " + lineNumber + " 行：手機號已經存在，忽略導入。");
                        } else if (message.contains(CommonConstant.SQL_INDEX_UNIQ_SYS_USER_EMAIL)) {
                            errorMessage.add("第 " + lineNumber + " 行：電子郵件已經存在，忽略導入。");
                        } else {
                            errorMessage.add("第 " + lineNumber + " 行：未知錯誤，忽略導入");
                            log.error(e.getMessage(), e);
                        }
                    }
                    // 批量將部門和用戶信息建立關聯關系
                    String departIds = sysUserExcel.getDepartIds();
                    if (StringUtils.isNotBlank(departIds)) {
                        String userId = sysUserExcel.getId();
                        String[] departIdArray = departIds.split(",");
                        List<SysUserDepart> userDepartList = new ArrayList<>(departIdArray.length);
                        for (String departId : departIdArray) {
                            userDepartList.add(new SysUserDepart(userId, departId));
                        }
                        sysUserDepartService.saveBatch(userDepartList);
                    }

                }
            } catch (Exception e) {
                errorMessage.add("發生異常：" + e.getMessage());
                log.error(e.getMessage(), e);
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                	log.error(e.getMessage(), e);
                }
            }
        }
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
    }

    /**
	 * @功能：根據id 批量查詢
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
	public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
		Result<Collection<SysUser>> result = new Result<>();
		String[] userId = userIds.split(",");
		Collection<String> idList = Arrays.asList(userId);
		Collection<SysUser> userRole = sysUserService.listByIds(idList);
		result.setSuccess(true);
		result.setResult(userRole);
		return result;
	}

	/**
	 * 首頁用戶重置密碼
	 */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
	public Result<?> updatePassword(@RequestBody JSONObject json) {
		String username = json.getString("username");
		String oldpassword = json.getString("oldpassword");
		String password = json.getString("password");
		String confirmpassword = json.getString("confirmpassword");
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if(!sysUser.getUsername().equals(username)){
            return Result.error("只允許修改自己的密碼！");
        }
		SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
		if(user==null) {
			return Result.error("用戶不存在！");
		}
		return sysUserService.resetPassword(username,oldpassword,password,confirmpassword);
	}

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page,roleId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 給指定角色添加用戶
     *
     * @param
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<String>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for(String sysUserId:sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId,sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id",sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if(one==null){
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出錯了: " + e.getMessage());
            return result;
        }
    }
    /**
     *   刪除指定角色的用戶關系
     * @param
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name="roleId") String roleId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id",userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("刪除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("刪除失敗！");
        }
        return result;
    }

    /**
     * 批量刪除指定角色的用戶關系
     *
     * @param
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name="roleId") String roleId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("刪除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("刪除失敗！");
        }
        return result;
    }

    /**
     * 部門用戶列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> departUserList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        //根據部門ID查詢,當前和下級所有的部門IDS
        List<String> subDepids = new ArrayList<>();
        //部門id為空時，查詢我的部門下所有用戶
        if(oConvertUtils.isEmpty(depId)){
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            int userIdentity = user.getUserIdentity() != null?user.getUserIdentity():CommonConstant.USER_IDENTITY_1;
            if(oConvertUtils.isNotEmpty(userIdentity) && userIdentity == CommonConstant.USER_IDENTITY_2 ){
                subDepids = sysDepartService.getMySubDepIdsByDepId(user.getDepartIds());
            }
        }else{
            subDepids = sysDepartService.getSubDepIdsByDepId(depId);
        }
        if(subDepids != null && subDepids.size()>0){
            IPage<SysUser> pageList = sysUserService.getUserByDepIds(page,subDepids,username);
            //批量查詢用戶的所屬部門
            //step.1 先拿到全部的 useids
            //step.2 通過 useids，一次性查詢用戶的所屬部門名字
            List<String> userIds = pageList.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
            if(userIds!=null && userIds.size()>0){
                Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
                pageList.getRecords().forEach(item -> {
                    //批量查詢用戶的所屬部門
                    item.setOrgCode(useDepNames.get(item.getId()));
                });
            }
            result.setSuccess(true);
            result.setResult(pageList);
        }else{
            result.setSuccess(true);
            result.setResult(null);
        }
        return result;
    }


    /**
     * 根據 orgCode 查詢用戶，包括子部門下的用戶
     * 若某個用戶包含多個部門，則會顯示多條記錄，可自行處理成單條記錄
     */
    @GetMapping("/queryByOrgCode")
    public Result<?> queryByDepartId(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode") String orgCode,
            SysUser userParams
    ) {
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, new Page(pageNo, pageSize));
        return Result.ok(pageList);
    }

    /**
     * 根據 orgCode 查詢用戶，包括子部門下的用戶
     * 針對通訊錄模塊做的接口，將多個部門的用戶合并成一條記錄，并轉成對前端友好的格式
     */
    @GetMapping("/queryByOrgCodeForAddressList")
    public Result<?> queryByOrgCodeForAddressList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orgCode",required = false) String orgCode,
            SysUser userParams
    ) {
        IPage page = new Page(pageNo, pageSize);
        IPage<SysUserSysDepartModel> pageList = sysUserService.queryUserByOrgCode(orgCode, userParams, page);
        List<SysUserSysDepartModel> list = pageList.getRecords();

        // 記錄所有出現過的 user, key = userId
        Map<String, JSONObject> hasUser = new HashMap<>(list.size());

        JSONArray resultJson = new JSONArray(list.size());

        for (SysUserSysDepartModel item : list) {
            String userId = item.getId();
            // userId
            JSONObject getModel = hasUser.get(userId);
            // 之前已存在過該用戶，直接合并數據
            if (getModel != null) {
                String departName = getModel.get("departName").toString();
                getModel.put("departName", (departName + " | " + item.getDepartName()));
            } else {
                // 將用戶對象轉換為json格式，并將部門信息合并到 json 中
                JSONObject json = JSON.parseObject(JSON.toJSONString(item));
                json.remove("id");
                json.put("userId", userId);
                json.put("departId", item.getDepartId());
                json.put("departName", item.getDepartName());
//                json.put("avatar", item.getSysUser().getAvatar());
                resultJson.add(json);
                hasUser.put(userId, json);
            }
        }

        IPage<JSONObject> result = new Page<>(pageNo, pageSize, pageList.getTotal());
        result.setRecords(resultJson.toJavaList(JSONObject.class));
        return Result.ok(result);
    }

    /**
     * 給指定部門添加對應的用戶
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for(String sysUserId:sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null,sysUserId,sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id",sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if(one==null){
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出錯了: " + e.getMessage());
            return result;
        }
    }

    /**
     *   刪除指定機構的用戶關系
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepart(@RequestParam(name="depId") String depId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id",userId);
            boolean b = sysUserDepartService.remove(queryWrapper);
            if(b){
                List<SysDepartRole> sysDepartRoleList = departRoleService.list(new QueryWrapper<SysDepartRole>().eq("depart_id",depId));
                List<String> roleIds = sysDepartRoleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
                if(roleIds != null && roleIds.size()>0){
                    QueryWrapper<SysDepartRoleUser> query = new QueryWrapper<>();
                    query.eq("user_id",userId).in("drole_id",roleIds);
                    departRoleUserService.remove(query);
                }
                result.success("刪除成功!");
            }else{
                result.error500("當前選中部門與用戶無關聯關系!");
            }
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("刪除失敗！");
        }
        return result;
    }

    /**
     * 批量刪除指定機構的用戶關系
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name="depId") String depId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id",Arrays.asList(userIds.split(",")));
            boolean b = sysUserDepartService.remove(queryWrapper);
            if(b){
                departRoleUserService.removeDeptRoleUser(Arrays.asList(userIds.split(",")),depId);
            }
            result.success("刪除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("刪除失敗！");
        }
        return result;
    }
    
    /**
         *  查詢當前用戶的所有部門/當前部門編碼
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String,Object>> getCurrentUserDeparts() {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
        	LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
            List<SysDepart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查詢失敗！");
        }
        return result;
    }

    


	/**
	 * 用戶注冊接口
	 * 
	 * @param jsonObject
	 * @param user
	 * @return
	 */
	@PostMapping("/register")
	public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
		Object code = redisUtil.get(phone);
		String username = jsonObject.getString("username");
		//未設置用戶名，則用手機號作為用戶名
		if(oConvertUtils.isEmpty(username)){
            username = phone;
        }
        //未設置密碼，則隨機生成一個密碼
		String password = jsonObject.getString("password");
		if(oConvertUtils.isEmpty(password)){
            password = RandomUtil.randomString(8);
        }
		String email = jsonObject.getString("email");
		SysUser sysUser1 = sysUserService.getUserByName(username);
		if (sysUser1 != null) {
			result.setMessage("用戶名已注冊");
			result.setSuccess(false);
			return result;
		}
		SysUser sysUser2 = sysUserService.getUserByPhone(phone);
		if (sysUser2 != null) {
			result.setMessage("該手機號已注冊");
			result.setSuccess(false);
			return result;
		}

		if(oConvertUtils.isNotEmpty(email)){
            SysUser sysUser3 = sysUserService.getUserByEmail(email);
            if (sysUser3 != null) {
                result.setMessage("郵箱已被注冊");
                result.setSuccess(false);
                return result;
            }
        }
        if(null == code){
            result.setMessage("手機驗證碼失效，請重新獲取");
            result.setSuccess(false);
            return result;
        }
		if (!smscode.equals(code.toString())) {
			result.setMessage("手機驗證碼錯誤");
			result.setSuccess(false);
			return result;
		}

		try {
			user.setCreateTime(new Date());// 設置創建時間
			String salt = oConvertUtils.randomGen(8);
			String passwordEncode = PasswordUtil.encrypt(username, password, salt);
			user.setSalt(salt);
			user.setUsername(username);
			user.setRealname(username);
			user.setPassword(passwordEncode);
			user.setEmail(email);
			user.setPhone(phone);
			user.setStatus(CommonConstant.USER_UNFREEZE);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			user.setActivitiSync(CommonConstant.ACT_SYNC_0);
			sysUserService.addUserWithRole(user,"ee8626f80f7c2619917b6236f3a7f02b");//默認臨時角色 test
			result.success("注冊成功");
		} catch (Exception e) {
			result.error500("注冊失敗");
		}
		return result;
	}

//	/**
//	 * 根據用戶名或手機號查詢用戶信息
//	 * @param
//	 * @return
//	 */
//	@GetMapping("/querySysUser")
//	public Result<Map<String, Object>> querySysUser(SysUser sysUser) {
//		String phone = sysUser.getPhone();
//		String username = sysUser.getUsername();
//		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (oConvertUtils.isNotEmpty(phone)) {
//			SysUser user = sysUserService.getUserByPhone(phone);
//			if(user!=null) {
//				map.put("username",user.getUsername());
//				map.put("phone",user.getPhone());
//				result.setSuccess(true);
//				result.setResult(map);
//				return result;
//			}
//		}
//		if (oConvertUtils.isNotEmpty(username)) {
//			SysUser user = sysUserService.getUserByName(username);
//			if(user!=null) {
//				map.put("username",user.getUsername());
//				map.put("phone",user.getPhone());
//				result.setSuccess(true);
//				result.setResult(map);
//				return result;
//			}
//		}
//		result.setSuccess(false);
//		result.setMessage("驗證失敗");
//		return result;
//	}

	/**
	 * 用戶手機號驗證
	 */
	@PostMapping("/phoneVerification")
	public Result<Map<String,String>> phoneVerification(@RequestBody JSONObject jsonObject) {
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMessage("手機驗證碼錯誤");
			result.setSuccess(false);
			return result;
		}
		//設置有效時間
		redisUtil.set(phone, smscode,600);
		//新增查詢用戶名
		LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getPhone,phone);
        SysUser user = sysUserService.getOne(query);
        Map<String,String> map = new HashMap<>();
        map.put("smscode",smscode);
        map.put("username",user.getUsername());
        result.setResult(map);
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * 用戶更改密碼
	 */
	@GetMapping("/passwordChange")
	public Result<SysUser> passwordChange(@RequestParam(name="username")String username,
										  @RequestParam(name="password")String password,
			                              @RequestParam(name="smscode")String smscode,
			                              @RequestParam(name="phone") String phone) {
        Result<SysUser> result = new Result<SysUser>();
        if(oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(password) || oConvertUtils.isEmpty(smscode)  || oConvertUtils.isEmpty(phone) ) {
            result.setMessage("重置密碼失敗！");
            result.setSuccess(false);
            return result;
        }

        SysUser sysUser=new SysUser();
        Object object= redisUtil.get(phone);
        if(null==object) {
        	result.setMessage("短信驗證碼失效！");
            result.setSuccess(false);
            return result;
        }
        if(!smscode.equals(object.toString())) {
        	result.setMessage("短信驗證碼不匹配！");
            result.setSuccess(false);
            return result;
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,username).eq(SysUser::getPhone,phone));
        if (sysUser == null) {
            result.setMessage("未找到用戶！");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密碼重置完成！");
            return result;
        }
    }
	

	/**
	 * 根據TOKEN獲取用戶的部分信息（返回的數據是可供表單設計器使用的數據）
	 * 
	 * @return
	 */
	@GetMapping("/getUserSectionInfoByToken")
	public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
		try {
			String username = null;
			// 如果沒有傳遞token，就從header中獲取token并獲取用戶信息
			if (oConvertUtils.isEmpty(token)) {
				 username = JwtUtil.getUserNameByToken(request);
			} else {
				 username = JwtUtil.getUsername(token);				
			}

			log.debug(" ------ 通過令牌獲取部分用戶信息，當前用戶： " + username);

			// 根據用戶名查詢用戶信息
			SysUser sysUser = sysUserService.getUserByName(username);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sysUserId", sysUser.getId());
			map.put("sysUserCode", sysUser.getUsername()); // 當前登錄用戶登錄賬號
			map.put("sysUserName", sysUser.getRealname()); // 當前登錄用戶真實名稱
			map.put("sysOrgCode", sysUser.getOrgCode()); // 當前登錄用戶部門編號

			log.debug(" ------ 通過令牌獲取部分用戶信息，已獲取的用戶信息： " + map);

			return Result.ok(map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(500, "查詢失敗:" + e.getMessage());
		}
	}
	
	/**
	 * 【APP端接口】獲取用戶列表  根據用戶名和真實名 模糊匹配
	 * @param keyword
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/appUserList")
	public Result<?> appUserList(@RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "username", required = false) String username,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(name = "syncFlow", required = false) String syncFlow) {
		try {
			//TODO 從查詢效率上將不要用mp的封裝的page分頁查詢 建議自己寫分頁語句
			LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>();
			if(oConvertUtils.isNotEmpty(syncFlow)){
                query.eq(SysUser::getActivitiSync, CommonConstant.ACT_SYNC_1);
            }
			query.eq(SysUser::getDelFlag,CommonConstant.DEL_FLAG_0);
			if(oConvertUtils.isNotEmpty(username)){
			    if(username.contains(",")){
                    query.in(SysUser::getUsername,username.split(","));
                }else{
                    query.eq(SysUser::getUsername,username);
                }
            }else{
                query.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
            }
			Page<SysUser> page = new Page<>(pageNo, pageSize);
			IPage<SysUser> res = this.sysUserService.page(page, query);
			return Result.ok(res);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error(500, "查詢失敗:" + e.getMessage());
		}
		
	}

    /**
     * 獲取被邏輯刪除的用戶列表，無分頁
     *
     * @return logicDeletedUserList
     */
    @GetMapping("/recycleBin")
    public Result getRecycleBin() {
        List<SysUser> logicDeletedUserList = sysUserService.queryLogicDeleted();
        if (logicDeletedUserList.size() > 0) {
            // 批量查詢用戶的所屬部門
            // step.1 先拿到全部的 userIds
            List<String> userIds = logicDeletedUserList.stream().map(SysUser::getId).collect(Collectors.toList());
            // step.2 通過 userIds，一次性查詢用戶的所屬部門名字
            Map<String, String> useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            logicDeletedUserList.forEach(item -> item.setOrgCode(useDepNames.get(item.getId())));
        }
        return Result.ok(logicDeletedUserList);
    }

    /**
     * 還原被邏輯刪除的用戶
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/putRecycleBin", method = RequestMethod.PUT)
    public Result putRecycleBin(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userIds = jsonObject.getString("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            SysUser updateUser = new SysUser();
            updateUser.setUpdateBy(JwtUtil.getUserNameByToken(request));
            updateUser.setUpdateTime(new Date());
            sysUserService.revertLogicDeleted(Arrays.asList(userIds.split(",")), updateUser);
        }
        return Result.ok("還原成功");
    }

    /**
     * 徹底刪除用戶
     *
     * @param userIds 被刪除的用戶ID，多個id用半角逗號分割
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteRecycleBin", method = RequestMethod.DELETE)
    public Result deleteRecycleBin(@RequestParam("userIds") String userIds) {
        if (StringUtils.isNotBlank(userIds)) {
            sysUserService.removeLogicDeleted(Arrays.asList(userIds.split(",")));
        }
        return Result.ok("刪除成功");
    }


    /**
     * 移動端修改用戶信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/appEdit", method = RequestMethod.PUT)
    public Result<SysUser> appEdit(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            SysUser sysUser = sysUserService.getUserByName(username);
            baseCommonService.addLog("移動端編輯用戶，id： " +jsonObject.getString("id") ,CommonConstant.LOG_TYPE_2, 2);
            String realname=jsonObject.getString("realname");
            String avatar=jsonObject.getString("avatar");
            String sex=jsonObject.getString("sex");
            String phone=jsonObject.getString("phone");
            String email=jsonObject.getString("email");
            Date birthday=jsonObject.getDate("birthday");
            SysUser userPhone = sysUserService.getUserByPhone(phone);
            if(sysUser==null) {
                result.error500("未找到對應用戶!");
            }else {
                if(userPhone!=null){
                    String userPhonename = userPhone.getUsername();
                    if(!userPhonename.equals(username)){
                        result.error500("手機號已存在!");
                        return result;
                    }
                }
                if(StringUtils.isNotBlank(realname)){
                    sysUser.setRealname(realname);
                }
                if(StringUtils.isNotBlank(avatar)){
                    sysUser.setAvatar(avatar);
                }
                if(StringUtils.isNotBlank(sex)){
                    sysUser.setSex(Integer.parseInt(sex));
                }
                if(StringUtils.isNotBlank(phone)){
                    sysUser.setPhone(phone);
                }
                if(StringUtils.isNotBlank(email)){
                    sysUser.setEmail(email);
                }
                if(null != birthday){
                    sysUser.setBirthday(birthday);
                }
                sysUser.setUpdateTime(new Date());
                sysUserService.updateById(sysUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失敗!");
        }
        return result;
    }
    /**
     * 移動端保存設備信息
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/saveClientId", method = RequestMethod.GET)
    public Result<SysUser> saveClientId(HttpServletRequest request,@RequestParam("clientId")String clientId) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            String username = JwtUtil.getUserNameByToken(request);
            SysUser sysUser = sysUserService.getUserByName(username);
            if(sysUser==null) {
                result.error500("未找到對應用戶!");
            }else {
                sysUser.setClientId(clientId);
                sysUserService.updateById(sysUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失敗!");
        }
        return result;
    }
    /**
     * 根據userid獲取用戶信息和部門員工信息
     *
     * @return Result
     */
    @GetMapping("/queryChildrenByUsername")
    public Result queryChildrenByUsername(@RequestParam("userId") String userId) {
        //獲取用戶信息
        Map<String,Object> map=new HashMap<String,Object>();
        SysUser sysUser = sysUserService.getById(userId);
        String username = sysUser.getUsername();
        Integer identity = sysUser.getUserIdentity();
        map.put("sysUser",sysUser);
        if(identity!=null && identity==2){
            //獲取部門用戶信息
            String departIds = sysUser.getDepartIds();
            if(StringUtils.isNotBlank(departIds)){
                List<String> departIdList = Arrays.asList(departIds.split(","));
                List<SysUser> childrenUser = sysUserService.queryByDepIds(departIdList,username);
                map.put("children",childrenUser);
            }
        }
        return Result.ok(map);
    }
    /**
     * 移動端查詢部門用戶信息
     * @param departId
     * @return
     */
    @GetMapping("/appQueryByDepartId")
    public Result<List<SysUser>> appQueryByDepartId(@RequestParam(name="departId", required = false) String departId) {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        List<String> list=new ArrayList<String> ();
        list.add(departId);
        List<SysUser> childrenUser = sysUserService.queryByDepIds(list,null);
        result.setResult(childrenUser);
        return result;
    }
    /**
     * 移動端查詢用戶信息(通過用戶名模糊查詢)
     * @param keyword
     * @return
     */
    @GetMapping("/appQueryUser")
    public Result<List<SysUser>> appQueryUser(@RequestParam(name = "keyword", required = false) String keyword) {
        Result<List<SysUser>> result = new Result<List<SysUser>>();
        LambdaQueryWrapper<SysUser> queryWrapper =new LambdaQueryWrapper<SysUser>();
        //TODO 外部模擬登陸臨時賬號，列表不顯示
        queryWrapper.ne(SysUser::getUsername,"_reserve_user_external");
        if(StringUtils.isNotBlank(keyword)){
            queryWrapper.and(i -> i.like(SysUser::getUsername, keyword).or().like(SysUser::getRealname, keyword));
        }
        List<SysUser> list = sysUserService.list(queryWrapper);
        //批量查詢用戶的所屬部門
        //step.1 先拿到全部的 useids
        //step.2 通過 useids，一次性查詢用戶的所屬部門名字
        List<String> userIds = list.stream().map(SysUser::getId).collect(Collectors.toList());
        if(userIds!=null && userIds.size()>0){
            Map<String,String>  useDepNames = sysUserService.getDepNamesByUserIds(userIds);
            list.forEach(item->{
                item.setOrgCodeTxt(useDepNames.get(item.getId()));
            });
        }
        result.setResult(list);
        return result;
    }

    /**
     * 根據用戶名修改手機號
     * @param json
     * @return
     */
    @RequestMapping(value = "/updateMobile", method = RequestMethod.PUT)
    public Result<?> changMobile(@RequestBody JSONObject json,HttpServletRequest request) {
        String smscode = json.getString("smscode");
        String phone = json.getString("phone");
        Result<SysUser> result = new Result<SysUser>();
        //獲取登錄用戶名
        String username = JwtUtil.getUserNameByToken(request);
        if(oConvertUtils.isEmpty(username) || oConvertUtils.isEmpty(smscode) || oConvertUtils.isEmpty(phone)) {
            result.setMessage("修改手機號失敗！");
            result.setSuccess(false);
            return result;
        }
        Object object= redisUtil.get(phone);
        if(null==object) {
            result.setMessage("短信驗證碼失效！");
            result.setSuccess(false);
            return result;
        }
        if(!smscode.equals(object.toString())) {
            result.setMessage("短信驗證碼不匹配！");
            result.setSuccess(false);
            return result;
        }
        SysUser user = sysUserService.getUserByName(username);
        if(user==null) {
            return Result.error("用戶不存在！");
        }
        user.setPhone(phone);
        sysUserService.updateById(user);
        return Result.ok("手機號設置成功!");
    }


    /**
     * 根據對象里面的屬性值作in查詢 屬性可能會變 用戶組件用到
     * @param sysUser
     * @return
     */
    @GetMapping("/getMultiUser")
    public List<SysUser> getMultiUser(SysUser sysUser){
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, null);
        List<SysUser> ls = this.sysUserService.list(queryWrapper);
        for(SysUser user: ls){
            user.setPassword(null);
            user.setSalt(null);
        }
        return ls;
    }

}
