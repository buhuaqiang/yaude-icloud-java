package com.yaude.icloud.openstack.vo;

import lombok.Data;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsUserProjectVo.java
 * @Description TODO
 * @createTime 2021年10月18日 15:30:00
 */
@Data
public class OsSecurityGroupRuleVo {

    private String id;
    //安全组名
    private String securityGroupName;
    //方向
    private String direction;
    //網絡類型
    private String etherType;
    //IP協議'
    private String protocol;

    private Integer port;
    //端口最小值
    private Integer portRangeMin;
    //端口最大值
    private Integer portRangeMax;
    //端口范围
    private String portRange;
   //远程IP前缀
    private String remoteIpPrefix;
    //安全組ID
    private String remoteGroupId;

    //安全组名称
    private String name;

    //安全组ID
    private String securityGroupId;

    //描述
    private String description;

    //項目名稱
    private String projectId;

    //項目名稱
    private String projectName;

    //规则
    private String securityGroupRule;

    //端口管控方式
    private String  openPortType;

    //远程管控方式
    private Integer  remoteType;

    //CIDR地址
    private String cidr;



}
