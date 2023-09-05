package com.yaude.common.util.dynamic.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.system.vo.DynamicDataSourceModel;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.util.SpringContextUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * 數據源緩存池
 */
public class DataSourceCachePool {
    /** 數據源連接池緩存【本地 class緩存 - 不支持分布式】 */
    private static Map<String, DruidDataSource> dbSources = new HashMap<>();
    private static RedisTemplate<String, Object> redisTemplate;

    private static RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = (RedisTemplate<String, Object>) SpringContextUtils.getBean("redisTemplate");
        }
        return redisTemplate;
    }

    /**
     * 獲取多數據源緩存
     *
     * @param dbKey
     * @return
     */
    public static DynamicDataSourceModel getCacheDynamicDataSourceModel(String dbKey) {
        String redisCacheKey = CacheConstant.SYS_DYNAMICDB_CACHE + dbKey;
        if (getRedisTemplate().hasKey(redisCacheKey)) {
            return (DynamicDataSourceModel) getRedisTemplate().opsForValue().get(redisCacheKey);
        }
        CommonAPI commonAPI = SpringContextUtils.getBean(CommonAPI.class);
        DynamicDataSourceModel dbSource = commonAPI.getDynamicDbSourceByCode(dbKey);
        if (dbSource != null) {
            getRedisTemplate().opsForValue().set(redisCacheKey, dbSource);
        }
        return dbSource;
    }

    public static DruidDataSource getCacheBasicDataSource(String dbKey) {
        return dbSources.get(dbKey);
    }

    /**
     * put 數據源緩存
     *
     * @param dbKey
     * @param db
     */
    public static void putCacheBasicDataSource(String dbKey, DruidDataSource db) {
        dbSources.put(dbKey, db);
    }

    /**
     * 清空數據源緩存
     */
    public static void cleanAllCache() {
        //關閉數據源連接
        for(Map.Entry<String, DruidDataSource> entry : dbSources.entrySet()){
            String dbkey = entry.getKey();
            DruidDataSource druidDataSource = entry.getValue();
            if(druidDataSource!=null && druidDataSource.isEnable()){
                druidDataSource.close();
            }
            //清空redis緩存
            getRedisTemplate().delete(CacheConstant.SYS_DYNAMICDB_CACHE + dbkey);
        }
        //清空緩存
        dbSources.clear();
    }

    public static void removeCache(String dbKey) {
        //關閉數據源連接
        DruidDataSource druidDataSource = dbSources.get(dbKey);
        if(druidDataSource!=null && druidDataSource.isEnable()){
            druidDataSource.close();
        }
        //清空redis緩存
        getRedisTemplate().delete(CacheConstant.SYS_DYNAMICDB_CACHE + dbKey);
        //清空緩存
        dbSources.remove(dbKey);
    }

}
