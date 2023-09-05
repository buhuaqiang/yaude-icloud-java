package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 填值規則
 * @Author: jeecg-boot
 * @Date: 2019-11-07
 * @Version: V1.0
 */
@Data
@TableName("sys_fill_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_fill_rule對象", description = "填值規則")
public class SysFillRule {

    /**
     * 主鍵ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主鍵ID")
    private java.lang.String id;
    /**
     * 規則名稱
     */
    @Excel(name = "規則名稱", width = 15)
    @ApiModelProperty(value = "規則名稱")
    private java.lang.String ruleName;
    /**
     * 規則Code
     */
    @Excel(name = "規則Code", width = 15)
    @ApiModelProperty(value = "規則Code")
    private java.lang.String ruleCode;
    /**
     * 規則實現類
     */
    @Excel(name = "規則實現類", width = 15)
    @ApiModelProperty(value = "規則實現類")
    private java.lang.String ruleClass;
    /**
     * 規則參數
     */
    @Excel(name = "規則參數", width = 15)
    @ApiModelProperty(value = "規則參數")
    private java.lang.String ruleParams;
    /**
     * 修改人
     */
    @Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
    /**
     * 修改時間
     */
    @Excel(name = "修改時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改時間")
    private java.util.Date updateTime;
    /**
     * 創建人
     */
    @Excel(name = "創建人", width = 15)
    @ApiModelProperty(value = "創建人")
    private java.lang.String createBy;
    /**
     * 創建時間
     */
    @Excel(name = "創建時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "創建時間")
    private java.util.Date createTime;
}
