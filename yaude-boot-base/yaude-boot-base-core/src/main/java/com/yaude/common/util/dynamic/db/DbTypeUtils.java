package com.yaude.common.util.dynamic.db;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * 數據庫類型判斷
 * 【有些數據庫引擎是一樣的，以達到復用目的】
 */
public class DbTypeUtils {

    public static boolean dbTypeIsMySQL(DbType dbType) {
        return dbTypeIf(dbType, DbType.MYSQL, DbType.MARIADB, DbType.CLICK_HOUSE, DbType.SQLITE);
    }

    public static boolean dbTypeIsOracle(DbType dbType) {
        return dbTypeIf(dbType, DbType.ORACLE, DbType.ORACLE_12C, DbType.DM);
    }

    public static boolean dbTypeIsSQLServer(DbType dbType) {
        return dbTypeIf(dbType, DbType.SQL_SERVER, DbType.SQL_SERVER2005);
    }

    public static boolean dbTypeIsPostgre(DbType dbType) {
        return dbTypeIf(dbType, DbType.POSTGRE_SQL, DbType.KINGBASE_ES, DbType.GAUSS);
    }

    /**
     * 判斷數據庫類型
     */
    public static boolean dbTypeIf(DbType dbType, DbType... correctTypes) {
        for (DbType type : correctTypes) {
            if (type.equals(dbType)) {
                return true;
            }
        }
        return false;
    }

}
