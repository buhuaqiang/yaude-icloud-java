package com.yaude.icloud.openstack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.icloud.openstack.entity.*;
import com.yaude.icloud.openstack.mapper.OsInstanceMapper;
import com.yaude.icloud.openstack.service.*;
import com.yaude.icloud.openstack.utils.JCloudsNova;
import com.yaude.icloud.openstack.utils.Openstack4jEntity;
import com.yaude.icloud.openstack.utils.Openstack4jNeutron;
import com.yaude.icloud.openstack.utils.Openstack4jNova;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jclouds.openstack.nova.v2_0.domain.*;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Addresses;
import org.openstack4j.model.compute.Limits;
import org.openstack4j.model.network.FloatingIP;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.storage.block.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
@Service
public class OsInstanceServiceImpl extends ServiceImpl<OsInstanceMapper, OsInstance> implements IOsInstanceService {

    @Autowired
    private IOsUserProjectService iOsUserProjectService;

    @Autowired
    private IOsApplyService iOsApplyService;
    @Autowired
    private IOsApplyDiskService iOsApplyDiskService;
    @Autowired
    private IOsApplyFloatipService iOsApplyFloatipService;

    @Override
    public List<OsInstanceVo> getInstanceList(OsInstanceVo osInstance) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //获取用户关联的项目
        QueryWrapper<OsUserProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        List<OsUserProject> osUserProjects = iOsUserProjectService.list(queryWrapper);

        //获取用户关联的实例
        QueryWrapper<OsApply> osApplyQueryWrapper = new QueryWrapper<>();
        osApplyQueryWrapper.eq("create_by",user.getUsername());
        osApplyQueryWrapper.eq("status","1");
        Date date = new Date();
        osApplyQueryWrapper.le("start_time",date); //小于
        osApplyQueryWrapper.ge("end_time",date); //大于
        osApplyQueryWrapper.isNotNull("vm_id");
        List<OsApply> osAppleList = iOsApplyService.list(osApplyQueryWrapper);
        List<String> InstanceIds = new ArrayList<>();
        for (int i = 0; i < osAppleList.size(); i++) {
            InstanceIds.add(osAppleList.get(i).getVmId());
        }
        Openstack4jEntity openstack4jEntity = null;
        Openstack4jNova openstack4JNova = null;
        List<OsInstanceVo>  osInstanceVos = new ArrayList<>();
        OsInstanceVo osInstanceVo = null;
        String imageId = null;
        String flavorId = null;
        List<org.openstack4j.model.compute.Image> images;
        List<org.openstack4j.model.compute.Server> servers;
        List<org.openstack4j.model.compute.Flavor> flavors;
        org.openstack4j.model.compute.Server server = null;
        for (int w = 0; w < osUserProjects.size(); w++) {
            openstack4jEntity = new Openstack4jEntity();
            openstack4jEntity.setProjectId(osUserProjects.get(w).getProjectId());
            try {
                openstack4JNova= new Openstack4jNova(openstack4jEntity);
            }catch (AuthenticationException e){
                log.debug("項目"+osUserProjects.get(w).getProjectId()+",認證失敗"+e);
                continue;
            }

            servers = openstack4JNova.getServers();
            images = openstack4JNova.getImages();
            flavors = openstack4JNova.getFlavors();
            for (int i = 0; i < servers.size(); i++) {
                server = servers.get(i);
                osInstanceVo = new OsInstanceVo();

                if(osUserProjects.get(w).getIsAdmin()==0) { //非管理員需要進行篩選
                    //根據申请状态篩選
                    if (!(InstanceIds.size() > 0 && InstanceIds.contains(server.getId()))) {
                        continue;
                    }
                }

                //根據實例名稱篩選
                if(StringUtils.isNotEmpty(osInstance.getInstanceName())){
                    if(server.getName().indexOf(osInstance.getInstanceName())<0){
                        continue;
                    }
                }

                osInstanceVo.setId(server.getId()); //实例ID
                osInstanceVo.setInstanceName(server.getName()); //实例名称
                osInstanceVo.setStatus(server.getStatus().toString());  //状态
                imageId =  server.getImage()==null?"":server.getImage().getId();
                osInstanceVo.setImgId(imageId); //鏡像ID
                if(StringUtils.isNotEmpty(imageId)){
                    for (org.openstack4j.model.compute.Image im:images) {//根據imgId查找ImgName
                        if(im.getId().equals(imageId)){
                            osInstanceVo.setImgName(im.getName());
                            break;
                        }
                    }
                }
                flavorId = server.getFlavor()==null?"":server.getFlavor().getId();
                osInstanceVo.setFlavorId(flavorId);
                if(StringUtils.isNotEmpty(flavorId)){
                    for (org.openstack4j.model.compute.Flavor fl:flavors) {//根據flavorId查找FlavorName
                        if(fl.getId().equals(flavorId)){
                            StringBuilder configureInfo = new StringBuilder();
                            int ram = fl.getRam();
                            String ramText = "";
                            if(ram<1024){
                                ramText = ram+"MB";
                            }else{
                                ramText = ram/1024 + "GB";
                            }
                            configureInfo.append("CPU："+fl.getVcpus()+"vcpu"+" 記憶體："+ramText +" 硬碟："+fl.getDisk()+"GB" );
                            osInstanceVo.setConfigureInfo(configureInfo.toString());
                            osInstanceVo.setFlavorName(fl.getName());
                            break;
                        }
                    }
                }
                StringBuilder ipAddress = new StringBuilder();
                Map<String, List<? extends org.openstack4j.model.compute.Address>> addresses = server.getAddresses().getAddresses();
                for(List<? extends org.openstack4j.model.compute.Address> addresses1 : addresses.values()){
                    for (int j = 0; j < addresses1.size(); j++) {
                        ipAddress.append(addresses1.get(j).getAddr()+",");
                    }
                }

                /*if(server.getAddresses()!=null&&server.getAddresses().size()>0){
                    for (Address addr : server.getAddresses().values()) {
                        ipAddress.append(addr.getAddr()+",");
                    }
                }*/
                if(StringUtils.isNotEmpty(ipAddress)){
                    osInstanceVo.setIpAddress(ipAddress.substring(0,ipAddress.length()-1)); //IP地址
                }
                osInstanceVo.setProjectId(osUserProjects.get(w).getProjectId());
                osInstanceVo.setProjectName(openstack4JNova.getProjectById(osUserProjects.get(w).getProjectId()).getName());//項目名稱

                Date created = server.getCreated();//創建時間
                Date now = new Date();
                long nd = 1000 * 24 * 60 * 60;
                long nh = 1000 * 60 * 60;
                long nm = 1000 * 60;
                // 获得两个时间的毫秒时间差异
                long diff = now.getTime() - created.getTime();
                // 计算差多少天
                long day = diff / nd;
                // 计算差多少小时
                long hour = diff % nd / nh;
                // 计算差多少分钟
                long min = diff % nd % nh / nm;
                osInstanceVo.setRunTime(day + "天" + hour + "小時" + min + "分鐘");
                osInstanceVos.add(osInstanceVo);
            }
        }


