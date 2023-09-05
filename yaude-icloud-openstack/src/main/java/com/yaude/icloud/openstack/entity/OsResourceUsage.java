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
 * @Description: 资源用量表
 * @Author: jeecg-boot
 * @Date:   2021-10-21
 * @Version: V1.0
 */
@Data
@TableName("os_resource_usage")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="os_resource_usage对象", description="资源用量表")
public class OsResourceUsage implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**统计时间*/
	@Excel(name = "统计时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "统计时间")
    private Date usageDate;

    /**项目id*/
    @Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;

	/**内存用量*/
	@Excel(name = "内存用量", width = 15)
    @ApiModelProperty(value = "内存用量")
    private Integer ram;
	/**周期内CPU-小时数*/
	@Excel(name = "周期内CPU-小时数", width = 15)
    @ApiModelProperty(value = "周期内CPU-小时数")
    private BigDecimal totalVcpusUsage;
	/**周期内磁盘GB-小时数*/
	@Excel(name = "周期内磁盘GB-小时数", width = 15)
    @ApiModelProperty(value = "周期内磁盘GB-小时数")
    private BigDecimal totalLocalGbUsage;
	/**周期内的 RAM-小时数*/
	@Excel(name = "周期内的 RAM-小时数", width = 15)
    @ApiModelProperty(value = "周期内的 RAM-小时数")
    private BigDecimal totalMemoryMbUsage;
	/**总运行时长*/
	@Excel(name = "总运行时长", width = 15)
    @ApiModelProperty(value = "总运行时长")
    private String totalHours;
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
