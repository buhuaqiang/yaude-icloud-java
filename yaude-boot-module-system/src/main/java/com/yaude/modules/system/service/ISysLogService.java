package com.yaude.modules.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yaude.modules.system.entity.SysLog;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系統日志表 服務類
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
public interface ISysLogService extends IService<SysLog> {

	/**
	 * @功能：清空所有日志記錄
	 */
	public void removeAll();
	
	/**
	 * 獲取系統總訪問次數
	 *
	 * @return Long
	 */
	Long findTotalVisitCount();

	//update-begin--Author:zhangweijian  Date:20190428 for：傳入開始時間，結束時間參數
	/**
	 * 獲取系統今日訪問次數
	 *
	 * @return Long
	 */
	Long findTodayVisitCount(Date dayStart, Date dayEnd);

	/**
	 * 獲取系統今日訪問 IP數
	 *
	 * @return Long
	 */
	Long findTodayIp(Date dayStart, Date dayEnd);
	//update-end--Author:zhangweijian  Date:20190428 for：傳入開始時間，結束時間參數
	
	/**
	 *   首頁：根據時間統計訪問數量/ip數量
	 * @param dayStart
	 * @param dayEnd
	 * @return
	 */
	List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd);
}
