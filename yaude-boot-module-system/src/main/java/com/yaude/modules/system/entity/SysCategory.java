package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 分類字典
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
@Data
@TableName("sys_category")
public class SysCategory implements Serializable,Comparable<SysCategory>{
    private static final long serialVersionUID = 1L;
    
	/**主鍵*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**父級節點*/
	private java.lang.String pid;
	/**類型名稱*/
	@Excel(name = "類型名稱", width = 15)
	private java.lang.String name;
	/**類型編碼*/
	@Excel(name = "類型編碼", width = 15)
	private java.lang.String code;
	/**創建人*/
	private java.lang.String createBy;
	/**創建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**更新人*/
	private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**所屬部門*/
	private java.lang.String sysOrgCode;
	/**是否有子節點*/
	@Excel(name = "是否有子節點(1:有)", width = 15)
	private java.lang.String hasChild;

	@Override
	public int compareTo(SysCategory o) {
		//比較條件我們定的是按照code的長度升序
		// <0：當前對象比傳入對象小。
		// =0：當前對象等于傳入對象。
		// >0：當前對象比傳入對象大。
		int	 s = this.code.length() - o.code.length();
		return s;
	}
	@Override
	public String toString() {
		return "SysCategory [code=" + code + ", name=" + name + "]";
	}
}
