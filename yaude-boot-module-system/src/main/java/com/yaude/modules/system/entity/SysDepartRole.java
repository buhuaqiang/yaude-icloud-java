package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yaude.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 部門角色
 * @Author: jeecg-boot
 * @Date:   2020-02-12
 * @Version: V1.0
 */
@Data
@TableName("sys_depart_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_depart_role對象", description="部門角色")
public class SysDepartRole {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**部門id*/
	@Excel(name = "部門id", width = 15)
	@ApiModelProperty(value = "部門id")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	private java.lang.String departId;
	/**部門角色名稱*/
	@Excel(name = "部門角色名稱", width = 15)
    @ApiModelProperty(value = "部門角色名稱")
	private java.lang.String roleName;
	/**部門角色編碼*/
	@Excel(name = "部門角色編碼", width = 15)
    @ApiModelProperty(value = "部門角色編碼")
	private java.lang.String roleCode;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
	private java.lang.String description;
	/**創建人*/
	@Excel(name = "創建人", width = 15)
    @ApiModelProperty(value = "創建人")
	private java.lang.String createBy;
	/**創建時間*/
	@Excel(name = "創建時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "創建時間")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private java.lang.String updateBy;
	/**更新時間*/
	@Excel(name = "更新時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新時間")
	private java.util.Date updateTime;


}
