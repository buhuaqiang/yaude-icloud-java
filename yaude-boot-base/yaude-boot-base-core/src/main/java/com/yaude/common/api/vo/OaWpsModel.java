package com.yaude.common.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 文檔
 * @Author: jeecg-boot
 * @Date: 2020-06-09
 * @Version: V1.0
 */
@Data
@TableName("oa_wps_file")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "oa_wps_file對象", description = "文檔")
public class OaWpsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**
     * name
     */
    @Excel(name = "name", width = 15)
    @ApiModelProperty(value = "name")
    private String name;
    /**
     * version
     */
    @Excel(name = "version", width = 15)
    @ApiModelProperty(value = "version")
    private Integer version;
    /**
     * size
     */
    @Excel(name = "size", width = 15)
    @ApiModelProperty(value = "size")
    private Integer size;
    /**
     * downloadUrl
     */
    @Excel(name = "downloadUrl", width = 15)
    @ApiModelProperty(value = "downloadUrl")
    private String downloadUrl;
    /**
     * deleted
     */
    @Excel(name = "deleted", width = 15)
    @ApiModelProperty(value = "deleted")
    private String deleted;
    /**
     * canDelete
     */
    @Excel(name = "canDelete", width = 15)
    @ApiModelProperty(value = "canDelete")
    private String canDelete;
    /**
     * 創建人
     */
    @ApiModelProperty(value = "創建人")
    private String createBy;
    /**
     * 創建時間
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "創建時間")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新時間
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新時間")
    private Date updateTime;
    /**
     * 組織機構編碼
     */
    @ApiModelProperty(value = "組織機構編碼")
    private String sysOrgCode;

    @TableField(exist = false)
    private String userId;
}
