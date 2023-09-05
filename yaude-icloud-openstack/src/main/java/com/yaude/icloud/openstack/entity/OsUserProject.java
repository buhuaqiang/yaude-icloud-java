package com.yaude.icloud.openstack.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
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
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Data
@TableName("os_user_project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="os_user_project对象", description="用戶 項目關聯表")
public class OsUserProject implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**用戶ID*/
	@Excel(name = "用戶ID", width = 15)
    @ApiModelProperty(value = "用戶ID")
    private String userId;
	/**項目ID*/
	@Excel(name = "項目ID", width = 15)
    @ApiModelProperty(value = "項目ID")
    private String projectId;
    /**是否管理員*/
    @Excel(name = "是否管理員", width = 15)
    @ApiModelProperty(value = "是否管理員")
    private Integer isAdmin;
	/**域ID*/
	@Excel(name = "域ID", width = 15)
    @ApiModelProperty(value = "域ID")
    private String domainId;
	/**域名*/
	@Excel(name = "域名", width = 15)
    @ApiModelProperty(value = "域名")
    private String domainName;
	/**openstack主機ip*/
	@Excel(name = "openstack主機ip", width = 15)
    @ApiModelProperty(value = "openstack主機ip")
    private String hostIp;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
}
