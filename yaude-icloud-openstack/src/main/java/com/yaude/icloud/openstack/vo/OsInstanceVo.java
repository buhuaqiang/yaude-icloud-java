package com.yaude.icloud.openstack.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yaude.icloud.openstack.entity.OsInstance;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsInstanceVo.java
 * @Description TODO
 * @createTime 2021年09月24日 15:30:00
 */
@Data
public class OsInstanceVo  extends OsInstance {

    //實例類型名稱
    private String flavorName;

    //鏡像名稱
    private String imgName;

    //配置詳情
    private String configureInfo;

    //IP地址
    private  String ipAddress;

    //所屬項目ID
    private String projectId;
    //所屬項目名稱
    private String projectName;

    //運行時辰
    private  String runTime;

    private Integer disk;

    private String diskText;

    private  Integer ram;

    private String ramText;

    private  Integer cpu;

    //待连接卷ID
    private String connectVolumeId;

    //正在使用的卷ID （分離用）
    private String  inUseVolumeId ;

    //浮動Ip id
    private String floatingIpId;

    //浮動Ip
    private String floatingIp;

    //查询浮动IP状态
    private String floatingIpStatus;

    /**起始日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTime;

    /**结束日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;
}
