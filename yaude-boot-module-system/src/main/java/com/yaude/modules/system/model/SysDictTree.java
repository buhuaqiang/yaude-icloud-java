package com.yaude.modules.system.model;

import java.io.Serializable;
import java.util.Date;

import com.yaude.modules.system.entity.SysDict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
public class SysDictTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
	
	private String title;
	
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
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
    
    public SysDictTree(SysDict node) {
    	this.id = node.getId();
		this.key = node.getId();
		this.title = node.getDictName();
		this.dictCode = node.getDictCode();
		this.description = node.getDescription();
		this.delFlag = node.getDelFlag();
		this.type = node.getType();
	}
    
}
