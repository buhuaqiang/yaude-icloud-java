package com.yaude.common.util.dynamic.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.yaude.common.exception.JeecgBootException;
import com.yaude.common.system.vo.DynamicDataSourceModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import com.yaude.common.util.ReflectHelper;
import com.yaude.common.util.oConvertUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring JDBC 實時數據庫訪問
 *
 * @author chenguobin
 * @version 1.0
 * @date 2014-09-05
 */
@Slf4j
public class DynamicDBUtil {

    /**
     * 獲取數據源【最底層方法，不要隨便調用】
     *
     * @param dbSource
     * @return
     */
    private static DruidDataSource getJdbcDataSource(final DynamicDataSourceModel dbSource) {
        DruidDataSource dataSource = new DruidDataSource();

        String driverClassName = dbSource.getDbDriver();
        String url = dbSource.getDbUrl();
        String dbUser = dbSource.getDbUsername();
        String dbPassword = dbSource.getDbPassword();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        //dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setBreakAfterAcquireFailure(true);
        dataSource.setConnectionErrorRetryAttempts(0);
        dataSource.setUsername(dbUser);
        dataSource.setMaxWait(30000);
        dataSource.setPassword(dbPassword);

        log.info("******************************************");
        log.info("*                                        *");
        log.info("*====【"+dbSource.getCode()+"】=====Druid連接池已啟用 ====*");
        log.info("*                                        *");
        log.info("******************************************");
        return dataSource;
    }

    /**
     * 通過 dbKey ,獲取數據源
     *
     * @param dbKey
     * @return
     */
    public static DruidDataSource getDbSourceByDbKey(final String dbKey) {
        //獲取多數據源配置
        DynamicDataSourceModel dbSource = DataSourceCachePool.getCacheDynamicDataSourceModel(dbKey);
        //先判斷緩存中是否存在數據庫鏈接
        DruidDataSource cacheDbSource = DataSourceCachePool.getCacheBasicDataSource(dbKey);
        if (cacheDbSource != null && !cacheDbSource.isClosed()) {
            log.debug("--------getDbSourceBydbKey------------------從緩存中獲取DB連接-------------------");
            return cacheDbSource;
        } else {
            DruidDataSource dataSource = getJdbcDataSource(dbSource);
            if(dataSource!=null && dataSource.isEnable()){
                DataSourceCachePool.putCacheBasicDataSource(dbKey, dataSource);
            }else{
                throw new JeecgBootException("動態數據源連接失敗，dbKey："+dbKey);
            }
            log.info("--------getDbSourceBydbKey------------------創建DB數據庫連接-------------------");
            return dataSource;
        }
    }

    /**
     * 關閉數據庫連接池
     *
     * @param dbKey
     * @return
     */
    public static void closeDbKey(final String dbKey) {
        DruidDataSource dataSource = getDbSourceByDbKey(dbKey);
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.getConnection().commit();
                dataSource.getConnection().close();
                dataSource.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static JdbcTemplate getJdbcTemplate(String dbKey) {
        DruidDataSource dataSource = getDbSourceByDbKey(dbKey);
        return new JdbcTemplate(dataSource);
    }

    /**
     * Executes the SQL statement in this <code>PreparedStatement</code> object,
     * which must be an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     */
    public static int update(final String dbKey, String sql, Object... param) {
        int effectCount;
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbKey);
        if (ArrayUtils.isEmpty(param)) {
            effectCount = jdbcTemplate.update(sql);
        } else {
            effectCount = jdbcTemplate.update(sql, param);
        }
        return effectCount;
    }

