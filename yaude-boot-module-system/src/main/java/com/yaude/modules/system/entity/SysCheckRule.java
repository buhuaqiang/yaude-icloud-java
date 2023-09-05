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

import java.util.Date;

/**
 * @Description: 編碼校驗規則
 * @Author: jeecg-boot
 * @Date: 2020-02-04
 * @Version: V1.0
 */
@Data
@TableName("sys_check_rule")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_check_rule對象", description = "編碼校驗規則")
public class SysCheckRule {

    /**
     * 主鍵id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主鍵id")
    private String id;
    /**
     * 規則名稱
     */
    @Excel(name = "規則名稱", width = 15)
    @ApiModelProperty(value = "規則名稱")
    private String ruleName;
    /**
     * 規則Code
     */
    @Excel(name = "規則Code", width = 15)
    @ApiModelProperty(value = "規則Code")
    private String ruleCode;
    /**
     * 規則JSON
     */
    @Excel(name = "規則JSON", width = 15)
    @ApiModelProperty(value = "規則JSON")
    private String ruleJson;
    /**
     * 規則描述
     */
    @Excel(name = "規則描述", width = 15)
    @ApiModelProperty(value = "規則描述")
    private String ruleDescription;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新時間
     */
    @Excel(name = "更新時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新時間")
    private Date updateTime;
    /**
     * 創建人
     */
    @Excel(name = "創建人", width = 15)
    @ApiModelProperty(value = "創建人")
    private String createBy;
    /**
     * 創建時間
     */
    @Excel(name = "創建時間", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "創建時間")
    private Date createTime;
}
