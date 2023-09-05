package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yaude.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: gateway路由管理
 * @Author: jeecg-boot
 * @Date:   2020-05-26
 * @Version: V1.0
 */
@Data
@TableName("sys_gateway_route")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_gateway_route對象", description="gateway路由管理")
public class SysGatewayRoute implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主鍵*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主鍵")
    private String id;

    /**routerKEy*/
    @ApiModelProperty(value = "路由ID")
    private String routerId;

    /**服務名*/
    @Excel(name = "服務名", width = 15)
    @ApiModelProperty(value = "服務名")
    private String name;

    /**服務地址*/
    @Excel(name = "服務地址", width = 15)
    @ApiModelProperty(value = "服務地址")
    private String uri;

    /**
     * 斷言配置
     */
    private String predicates;

    /**
     * 過濾配置
     */
    private String filters;

    /**是否忽略前綴0-否 1-是*/
    @Excel(name = "忽略前綴", width = 15)
    @ApiModelProperty(value = "忽略前綴")
    @Dict(dicCode = "yn")
    private Integer stripPrefix;

    /**是否重試0-否 1-是*/
    @Excel(name = "是否重試", width = 15)
    @ApiModelProperty(value = "是否重試")
    @Dict(dicCode = "yn")
    private Integer retryable;

    /**是否為保留數據:0-否 1-是*/
    @Excel(name = "保留數據", width = 15)
    @ApiModelProperty(value = "保留數據")
    @Dict(dicCode = "yn")
    private Integer persistable;

    /**是否在接口文檔中展示:0-否 1-是*/
    @Excel(name = "在接口文檔中展示", width = 15)
    @ApiModelProperty(value = "在接口文檔中展示")
    @Dict(dicCode = "yn")
    private Integer showApi;

    /**狀態 1有效 0無效*/
    @Excel(name = "狀態", width = 15)
    @ApiModelProperty(value = "狀態")
    @Dict(dicCode = "yn")
    private Integer status;

    /**創建人*/
    @ApiModelProperty(value = "創建人")
    private String createBy;
    /**創建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "創建日期")
    private Date createTime;
    /*    *//**更新人*//*
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    *//**更新日期*//*
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    *//**所屬部門*//*
    @ApiModelProperty(value = "所屬部門")
    private String sysOrgCode;*/
}
