package com.yaude.icloud.openstack.vo;

import com.yaude.icloud.openstack.entity.OsUserProject;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsUserProjectVo.java
 * @Description TODO
 * @createTime 2021年10月18日 15:30:00
 */
@Data
public class OsSecurityGroupVo{

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



}
