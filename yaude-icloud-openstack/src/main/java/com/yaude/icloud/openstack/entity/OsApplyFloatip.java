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
import com.yaude.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 浮动ip申请明细档
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
@Data
@TableName("os_apply_floatip")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="os_apply_floatip对象", description="浮动ip申请明细档")
public class OsApplyFloatip implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**网络名称*/
	@Excel(name = "网络名称", width = 15)
    @ApiModelProperty(value = "网络名称")
    private String networkName;
	/**子网名称*/
	@Excel(name = "子网名称", width = 15)
    @ApiModelProperty(value = "子网名称")
    private String subnetName;
	/**子网id*/
	@Excel(name = "子网id", width = 15)
    @ApiModelProperty(value = "子网id")
    private String subnetId;
	/**浮动ip*/
	@Excel(name = "浮动ip", width = 15)
    @ApiModelProperty(value = "浮动ip")
    private String floatIp;
	/**申請类型*/
	@Excel(name = "申請类型", width = 15)
    @ApiModelProperty(value = "申請类型")
    @Dict(dicCode = "options")
    private String options;
	/**申请狀態*/
	@Excel(name = "申请狀態", width = 15)
    @ApiModelProperty(value = "申请狀態")
    @Dict(dicCode = "options_status")
    private String status;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String represent;
	/**申请时间*/
	@Excel(name = "申请时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "申请时间")
    private Date startTime;
    /**结束日期*/
    @Excel(name = "结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endTime;
    /**项目名称*/
    @Excel(name = "项目名称", width = 15)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**项目id*/
    @Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;
	/**映射vm_ip*/
	@Excel(name = "映射vm_ip", width = 15)
    @ApiModelProperty(value = "映射vm_ip")
    private String mapperIp;
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
