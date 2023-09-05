package com.yaude.modules.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.util.CommonUtils;
import com.yaude.modules.system.entity.SysLog;
import com.yaude.modules.system.mapper.SysLogMapper;
import com.yaude.modules.system.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 系統日志表 服務實現類
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

	@Resource
	private SysLogMapper sysLogMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	
	/**
	 * @功能：清空所有日志記錄
	 */
	@Override
	public void removeAll() {
		sysLogMapper.removeAll();
	}

	@Override
	public Long findTotalVisitCount() {
		return sysLogMapper.findTotalVisitCount();
	}

	//update-begin--Author:zhangweijian  Date:20190428 for：傳入開始時間，結束時間參數
	@Override
	public Long findTodayVisitCount(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayVisitCount(dayStart,dayEnd);
	}

	@Override
	public Long findTodayIp(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayIp(dayStart,dayEnd);
	}
	//update-end--Author:zhangweijian  Date:20190428 for：傳入開始時間，結束時間參數

	@Override
	public List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd) {
		DbType dbType = CommonUtils.getDatabaseTypeEnum();
		return sysLogMapper.findVisitCount(dayStart, dayEnd,dbType.getDb());
	}
}
