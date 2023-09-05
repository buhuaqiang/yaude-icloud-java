package com.yaude.icloud.openstack.vo;

import com.yaude.icloud.openstack.entity.OsInstance;
import com.yaude.icloud.openstack.entity.OsUserProject;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsUserProjectVo.java
 * @Description TODO
 * @createTime 2021年10月07日 15:30:00
 */
@Data
public class OsUserProjectVo extends OsUserProject {

    //項目名稱
    private String projectName;

    //项目描述
    private String description;

    private Boolean enabled;

    //項目下的所有成員姓名
    private String projectUserNames;

    //項目下的所有成員ID
    private ArrayList projectUserIds;

    //項目下的所有管理員ID
    private ArrayList isAdminKeys;

    //用户名称
    private String username;


}
