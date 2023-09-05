package com.yaude.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeecg.dingtalk.api.core.response.Response;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.constant.CommonSendStatus;
import com.yaude.common.constant.WebsocketConst;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.RedisUtil;
import com.yaude.modules.message.websocket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.util.TokenUtils;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysAnnouncement;
import com.yaude.modules.system.entity.SysAnnouncementSend;
import com.yaude.modules.system.service.ISysAnnouncementSendService;
import com.yaude.modules.system.service.ISysAnnouncementService;
import com.yaude.modules.system.service.impl.ThirdAppDingtalkServiceImpl;
import com.yaude.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import com.yaude.modules.system.util.XSSUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: Controller
 * @Description: 系統通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/annountCement")
@Slf4j
public class SysAnnouncementController {
	@Autowired
	private ISysAnnouncementService sysAnnouncementService;
	@Autowired
	private ISysAnnouncementSendService sysAnnouncementSendService;
	@Resource
    private WebSocket webSocket;
	@Autowired
	ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
	@Autowired
	ThirdAppDingtalkServiceImpl dingtalkService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	@Lazy
	private RedisUtil redisUtil;

	/**
	  * 分頁列表查詢
	 * @param sysAnnouncement
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysAnnouncement>> queryPageList(SysAnnouncement sysAnnouncement,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SysAnnouncement>> result = new Result<IPage<SysAnnouncement>>();
		sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
		QueryWrapper<SysAnnouncement> queryWrapper = new QueryWrapper<SysAnnouncement>(sysAnnouncement);
		Page<SysAnnouncement> page = new Page<SysAnnouncement>(pageNo,pageSize);
		//排序邏輯 處理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			}else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}
		IPage<SysAnnouncement> pageList = sysAnnouncementService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   添加
	 * @param sysAnnouncement
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysAnnouncement> add(@RequestBody SysAnnouncement sysAnnouncement) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		try {
			// update-begin-author:liusq date:20210804 for:標題處理xss攻擊的問題
			String title = XSSUtils.striptXSS(sysAnnouncement.getTitile());
			sysAnnouncement.setTitile(title);
			// update-end-author:liusq date:20210804 for:標題處理xss攻擊的問題
			sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
			sysAnnouncement.setSendStatus(CommonSendStatus.UNPUBLISHED_STATUS_0);//未發布
			sysAnnouncementService.saveAnnouncement(sysAnnouncement);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	  *  編輯
	 * @param sysAnnouncement
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<SysAnnouncement> eidt(@RequestBody SysAnnouncement sysAnnouncement) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncementEntity = sysAnnouncementService.getById(sysAnnouncement.getId());
		if(sysAnnouncementEntity==null) {
			result.error500("未找到對應實體");
		}else {
			// update-begin-author:liusq date:20210804 for:標題處理xss攻擊的問題
			String title = XSSUtils.striptXSS(sysAnnouncement.getTitile());
			sysAnnouncement.setTitile(title);
			// update-end-author:liusq date:20210804 for:標題處理xss攻擊的問題
			boolean ok = sysAnnouncementService.upDateAnnouncement(sysAnnouncement);
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
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysAnnouncement> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("未找到對應實體");
		}else {
			sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				result.success("刪除成功!");
			}
		}

		return result;
	}

	/**
	  *  批量刪除
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysAnnouncement> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("參數不識別！");
		}else {
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++) {
				SysAnnouncement announcement = sysAnnouncementService.getById(id[i]);
				announcement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
				sysAnnouncementService.updateById(announcement);
			}
			result.success("刪除成功!");
		}
		return result;
	}

	/**
	  * 通過id查詢
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<SysAnnouncement> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("未找到對應實體");
		}else {
			result.setResult(sysAnnouncement);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 *	 更新發布操作
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReleaseData", method = RequestMethod.GET)
	public Result<SysAnnouncement> doReleaseData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("未找到對應實體");
		}else {
			sysAnnouncement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);//發布中
			sysAnnouncement.setSendTime(new Date());
			String currentUserName = JwtUtil.getUserNameByToken(request);
			sysAnnouncement.setSender(currentUserName);
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				result.success("該系統通知發布成功");
				if(sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
					JSONObject obj = new JSONObject();
			    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
					obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
					obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
			    	webSocket.sendMessage(obj.toJSONString());
				}else {
					// 2.插入用戶通告閱讀標記表記錄
					String userId = sysAnnouncement.getUserIds();
					String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
					String anntId = sysAnnouncement.getId();
					Date refDate = new Date();
					JSONObject obj = new JSONObject();
			    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
					obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
					obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
			    	webSocket.sendMessage(userIds, obj.toJSONString());
				}
				try {
					// 同步企業微信、釘釘的消息通知
					Response<String> dtResponse = dingtalkService.sendActionCardMessage(sysAnnouncement, true);
					wechatEnterpriseService.sendTextCardMessage(sysAnnouncement, true);

					if (dtResponse != null && dtResponse.isSuccess()) {
						String taskId = dtResponse.getResult();
						sysAnnouncement.setDtTaskId(taskId);
						sysAnnouncementService.updateById(sysAnnouncement);
					}
				} catch (Exception e) {
					log.error("同步發送第三方APP消息失敗：", e);
				}
			}
		}

		return result;
	}

	/**
	 *	 更新撤銷操作
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReovkeData", method = RequestMethod.GET)
	public Result<SysAnnouncement> doReovkeData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("未找到對應實體");
		}else {
			sysAnnouncement.setSendStatus(CommonSendStatus.REVOKE_STATUS_2);//撤銷發布
			sysAnnouncement.setCancelTime(new Date());
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				result.success("該系統通知撤銷成功");
				if (oConvertUtils.isNotEmpty(sysAnnouncement.getDtTaskId())) {
					try {
						dingtalkService.recallMessage(sysAnnouncement.getDtTaskId());
					} catch (Exception e) {
						log.error("第三方APP撤回消息失敗：", e);
					}
				}
			}
		}

		return result;
	}

	/**
	 * @功能：補充用戶數據，并返回系統消息
	 * @return
	 */
	@RequestMapping(value = "/listByUser", method = RequestMethod.GET)
	public Result<Map<String,Object>> listByUser() {
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		// 1.將系統消息補充到用戶通告閱讀標記表中
		LambdaQueryWrapper<SysAnnouncement> querySaWrapper = new LambdaQueryWrapper<SysAnnouncement>();
		querySaWrapper.eq(SysAnnouncement::getMsgType,CommonConstant.MSG_TYPE_ALL); // 全部人員
		querySaWrapper.eq(SysAnnouncement::getDelFlag,CommonConstant.DEL_FLAG_0.toString());  // 未刪除
		querySaWrapper.eq(SysAnnouncement::getSendStatus, CommonConstant.HAS_SEND); //已發布
		querySaWrapper.ge(SysAnnouncement::getEndTime, sysUser.getCreateTime()); //新注冊用戶不看結束通知
		//update-begin--Author:liusq  Date:20210108 for：[JT-424] 【開源issue】bug處理--------------------
		querySaWrapper.notInSql(SysAnnouncement::getId,"select annt_id from sys_announcement_send where user_id='"+userId+"'");
		//update-begin--Author:liusq  Date:20210108  for： [JT-424] 【開源issue】bug處理--------------------
		List<SysAnnouncement> announcements = sysAnnouncementService.list(querySaWrapper);
		if(announcements.size()>0) {
			for(int i=0;i<announcements.size();i++) {
				//update-begin--Author:wangshuai  Date:20200803  for： 通知公告消息重復LOWCOD-759--------------------
				//因為websocket沒有判斷是否存在這個用戶，要是判斷會出現問題，故在此判斷邏輯
				LambdaQueryWrapper<SysAnnouncementSend> query = new LambdaQueryWrapper<>();
				query.eq(SysAnnouncementSend::getAnntId,announcements.get(i).getId());
				query.eq(SysAnnouncementSend::getUserId,userId);
				SysAnnouncementSend one = sysAnnouncementSendService.getOne(query);
				if(null==one){
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(announcements.get(i).getId());
				announcementSend.setUserId(userId);
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendService.save(announcementSend);
				}
				//update-end--Author:wangshuai  Date:20200803  for： 通知公告消息重復LOWCOD-759------------
			}
		}
		// 2.查詢用戶未讀的系統消息
		Page<SysAnnouncement> anntMsgList = new Page<SysAnnouncement>(0,5);
		anntMsgList = sysAnnouncementService.querySysCementPageByUserId(anntMsgList,userId,"1");//通知公告消息
		Page<SysAnnouncement> sysMsgList = new Page<SysAnnouncement>(0,5);
		sysMsgList = sysAnnouncementService.querySysCementPageByUserId(sysMsgList,userId,"2");//系統消息
		Map<String,Object> sysMsgMap = new HashMap<String, Object>();
		sysMsgMap.put("sysMsgList", sysMsgList.getRecords());
		sysMsgMap.put("sysMsgTotal", sysMsgList.getTotal());
		sysMsgMap.put("anntMsgList", anntMsgList.getRecords());
		sysMsgMap.put("anntMsgTotal", anntMsgList.getTotal());
		result.setSuccess(true);
		result.setResult(sysMsgMap);
		return result;
	}


