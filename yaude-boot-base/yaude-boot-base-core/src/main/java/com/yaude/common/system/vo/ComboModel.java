package com.yaude.common.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComboModel implements Serializable {
    private String id;
    private String title;
    /**文檔管理 表單table默認選中*/
    private boolean checked;
    /**文檔管理 表單table 用戶賬號*/
    private String username;
    /**文檔管理 表單table 用戶郵箱*/
    private String email;
    /**文檔管理 表單table 角色編碼*/
    private String roleCode;

    public ComboModel(){

    };

    public ComboModel(String id,String title,boolean checked,String username){
        this.id = id;
        this.title = title;
        this.checked = false;
        this.username = username;
    };
}
