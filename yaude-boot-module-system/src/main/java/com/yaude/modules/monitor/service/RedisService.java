package com.yaude.modules.monitor.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.yaude.modules.monitor.exception.RedisConnectException;
import com.yaude.modules.monitor.domain.RedisInfo;

public interface RedisService {

	/**
	 * 獲取 redis 的詳細信息
	 *
	 * @return List
	 */
	List<RedisInfo> getRedisInfo() throws RedisConnectException;

	/**
	 * 獲取 redis key 數量
	 *
	 * @return Map
	 */
	Map<String, Object> getKeysSize() throws RedisConnectException;

	/**
	 * 獲取 redis 內存信息
	 *
	 * @return Map
	 */
	Map<String, Object> getMemoryInfo() throws RedisConnectException;
	/**
	 * 獲取 報表需要個redis信息
	 *
	 * @return Map
	 */
	Map<String, JSONArray> getMapForReport(String type) throws RedisConnectException ;
}
