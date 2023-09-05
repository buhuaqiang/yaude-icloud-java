package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * [預留字段，暫時無用]
     * 字典類型,0 string,1 number類型,2 boolean
     * 前端js對stirng類型和number類型 boolean 類型敏感，需要區分。在select 標簽匹配的時候會用到
     * 默認為string類型
     */
    private Integer type;
    
    /**
     * 字典名稱
     */
    private String dictName;

    /**
     * 字典編碼
     */
    private String dictCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 刪除狀態
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 創建人
     */
    private String createBy;

    /**
     * 創建時間
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新時間
     */
    private Date updateTime;


}
