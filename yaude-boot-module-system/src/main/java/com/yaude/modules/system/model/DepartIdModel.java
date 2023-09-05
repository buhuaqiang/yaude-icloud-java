package com.yaude.modules.system.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.yaude.modules.system.entity.SysDepart;

/**
 * <p>
 * 部門表 封裝樹結構的部門的名稱的實體類
 * <p>
 * 
 * @Author Steve
 * @Since 2019-01-22 
 *
 */
public class DepartIdModel implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主鍵ID
    private String key;

    // 主鍵ID
    private String value;

    // 部門名稱
    private String title;
    
    List<DepartIdModel> children = new ArrayList<>();
    
    /**
     * 將SysDepartTreeModel的部分數據放在該對象當中
     * @param treeModel
     * @return
     */
    public DepartIdModel convert(SysDepartTreeModel treeModel) {
        this.key = treeModel.getId();
        this.value = treeModel.getId();
        this.title = treeModel.getDepartName();
        return this;
    }
    
    /**
     * 該方法為用戶部門的實現類所使用
     * @param sysDepart
     * @return
     */
    public DepartIdModel convertByUserDepart(SysDepart sysDepart) {
        this.key = sysDepart.getId();
        this.value = sysDepart.getId();
        this.title = sysDepart.getDepartName();
        return this;
    } 

    public List<DepartIdModel> getChildren() {
        return children;
    }

    public void setChildren(List<DepartIdModel> children) {
        this.children = children;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
