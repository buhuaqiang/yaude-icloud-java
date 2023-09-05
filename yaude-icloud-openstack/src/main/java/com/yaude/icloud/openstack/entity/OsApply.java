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
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
@Data
@TableName("os_apply")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="os_apply对象", description="申請明細檔")
public class OsApply implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**實例名稱*/
	@Excel(name = "實例名稱", width = 15)
    @ApiModelProperty(value = "實例名稱")
    private String instanceName;
	/**申請狀態*/
	@Excel(name = "申請狀態", width = 15)
    @ApiModelProperty(value = "申請狀態")
    @Dict(dicCode = "options")
    private String options;
	/**狀態*/
	@Excel(name = "狀態", width = 15)
    @ApiModelProperty(value = "狀態")
    @Dict(dicCode = "options_status")
    private String status;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String represent;
	/**鏡像id*/
	@Excel(name = "鏡像id", width = 15)
    @ApiModelProperty(value = "鏡像id")
    private String imgId;
    /**鏡像名称*/
    @Excel(name = "鏡像名称", width = 15)
    @ApiModelProperty(value = "鏡像名称")
    private String imgName;
	/**刪除實例時是否刪除卷*/
	@Excel(name = "刪除實例時是否刪除卷", width = 15)
    @ApiModelProperty(value = "刪除實例時是否刪除卷")
    @Dict(dicCode = "is_delete")
    private String isDelete;
	/**實例類型id*/
	@Excel(name = "實例類型id", width = 15)
    @ApiModelProperty(value = "實例類型id")
    private String flavorId;
    /**實例類型名称*/
    @Excel(name = "實例類型名称", width = 15)
    @ApiModelProperty(value = "實例類型名称")
    private String flavorName;
	/**運行狀態*/
	@Excel(name = "運行狀態", width = 15)
    @ApiModelProperty(value = "運行狀態")
    @Dict(dicCode = "run_status")
    private String runStatus;
	/**安全組*/
	@Excel(name = "安全組", width = 15)
    @ApiModelProperty(value = "安全組")
    private String securityName;
	/**網絡*/
	@Excel(name = "網絡", width = 15)
    @ApiModelProperty(value = "網絡")
    private String networkId;
    /**網絡名称*/
    @Excel(name = "網絡名称", width = 15)
    @ApiModelProperty(value = "網絡名称")
    private String networkName;

    /**项目名称*/
    @Excel(name = "项目名称", width = 15)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**项目id*/
    @Excel(name = "项目id", width = 15)
    @ApiModelProperty(value = "项目id")
    private String projectId;

    /**起始日期*/
    @Excel(name = "起始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "起始日期")
    private Date startTime;

    /**结束日期*/
    @Excel(name = "结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endTime;

    /**vmid*/
    @Excel(name = "vmid", width = 15)
    @ApiModelProperty(value = "vmid")
    private String vmId;

    /**秘钥id*/
    @Excel(name = "keypairsId", width = 15)
    @ApiModelProperty(value = "keypairsId")
    private String keypairsId;

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
