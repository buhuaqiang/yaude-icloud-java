package com.yaude.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 部門角色人員信息
 * @Author: jeecg-boot
 * @Date:   2020-02-13
 * @Version: V1.0
 */
@Data
@TableName("sys_depart_role_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_depart_role_user對象", description="部門角色人員信息")
public class SysDepartRoleUser {
    
	/**主鍵id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主鍵id")
	private java.lang.String id;
	/**用戶id*/
	@Excel(name = "用戶id", width = 15)
    @ApiModelProperty(value = "用戶id")
	private java.lang.String userId;
	/**角色id*/
	@Excel(name = "角色id", width = 15)
    @ApiModelProperty(value = "角色id")
	private java.lang.String droleId;

	public SysDepartRoleUser() {

	}

	public SysDepartRoleUser(String userId, String droleId) {
		this.userId = userId;
		this.droleId = droleId;
	}
}
