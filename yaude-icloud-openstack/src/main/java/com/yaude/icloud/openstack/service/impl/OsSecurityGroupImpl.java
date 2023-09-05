package com.yaude.icloud.openstack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.yaude.icloud.openstack.mapper.OsUserProjectMapper;
import com.yaude.icloud.openstack.service.IOsSecurityGroupService;
import com.yaude.icloud.openstack.service.IOsUserProjectService;
import com.yaude.icloud.openstack.utils.*;
import com.yaude.icloud.openstack.vo.OsSecurityGroupRuleVo;
import com.yaude.icloud.openstack.vo.OsSecurityGroupVo;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.openstack.neutron.v2.domain.*;
import org.jclouds.profitbricks.domain.Firewall;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.identity.v3.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Service
public class OsSecurityGroupImpl implements IOsSecurityGroupService {


    @Override
    public List<OsSecurityGroupVo>  getSecurityGroupList(OsSecurityGroupVo osSecurityGroupVo) {
        List<OsSecurityGroupVo> osSecurityGroupVoList = new ArrayList<>();
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupVo.getProjectId());
        JCloudsNeutron jCloudsNeutron = new JCloudsNeutron(openstack4jEntity);
        FluentIterable<SecurityGroup> listSecurityGroup = jCloudsNeutron.getListSecurityGroup();
        OsSecurityGroupVo osg = null;
        for (SecurityGroup sg:listSecurityGroup) {
            if(sg.getTenantId().equals(osSecurityGroupVo.getProjectId())){
                osg = new OsSecurityGroupVo();
                osg.setProjectId(sg.getTenantId());
                osg.setName(sg.getName());
                osg.setSecurityGroupId(sg.getId());
                osg.setDescription(sg.getDescription());
                osSecurityGroupVoList.add(osg);
            }
        }
        //根據項目名稱篩選                                                                                            名稱篩選
        if(StringUtils.isNotEmpty(osSecurityGroupVo.getName())){
            osSecurityGroupVoList =  osSecurityGroupVoList.stream().filter(se -> se.getName().equals(osSecurityGroupVo.getName())).collect(Collectors.toList());
        }
        return osSecurityGroupVoList;
    }

    @Override
    public SecGroupExtension createSecurityGroup(OsSecurityGroupVo osSecurityGroupVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        SecGroupExtension securityGroup = openstack4jNeutron.createSecurityGroup(osSecurityGroupVo.getName(), osSecurityGroupVo.getDescription());
        return securityGroup;
    }

    @Override
    public SecGroupExtension updateSecurityGroup(OsSecurityGroupVo osSecurityGroupVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        SecGroupExtension securityGroup = openstack4jNeutron.updateSecurityGroup(osSecurityGroupVo.getSecurityGroupId(),osSecurityGroupVo.getName(), osSecurityGroupVo.getDescription());
        return securityGroup;
    }

    @Override
    public ActionResponse deleteSecurityGroup(OsSecurityGroupVo osSecurityGroupVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        ActionResponse actionResponse = openstack4jNeutron.deleteSecurityGroup(osSecurityGroupVo.getSecurityGroupId());
        return actionResponse;
    }

    @Override
    public ActionResponse deleteSecurityGroupRule(OsSecurityGroupRuleVo osSecurityGroupRuleVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupRuleVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        ActionResponse actionResponse = openstack4jNeutron.deleteSecurityGroupRule(osSecurityGroupRuleVo.getId());
        return actionResponse;
    }

    @Override
    public ActionResponse deleteBatchSecurityGroupRule(String ids, String projectId) {
        List<String> strings = Arrays.asList(ids.split(","));
        ActionResponse actionResponse = null;
        if(strings.size()>0){
            Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
            openstack4jEntity.setProjectId(projectId);
            Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
            for (String id:strings) {
                actionResponse = openstack4jNeutron.deleteSecurityGroupRule(id);
            }

        }
        return actionResponse;
    }

    @Override
    public List<OsSecurityGroupRuleVo>  getSecurityGroupById(OsSecurityGroupVo osSecurityGroupVo) {
        List<OsSecurityGroupRuleVo> osSecurityGroupRuleVos =new ArrayList<>();
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupVo.getProjectId());
        JCloudsNeutron jCloudsNeutron = new JCloudsNeutron(openstack4jEntity);
        SecurityGroup securityGroupById = jCloudsNeutron.getSecurityGroupById(osSecurityGroupVo.getSecurityGroupId());
        ImmutableList<Rule> rules = securityGroupById.getRules();
        OsSecurityGroupRuleVo vo = null;
        for (Rule r:rules) {
            vo = new OsSecurityGroupRuleVo();
            vo.setId(r.getId());
            String direction = "";
            if(r.getDirection().toString().equals("egress")){
                direction = "出口";
            }else if(r.getDirection().toString().equals("ingress")){
                direction = "入口";
            }
            //方向
            vo.setDirection(direction);
            //網絡類型
            vo.setEtherType(r.getEthertype().toString());
            //IP協議'
            vo.setProtocol(r.getProtocol()==null?"任何":r.getProtocol().toString());
            //端口最小值
            int min = r.getPortRangeMin()==null?0:r.getPortRangeMin();
            vo.setPortRangeMin(r.getPortRangeMin());
            //端口最大值
            int max = r.getPortRangeMax()==null?0:r.getPortRangeMax();
            vo.setPortRangeMax(r.getPortRangeMax());
            //端口范围
            String portRange = "";
            if(max<=0||min<=0){
                portRange = "任何";
            }else if(max>min){
                portRange = min+" - "+max;
            }else if(max==min){
                portRange = max+PortNameUtil.getNameByPort(max);
            }
            vo.setPortRange(portRange);
            //远程IP前缀
            vo.setRemoteIpPrefix(r.getRemoteIpPrefix());
            //安全組ID
            vo.setRemoteGroupId(r.getRemoteGroupId());
            vo.setSecurityGroupName(osSecurityGroupVo.getName());
            //描述
            vo.setDescription(r.getDirection().toString());
            osSecurityGroupRuleVos.add(vo);
        }

        osSecurityGroupRuleVos = osSecurityGroupRuleVos.stream().sorted(Comparator.comparing(OsSecurityGroupRuleVo::getDirection)).collect(Collectors.toList());
        return osSecurityGroupRuleVos;
    }

    @Override
    public Rule createSecurityGroupRule(OsSecurityGroupRuleVo osSecurityGroupRuleVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osSecurityGroupRuleVo.getProjectId());
        JCloudsNeutron jCloudsNeutron = new JCloudsNeutron(openstack4jEntity);

        if(StringUtils.isEmpty(osSecurityGroupRuleVo.getEtherType())){
            osSecurityGroupRuleVo.setEtherType("IPv4");
        }

        //规则类型
        String ruleProtocol = "";
        if(osSecurityGroupRuleVo.getSecurityGroupRule().equals("TCP")){
            ruleProtocol = "TCP";
        }else if(osSecurityGroupRuleVo.getSecurityGroupRule().equals("UDP")){
            ruleProtocol = "UDP";
        }else if(osSecurityGroupRuleVo.getSecurityGroupRule().equals("ICMP")){
            ruleProtocol = "ICMP";
        }

        if(osSecurityGroupRuleVo.getOpenPortType().equals("port")){//指定端口
            osSecurityGroupRuleVo.setPortRangeMax(osSecurityGroupRuleVo.getPort());
            osSecurityGroupRuleVo.setPortRangeMin(osSecurityGroupRuleVo.getPort());
        }else if(osSecurityGroupRuleVo.getOpenPortType().equals("portRange")){//端口范围

        }else if(osSecurityGroupRuleVo.getOpenPortType().equals("allPort")){//全部端口

        }

        if(osSecurityGroupRuleVo.getRemoteType() == 2){ //安全组
            osSecurityGroupRuleVo.setCidr(null);  //CIDR地址设为null
        }

        Rule.CreateRule createRule = Rule.CreateRule.createBuilder(RuleDirection.fromValue(osSecurityGroupRuleVo.getDirection())
                ,osSecurityGroupRuleVo.getSecurityGroupId())
                .ethertype(RuleEthertype.fromValue(osSecurityGroupRuleVo.getEtherType()))
                .protocol(RuleProtocol.fromValue(ruleProtocol))
                .portRangeMax(osSecurityGroupRuleVo.getPortRangeMax())
                .portRangeMin(osSecurityGroupRuleVo.getPortRangeMin())
                .remoteIpPrefix(osSecurityGroupRuleVo.getCidr())
                .remoteGroupId(osSecurityGroupRuleVo.getRemoteGroupId())
                .build();
        Rule securityGroupRule = jCloudsNeutron.createSecurityGroupRule(createRule);
        return securityGroupRule;
    }
}
