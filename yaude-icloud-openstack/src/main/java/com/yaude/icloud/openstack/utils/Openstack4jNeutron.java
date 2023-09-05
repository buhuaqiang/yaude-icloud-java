package com.yaude.icloud.openstack.utils;

import com.yaude.common.util.SpringContextUtils;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeType;
import org.openstack4j.openstack.OSFactory;

import java.util.List;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName Openstack4jApi.java
 * @Description TODO
 * @createTime 2021年09月27日 16:51:00
 */
public class Openstack4jNeutron {
    private final OSClient.OSClientV3 os;

    public Openstack4jNeutron(){
        // 开始认证
        System.out.println("开始认证：");
         os  = OSFactory
                .builderV3()
                .endpoint("http://192.168.11.23/identity/v3/")
                .credentials("admin", "admin", Identifier.byName("Default"))
                .scopeToProject(Identifier.byName("admin"), Identifier.byName("Default"))
                .withConfig(
                        Config.newConfig()
                                .withConnectionTimeout(1000 * 60)
                                .withReadTimeout(1000 * 60 * 5))
                .authenticate();
    }

    public Openstack4jNeutron(Openstack4jEntity openstack4jEntity){
        // 开始认证
        System.out.println("开始认证：");
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        os  = OSFactory
                .builderV3()
                .endpoint(bean.getHostUrl())
                .credentials(bean.getUsername(), bean.getPassword(), Identifier.byName("Default"))
                .scopeToProject(Identifier.byId(openstack4jEntity.getProjectId()))
                .withConfig(
                        Config.newConfig()
                                .withConnectionTimeout(1000 * 60)
                                .withReadTimeout(1000 * 60 * 5))
                .authenticate();
    }

    public List<NetFloatingIP> getFloatingIps(){
        List<NetFloatingIP> list = (List<NetFloatingIP>) os.networking().floatingip().list();
        return list;
    }

    public ActionResponse addFloatingIp(String SeverID,String ipAddress){
        Server server = os.compute().servers().get(SeverID);
        ActionResponse actionResponse = os.compute().floatingIps().addFloatingIP(server, ipAddress);
        return actionResponse;
    }

    public ActionResponse removeFloatingIP(String SeverID,String ipAddress){
        Server server = os.compute().servers().get(SeverID);
        ActionResponse actionResponse = os.compute().floatingIps().removeFloatingIP(server, ipAddress);
        return actionResponse;
    }

    //創建安全組
    public SecGroupExtension createSecurityGroup(String name,String description){
        SecGroupExtension secGroupExtension = os.compute().securityGroups().create(name, description);
        return secGroupExtension;
    }


    //更新安全組
    public SecGroupExtension updateSecurityGroup(String securityGroupId,String name,String description){
        SecGroupExtension secGroupExtension = os.compute().securityGroups().update(securityGroupId,name, description);
        return secGroupExtension;
    }

    //刪除安全組
    public ActionResponse deleteSecurityGroup(String securityGroupId){
        ActionResponse delete = os.compute().securityGroups().delete(securityGroupId);
        return delete;
    }

    //刪除安全組规则
    public ActionResponse deleteSecurityGroupRule(String securityGroupRuleId){
        ActionResponse delete = os.compute().securityGroups().deleteRule(securityGroupRuleId);
        return delete;
    }

    //創建安全組规则
    public SecGroupExtension.Rule  createSecurityGroupRule(String securityGroupId){
        SecGroupExtension.Rule rule = os.compute().securityGroups().createRule(Builders.secGroupRule()
                .parentGroupId(securityGroupId)//当前安全组ID
                .protocol(IPProtocol.TCP)
                .cidr("0.0.0.0/0")
                .groupId(securityGroupId)//远程安全组ID
                .range(91, 91).build());
        return rule;
    }


    public void test(){


        List<? extends SecGroupExtension> secList = os.compute().securityGroups().list();
        secList.stream().forEach(l ->{
            System.out.println(l.toString());
        });
        /*List<? extends Network> netList = os.networking().network().list();
        netList.stream().forEach(l ->{
            System.out.println(l.toString());
        });*/
       /* List<? extends NetFloatingIP> list = os.networking().floatingip().list();
        list.stream().forEach(l ->{
            System.out.println(l.toString());
        });
        System.out.println(list.size());*/
    }


    public static void main(String[] args) {
        //Openstack4jNova openstack4JNova = new Openstack4jNova();
        //Serializable aa = openstack4jApi.upServer("7d6cfdb0-008a-4e7d-ba7e-71efed1ac3f6","3");
        //System.out.println(openstack4JNova.getVNCURL("990752a9-d60a-409b-bb80-64af31a08c8a"));
        //System.out.println( openstack4JNova.createSnapshot("990752a9-d60a-409b-bb80-64af31a08c8a","test"));
        /*Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId("673c2c0f0d714602b5767ddbb399f6db");
        Openstack4jNeutron openstack4JNova = new Openstack4jNeutron(openstack4jEntity);
        openstack4JNova.test();*/
         /*OSClient.OSClientV3 os  = OSFactory
                .builderV3()
                .endpoint("http://192.168.2.8:5000/v3/")
                .credentials("admin", "admin", Identifier.byName("Default"))
                .scopeToProject(Identifier.byName("admin"), Identifier.byName("Default"))
                .withConfig(
                        Config.newConfig()
                                .withConnectionTimeout(1000 * 60)
                                .withReadTimeout(1000 * 60 * 5))
                .authenticate();



       List<? extends SecGroupExtension> secList = os.compute().securityGroups().list();
        secList.stream().forEach(l ->{
            System.out.println(l.toString());
        });
*/

        /*SecurityGroup group = (SecurityGroup) os.compute().securityGroups().get("23e92ecc-c95d-4621-ba2b-916770179059");
        System.out.println(group);*/

        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron();
        openstack4jNeutron.createSecurityGroupRule("23e92ecc-c95d-4621-ba2b-916770179059");
    }

}
