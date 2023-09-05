package com.yaude.modules.quartz.entity;

import java.io.Serializable;

import com.yaude.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description: 定時任務在線管理
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Data
@TableName("sys_quartz_job")
public class QuartzJob implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**創建人*/
	private java.lang.String createBy;
	/**創建時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**刪除狀態*/
	private java.lang.Integer delFlag;
	/**修改人*/
	private java.lang.String updateBy;
	/**修改時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**任務類名*/
	@Excel(name="任務類名",width=40)
	private java.lang.String jobClassName;
	/**cron表達式*/
	@Excel(name="cron表達式",width=30)
	private java.lang.String cronExpression;
	/**參數*/
	@Excel(name="參數",width=15)
	private java.lang.String parameter;
	/**描述*/
	@Excel(name="描述",width=40)
	private java.lang.String description;
	/**狀態 0正常 -1停止*/
	@Excel(name="狀態",width=15,dicCode="quartz_status")
	@Dict(dicCode = "quartz_status")
	private java.lang.Integer status;

}
