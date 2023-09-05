package com.yaude.icloud.openstack.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import com.yaude.common.util.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.collect.PagedIterable;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.*;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
@Author ERIC
@Description openstack 網絡API支持類，
@Date 2021/9/22 16:58
*/


public class JCloudsNeutron {

    private final NeutronApi neutronApi;
    private final Set<String> regions;

    public static void main(String[] args) throws IOException {
        JCloudsNeutron jcloudsNeutron = new JCloudsNeutron();

        try {
            FluentIterable<SecurityGroup> listSecurityGroup = jcloudsNeutron.getListSecurityGroup();
            System.out.println("分割线-----------------------------------");
            listSecurityGroup.stream().forEach(l ->{
                System.out.println(l.toString());
            });
            jcloudsNeutron.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jcloudsNeutron.close();
        }
    }

    public JCloudsNeutron() {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        // Please refer to 'Keystone v2-v3 authentication' section for complete authentication use case
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        String provider = "openstack-neutron";
        String identity = "Default:"+bean.getUsername(); // tenantName:userName
        String credential = bean.getPassword();
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:admin");
        neutronApi = ContextBuilder.newBuilder(provider)
                .endpoint(bean.getHostUrl())
                .credentials(identity, credential)
                .overrides(overrides)
                .modules(modules)
                .buildApi(NeutronApi.class);
        regions = neutronApi.getConfiguredRegions();
    }


    public JCloudsNeutron(Openstack4jEntity openstack4jEntity) {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        // Please refer to 'Keystone v2-v3 authentication' section for complete authentication use case
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        String provider = "openstack-neutron";
        String identity = "Default:"+bean.getUsername(); // tenantName:userName
        String credential = bean.getPassword();
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "projectId:"+openstack4jEntity.getProjectId());
        neutronApi = ContextBuilder.newBuilder(provider)
                .endpoint(bean.getHostUrl())
                .credentials(identity, credential)
                .overrides(overrides)
                .modules(modules)
                .buildApi(NeutronApi.class);
        regions = neutronApi.getConfiguredRegions();
    }
    



    /**
     * 返回 可用網絡組
     */
    public FluentIterable<Network>  getListNetwork(String projectId) {
        for (String region : regions) {
            NetworkApi networkApi = neutronApi.getNetworkApi(region);
            PaginationOptions options = new PaginationOptions();
            Multimap<String, String> queryParams = ArrayListMultimap.create();
            queryParams.put("status","ACTIVE");
            options.queryParameters(queryParams);
            FluentIterable<Network>  networks =   networkApi.list(options);
            for(Network network:networks){
                System.out.println("network=="+network.getShared()+"  network name "+network.getName() +" network project id=" + network);
                if(!network.getTenantId().equalsIgnoreCase(projectId)){
                    continue;
                }


            }
            return networks;
        }
        return null;
    }


    /**
     * 返回安全組列表
     */
    public FluentIterable<SecurityGroup>  getListSecurityGroup() {
        for (String region : regions) {
            SecurityGroupApi securityGroupApi = neutronApi.getSecurityGroupApi(region);
            FluentIterable<SecurityGroup>  securityGroup =   securityGroupApi.listSecurityGroups().concat();
            return securityGroup;
        }
        return null;
    }

    /**
     * 根據ID返回安全組
     */
    public SecurityGroup  getSecurityGroupById(String securityGroupId) {
        for (String region : regions) {
            SecurityGroupApi securityGroupApi = neutronApi.getSecurityGroupApi(region);
            SecurityGroup securityGroup = securityGroupApi.getSecurityGroup(securityGroupId);
            return securityGroup;
        }
        return null;
    }

    /**
     * 创建安全組规则
     */
    public Rule  createSecurityGroupRule(Rule.CreateRule createRule) {
        for (String region : regions) {
            SecurityGroupApi securityGroupApi = neutronApi.getSecurityGroupApi(region);
            Rule rule = securityGroupApi.create(createRule);
            return rule;
        }
        return null;
    }



    public void close() throws IOException {
        Closeables.close(neutronApi, true);
    }
}