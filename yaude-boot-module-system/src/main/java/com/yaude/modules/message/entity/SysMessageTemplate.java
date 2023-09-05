package com.yaude.modules.message.entity;

import com.yaude.common.system.base.entity.JeecgEntity;
import org.jeecgframework.poi.excel.annotation.Excel;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_sms_template")
public class SysMessageTemplate extends JeecgEntity{
	/**模板CODE*/
	@Excel(name = "模板CODE", width = 15)
	private java.lang.String templateCode;
	/**模板標題*/
	@Excel(name = "模板標題", width = 30)
	private java.lang.String templateName;
	/**模板內容*/
	@Excel(name = "模板內容", width = 50)
	private java.lang.String templateContent;
	/**模板測試json*/
	@Excel(name = "模板測試json", width = 15)
	private java.lang.String templateTestJson;
	/**模板類型*/
	@Excel(name = "模板類型", width = 15)
	private java.lang.String templateType;
}