    /**
     * 導出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysAnnouncement sysAnnouncement,HttpServletRequest request) {
        // Step.1 組裝查詢條件
        LambdaQueryWrapper<SysAnnouncement> queryWrapper = new LambdaQueryWrapper<SysAnnouncement>(sysAnnouncement);
        //Step.2 AutoPoi 導出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		queryWrapper.eq(SysAnnouncement::getDelFlag,CommonConstant.DEL_FLAG_0);
        List<SysAnnouncement> pageList = sysAnnouncementService.list(queryWrapper);
        //導出文件名稱
        mv.addObject(NormalExcelConstants.FILE_NAME, "系統通告列表");
        mv.addObject(NormalExcelConstants.CLASS, SysAnnouncement.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("系統通告列表數據", "導出人:"+user.getRealname(), "導出信息"));
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
                List<SysAnnouncement> listSysAnnouncements = ExcelImportUtil.importExcel(file.getInputStream(), SysAnnouncement.class, params);
                for (SysAnnouncement sysAnnouncementExcel : listSysAnnouncements) {
                	if(sysAnnouncementExcel.getDelFlag()==null){
                		sysAnnouncementExcel.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
					}
                    sysAnnouncementService.save(sysAnnouncementExcel);
                }
                return Result.ok("文件導入成功！數據行數：" + listSysAnnouncements.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("文件導入失敗！");
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
	 *同步消息
	 * @param anntId
	 * @return
	 */
	@RequestMapping(value = "/syncNotic", method = RequestMethod.GET)
	public Result<SysAnnouncement> syncNotic(@RequestParam(name="anntId",required=false) String anntId, HttpServletRequest request) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		JSONObject obj = new JSONObject();
		if(StringUtils.isNotBlank(anntId)){
			SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(anntId);
			if(sysAnnouncement==null) {
				result.error500("未找到對應實體");
			}else {
				if(sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
					obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
					obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
					obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
					webSocket.sendMessage(obj.toJSONString());
				}else {
					// 2.插入用戶通告閱讀標記表記錄
					String userId = sysAnnouncement.getUserIds();
					if(oConvertUtils.isNotEmpty(userId)){
						String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
						obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
						obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
						obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
						webSocket.sendMessage(userIds, obj.toJSONString());
					}
				}
			}
		}else{
			obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
			obj.put(WebsocketConst.MSG_TXT, "批量設置已讀");
			webSocket.sendMessage(obj.toJSONString());
		}
		return result;
	}

	/**
	 * 通告查看詳情頁面（用于第三方APP）
	 * @param modelAndView
	 * @param id
	 * @return
	 */
    @GetMapping("/show/{id}")
    public ModelAndView showContent(ModelAndView modelAndView, @PathVariable("id") String id, HttpServletRequest request) {
        SysAnnouncement announcement = sysAnnouncementService.getById(id);
        if (announcement != null) {
            boolean tokenOK = false;
            try {
                // 驗證Token有效性
                tokenOK = TokenUtils.verifyToken(request, sysBaseAPI, redisUtil);
            } catch (Exception ignored) {
            }
            // 判斷是否傳遞了Token，并且Token有效，如果傳了就不做查看限制，直接返回
            // 如果Token無效，就做查看限制：只能查看已發布的
            if (tokenOK || CommonConstant.ANNOUNCEMENT_SEND_STATUS_1.equals(announcement.getSendStatus())) {
                modelAndView.addObject("data", announcement);
                modelAndView.setViewName("announcement/showContent");
                return modelAndView;
            }
        }
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

}
