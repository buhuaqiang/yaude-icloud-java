package com.yaude.modules.demo.test.entity;

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
 * @Description: 阿德測試
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
@Data
@TableName("testad")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="testad對象", description="阿德測試")
public class Testad implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主鍵*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主鍵")
    private String id;
	/**編號*/
	@Excel(name = "編號", width = 15)
    @ApiModelProperty(value = "編號")
    private Integer num;
	/**文本*/
	@Excel(name = "文本", width = 15)
    @ApiModelProperty(value = "文本")
    private String text;
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
