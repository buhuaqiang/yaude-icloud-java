package com.yaude.common.system.vo;

import lombok.Data;

/**
 * 字典查詢參數實體
 */
@Data
public class DictQuery {
    /**
     * 表名
     */
    private String table;
    /**
     * 存儲列
     */
    private String code;

    /**
     * 顯示列
     */
    private String text;

    /**
     * 關鍵字查詢
     */
    private String keyword;

    /**
     * 存儲列的值 用于回顯查詢
     */
    private String codeValue;

}
