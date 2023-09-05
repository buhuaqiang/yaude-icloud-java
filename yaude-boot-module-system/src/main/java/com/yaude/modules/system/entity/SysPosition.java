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

/**
 * @Description: 職務表
 * @Author: jeecg-boot
 * @Date: 2019-09-19
 * @Version: V1.0
 */
@Data
@TableName("sys_position")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_position對象", description = "職務表")
public class SysPosition {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
    /**
     * 職務編碼
     */
    @Excel(name = "職務編碼", width = 15)
    @ApiModelProperty(value = "職務編碼")
    private java.lang.String code;
    /**
     * 職務名稱
     */
    @Excel(name = "職務名稱", width = 15)
    @ApiModelProperty(value = "職務名稱")
    private java.lang.String name;
    /**
     * 職級
     */
    @Excel(name = "職級", width = 15,dicCode ="position_rank")
    @ApiModelProperty(value = "職級")
    @Dict(dicCode = "position_rank")
    private java.lang.String postRank;
    /**
     * 公司id
     */
    @Excel(name = "公司id", width = 15)
    @ApiModelProperty(value = "公司id")
    private java.lang.String companyId;
    /**
     * 創建人
     */
    @ApiModelProperty(value = "創建人")
    private java.lang.String createBy;
    /**
     * 創建時間
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "創建時間")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
    /**
     * 修改時間
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改時間")
    private java.util.Date updateTime;
    /**
     * 組織機構編碼
     */
    @Excel(name = "組織機構編碼", width = 15)
    @ApiModelProperty(value = "組織機構編碼")
    private java.lang.String sysOrgCode;
}
