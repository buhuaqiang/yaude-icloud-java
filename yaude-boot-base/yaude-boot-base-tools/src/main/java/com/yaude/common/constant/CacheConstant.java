package com.yaude.common.constant;

/**
 * @author: huangxutao
 * @date: 2019-06-14
 * @description: 緩存常量
 */
public interface CacheConstant {

	/**
	 * license證書信息緩存
	 */
	public static final String SYS_LICENSE_CACHE = "sys:cache:license";


	/**
	 * 多語言信息全表緩存
	 */
	public static final String SYS_TRANSLATE_ALLTABLE_CACHE = "sys:cache:translatealltable";

	/**
	 * 字典信息緩存（含禁用的字典項）
	 */
    public static final String SYS_DICT_CACHE = "sys:cache:dict";

	/**
	 * 字典信息緩存 status為有效的
	 */
	public static final String SYS_ENABLE_DICT_CACHE = "sys:cache:dictEnable";

	/**
	 * 字典信息全表緩存
	 */
	public static final String SYS_DICT_ALLTABLE_CACHE = "sys:cache:dictAllTable";
	/**
	 * 表字典信息緩存
	 */
    public static final String SYS_DICT_TABLE_CACHE = "sys:cache:dictTable";
	public static final String SYS_DICT_TABLE_BY_KEYS_CACHE = SYS_DICT_TABLE_CACHE + "ByKeys";

	/**
	 * 數據權限配置緩存
	 */
    public static final String SYS_DATA_PERMISSIONS_CACHE = "sys:cache:permission:datarules";

	/**
	 * 緩存用戶信息
	 */
	public static final String SYS_USERS_CACHE = "sys:cache:user";

	/**
	 * 全部部門信息緩存
	 */
	public static final String SYS_DEPARTS_CACHE = "sys:cache:depart:alldata";


	/**
	 * 全部部門ids緩存
	 */
	public static final String SYS_DEPART_IDS_CACHE = "sys:cache:depart:allids";


	/**
	 * 測試緩存key
	 */
	public static final String TEST_DEMO_CACHE = "test:demo";

	/**
	 * 字典信息緩存
	 */
	public static final String SYS_DYNAMICDB_CACHE = "sys:cache:dbconnect:dynamic:";

	/**
	 * gateway路由緩存
	 */
	public static final String GATEWAY_ROUTES = "sys:cache:cloud:gateway_routes";


	/**
	 * gateway路由 reload key
	 */
	public static final String ROUTE_JVM_RELOAD_TOPIC = "gateway_jvm_route_reload_topic";

	/**
	 * TODO 冗余代碼 待刪除
	 *插件商城排行榜
	 */
	public static final String PLUGIN_MALL_RANKING = "pluginMall::rankingList";
	/**
	 * TODO 冗余代碼 待刪除
	 *插件商城排行榜
	 */
	public static final String PLUGIN_MALL_PAGE_LIST = "pluginMall::queryPageList";


	/**
	 * online列表頁配置信息緩存key
	 */
	public static final String ONLINE_LIST = "sys:cache:online:list";

	/**
	 * online表單頁配置信息緩存key
	 */
	public static final String ONLINE_FORM = "sys:cache:online:form";

	/**
	 * online報表
	 */
	public static final String ONLINE_RP = "sys:cache:online:rp";

	/**
	 * online圖表
	 */
	public static final String ONLINE_GRAPH = "sys:cache:online:graph";
}
