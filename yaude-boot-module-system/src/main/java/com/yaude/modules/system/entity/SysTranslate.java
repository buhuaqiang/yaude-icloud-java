package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 多語言
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
@Data
@TableName("sys_translate")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_translate對象", description="多語言")
public class SysTranslate implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主鍵*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主鍵")
    private String id;
	/**關聯表名稱*/
	@Excel(name = "關聯表名稱", width = 15)
    @ApiModelProperty(value = "關聯表名稱")
    private String relateTable;
	/**關聯主鍵id*/
	@Excel(name = "關聯主鍵id", width = 15)
    @ApiModelProperty(value = "關聯主鍵id")
    private String relateId;
	/**中文*/
	@Excel(name = "中文", width = 15)
    @ApiModelProperty(value = "中文")
    private String chinese;
	/**繁體中文*/
	@Excel(name = "繁體中文", width = 15)
    @ApiModelProperty(value = "繁體中文")
    private String taiwan;
	/**英語*/
	@Excel(name = "英語", width = 15)
    @ApiModelProperty(value = "英語")
    private String english;
	/**創建人*/
    @ApiModelProperty(value = "創建人")
    private String createBy;
	/**創建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "創建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所屬部門*/
    @ApiModelProperty(value = "所屬部門")
    private String sysOrgCode;
}
