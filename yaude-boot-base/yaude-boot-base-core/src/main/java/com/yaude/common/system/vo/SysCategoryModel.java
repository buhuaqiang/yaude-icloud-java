package com.yaude.common.system.vo;

import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author qinfeng
 * @Date 2020/2/19 12:01
 * @Description:
 * @Version 1.0
 */
public class SysCategoryModel {
    /**主鍵*/
    private java.lang.String id;
    /**父級節點*/
    private java.lang.String pid;
    /**類型名稱*/
    private java.lang.String name;
    /**類型編碼*/
    private java.lang.String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
