package com.yaude.icloud.openstack.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName Openstack4jEntity.java
 * @Description TODO
 * @createTime 2021年10月08日 16:25:00
 */
@Data
@Component
public class Openstack4jEntity {

    @Value(value ="${openstack.host.url}")
    private String hostUrl;
    //项目ID
    private String projectId;
    //项目名称
    private String projectName;

    @Value(value ="${openstack.default.username}")
    private String username;
    @Value(value ="${openstack.default.password}")
    private String password;
}
