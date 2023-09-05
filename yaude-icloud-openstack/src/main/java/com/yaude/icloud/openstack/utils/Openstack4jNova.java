package com.yaude.icloud.openstack.utils;

import com.yaude.common.util.SpringContextUtils;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.openstack4j.model.storage.block.VolumeType;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName Openstack4jApi.java
 * @Description TODO
 * @createTime 2021年09月27日 16:51:00
 */
public class Openstack4jNova {
    private final OSClient.OSClientV3 os;

    public Openstack4jNova(){
        //System.out.println("haha"+ SpringContextUtils.getApplicationContext().getEnvironment().getProperty("server.port"));
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

    public Openstack4jNova(Openstack4jEntity openstack4jEntity){
        // 开始认证
        System.out.println("开始认证："+openstack4jEntity.getProjectId());
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        System.out.println("主机IP："+ bean.getHostUrl());
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


    //獲取VNC地址
    public  String getVNCURL(String SeverID) {
        return os.compute().servers().getVNCConsole(SeverID, null).getURL();
    }

    //创建快照
    public String  createSnapshot(String SeverID,String imgName) {
        String imageId = os.compute().servers().createSnapshot(SeverID, imgName);
        return imageId;
    }

    /**
     * 刪除實例
     * @param ServerID
     */
    public void delete(String ServerID){
        os.compute().servers().delete(ServerID);
    }

    /**
     * 獲取server
     * @param ServerID
     * @return
     */
    public  Server getServerById(String ServerID){
        return os.compute().servers().get(ServerID);
    }

    //更新服务器
    public ActionResponse updateServer(String serverID, String flavorId){
        return os.compute().servers().resize(serverID, flavorId);
    }
    //确认更新
    public ActionResponse confirm(String serverID){
        return os.compute().servers().confirmResize(serverID);
    }

    /**
     * 獲取flavor
     * @return
     */
    public Flavor getFlavorById(String flavorId){
        return  os.compute().flavors().get(flavorId);
    }
    /**
     * 獲取flavors
     * @return
     */
    public List<Flavor> getFlavors(){
        return (List<Flavor>) os.compute().flavors().list();
    }


    /**
     * 獲取image
     * @return
     */
    public Image getImageById(String imageId){
        return  os.compute().images().get(imageId);
    }
    /**
     * 獲取images
     * @return
     */
    public List<Image> getImages(){
        return (List<Image>) os.compute().images().list();
    }

    /**
     * 獲取servers
     * @return
     */
    public List<Server> getServers(){
        return (List<Server>) os.compute().servers().list();
    }




    public void test(){
       /* List<? extends Server> list = os.compute().servers().list();
        list.stream().forEach(l ->{
            System.out.println(l.toString());
        });*/

        /*List<? extends Volume> volumes = os.blockStorage().volumes().list();
        volumes.stream().forEach(l ->{
            System.out.println(l.toString());
        });*/

        /*List<? extends SimpleTenantUsage> tenantUsages = os.compute().quotaSets().listTenantUsages();
        tenantUsages.stream().forEach(l ->{
            System.out.println(l.toString());
        });

        Limits limits = os.compute().quotaSets().limits();
        System.out.println(limits.toString());*/

        SimpleTenantUsage usage2 = os.compute().quotaSets().getTenantUsage("04987b0c4ad54494a79f0c41a7fb6c02","2020-10-10T00:00:00","2020-10-11T00:00:00");
        System.out.println(usage2);
    }

    /**
     * 獲取磁盘类型
     * @return
     */
    public List<VolumeType> getVolumeType(){
        return (List<VolumeType>) os.blockStorage().volumes().listVolumeTypes();
    }

    /**
     * 獲取卷列表
     * @return
     */
    public List<Volume> getVolumes(){
        return (List<Volume>) os.blockStorage().volumes().list();
    }
    //根据id获取卷
    public Volume getVolume(String volumeId){
        return os.blockStorage().volumes().get(volumeId);
    }

    /**
     * 獲取快照
     * @return
     */
    public List<? extends VolumeSnapshot> getVolumeSnapshots(){
        return os.blockStorage().snapshots().list();
    }


    //连接卷
    public VolumeAttachment  connectVolume(String SeverID,String volumeId) {
        VolumeAttachment volumeAttachment = os.compute().servers().attachVolume(SeverID, volumeId, "/dev/vda");
        return volumeAttachment;
    }


    //分離卷
    public ActionResponse  detachVolume(String SeverID,String volumeId) {
        ActionResponse actionResponse = os.compute().servers().detachVolume(SeverID, volumeId);
        return actionResponse;
    }

    //创建卷
    public Volume createVolume(String volumeName,String description,int size){
        Volume v = os.blockStorage().volumes()
                .create(Builders.volume()
                        .name(volumeName)
                        .description(description)
                        .size(size)
                        .build()
                );
        return  v;
    }
    //更新卷大小
    public ActionResponse upVolume(String volumeid,int size){
        return os.blockStorage().volumes().extend(volumeid,size);
    }

    /**
     * 根据项目id獲取项目
     * @return
     */
    public Project getProjectById(String projectId){
        return  os.identity().projects().get(projectId);
    }
    /**
     * 获取网络
     * @return
     */
    public List<? extends Network> networks(){
        return os.networking().network().list();
    }
    /**
     * 根据网络id获取网络
     * @return
     */
    public Network network(String networkId){
        return os.networking().network().get(networkId);
    }
    /**
     * 获取子网
     * @return
     */
    public List<? extends Subnet> subnets(){
        return os.networking().subnet().list();
    }
    /**
     * 根据子网id获取子网
     * @return
     */
    public Subnet  subnet(String subnetId){
        return os.networking().subnet().get(subnetId);
    }
    /**
     * 获取浮动ip
     * @return
     */
    public String floatingIP(String networkId,String subnetId){
        HttpTest httpTest = new HttpTest();
        String tokenId = os.getToken().getId();
        String floatIp = httpTest.test2(tokenId,networkId,subnetId);
        return floatIp;
    }

    /**
     * 创建vm并获取vmid
     * @return
     */
    public String vmId(String networkId,String securityName,String imgId,String size,String isDelete,String instanceName,String flavorId){
        HttpTest httpTest = new HttpTest();
        String tokenId = os.getToken().getId();
        String vmId = httpTest.server(tokenId,networkId,securityName,imgId,size,isDelete,instanceName,flavorId);
        return vmId;
    }

    /**
     * 查询系统限制
     * @return
     */
    public Limits getProjectLimits(){
        Limits limits = os.compute().quotaSets().limits();
        return limits;
    }

    public SimpleTenantUsage getTenantUsage(String projectId,String startTime,String endTime){
        SimpleTenantUsage tenantUsage = os.compute().quotaSets().getTenantUsage(projectId, startTime, endTime);
        return  tenantUsage;
    }

    public static void main(String[] args) {
        //Openstack4jNova openstack4JNova = new Openstack4jNova();
        //Serializable aa = openstack4jApi.upServer("7d6cfdb0-008a-4e7d-ba7e-71efed1ac3f6","3");
        //System.out.println(openstack4JNova.getVNCURL("990752a9-d60a-409b-bb80-64af31a08c8a"));
        //System.out.println( openstack4JNova.createSnapshot("990752a9-d60a-409b-bb80-64af31a08c8a","test"));

        Openstack4jNova openstack4JNova = new Openstack4jNova();
        openstack4JNova.test();
        /*List<Volume> VolumeSnapshots =  openstack4JNova.getVolumes();
        VolumeSnapshots.stream().forEach(f->{
            System.out.println(f.toString());
        });*/

    }

}
