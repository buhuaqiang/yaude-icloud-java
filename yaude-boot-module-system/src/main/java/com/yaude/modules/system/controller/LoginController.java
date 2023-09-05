package com.yaude.modules.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.*;
import com.yaude.modules.base.service.BaseCommonService;
import com.yaude.modules.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.util.*;
import com.yaude.common.util.encryption.EncryptedString;
import com.yaude.modules.system.entity.SysDepart;
import com.yaude.modules.system.entity.SysTenant;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.model.SysLoginModel;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.util.RandImageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags="用戶登錄")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private ISysLogService logService;
	@Autowired
    private RedisUtil redisUtil;
	@Autowired
    private ISysDepartService sysDepartService;
	@Autowired
	private ISysTenantService sysTenantService;
	@Autowired
    private ISysDictService sysDictService;
	@Autowired
	private ISysTranslateService sysTranslateService;
	@Resource
	private BaseCommonService baseCommonService;

	private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@ApiOperation("登錄接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		//update-begin--Author:scott  Date:20190805 for：暫時註釋掉密碼加密邏輯，有點問題
		//前端密碼加密，後端進行密碼解密
		//password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密碼解密
		//update-begin--Author:scott  Date:20190805 for：暫時註釋掉密碼加密邏輯，有點問題

		//update-begin-author:taoyan date:20190828 for:校驗驗證碼
        String captcha = sysLoginModel.getCaptcha();
        if(captcha==null){
            result.error500("驗證碼無效");
            return result;
        }
        String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = MD5Util.MD5Encode(lowerCaseCaptcha+sysLoginModel.getCheckKey(), "utf-8");
		Object checkCode = redisUtil.get(realKey);
		//當進入登錄頁時，有一定幾率出現驗證碼錯誤 #1714
		if(checkCode==null || !checkCode.toString().equals(lowerCaseCaptcha)) {
			result.error500("驗證碼錯誤");
			return result;
		}
		//update-end-author:taoyan date:20190828 for:校驗驗證碼
		
		//1. 校驗用戶是否有效
		//update-begin-author:wangshuai date:20200601 for: 登錄代碼驗證用戶是否註銷bug，if條件永遠為false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		//update-end-author:wangshuai date:20200601 for: 登錄代碼驗證用戶是否註銷bug，if條件永遠為false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校驗用戶名或密碼是否正確
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用戶名或密碼錯誤");
			return result;
		}
				
		//用戶登錄信息
		userInfo(sysUser, result);
		//update-begin--Author:liusq  Date:20210126  for：登錄成功，刪除redis中的驗證碼
		redisUtil.del(realKey);
		//update-begin--Author:liusq  Date:20210126  for：登錄成功，刪除redis中的驗證碼
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		baseCommonService.addLog("用戶名: " + username + ",登錄成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
        //update-end--Author:wangshuai  Date:20200714  for：登錄日誌沒有記錄人員
		return result;
	}
	
	/**
	 * 退出登錄
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用戶退出邏輯
	    String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
	    if(oConvertUtils.isEmpty(token)) {
	    	return Result.error("退出登錄失敗！");
	    }
	    String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseAPI.getUserByName(username);
	    if(sysUser!=null) {
			//update-begin--Author:wangshuai  Date:20200714  for：登出日誌沒有記錄人員
			baseCommonService.addLog("用戶名: "+sysUser.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null,sysUser);
			//update-end--Author:wangshuai  Date:20200714  for：登出日誌沒有記錄人員
	    	log.info(" 用戶名:  "+sysUser.getRealname()+",退出成功！ ");
	    	//清空用戶登錄Token緩存
	    	redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
	    	//清空用戶登錄Shiro權限緩存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用戶的緩存信息（包括部門信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//調用shiro的logout
			SecurityUtils.getSubject().logout();
	    	return Result.ok("退出登錄成功！");
	    }else {
	    	return Result.error("Token無效!");
	    }
	}
	
	/**
	 * 獲取訪問量
	 * @return
	 */
	@GetMapping("loginfo")
	public Result<JSONObject> loginfo() {
		Result<JSONObject> result = new Result<JSONObject>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：傳入開始時間，結束時間參數
		// 獲取一天的開始和結束時間
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// 獲取系統訪問記錄
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：傳入開始時間，結束時間參數
		obj.put("todayIp", todayIp);
		result.setResult(obj);
		result.success("登錄成功");
		return result;
	}
	
	/**
	 * 獲取訪問量
	 * @return
	 */
	@GetMapping("visitInfo")
	public Result<List<Map<String,Object>>> visitInfo() {
		Result<List<Map<String,Object>>> result = new Result<List<Map<String,Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String,Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setResult(oConvertUtils.toLowerCasePageList(list));
		return result;
	}
	
	
	/**
	 * 登陸成功選擇用戶當前部門
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = user.getUsername();
		if(oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		String orgCode= user.getOrgCode();
		this.sysUserService.updateUserDepart(username, orgCode);
		SysUser sysUser = sysUserService.getUserByName(username);
		JSONObject obj = new JSONObject();
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		return result;
	}

	/**
	 * 短信登錄接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	public Result<String> sms(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手機號模式 登錄模式: "2"  註冊模式: "1"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMessage("手機號不允許為空！");
			result.setSuccess(false);
			return result;
		}
		Object object = redisUtil.get(mobile);
		if (object != null) {
			result.setMessage("驗證碼10分鐘內，仍然有效！");
			result.setSuccess(false);
			return result;
		}

		//隨機數
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
    	obj.put("code", captcha);
		try {
			boolean b = false;
			//註冊模板
			if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				if(sysUser!=null) {
					result.error500(" 手機號已經註冊，請直接登錄！");
					baseCommonService.addLog("手機號已經註冊，請直接登錄！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			}else {
				//登錄模式，校驗用戶有效性
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if(!result.isSuccess()) {
					String message = result.getMessage();
					if("該用戶不存在，請註冊".equals(message)){
						result.error500("該用戶不存在或未綁定手機號");
					}
					return result;
				}
				
				/**
				 * smsmode 短信模板方式  0 .登錄模板、1.註冊模板、2.忘記密碼模板
				 */
				if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//登錄模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if(CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
					//忘記密碼模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
				}
			}

			if (b == false) {
				result.setMessage("短信驗證碼發送失敗,請稍後重試");
				result.setSuccess(false);
				return result;
			}
			//驗證碼10分鐘內有效
			redisUtil.set(mobile, captcha, 600);
			//update-begin--Author:scott  Date:20190812 for：issues#391
			//result.setResult(captcha);
			//update-end--Author:scott  Date:20190812 for：issues#391
			result.setSuccess(true);

		} catch (ClientException e) {
			e.printStackTrace();
			result.error500(" 短信接口未配置，請聯繫管理員！");
			return result;
		}
		return result;
	}
	

	/**
	 * 手機號登錄接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手機號登錄接口")
	@PostMapping("/phoneLogin")
	public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("mobile");
		
		//校驗用戶有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		String smscode = jsonObject.getString("captcha");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMessage("手機驗證碼錯誤");
			return result;
		}
		//用戶信息
		userInfo(sysUser, result);
		//添加日誌
		baseCommonService.addLog("用戶名: " + sysUser.getUsername() + ",登錄成功！", CommonConstant.LOG_TYPE_1, null);

		return result;
	}


	/**
	 * 用戶信息
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String syspassword = sysUser.getPassword();
		String username = sysUser.getUsername();
		// 獲取用戶部門信息
		JSONObject obj = new JSONObject();
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		if (departs == null || departs.size() == 0) {
			obj.put("multi_depart", 0);
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			obj.put("multi_depart", 1);
		} else {
			//查詢當前是否有登錄部門
			// update-begin--Author:wangshuai Date:20200805 for：如果用戶為選擇部門，數據庫為存在上一次登錄部門，則取一條存進去
			SysUser sysUserById = sysUserService.getById(sysUser.getId());
			if(oConvertUtils.isEmpty(sysUserById.getOrgCode())){
				sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			}
			// update-end--Author:wangshuai Date:20200805 for：如果用戶為選擇部門，數據庫為存在上一次登錄部門，則取一條存進去
			obj.put("multi_depart", 2);
		}
		// update-begin--Author:sunjianlei Date:20210802 for：獲取用戶租戶信息
		String tenantIds = sysUser.getRelTenantIds();
		if (oConvertUtils.isNotEmpty(tenantIds)) {
			List<String> tenantIdList = Arrays.asList(tenantIds.split(","));
			// 該方法僅查詢有效的租戶，如果返回0個就說明所有的租戶均無效。
			List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
			if (tenantList.size() == 0) {
				result.error500("與該用戶關聯的租戶均已被凍結，無法登錄！");
				return result;
			} else {
				obj.put("tenantList", tenantList);
			}
		}
		// update-end--Author:sunjianlei Date:20210802 for：獲取用戶租戶信息
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 設置token緩存有效時間
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
		//obj.put("sysTranslateItems",sysTranslateService.queryAllSysTranslateItems());
		result.setResult(obj);
		result.success("登錄成功");
		return result;
	}

	/**
	 * 獲取加密字符串
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String,String>> getEncryptedString(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * 後臺生成圖形驗證碼 ：有效
	 * @param response
	 * @param key
	 */
	@ApiOperation("獲取驗證碼")
	@GetMapping(value = "/randomImage/{key}")
	public Result<String> randomImage(HttpServletResponse response,@PathVariable String key){
		Result<String> res = new Result<String>();
		try {
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);
			String lowerCaseCode = code.toLowerCase();
			String realKey = MD5Util.MD5Encode(lowerCaseCode+key, "utf-8");
			redisUtil.set(realKey, lowerCaseCode, 60);
			String base64 = RandImageUtil.generate(code);
			res.setSuccess(true);
			res.setMessage(code);
			res.setResult(base64);
		} catch (Exception e) {
			res.error500("獲取驗證碼出錯"+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * app登錄
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		
		//1. 校驗用戶是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校驗用戶名或密碼是否正確
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用戶名或密碼錯誤");
			return result;
		}
		
		String orgCode = sysUser.getOrgCode();
		if(oConvertUtils.isEmpty(orgCode)) {
			//如果當前用戶無選擇部門 查看部門關聯信息
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
			if (departs == null || departs.size() == 0) {
				result.error500("用戶暫未歸屬部門,不可登錄!");
				return result;
			}
			orgCode = departs.get(0).getOrgCode();
			sysUser.setOrgCode(orgCode);
			this.sysUserService.updateUserDepart(username, orgCode);
		}
		JSONObject obj = new JSONObject();
		//用戶登錄信息
		obj.put("userInfo", sysUser);
		
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 設置超時時間
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("用戶名: " + username + ",登錄成功[移動端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 圖形驗證碼
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Result<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel){
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if(captcha==null){
			return Result.error("驗證碼無效");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = MD5Util.MD5Encode(lowerCaseCaptcha+checkKey, "utf-8");
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return Result.error("驗證碼錯誤");
		}
		return Result.ok();
	}

}