        //默认admin项目获取方式
        /*String projectName ="admin";
        JCloudsNova jCloudsNova = new JCloudsNova(projectName);
        ImmutableList<Server> allservers = jCloudsNova.getServersToList();
        servers = allservers;

        //根據實例名稱篩選
        if(StringUtils.isNotEmpty(osInstance.getInstanceName())){
            servers =  servers.stream().filter(se -> se.getName().equals(osInstance.getInstanceName())).collect(Collectors.toList());
        }
        ImmutableList<Flavor> flavors = jCloudsNova.getFlavorsToList();
        ImmutableList<Image>  images = jCloudsNova.getImagesToList();
        */



        return osInstanceVos;
    }

    @Override
    public OsInstanceVo getServerDetailById(String serverId,String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4JNova = new Openstack4jNova(openstack4jEntity);
        org.openstack4j.model.compute.Server server = openstack4JNova.getServerById(serverId);
        OsInstanceVo osInstanceVo = new OsInstanceVo();
        osInstanceVo.setId(server.getId()); //实例ID
        osInstanceVo.setInstanceName(server.getName()); //实例名称
        osInstanceVo.setStatus(server.getStatus().toString());  //状态
        osInstanceVo.setProjectName("admin"); //項目名稱

        //获取IP地址
        StringBuilder ipAddress = new StringBuilder();
        Map<String, List<? extends org.openstack4j.model.compute.Address>> addresses = server.getAddresses().getAddresses();
        for(List<? extends org.openstack4j.model.compute.Address> addresses1 : addresses.values()){
            for (int j = 0; j < addresses1.size(); j++) {
                ipAddress.append(addresses1.get(j).getAddr()+",");
                if(addresses1.get(j).getType().equals("floating")){
                    osInstanceVo.setFloatingIp(addresses1.get(j).getAddr());
                }
            }
        }
        if(StringUtils.isNotEmpty(ipAddress)){
            osInstanceVo.setIpAddress(ipAddress.substring(0,ipAddress.length()-1)); //IP地址
        }

        //獲取鏡像信息
        if(StringUtils.isNotEmpty(server.getImageId())){
            osInstanceVo.setImgId(server.getImageId());
            org.openstack4j.model.compute.Image image = openstack4JNova.getImageById(server.getImageId());
            osInstanceVo.setImgName(image.getName());
        }

        if(StringUtils.isNotEmpty(server.getFlavorId())){
            org.openstack4j.model.compute.Flavor flavor = openstack4JNova.getFlavorById(server.getFlavorId());
            osInstanceVo.setFlavorId(server.getFlavorId());
            osInstanceVo.setFlavorName(flavor.getName());
            osInstanceVo.setDiskText(flavor.getDisk()+"GB");
            int ram = flavor.getRam();
            String ramText = "";
            if(ram<1024){
                ramText = ram+"MB";
            }else{
                ramText = ram/1024 + "GB";
            }
            osInstanceVo.setRamText(ramText);
            osInstanceVo.setCpu(flavor.getVcpus());
        }

        return osInstanceVo;
    }

    /*
     開啓實例
     */
    @Override
    public void startInstance(OsInstance osInstance) throws InterruptedException {
        if(StringUtils.isNotEmpty(osInstance.getId())){
            String projectName ="admin";
            JCloudsNova jCloudsNova = new JCloudsNova(projectName);
            jCloudsNova.startInstance(osInstance.getId());

           int time = 0;
           while(time <10){
               Thread.sleep(500);
               time++;
               Server.Status status = jCloudsNova.getServerStatusById(osInstance.getId());
               if(status.value().equals("ACTIVE"))
                   break;
           }
        }
    }

    /*
     關閉實例
     */
    @Override
    public void stopInstance(OsInstance osInstance) throws InterruptedException {
        if(StringUtils.isNotEmpty(osInstance.getId())){
            String projectName ="admin";
            JCloudsNova jCloudsNova = new JCloudsNova(projectName);
            jCloudsNova.stopInstance(osInstance.getId());

            int time = 0;
            while(time <10){
                Thread.sleep(500);
                time++;
                Server.Status status = jCloudsNova.getServerStatusById(osInstance.getId());
                if(status.value().equals("SHUTOFF"))
                    break;
            }
        }
    }

    //硬重啓
    @Override
    public void rebootInstanceByHARD(OsInstance osInstance) throws InterruptedException {
        if(StringUtils.isNotEmpty(osInstance.getId())){
            String projectName ="admin";
            JCloudsNova jCloudsNova = new JCloudsNova(projectName);
            jCloudsNova.rebootInstance(osInstance.getId(), RebootType.HARD);

            int time = 0;
            while(time <10){
                Thread.sleep(500);
                time++;
                Server.Status status = jCloudsNova.getServerStatusById(osInstance.getId());
                if(status.value().equals("ACTIVE"))
                    break;
            }
        }
    }

    //軟重啓
    @Override
    public void rebootInstanceBySOFT(OsInstance osInstance) throws InterruptedException {
        if(StringUtils.isNotEmpty(osInstance.getId())){
            String projectName ="admin";
            JCloudsNova jCloudsNova = new JCloudsNova(projectName);
            jCloudsNova.rebootInstance(osInstance.getId(), RebootType.SOFT);

            int time = 0;
            while(time <10){
                Thread.sleep(500);
                time++;
                Server.Status status = jCloudsNova.getServerStatusById(osInstance.getId());
                if(status.value().equals("ACTIVE"))
                    break;
            }
        }
    }

    @Override
    public String getConsoleUrl(String instanceID,String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4JNova = new Openstack4jNova(openstack4jEntity);
        return openstack4JNova.getVNCURL(instanceID);
    }

    @Override
    public String createSnapshot(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4JNova = new Openstack4jNova(openstack4jEntity);
        return openstack4JNova.createSnapshot(osInstanceVo.getId(),osInstanceVo.getImgName());
    }

    @Override
    public void delete(String serverid,String projectId) throws InterruptedException {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4JNova = new Openstack4jNova(openstack4jEntity);
        openstack4JNova.delete(serverid);
        int time = 0;
        while(time <10){
            Thread.sleep(500);
            time++;
            org.openstack4j.model.compute.Server server  = openstack4JNova.getServerById(serverid);
            if(server==null)
                break;
        }
    }

    @Override
    public void deleteBatch(List<String> serverids) {
        /*Openstack4jNova openstack4JNova = new Openstack4jNova();
        for (String serverid:serverids
             ) {
            openstack4JNova.delete(serverid);
        }*/
    }

    @Override
    public void connectVolume(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        openstack4jNova.connectVolume(osInstanceVo.getId(),osInstanceVo.getConnectVolumeId());
    }

    @Override
    public ActionResponse detachVolume(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        return openstack4jNova.detachVolume(osInstanceVo.getId(),osInstanceVo.getInUseVolumeId());
    }

    @Override
    public ActionResponse addFloatingIp(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        ActionResponse actionResponse = openstack4jNeutron.addFloatingIp(osInstanceVo.getId(), osInstanceVo.getFloatingIpId());
        return actionResponse;
    }

    @Override
    public ActionResponse removeFloatingIP(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        ActionResponse actionResponse = openstack4jNeutron.removeFloatingIP(osInstanceVo.getId(), osInstanceVo.getFloatingIpId());
        return actionResponse;
    }

    @Override
    public List<Volume> getAvailableVolumes(OsInstanceVo osInstanceVo) {
        List<Volume> res = new ArrayList<>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //获取用户关联的可用卷
        QueryWrapper<OsApplyDisk> osApplyDiskQueryWrapper = new QueryWrapper<>();
        osApplyDiskQueryWrapper.eq("create_by",user.getUsername());
        osApplyDiskQueryWrapper.eq("status","1");
        Date date = new Date();
        osApplyDiskQueryWrapper.le("start_time",date); //小于
        osApplyDiskQueryWrapper.ge("end_time",date); //大于
        osApplyDiskQueryWrapper.isNotNull("disk_id");
        List<OsApplyDisk> osApplyDisks = iOsApplyDiskService.list(osApplyDiskQueryWrapper);

        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<Volume> volumes = openstack4jNova.getVolumes();
        volumes = volumes.stream().filter(volume -> volume.getStatus().toString().equals(Volume.Status.AVAILABLE.toString())).collect(Collectors.toList());

        for (OsApplyDisk osApplyDisk: osApplyDisks) {
            for (Volume v:volumes) {
                if(osApplyDisk.getDiskId().equals(v.getId())){
                    res.add(v);
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public List<Volume> getInUseVolumes(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<Volume> volumes = openstack4jNova.getVolumes();
        List<Volume> res = new ArrayList<>();
        for (Volume v:volumes) {
            if(v.getStatus().toString().equals(Volume.Status.IN_USE.toString())){
                if(v.getAttachments()!=null&&v.getAttachments().size()>0){
                    if(v.getAttachments().get(0).getServerId().equals(osInstanceVo.getId())){
                        res.add(v);
                    }
                }
            }
        }
        //volumes = volumes.stream().filter(volume -> volume.getAttachments().get(0).getServerId().equals(osInstanceVo.getId())).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<NetFloatingIP> getFloatingIps(OsInstanceVo osInstanceVo) {
        List<NetFloatingIP> res = new ArrayList<>();
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //获取用户关联的可用卷
        QueryWrapper<OsApplyFloatip> osApplyFloatipQueryWrapper = new QueryWrapper<>();
        osApplyFloatipQueryWrapper.eq("create_by",user.getUsername());
        osApplyFloatipQueryWrapper.eq("status","1");
        Date date = new Date();
        osApplyFloatipQueryWrapper.le("start_time",date); //小于
        //osApplyFloatipQueryWrapper.ge("end_time",date); //大于
        osApplyFloatipQueryWrapper.isNotNull("float_ip");
        List<OsApplyFloatip> osApplyFloatips = iOsApplyFloatipService.list(osApplyFloatipQueryWrapper);

        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNeutron openstack4jNeutron = new Openstack4jNeutron(openstack4jEntity);
        List<NetFloatingIP> floatingIps = openstack4jNeutron.getFloatingIps();

        for (OsApplyFloatip osaf:osApplyFloatips) {
            for (NetFloatingIP nfi:floatingIps) {
                if(osaf.getFloatIp().equals(nfi.getFloatingIpAddress())){
                    res.add(nfi);
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public Limits getProjectLimits(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        Limits projectLimits = openstack4jNova.getProjectLimits();
        return projectLimits;
    }

    @Override
    public org.openstack4j.model.compute.SimpleTenantUsage getTenantUsage(OsInstanceVo osInstanceVo) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(osInstanceVo.getProjectId());
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd");
        String startTime = aDate.format( osInstanceVo.getStartTime());
        String endTime = aDate.format( osInstanceVo.getEndTime());
        org.openstack4j.model.compute.SimpleTenantUsage tenantUsage = openstack4jNova.getTenantUsage(osInstanceVo.getProjectId(), startTime+"T00:00:00", endTime+"T23:59:59");
        return tenantUsage;
    }

}
