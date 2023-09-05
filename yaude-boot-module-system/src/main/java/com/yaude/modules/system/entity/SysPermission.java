package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yaude.common.aspect.annotation.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜單權限表
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 父id
	 */
	private String parentId;

	/**
	 * 菜單名稱
	 */
	private String name;

	/**
	 * 菜單權限編碼，例如：“sys:schedule:list,sys:schedule:info”,多個逗號隔開
	 */
	private String perms;
	/**
	 * 權限策略1顯示2禁用
	 */
	private String permsType;

	/**
	 * 菜單圖標
	 */
	private String icon;

	/**
	 * 組件
	 */
	private String component;
	
	/**
	 * 組件名字
	 */
	private String componentName;

	/**
	 * 路徑
	 */
	private String url;
	/**
	 * 一級菜單跳轉地址
	 */
	private String redirect;

	/**
	 * 菜單排序
	 */
	private Double sortNo;

	/**
	 * 類型（0：一級菜單；1：子菜單 ；2：按鈕權限）
	 */
	@Dict(dicCode = "menu_type")
	private Integer menuType;

	/**
	 * 是否葉子節點: 1:是  0:不是
	 */
	@TableField(value="is_leaf")
	private boolean leaf;
	
	/**
	 * 是否路由菜單: 0:不是  1:是（默認值1）
	 */
	@TableField(value="is_route")
	private boolean route;


	/**
	 * 是否緩存頁面: 0:不是  1:是（默認值1）
	 */
	@TableField(value="keep_alive")
	private boolean keepAlive;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 創建人
	 */
	private String createBy;

	/**
	 * 刪除狀態 0正常 1已刪除
	 */
	private Integer delFlag;
	
	/**
	 * 是否配置菜單的數據權限 1是0否 默認0
	 */
	private Integer ruleFlag;
	
	/**
	 * 是否隱藏路由菜單: 0否,1是（默認值0）
	 */
	private boolean hidden;

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
	
	/**按鈕權限狀態(0無效1有效)*/
	private java.lang.String status;
	
	/**alwaysShow*/
    private boolean alwaysShow;

	/*update_begin author:wuxianquan date:20190908 for:實體增加字段 */
    /** 外鏈菜單打開方式 0/內部打開 1/外部打開 */
    private boolean internalOrExternal;
	/*update_end author:wuxianquan date:20190908 for:實體增加字段 */

    public SysPermission() {
    	
    }
    public SysPermission(boolean index) {
    	if(index) {
    		this.id = "9502685863ab87f0ad1134142788a385";
        	this.name="首頁";
        	this.component="dashboard/Analysis";
        	this.componentName="dashboard-analysis";
        	this.url="/dashboard/analysis";
        	this.icon="home";
        	this.menuType=0;
        	this.sortNo=0.0;
        	this.ruleFlag=0;
        	this.delFlag=0;
        	this.alwaysShow=false;
        	this.route=true;
        	this.keepAlive=true;
        	this.leaf=true;
        	this.hidden=false;
    	}
    	
    }
}
