package com.yaude.icloud.openstack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.FluentIterable;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.yaude.icloud.openstack.vo.OsSecurityGroupRuleVo;
import com.yaude.icloud.openstack.vo.OsSecurityGroupVo;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroup;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.SecGroupExtension;

import java.util.List;

/**
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
public interface IOsSecurityGroupService  {

    List<OsSecurityGroupVo> getSecurityGroupList(OsSecurityGroupVo osSecurityGroupVo);

    SecGroupExtension createSecurityGroup(OsSecurityGroupVo osSecurityGroupVo);

    SecGroupExtension updateSecurityGroup(OsSecurityGroupVo osSecurityGroupVo);

    ActionResponse deleteSecurityGroup(OsSecurityGroupVo osSecurityGroupVo);

    ActionResponse deleteSecurityGroupRule(OsSecurityGroupRuleVo osSecurityGroupRuleVo);

    ActionResponse deleteBatchSecurityGroupRule(String ids,String projectId);

    List<OsSecurityGroupRuleVo>  getSecurityGroupById(OsSecurityGroupVo osSecurityGroupVo);

    Rule createSecurityGroupRule(OsSecurityGroupRuleVo osSecurityGroupRuleVo);

}
