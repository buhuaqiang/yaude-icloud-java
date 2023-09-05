package com.yaude.common.system.vo;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class DynamicDataSourceModel {

    public DynamicDataSourceModel() {

    }

    public DynamicDataSourceModel(Object dbSource) {
        if (dbSource != null) {
            BeanUtils.copyProperties(dbSource, this);
        }
    }

    /**
     * id
     */
    private java.lang.String id;
    /**
     * 數據源編碼
     */
    private java.lang.String code;
    /**
     * 數據庫類型
     */
    private java.lang.String dbType;
    /**
     * 驅動類
     */
    private java.lang.String dbDriver;
    /**
     * 數據源地址
     */
    private java.lang.String dbUrl;

//    /**
//     * 數據庫名稱
//     */
//    private java.lang.String dbName;

    /**
     * 用戶名
     */
    private java.lang.String dbUsername;
    /**
     * 密碼
     */
    private java.lang.String dbPassword;

}