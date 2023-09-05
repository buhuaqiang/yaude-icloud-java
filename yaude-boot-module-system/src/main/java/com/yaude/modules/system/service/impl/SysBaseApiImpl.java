package com.yaude.modules.system.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.aspect.UrlMatchEnum;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.constant.DataBaseConstant;
import com.yaude.common.constant.WebsocketConst;
import com.yaude.common.exception.JeecgBootException;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.*;
import com.yaude.modules.message.entity.SysMessageTemplate;
import com.yaude.modules.message.handle.impl.EmailSendMsgHandle;
import com.yaude.modules.message.service.ISysMessageTemplateService;
import com.yaude.modules.message.websocket.WebSocket;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.mapper.*;
import com.yaude.modules.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.dto.OnlineAuthDTO;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.api.dto.message.*;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.system.vo.*;
import com.yaude.common.util.SysAnnmentTypeEnum;
import com.yaude.common.util.YouBianCodeUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.mapper.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.entity.*;
import com.yaude.modules.system.mapper.*;
import com.yaude.modules.system.service.*;
import com.yaude.modules.system.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import com.yaude.common.system.vo.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @Description: 底層共通業務API，提供其他獨立模塊調用
 * @Author: scott
 * @Date:2019-4-20 
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
	/** 當前系統數據庫類型 */
	private static String DB_TYPE = "";
	@Autowired
	private ISysMessageTemplateService sysMessageTemplateService;
	@Resource
	private SysLogMapper sysLogMapper;
	@Resource
	private SysUserMapper userMapper;
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysDictService sysDictService;
	@Resource
	private SysAnnouncementMapper sysAnnouncementMapper;
	@Resource
	private SysAnnouncementSendMapper sysAnnouncementSendMapper;
	@Resource
    private WebSocket webSocket;
	@Resource
	private SysRoleMapper roleMapper;
	@Resource
	private SysDepartMapper departMapper;
	@Resource
	private SysCategoryMapper categoryMapper;

	@Autowired
	private ISysDataSourceService dataSourceService;
	@Autowired
	private ISysUserDepartService sysUserDepartService;
	@Resource
	private SysPermissionMapper sysPermissionMapper;
	@Autowired
	private ISysPermissionDataRuleService sysPermissionDataRuleService;

	@Autowired
	private ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
	@Autowired
	private ThirdAppDingtalkServiceImpl dingtalkService;

	@Autowired
	ISysCategoryService sysCategoryService;

	@Override
	@Cacheable(cacheNames= CacheConstant.SYS_USERS_CACHE, key="#username")
	public LoginUser getUserByName(String username) {
		if(oConvertUtils.isEmpty(username)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		SysUser sysUser = userMapper.getUserByName(username);
		if(sysUser==null) {
			return null;
		}
		BeanUtils.copyProperties(sysUser, loginUser);
		return loginUser;
	}

	@Override
	public String translateDictFromTable(String table, String text, String code, String key) {
		return sysDictService.queryTableDictTextByKey(table, text, code, key);
	}

	@Override
	public String translateDict(String code, String key) {
		return sysDictService.queryDictTextByKey(code, key);
	}

	@Override
	public List<SysPermissionDataRuleModel> queryPermissionDataRule(String component, String requestPath, String username) {
		List<SysPermission> currentSyspermission = null;
		if(oConvertUtils.isNotEmpty(component)) {
			//1.通過注解屬性pageComponent 獲取菜單
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getDelFlag,0);
			query.eq(SysPermission::getComponent, component);
			currentSyspermission = sysPermissionMapper.selectList(query);
		}else {
			//1.直接通過前端請求地址查詢菜單
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getMenuType,2);
			query.eq(SysPermission::getDelFlag,0);
			query.eq(SysPermission::getUrl, requestPath);
			currentSyspermission = sysPermissionMapper.selectList(query);
			//2.未找到 再通過自定義匹配URL 獲取菜單
			if(currentSyspermission==null || currentSyspermission.size()==0) {
				//通過自定義URL匹配規則 獲取菜單（實現通過菜單配置數據權限規則，實際上針對獲取數據接口進行數據規則控制）
				String userMatchUrl = UrlMatchEnum.getMatchResultByUrl(requestPath);
				LambdaQueryWrapper<SysPermission> queryQserMatch = new LambdaQueryWrapper<SysPermission>();
				queryQserMatch.eq(SysPermission::getMenuType, 1);
				queryQserMatch.eq(SysPermission::getDelFlag, 0);
				queryQserMatch.eq(SysPermission::getUrl, userMatchUrl);
				if(oConvertUtils.isNotEmpty(userMatchUrl)){
					currentSyspermission = sysPermissionMapper.selectList(queryQserMatch);
				}
			}
			//3.未找到 再通過正則匹配獲取菜單
			if(currentSyspermission==null || currentSyspermission.size()==0) {
				//通過正則匹配權限配置
				String regUrl = getRegexpUrl(requestPath);
				if(regUrl!=null) {
					currentSyspermission = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getMenuType,2).eq(SysPermission::getUrl, regUrl).eq(SysPermission::getDelFlag,0));
				}
			}
		}
		if(currentSyspermission!=null && currentSyspermission.size()>0){
			List<SysPermissionDataRuleModel> dataRules = new ArrayList<SysPermissionDataRuleModel>();
			for (SysPermission sysPermission : currentSyspermission) {
				// update-begin--Author:scott Date:20191119 for：數據權限規則編碼不規范，項目存在相同包名和類名 #722
				List<SysPermissionDataRule> temp = sysPermissionDataRuleService.queryPermissionDataRules(username, sysPermission.getId());
				if(temp!=null && temp.size()>0) {
					//dataRules.addAll(temp);
					dataRules = oConvertUtils.entityListToModelList(temp,SysPermissionDataRuleModel.class);
				}
				// update-end--Author:scott Date:20191119 for：數據權限規則編碼不規范，項目存在相同包名和類名 #722
			}
			return dataRules;
		}
		return null;
	}

	/**
	 * 匹配前端傳過來的地址 匹配成功返回正則地址
	 * AntPathMatcher匹配地址
	 *()* 匹配0個或多個字符
	 *()**匹配0個或多個目錄
	 */
	private String getRegexpUrl(String url) {
		List<String> list = sysPermissionMapper.queryPermissionUrlWithStar();
		if(list!=null && list.size()>0) {
			for (String p : list) {
				PathMatcher matcher = new AntPathMatcher();
				if(matcher.match(p, url)) {
					return p;
				}
			}
		}
		return null;
	}

	@Override
	public SysUserCacheInfo getCacheUser(String username) {
		SysUserCacheInfo info = new SysUserCacheInfo();
		info.setOneDepart(true);
		LoginUser user = this.getUserByName(username);
		if(user!=null) {
			info.setSysUserCode(user.getUsername());
			info.setSysUserName(user.getRealname());
			info.setSysOrgCode(user.getOrgCode());
		}else{
			return null;
		}
		//多部門支持in查詢
		List<SysDepart> list = departMapper.queryUserDeparts(user.getId());
		List<String> sysMultiOrgCode = new ArrayList<String>();
		if(list==null || list.size()==0) {
			//當前用戶無部門
			//sysMultiOrgCode.add("0");
		}else if(list.size()==1) {
			sysMultiOrgCode.add(list.get(0).getOrgCode());
		}else {
			info.setOneDepart(false);
			for (SysDepart dpt : list) {
				sysMultiOrgCode.add(dpt.getOrgCode());
			}
		}
		info.setSysMultiOrgCode(sysMultiOrgCode);
		return info;
	}

	@Override
	public LoginUser getUserById(String id) {
		if(oConvertUtils.isEmpty(id)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		SysUser sysUser = userMapper.selectById(id);
		if(sysUser==null) {
			return null;
		}
		BeanUtils.copyProperties(sysUser, loginUser);
		return loginUser;
	}

	@Override
	public List<String> getRolesByUsername(String username) {
		return sysUserRoleMapper.getRoleByUserName(username);
	}

	@Override
	public List<String> getDepartIdsByUsername(String username) {
		List<SysDepart> list = sysDepartService.queryDepartsByUsername(username);
		List<String> result = new ArrayList<>(list.size());
		for (SysDepart depart : list) {
			result.add(depart.getId());
		}
		return result;
	}

	@Override
	public List<String> getDepartNamesByUsername(String username) {
		List<SysDepart> list = sysDepartService.queryDepartsByUsername(username);
		List<String> result = new ArrayList<>(list.size());
		for (SysDepart depart : list) {
			result.add(depart.getDepartName());
		}
		return result;
	}

	@Override
	public DictModel getParentDepartId(String departId) {
		SysDepart depart = departMapper.getParentDepartId(departId);
		DictModel model = new DictModel(depart.getId(),depart.getParentId());
		return model;
	}

	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_CACHE,key = "#code", unless = "#result == null ")
	public List<DictModel> queryDictItemsByCode(String code) {
		return sysDictService.queryDictItemsByCode(code);
	}

	@Override
	@Cacheable(value = CacheConstant.SYS_ENABLE_DICT_CACHE,key = "#code", unless = "#result == null ")
	public List<DictModel> queryEnableDictItemsByCode(String code) {
		return sysDictService.queryEnableDictItemsByCode(code);
	}

	@Override
	public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
		//update-begin-author:taoyan date:20200820 for:【Online+系統】字典表加權限控制機制邏輯，想法不錯 LOWCOD-799
		if(table.indexOf("#{")>=0){
			table = QueryGenerator.getSqlRuleValue(table);
		}
		//update-end-author:taoyan date:20200820 for:【Online+系統】字典表加權限控制機制邏輯，想法不錯 LOWCOD-799
		return sysDictService.queryTableDictItemsByCode(table, text, code);
	}

	@Override
	public List<DictModel> queryAllDepartBackDictModel() {
		return sysDictService.queryAllDepartBackDictModel();
	}

	@Override
	public void sendSysAnnouncement(MessageDTO message) {
		this.sendSysAnnouncement(message.getFromUser(),
				message.getToUser(),
				message.getTitle(),
				message.getContent(),
				message.getCategory());
		try {
			// 同步發送第三方APP消息
			wechatEnterpriseService.sendMessage(message, true);
			dingtalkService.sendMessage(message, true);
		} catch (Exception e) {
			log.error("同步發送第三方APP消息失敗！", e);
		}
	}

	@Override
	public void sendBusAnnouncement(BusMessageDTO message) {
		sendBusAnnouncement(message.getFromUser(),
				message.getToUser(),
				message.getTitle(),
				message.getContent(),
				message.getCategory(),
				message.getBusType(),
				message.getBusId());
		try {
			// 同步發送第三方APP消息
			wechatEnterpriseService.sendMessage(message, true);
			dingtalkService.sendMessage(message, true);
		} catch (Exception e) {
			log.error("同步發送第三方APP消息失敗！", e);
		}
	}

	@Override
	public void sendTemplateAnnouncement(TemplateMessageDTO message) {
		String templateCode = message.getTemplateCode();
		String title = message.getTitle();
		Map<String,String> map = message.getTemplateParam();
		String fromUser = message.getFromUser();
		String toUser = message.getToUser();

		List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
		if(sysSmsTemplates==null||sysSmsTemplates.size()==0){
			throw new JeecgBootException("消息模板不存在，模板編碼："+templateCode);
		}
		SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
		//模板標題
		title = title==null?sysSmsTemplate.getTemplateName():title;
		//模板內容
		String content = sysSmsTemplate.getTemplateContent();
		if(map!=null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String str = "${" + entry.getKey() + "}";
				if(oConvertUtils.isNotEmpty(title)){
					title = title.replace(str, entry.getValue());
				}
				content = content.replace(str, entry.getValue());
			}
		}

		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(content);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用戶通告閱讀標記表記錄
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
			}
		}
		try {
			// 同步企業微信、釘釘的消息通知
			dingtalkService.sendActionCardMessage(announcement, true);
			wechatEnterpriseService.sendTextCardMessage(announcement, true);
		} catch (Exception e) {
			log.error("同步發送第三方APP消息失敗！", e);
		}

	}

	@Override
	public void sendBusTemplateAnnouncement(BusTemplateMessageDTO message) {
		String templateCode = message.getTemplateCode();
		String title = message.getTitle();
		Map<String,String> map = message.getTemplateParam();
		String fromUser = message.getFromUser();
		String toUser = message.getToUser();
		String busId = message.getBusId();
		String busType = message.getBusType();

		List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
		if(sysSmsTemplates==null||sysSmsTemplates.size()==0){
			throw new JeecgBootException("消息模板不存在，模板編碼："+templateCode);
		}
		SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
		//模板標題
		title = title==null?sysSmsTemplate.getTemplateName():title;
		//模板內容
		String content = sysSmsTemplate.getTemplateContent();
		if(map!=null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String str = "${" + entry.getKey() + "}";
				title = title.replace(str, entry.getValue());
				content = content.replace(str, entry.getValue());
			}
		}
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(content);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		announcement.setBusId(busId);
		announcement.setBusType(busType);
		announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType).getOpenType());
		announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType).getOpenPage());
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用戶通告閱讀標記表記錄
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
			}
		}
		try {
			// 同步企業微信、釘釘的消息通知
			dingtalkService.sendActionCardMessage(announcement, true);
			wechatEnterpriseService.sendTextCardMessage(announcement, true);
		} catch (Exception e) {
			log.error("同步發送第三方APP消息失敗！", e);
		}

	}

	@Override
	public String parseTemplateByCode(TemplateDTO templateDTO) {
		String templateCode = templateDTO.getTemplateCode();
		Map<String, String> map = templateDTO.getTemplateParam();
		List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
		if(sysSmsTemplates==null||sysSmsTemplates.size()==0){
			throw new JeecgBootException("消息模板不存在，模板編碼："+templateCode);
		}
		SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
		//模板內容
		String content = sysSmsTemplate.getTemplateContent();
		if(map!=null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String str = "${" + entry.getKey() + "}";
				content = content.replace(str, entry.getValue());
			}
		}
		return content;
	}

	@Override
	public void updateSysAnnounReadFlag(String busType, String busId) {
		SysAnnouncement announcement = sysAnnouncementMapper.selectOne(new QueryWrapper<SysAnnouncement>().eq("bus_type",busType).eq("bus_id",busId));
		if(announcement != null){
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			String userId = sysUser.getId();
			LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
			updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
			updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
			updateWrapper.last("where annt_id ='"+announcement.getId()+"' and user_id ='"+userId+"'");
			SysAnnouncementSend announcementSend = new SysAnnouncementSend();
			sysAnnouncementSendMapper.update(announcementSend, updateWrapper);
		}
	}

	/**
	 * 獲取數據庫類型
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	private String getDatabaseTypeByDataSource(DataSource dataSource) throws SQLException{
		if("".equals(DB_TYPE)) {
			Connection connection = dataSource.getConnection();
			try {
				DatabaseMetaData md = connection.getMetaData();
				String dbType = md.getDatabaseProductName().toLowerCase();
				if(dbType.indexOf("mysql")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
				}else if(dbType.indexOf("oracle")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
				}else if(dbType.indexOf("sqlserver")>=0||dbType.indexOf("sql server")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
				}else if(dbType.indexOf("postgresql")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
				}else if(dbType.indexOf("mariadb")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_MARIADB;
				}else {
					log.error("數據庫類型:[" + dbType + "]不識別!");
					//throw new JeecgBootException("數據庫類型:["+dbType+"]不識別!");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally {
				connection.close();
			}
		}
		return DB_TYPE;

	}

	@Override
	public List<DictModel> queryAllDict() {
		// 查詢并排序
		QueryWrapper<SysDict> queryWrapper = new QueryWrapper<SysDict>();
		queryWrapper.orderByAsc("create_time");
		List<SysDict> dicts = sysDictService.list(queryWrapper);
		// 封裝成 model
		List<DictModel> list = new ArrayList<DictModel>();
		for (SysDict dict : dicts) {
			list.add(new DictModel(dict.getDictCode(), dict.getDictName()));
		}

		return list;
	}

	@Override
	public List<SysCategoryModel> queryAllDSysCategory() {
		List<SysCategory> ls = categoryMapper.selectList(null);
		List<SysCategoryModel> res = oConvertUtils.entityListToModelList(ls,SysCategoryModel.class);
		return res;
	}

	@Override
	public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql) {
		return sysDictService.queryTableDictItemsByCodeAndFilter(table,text,code,filterSql);
	}

	@Override
	public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
		return sysDictService.queryTableDictByKeys(table,text,code,Joiner.on(",").join(keyArray));
	}

	@Override
	public List<ComboModel> queryAllUserBackCombo() {
		List<ComboModel> list = new ArrayList<ComboModel>();
		List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0));
		for(SysUser user : userList){
			ComboModel model = new ComboModel();
			model.setTitle(user.getRealname());
			model.setId(user.getId());
			model.setUsername(user.getUsername());
			list.add(model);
		}
		return list;
	}

	@Override
	public JSONObject queryAllUser(String userIds, Integer pageNo, Integer pageSize) {
		JSONObject json = new JSONObject();
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0);
		List<ComboModel> list = new ArrayList<ComboModel>();
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		IPage<SysUser> pageList = userMapper.selectPage(page, queryWrapper);
		for(SysUser user : pageList.getRecords()){
			ComboModel model = new ComboModel();
			model.setUsername(user.getUsername());
			model.setTitle(user.getRealname());
			model.setId(user.getId());
			model.setEmail(user.getEmail());
			if(oConvertUtils.isNotEmpty(userIds)){
				String[] temp = userIds.split(",");
				for(int i = 0; i<temp.length;i++){
					if(temp[i].equals(user.getId())){
						model.setChecked(true);
					}
				}
			}
			list.add(model);
		}
		json.put("list",list);
		json.put("total",pageList.getTotal());
		return json;
	}

	@Override
	public List<ComboModel> queryAllRole() {
		List<ComboModel> list = new ArrayList<ComboModel>();
		List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
		for(SysRole role : roleList){
			ComboModel model = new ComboModel();
			model.setTitle(role.getRoleName());
			model.setId(role.getId());
			list.add(model);
		}
		return list;
	}

    @Override
    public List<ComboModel> queryAllRole(String[] roleIds) {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for(SysRole role : roleList){
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            model.setRoleCode(role.getRoleCode());
            if(oConvertUtils.isNotEmpty(roleIds)) {
                for (int i = 0; i < roleIds.length; i++) {
                    if (roleIds[i].equals(role.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        return list;
    }

	@Override
	public List<String> getRoleIdsByUsername(String username) {
		return sysUserRoleMapper.getRoleIdByUserName(username);
	}

	@Override
	public String getDepartIdsByOrgCode(String orgCode) {
		return departMapper.queryDepartIdByOrgCode(orgCode);
	}

	@Override
	public List<SysDepartModel> getAllSysDepart() {
		List<SysDepartModel> departModelList = new ArrayList<SysDepartModel>();
		List<SysDepart> departList = departMapper.selectList(new QueryWrapper<SysDepart>().eq("del_flag","0"));
		for(SysDepart depart : departList){
			SysDepartModel model = new SysDepartModel();
			BeanUtils.copyProperties(depart,model);
			departModelList.add(model);
		}
		return departModelList;
	}

	@Override
	public DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId) {
		SysDataSource dbSource = dataSourceService.getById(dbSourceId);
		if(dbSource!=null && StringUtils.isNotBlank(dbSource.getDbPassword())){
			String dbPassword = dbSource.getDbPassword();
			String decodedStr = SecurityUtil.jiemi(dbPassword);
			dbSource.setDbPassword(decodedStr);
		}
		return new DynamicDataSourceModel(dbSource);
	}

	@Override
	public DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode) {
		SysDataSource dbSource = dataSourceService.getOne(new LambdaQueryWrapper<SysDataSource>().eq(SysDataSource::getCode, dbSourceCode));
		if(dbSource!=null && StringUtils.isNotBlank(dbSource.getDbPassword())){
			String dbPassword = dbSource.getDbPassword();
			String decodedStr = SecurityUtil.jiemi(dbPassword);
			dbSource.setDbPassword(decodedStr);
		}
		return new DynamicDataSourceModel(dbSource);
	}

	@Override
	public List<String> getDeptHeadByDepId(String deptId) {
		List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().like("depart_ids",deptId).eq("status",1).eq("del_flag",0));
		List<String> list = new ArrayList<>();
		for(SysUser user : userList){
			list.add(user.getUsername());
		}
		return list;
	}

	@Override
	public void sendWebSocketMsg(String[] userIds, String cmd) {
		JSONObject obj = new JSONObject();
		obj.put(WebsocketConst.MSG_CMD, cmd);
		webSocket.sendMessage(userIds, obj.toJSONString());
	}

	@Override
	public List<LoginUser> queryAllUserByIds(String[] userIds) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0);
		queryWrapper.in("id",userIds);
		List<LoginUser> loginUsers = new ArrayList<>();
		List<SysUser> sysUsers = userMapper.selectList(queryWrapper);
		for (SysUser user:sysUsers) {
			LoginUser loginUser=new LoginUser();
			BeanUtils.copyProperties(user, loginUser);
			loginUsers.add(loginUser);
		}
		return loginUsers;
	}

	/**
	 * 推送簽到人員信息
	 * @param userId
	 */
	@Override
	public void meetingSignWebsocket(String userId) {
		JSONObject obj = new JSONObject();
		obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_SIGN);
		obj.put(WebsocketConst.MSG_USER_ID,userId);
		//TODO 目前全部推送，后面修改
		webSocket.sendMessage(obj.toJSONString());
	}

	@Override
	public List<LoginUser> queryUserByNames(String[] userNames) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0);
		queryWrapper.in("username",userNames);
		List<LoginUser> loginUsers = new ArrayList<>();
		List<SysUser> sysUsers = userMapper.selectList(queryWrapper);
		for (SysUser user:sysUsers) {
			LoginUser loginUser=new LoginUser();
			BeanUtils.copyProperties(user, loginUser);
			loginUsers.add(loginUser);
		}
		return loginUsers;
	}

	@Override
	public SysDepartModel selectAllById(String id) {
		SysDepart sysDepart = sysDepartService.getById(id);
		SysDepartModel sysDepartModel = new SysDepartModel();
		BeanUtils.copyProperties(sysDepart,sysDepartModel);
		return sysDepartModel;
	}

	@Override
	public List<String> queryDeptUsersByUserId(String userId) {
		List<String> userIds = new ArrayList<>();
		List<SysUserDepart> userDepartList = sysUserDepartService.list(new QueryWrapper<SysUserDepart>().eq("user_id",userId));
		if(userDepartList != null){
			//查找所屬公司
			String orgCodes = "";
			for(SysUserDepart userDepart : userDepartList){
				//查詢所屬公司編碼
				SysDepart depart = sysDepartService.getById(userDepart.getDepId());
				int length = YouBianCodeUtil.zhanweiLength;
				String compyOrgCode = "";
				if(depart != null && depart.getOrgCode() != null){
					compyOrgCode = depart.getOrgCode().substring(0,length);
					if(orgCodes.indexOf(compyOrgCode) == -1){
						orgCodes = orgCodes + "," + compyOrgCode;
					}
				}
			}
			if(oConvertUtils.isNotEmpty(orgCodes)){
				orgCodes = orgCodes.substring(1);
				List<String> listIds = departMapper.getSubDepIdsByOrgCodes(orgCodes.split(","));
				List<SysUserDepart> userList = sysUserDepartService.list(new QueryWrapper<SysUserDepart>().in("dep_id",listIds));
				for(SysUserDepart userDepart : userList){
					if(!userIds.contains(userDepart.getUserId())){
						userIds.add(userDepart.getUserId());
					}
				}
			}
		}
		return userIds;
	}

	/**
	 * 查詢用戶擁有的角色集合
	 * @param username
	 * @return
	 */
	@Override
	public Set<String> getUserRoleSet(String username) {
		// 查詢用戶擁有的角色集合
		List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
		log.info("-------通過數據庫讀取用戶擁有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
		return new HashSet<>(roles);
	}

	/**
	 * 查詢用戶擁有的權限集合
	 * @param username
	 * @return
	 */
	@Override
	public Set<String> getUserPermissionSet(String username) {
		Set<String> permissionSet = new HashSet<>();
		List<SysPermission> permissionList = sysPermissionMapper.queryByUser(username);
		for (SysPermission po : permissionList) {
//			// TODO URL規則有問題？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
			if (oConvertUtils.isNotEmpty(po.getPerms())) {
				permissionSet.add(po.getPerms());
			}
		}
		log.info("-------通過數據庫讀取用戶擁有的權限Perms------username： "+ username+",Perms size: "+ (permissionSet==null?0:permissionSet.size()) );
		return permissionSet;
	}

	/**
	 * 判斷online菜單是否有權限
	 * @param onlineAuthDTO
	 * @return
	 */
	@Override
	public boolean hasOnlineAuth(OnlineAuthDTO onlineAuthDTO) {
		String username = onlineAuthDTO.getUsername();
		List<String> possibleUrl = onlineAuthDTO.getPossibleUrl();
		String onlineFormUrl = onlineAuthDTO.getOnlineFormUrl();
		//查詢菜單
		LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
		query.eq(SysPermission::getDelFlag, 0);
		query.in(SysPermission::getUrl, possibleUrl);
		List<SysPermission> permissionList = sysPermissionMapper.selectList(query);
		if (permissionList == null || permissionList.size() == 0) {
			//沒有配置菜單 找online表單菜單地址
			SysPermission sysPermission = new SysPermission();
			sysPermission.setUrl(onlineFormUrl);
			int count = sysPermissionMapper.queryCountByUsername(username, sysPermission);
			if(count<=0){
				return false;
			}
		} else {
			//找到菜單了
			boolean has = false;
			for (SysPermission p : permissionList) {
				int count = sysPermissionMapper.queryCountByUsername(username, p);
				has = has || (count>0);
			}
			return has;
		}
		return true;
	}

	/**
	 * 查詢用戶擁有的角色集合 common api 里面的接口實現
	 * @param username
	 * @return
	 */
	@Override
	public Set<String> queryUserRoles(String username) {
		return getUserRoleSet(username);
	}

	/**
	 * 查詢用戶擁有的權限集合 common api 里面的接口實現
	 * @param username
	 * @return
	 */
	@Override
	public Set<String> queryUserAuths(String username) {
		return getUserPermissionSet(username);
	}

	/**
	 * 36根據多個用戶賬號(逗號分隔)，查詢返回多個用戶信息
	 * @param usernames
	 * @return
	 */
	@Override
	public List<JSONObject> queryUsersByUsernames(String usernames) {
		LambdaQueryWrapper<SysUser> queryWrapper =  new LambdaQueryWrapper<>();
		queryWrapper.in(SysUser::getUsername,usernames.split(","));
		return JSON.parseArray(JSON.toJSONString(userMapper.selectList(queryWrapper))).toJavaList(JSONObject.class);
	}

	@Override
	public List<JSONObject> queryUsersByIds(String ids) {
		LambdaQueryWrapper<SysUser> queryWrapper =  new LambdaQueryWrapper<>();
		queryWrapper.in(SysUser::getId,ids.split(","));
		return JSON.parseArray(JSON.toJSONString(userMapper.selectList(queryWrapper))).toJavaList(JSONObject.class);
	}

	/**
	 * 37根據多個部門編碼(逗號分隔)，查詢返回多個部門信息
	 * @param orgCodes
	 * @return
	 */
	@Override
	public List<JSONObject> queryDepartsByOrgcodes(String orgCodes) {
		LambdaQueryWrapper<SysDepart> queryWrapper =  new LambdaQueryWrapper<>();
		queryWrapper.in(SysDepart::getOrgCode,orgCodes.split(","));
		return JSON.parseArray(JSON.toJSONString(sysDepartService.list(queryWrapper))).toJavaList(JSONObject.class);
	}

	@Override
	public List<JSONObject> queryDepartsByIds(String ids) {
		LambdaQueryWrapper<SysDepart> queryWrapper =  new LambdaQueryWrapper<>();
		queryWrapper.in(SysDepart::getId,ids.split(","));
		return JSON.parseArray(JSON.toJSONString(sysDepartService.list(queryWrapper))).toJavaList(JSONObject.class);
	}

	/**
	 * 發消息
	 * @param fromUser
	 * @param toUser
	 * @param title
	 * @param msgContent
	 * @param setMsgCategory
	 */
	private void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory) {
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(msgContent);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(setMsgCategory);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用戶通告閱讀標記表記錄
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
			}
		}

	}

	/**
	 * 發消息 帶業務參數
	 * @param fromUser
	 * @param toUser
	 * @param title
	 * @param msgContent
	 * @param setMsgCategory
	 * @param busType
	 * @param busId
	 */
	private void sendBusAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory, String busType, String busId) {
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(msgContent);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(setMsgCategory);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		announcement.setBusId(busId);
		announcement.setBusType(busType);
		announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType).getOpenType());
		announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType).getOpenPage());
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用戶通告閱讀標記表記錄
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
			}
		}
	}

	/**
	 * 發送郵件消息
	 * @param email
	 * @param title
	 * @param content
	 */
	@Override
	public void sendEmailMsg(String email, String title, String content) {
			EmailSendMsgHandle emailHandle=new EmailSendMsgHandle();
			emailHandle.SendMsg(email, title, content);
	}

	/**
	 * 獲取公司下級部門和所有用戶id信息
	 * @param orgCode
	 * @return
	 */
	@Override
	public List<Map> getDeptUserByOrgCode(String orgCode) {
		//1.獲取公司信息
		SysDepart comp=sysDepartService.queryCompByOrgCode(orgCode);
		if(comp!=null){
			//2.獲取公司下級部門
			List<SysDepart> departs=sysDepartService.queryDeptByPid(comp.getId());
			//3.獲取部門下的人員信息
			 List<Map> list=new ArrayList();
			 //4.處理部門和下級用戶數據
			for (SysDepart dept:departs) {
				Map map=new HashMap();
				//部門名稱
				String departName = dept.getDepartName();
				//根據部門編碼獲取下級部門id
				List<String> listIds = departMapper.getSubDepIdsByDepId(dept.getId());
				//根據下級部門ids獲取下級部門的所有用戶
				List<SysUserDepart> userList = sysUserDepartService.list(new QueryWrapper<SysUserDepart>().in("dep_id",listIds));
				List<String> userIds = new ArrayList<>();
				for(SysUserDepart userDepart : userList){
					if(!userIds.contains(userDepart.getUserId())){
						userIds.add(userDepart.getUserId());
					}
				}
				map.put("name",departName);
				map.put("ids",userIds);
				list.add(map);
			}
			return list;
		}
		return null;
	}

	/**
	 * 查詢分類字典翻譯
	 *
	 * @param ids 分類字典表id
	 * @return
	 */
	@Override
	public List<String> loadCategoryDictItem(String ids) {
		return sysCategoryService.loadDictItem(ids, false);
	}

	/**
	 * 根據字典code加載字典text
	 *
	 * @param dictCode 順序：tableName,text,code
	 * @param keys     要查詢的key
	 * @return
	 */
	@Override
	public List<String> loadDictItem(String dictCode, String keys) {
		String[] params = dictCode.split(",");
		return sysDictService.queryTableDictByKeys(params[0], params[1], params[2], keys, false);
	}

	/**
	 * 根據字典code查詢字典項
	 *
	 * @param dictCode 順序：tableName,text,code
	 * @param dictCode 要查詢的key
	 * @return
	 */
	@Override
	public List<DictModel> getDictItems(String dictCode) {
		List<DictModel> ls = sysDictService.getDictItems(dictCode);
		if (ls == null) {
			ls = new ArrayList<>();
		}
		return ls;
	}

	/**
	 * 根據多個字典code查詢多個字典項
	 *
	 * @param dictCodeList
	 * @return key = dictCode ； value=對應的字典項
	 */
	@Override
	public Map<String, List<DictModel>> getManyDictItems(List<String> dictCodeList) {
		return sysDictService.queryDictItemsByCodeList(dictCodeList);
	}

	/**
	 * 【下拉搜索】
	 * 大數據量的字典表 走異步加載，即前端輸入內容過濾數據
	 *
	 * @param dictCode 字典code格式：table,text,code
	 * @param keyword  過濾關鍵字
	 * @return
	 */
	@Override
	public List<DictModel> loadDictItemByKeyword(String dictCode, String keyword, Integer pageSize) {
		return sysDictService.loadDict(dictCode, keyword, pageSize);
	}

	@Override
	public Map<String, List<DictModel>> translateManyDict(String dictCodes, String keys) {
		List<String> dictCodeList = Arrays.asList(dictCodes.split(","));
		List<String> values = Arrays.asList(keys.split(","));
		return sysDictService.queryManyDictByKeys(dictCodeList, values);
	}

	@Override
	public List<DictModel> translateDictFromTableByKeys(String table, String text, String code, String keys) {
		return sysDictService.queryTableDictTextByKeys(table, text, code, Arrays.asList(keys.split(",")));
	}

}