package com.yaude.modules.demo.test.entity;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description: 流程測試
 * @Author: jeecg-boot
 * @Date:   2019-05-14
 * @Version: V1.0
 */
@Data
@TableName("joa_demo")
public class JoaDemo implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**請假人*/
	@Excel(name = "請假人", width = 15)
	private String name;
	/**請假天數*/
	@Excel(name = "請假天數", width = 15)
	private Integer days;
	/**開始時間*/
	@Excel(name = "開始時間", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date beginDate;
	/**請假結束時間*/
	@Excel(name = "請假結束時間", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date endDate;
	/**請假原因*/
	@Excel(name = "請假原因", width = 15)
	private String reason;
	/**流程狀態*/
	@Excel(name = "流程狀態", width = 15)
	private String bpmStatus;
	/**創建人id*/
	@Excel(name = "創建人id", width = 15)
	private String createBy;
	/**創建時間*/
	@Excel(name = "創建時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**修改時間*/
	@Excel(name = "修改時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**修改人id*/
	@Excel(name = "修改人id", width = 15)
	private String updateBy;
}
