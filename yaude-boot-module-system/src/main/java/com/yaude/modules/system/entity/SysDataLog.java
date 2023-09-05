package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDataLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId(type = IdType.ASSIGN_ID)
	private String id; //id'
	private String createBy; //創建人登錄名稱
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime; //創建日期
	private String updateBy; //更新人登錄名稱
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime; //更新日期
	private String dataTable; //表名
	private String dataId; //數據ID
	private String dataContent; //數據內容
	private String dataVersion; //版本號
}
