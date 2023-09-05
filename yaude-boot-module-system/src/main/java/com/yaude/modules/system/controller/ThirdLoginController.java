package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xkcoding.justauth.AuthRequestFactory;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.util.RedisUtil;
import com.yaude.config.thirdapp.ThirdAppConfig;
import com.yaude.config.thirdapp.ThirdAppTypeItemVo;
import com.yaude.modules.base.service.BaseCommonService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.PasswordUtil;
import com.yaude.common.util.RestUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysThirdAccount;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.model.ThirdLoginModel;
import com.yaude.modules.system.service.ISysThirdAccountService;
import com.yaude.modules.system.service.ISysUserService;
import com.yaude.modules.system.service.impl.ThirdAppDingtalkServiceImpl;
import com.yaude.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Author scott
 * @since 2018-12-17
 */
@Controller
@RequestMapping("/sys/thirdLogin")
@Slf4j
public class ThirdLoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysThirdAccountService sysThirdAccountService;

	@Autowired
	private BaseCommonService baseCommonService;
	@Autowired
    private RedisUtil redisUtil;
	@Autowired
	private AuthRequestFactory factory;

	@Autowired
	ThirdAppConfig thirdAppConfig;
	@Autowired
	private ThirdAppWechatEnterpriseServiceImpl thirdAppWechatEnterpriseService;
	@Autowired
	private ThirdAppDingtalkServiceImpl thirdAppDingtalkService;

	@RequestMapping("/render/{source}")
    public void render(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        log.info("第三方登錄進入render：" + source);
        AuthRequest authRequest = factory.get(source);
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        log.info("第三方登錄認證地址：" + authorizeUrl);
        response.sendRedirect(authorizeUrl);
    }

	@RequestMapping("/{source}/callback")
    public String loginThird(@PathVariable("source") String source, AuthCallback callback,ModelMap modelMap) {
		log.info("第三方登錄進入callback：" + source + " params：" + JSONObject.toJSONString(callback));
        AuthRequest authRequest = factory.get(source);
        AuthResponse response = authRequest.login(callback);
        log.info(JSONObject.toJSONString(response));
        Result<JSONObject> result = new Result<JSONObject>();
        if(response.getCode()==2000) {

        	JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(response.getData()));
        	String username = data.getString("username");
        	String avatar = data.getString("avatar");
        	String uuid = data.getString("uuid");
        	//構造第三方登錄信息存儲對象
			ThirdLoginModel tlm = new ThirdLoginModel(source, uuid, username, avatar);
        	//判斷有沒有這個人
			//update-begin-author:wangshuai date:20201118 for:修改成查詢第三方賬戶表
        	LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<SysThirdAccount>();
        	query.eq(SysThirdAccount::getThirdUserUuid, uuid);
        	query.eq(SysThirdAccount::getThirdType, source);
        	List<SysThirdAccount> thridList = sysThirdAccountService.list(query);
			SysThirdAccount user = null;
        	if(thridList==null || thridList.size()==0) {
				//否則直接創建新賬號
				user = sysThirdAccountService.saveThirdUser(tlm);
        	}else {
        		//已存在 只設置用戶名 不設置頭像
        		user = thridList.get(0);
        	}
        	// 生成token
			//update-begin-author:wangshuai date:20201118 for:從第三方登錄查詢是否存在用戶id，不存在綁定手機號
			if(oConvertUtils.isNotEmpty(user.getSysUserId())) {
				String sysUserId = user.getSysUserId();
				SysUser sysUser = sysUserService.getById(sysUserId);
				String token = saveToken(sysUser);
    			modelMap.addAttribute("token", token);
			}else{
				modelMap.addAttribute("token", "綁定手機號,"+""+uuid);
			}
			//update-end-author:wangshuai date:20201118 for:從第三方登錄查詢是否存在用戶id，不存在綁定手機號
		//update-begin--Author:wangshuai  Date:20200729 for：接口在簽名校驗失敗時返回失敗的標識碼 issues#1441--------------------
        }else{
			modelMap.addAttribute("token", "登錄失敗");
		}
		//update-end--Author:wangshuai  Date:20200729 for：接口在簽名校驗失敗時返回失敗的標識碼 issues#1441--------------------
        result.setSuccess(false);
        result.setMessage("第三方登錄異常,請聯系管理員");
        return "thirdLogin";
    }

	/**
	 * 創建新賬號
	 * @param model
	 * @return
	 */
	@PostMapping("/user/create")
	@ResponseBody
	public Result<String> thirdUserCreate(@RequestBody ThirdLoginModel model) {
		log.info("第三方登錄創建新賬號：" );
		Result<String> res = new Result<>();
		Object operateCode = redisUtil.get(CommonConstant.THIRD_LOGIN_CODE);
		if(operateCode==null || !operateCode.toString().equals(model.getOperateCode())){
			res.setSuccess(false);
			res.setMessage("校驗失敗");
			return res;
		}
		//創建新賬號
		//update-begin-author:wangshuai date:20201118 for:修改成從第三方登錄查出來的user_id，在查詢用戶表盡行token
		SysThirdAccount user = sysThirdAccountService.saveThirdUser(model);
		if(oConvertUtils.isNotEmpty(user.getSysUserId())){
			String sysUserId = user.getSysUserId();
			SysUser sysUser = sysUserService.getById(sysUserId);
			// 生成token
			String token = saveToken(sysUser);
			//update-end-author:wangshuai date:20201118 for:修改成從第三方登錄查出來的user_id，在查詢用戶表盡行token
			res.setResult(token);
			res.setSuccess(true);
		}
		return res;
	}

	/**
	 * 綁定賬號 需要設置密碼 需要走一遍校驗
	 * @param json
	 * @return
	 */
	@PostMapping("/user/checkPassword")
	@ResponseBody
	public Result<String> checkPassword(@RequestBody JSONObject json) {
		Result<String> result = new Result<>();
		Object operateCode = redisUtil.get(CommonConstant.THIRD_LOGIN_CODE);
		if(operateCode==null || !operateCode.toString().equals(json.getString("operateCode"))){
			result.setSuccess(false);
			result.setMessage("校驗失敗");
			return result;
		}
		String username = json.getString("uuid");
		SysUser user = this.sysUserService.getUserByName(username);
		if(user==null){
			result.setMessage("用戶未找到");
			result.setSuccess(false);
			return result;
		}
		String password = json.getString("password");
		String salt = user.getSalt();
		String passwordEncode = PasswordUtil.encrypt(user.getUsername(), password, salt);
		if(!passwordEncode.equals(user.getPassword())){
			result.setMessage("密碼不正確");
			result.setSuccess(false);
			return result;
		}

		sysUserService.updateById(user);
		result.setSuccess(true);
		// 生成token
		String token = saveToken(user);
		result.setResult(token);
		return result;
	}

	private String saveToken(SysUser user) {
		// 生成token
		String token = JwtUtil.sign(user.getUsername(), user.getPassword());
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		// 設置超時時間
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);
		return token;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getLoginUser/{token}/{thirdType}", method = RequestMethod.GET)
	@ResponseBody
	public Result<JSONObject> getThirdLoginUser(@PathVariable("token") String token,@PathVariable("thirdType") String thirdType) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = JwtUtil.getUsername(token);

		//1. 校驗用戶是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		//update-begin-author:wangshuai date:20201118 for:如果真實姓名和頭像不存在就取第三方登錄的
		LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
		query.eq(SysThirdAccount::getSysUserId,sysUser.getId());
		query.eq(SysThirdAccount::getThirdType,thirdType);
		SysThirdAccount account = sysThirdAccountService.getOne(query);
		if(oConvertUtils.isEmpty(sysUser.getRealname())){
			sysUser.setRealname(account.getRealname());
		}
		if(oConvertUtils.isEmpty(sysUser.getAvatar())){
			sysUser.setAvatar(account.getAvatar());
		}
		//update-end-author:wangshuai date:20201118 for:如果真實姓名和頭像不存在就取第三方登錄的
		JSONObject obj = new JSONObject();
		//用戶登錄信息
		obj.put("userInfo", sysUser);
		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("用戶名: " + username + ",登錄成功[第三方用戶]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}
	/**
	 * 第三方綁定手機號返回token
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手機號登錄接口")
	@PostMapping("/bindingThirdPhone")
	@ResponseBody
	public Result<String> bindingThirdPhone(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String phone = jsonObject.getString("mobile");
		String thirdUserUuid = jsonObject.getString("thirdUserUuid");
		//校驗用戶有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		if(sysUser != null){
			sysThirdAccountService.updateThirdUserId(sysUser,thirdUserUuid);
		}else{
			// 不存在手機號，創建用戶
			String smscode = jsonObject.getString("captcha");
			Object code = redisUtil.get(phone);
			if (!smscode.equals(code)) {
				result.setMessage("手機驗證碼錯誤");
				result.setSuccess(false);
				return result;
			}
			//創建用戶
			sysUser = sysThirdAccountService.createUser(phone,thirdUserUuid);
		}
		String token = saveToken(sysUser);
		result.setSuccess(true);
		result.setResult(token);
		return result;
	}

	/**
	 * 企業微信/釘釘 OAuth2登錄
	 *
	 * @param source
	 * @param state
	 * @return
	 */
	@ResponseBody
	@GetMapping("/oauth2/{source}/login")
	public String oauth2LoginCallback(@PathVariable("source") String source, @RequestParam("state") String state, HttpServletResponse response) throws Exception {
		String url;
		if (ThirdAppConfig.WECHAT_ENTERPRISE.equalsIgnoreCase(source)) {
			ThirdAppTypeItemVo config = thirdAppConfig.getWechatEnterprise();
			StringBuilder builder = new StringBuilder();
			// 構造企業微信OAuth2登錄授權地址
			builder.append("https://open.weixin.qq.com/connect/oauth2/authorize");
			// 企業的CorpID
			builder.append("?appid=").append(config.getClientId());
			// 授權后重定向的回調鏈接地址，請使用urlencode對鏈接進行處理
			String redirectUri = RestUtil.getBaseUrl() + "/sys/thirdLogin/oauth2/wechat_enterprise/callback";
			builder.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"));
			// 返回類型，此時固定為：code
			builder.append("&response_type=code");
			// 應用授權作用域。
			// snsapi_base：靜默授權，可獲取成員的的基礎信息（UserId與DeviceId）；
			builder.append("&scope=snsapi_base");
			// 重定向后會帶上state參數，長度不可超過128個字節
			builder.append("&state=").append(state);
			// 終端使用此參數判斷是否需要帶上身份信息
			builder.append("#wechat_redirect");
			url = builder.toString();
		} else if (ThirdAppConfig.DINGTALK.equalsIgnoreCase(source)) {
			ThirdAppTypeItemVo config = thirdAppConfig.getDingtalk();
			StringBuilder builder = new StringBuilder();
			// 構造釘釘OAuth2登錄授權地址
			builder.append("https://login.dingtalk.com/oauth2/auth");
			// 授權通過/拒絕后回調地址。
			// 注意 需要與注冊應用時登記的域名保持一致。
			String redirectUri = RestUtil.getBaseUrl() + "/sys/thirdLogin/oauth2/dingtalk/callback";
			builder.append("?redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"));
			// 固定值為code。
			// 授權通過后返回authCode。
			builder.append("&response_type=code");
			// 步驟一中創建的應用詳情中獲取。
			// 企業內部應用：client_id為應用的AppKey。
			builder.append("&client_id=").append(config.getClientId());
			// 授權范圍，授權頁面顯示的授權信息以應用注冊時配置的為準。
			// openid：授權后可獲得用戶userid
			builder.append("&scope=openid");
			// 跟隨authCode原樣返回。
			builder.append("&state=").append(state);
			url = builder.toString();
		} else {
			return "不支持的source";
		}
		log.info("oauth2 login url:" + url);
		response.sendRedirect(url);
		return "login…";
	}

    /**
     * 企業微信/釘釘 OAuth2登錄回調
     *
     * @param code
     * @param state
     * @param response
     * @return
     */
	@ResponseBody
	@GetMapping("/oauth2/{source}/callback")
	public String oauth2LoginCallback(
			@PathVariable("source") String source,
			// 企業微信返回的code
			@RequestParam(value = "code", required = false) String code,
			// 釘釘返回的code
			@RequestParam(value = "authCode", required = false) String authCode,
			@RequestParam("state") String state,
			HttpServletResponse response
	) {
        SysUser loginUser;
        if (ThirdAppConfig.WECHAT_ENTERPRISE.equalsIgnoreCase(source)) {
            log.info("【企業微信】OAuth2登錄進入callback：code=" + code + ", state=" + state);
            loginUser = thirdAppWechatEnterpriseService.oauth2Login(code);
            if (loginUser == null) {
                return "登錄失敗";
            }
        } else if (ThirdAppConfig.DINGTALK.equalsIgnoreCase(source)) {
			log.info("【釘釘】OAuth2登錄進入callback：authCode=" + authCode + ", state=" + state);
			loginUser = thirdAppDingtalkService.oauth2Login(authCode);
			if (loginUser == null) {
				return "登錄失敗";
			}
        } else {
            return "不支持的source";
        }
        try {
            String token = saveToken(loginUser);
			state += "/oauth2-app/login?oauth2LoginToken=" + URLEncoder.encode(token, "UTF-8");
			state += "&thirdType=" + "wechat_enterprise";
			log.info("OAuth2登錄重定向地址: " + state);
            try {
                response.sendRedirect(state);
                return "ok";
            } catch (IOException e) {
                e.printStackTrace();
                return "重定向失敗";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "解碼失敗";
        }
    }

}