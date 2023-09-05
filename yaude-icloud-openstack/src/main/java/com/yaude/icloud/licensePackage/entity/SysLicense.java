package com.yaude.icloud.licensePackage.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

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
 * @Description: 认证证书
 * @Author: jeecg-boot
 * @Date:   2021-11-04
 * @Version: V1.0
 */
@Data
@TableName("sys_license")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_license对象", description="认证证书")
public class SysLicense implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**证书名称*/
	@Excel(name = "证书名称", width = 15)
    @ApiModelProperty(value = "证书名称")
    private String subject;


	/**证书生成路径*/
	@Excel(name = "证书生成路径", width = 15)
    @ApiModelProperty(value = "证书生成路径")
    private String licensePath;

	/**证书生效时间*/
	@Excel(name = "证书生效时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "证书生效时间")
    private Date issuedTime;
	/**证书失效时间*/
	@Excel(name = "证书失效时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "证书失效时间")
    private Date expiryTime;
	/**用户类型*/
	@Excel(name = "用户类型", width = 15)
    @ApiModelProperty(value = "用户类型")
    private String consumerType;
	/**用户数量*/
	@Excel(name = "用户数量", width = 15)
    @ApiModelProperty(value = "用户数量")
    private Integer consumerAmount;

    /**可被允许的IP地址*/
    @Excel(name = "可被允许的IP地址", width = 15)
    @ApiModelProperty(value = "可被允许的IP地址")
    private String ipAddress;
    /**舊ip地址*/
    @Excel(name = "可被允许的MAC地址", width = 15)
    @ApiModelProperty(value = "可被允许的MAC地址")
    private String macAddress;
    /**舊ip地址*/
    @Excel(name = "可被允许的CPU序列号", width = 15)
    @ApiModelProperty(value = "可被允许的CPU序列号")
    private String cpuSerial;
    /**舊ip地址*/
    @Excel(name = "可被允许的主板序列号", width = 15)
    @ApiModelProperty(value = "可被允许的主板序列号")
    private String mainBoardSerial;

    /**舊ip地址*/
    @Excel(name = "舊ip地址", width = 15)
    @ApiModelProperty(value = "舊ip地址")
    private String oldIpAddress;


	/**描述信息*/
	@Excel(name = "描述信息", width = 15)
    @ApiModelProperty(value = "描述信息")
    private String description;
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
