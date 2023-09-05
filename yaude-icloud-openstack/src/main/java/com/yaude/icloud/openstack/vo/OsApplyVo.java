package com.yaude.icloud.openstack.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsInstance;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsApplyVo.java
 * @Description TODO
 * @createTime 2021年09月24日 15:30:00
 */
@Data
public class OsApplyVo extends OsApply  {

    //實例類型名稱
   // private String flavorName;

    //鏡像名稱
    //private String imgName;

    //网络名稱
    //private String networkName;

    //审批意见
    private String optionsText;

    //审批是否通过
    private String optionsType;

    //剩余时间
    private String sectionTime;

    //审核类型
    private String applyType;

    //磁盘名称
    private String diskName;

    //磁盘id
    private String diskId;

    //磁盘描述
    private String diskrepresent;

    //磁盘大小
    private String size;

    //审核id
    private String optionId;

    //秘钥名称
    private String keyName;

    //私钥
    private String privateKey;

    //网络id
    private String floatNetworkId;

    //子网id
    private String subnetId;
}