    /**
     * 支持miniDao語法操作的Update
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持minidao語法邏輯
     * @param data  sql語法中需要判斷的數據及sql拼接注入中需要的數據
     * @return
     */
    public static int updateByHash(final String dbKey, String sql, HashMap<String, Object> data) {
        int effectCount;
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbKey);
        //根據模板獲取sql
        sql = FreemarkerParseFactory.parseTemplateContent(sql, data);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        effectCount = namedParameterJdbcTemplate.update(sql, data);
        return effectCount;
    }

    public static Object findOne(final String dbKey, String sql, Object... param) {
        List<Map<String, Object>> list;
        list = findList(dbKey, sql, param);
        if (oConvertUtils.listIsEmpty(list)) {
            log.error("Except one, but not find actually");
            return null;
        }
        if (list.size() > 1) {
            log.error("Except one, but more than one actually");
        }
        return list.get(0);
    }

    /**
     * 支持miniDao語法操作的查詢 返回HashMap
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持minidao語法邏輯
     * @param data  sql語法中需要判斷的數據及sql拼接注入中需要的數據
     * @return
     */
    public static Object findOneByHash(final String dbKey, String sql, HashMap<String, Object> data) {
        List<Map<String, Object>> list;
        list = findListByHash(dbKey, sql, data);
        if (oConvertUtils.listIsEmpty(list)) {
            log.error("Except one, but not find actually");
        }
        if (list.size() > 1) {
            log.error("Except one, but more than one actually");
        }
        return list.get(0);
    }

    /**
     * 直接sql查詢 根據clazz返回單個實例
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句
     * @param clazz 返回實例的Class
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Object findOne(final String dbKey, String sql, Class<T> clazz, Object... param) {
        Map<String, Object> map = (Map<String, Object>) findOne(dbKey, sql, param);
        return ReflectHelper.setAll(clazz, map);
    }

    /**
     * 支持miniDao語法操作的查詢 返回單個實例
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持minidao語法邏輯
     * @param clazz 返回實例的Class
     * @param data  sql語法中需要判斷的數據及sql拼接注入中需要的數據
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Object findOneByHash(final String dbKey, String sql, Class<T> clazz, HashMap<String, Object> data) {
        Map<String, Object> map = (Map<String, Object>) findOneByHash(dbKey, sql, data);
        return ReflectHelper.setAll(clazz, map);
    }

    public static List<Map<String, Object>> findList(final String dbKey, String sql, Object... param) {
        List<Map<String, Object>> list;
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbKey);

        if (ArrayUtils.isEmpty(param)) {
            list = jdbcTemplate.queryForList(sql);
        } else {
            list = jdbcTemplate.queryForList(sql, param);
        }
        return list;
    }

    /**
     * 支持miniDao語法操作的查詢
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持minidao語法邏輯
     * @param data  sql語法中需要判斷的數據及sql拼接注入中需要的數據
     * @return
     */
    public static List<Map<String, Object>> findListByHash(final String dbKey, String sql, HashMap<String, Object> data) {
        List<Map<String, Object>> list;
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbKey);
        //根據模板獲取sql
        sql = FreemarkerParseFactory.parseTemplateContent(sql, data);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        list = namedParameterJdbcTemplate.queryForList(sql, data);
        return list;
    }

    //此方法只能返回單列，不能返回實體類
    public static <T> List<T> findList(final String dbKey, String sql, Class<T> clazz, Object... param) {
        List<T> list;
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbKey);

        if (ArrayUtils.isEmpty(param)) {
            list = jdbcTemplate.queryForList(sql, clazz);
        } else {
            list = jdbcTemplate.queryForList(sql, clazz, param);
        }
        return list;
    }

    /**
     * 支持miniDao語法操作的查詢 返回單列數據list
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持minidao語法邏輯
     * @param clazz 類型Long、String等
     * @param data  sql語法中需要判斷的數據及sql拼接注入中需要的數據
     * @return
     */
    public static <T> List<T> findListByHash(final String dbKey, String sql, Class<T> clazz, HashMap<String, Object> data) {
        List<T> list;
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dbKey);
        //根據模板獲取sql
        sql = FreemarkerParseFactory.parseTemplateContent(sql, data);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        list = namedParameterJdbcTemplate.queryForList(sql, data, clazz);
        return list;
    }

    /**
     * 直接sql查詢 返回實體類列表
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持 minidao 語法邏輯
     * @param clazz 返回實體類列表的class
     * @param param sql拼接注入中需要的數據
     * @return
     */
    public static <T> List<T> findListEntities(final String dbKey, String sql, Class<T> clazz, Object... param) {
        List<Map<String, Object>> queryList = findList(dbKey, sql, param);
        return ReflectHelper.transList2Entrys(queryList, clazz);
    }

    /**
     * 支持miniDao語法操作的查詢 返回實體類列表
     *
     * @param dbKey 數據源標識
     * @param sql   執行sql語句，sql支持minidao語法邏輯
     * @param clazz 返回實體類列表的class
     * @param data  sql語法中需要判斷的數據及sql拼接注入中需要的數據
     * @return
     */
    public static <T> List<T> findListEntitiesByHash(final String dbKey, String sql, Class<T> clazz, HashMap<String, Object> data) {
        List<Map<String, Object>> queryList = findListByHash(dbKey, sql, data);
        return ReflectHelper.transList2Entrys(queryList, clazz);
    }
}
