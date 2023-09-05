package com.yaude.icloud.openstack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.icloud.openstack.entity.*;
import com.yaude.icloud.openstack.mapper.OsApplyMapper;
import com.yaude.icloud.openstack.mapper.OsOptionMapper;
import com.yaude.icloud.openstack.service.*;
import com.yaude.icloud.openstack.utils.*;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsOptionVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.storage.block.Volume;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 審核意見細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class OsOptionServiceImpl extends ServiceImpl<OsOptionMapper, OsOption> implements IOsOptionService {
    @Resource
    private IOsApplyService iOsApplyService;
    @Autowired
    private OsApplyMapper osApplyMapper;
    @Autowired
    private OsOptionMapper osOptionMapper;
    @Resource
    private IOsApplyFloatipService osApplyFloatipService;
    @Resource
    private IOsApplyDiskService osApplyDiskService;
    @Autowired
    private IOsOptionService osOptionService;
    @Autowired
    private IOsUserProjectService iOsUserProjectService;


    @Override
    public List<OsOptionVo> getOsOptionVoListByOsApply(List<OsOption> osOptions){//查詢審核

        LambdaQueryWrapper<OsOption> queryWrapper = new LambdaQueryWrapper<OsOption>();
        //當前登錄人
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();




        List<OsOptionVo>  osOptionVos = new ArrayList<>();

        for (OsOption OsOption :osOptions) {
            OsOptionVo osOptionVo = new OsOptionVo();
            BeanUtils.copyProperties(OsOption,osOptionVo);
            String applyId = OsOption.getApplyId();
            //根據當前登錄人查詢是否是管理員以即管理的專案
            List<OsOptionVo> osApply = osOptionMapper.getApply(applyId,userId);
            for(int i=0;i<osApply.size();i++){
                osOptionVo.setOptions(osApply.get(0).getOptions());
                osOptionVo.setStatus(osApply.get(0).getStatus());
                osOptionVo.setApplyName(osApply.get(0).getApplyName());
                osOptionVo.setApplyId(osApply.get(0).getApplyId());
                osOptionVos.add(osOptionVo);
            }
        }

        return osOptionVos;
    }

    @Override
    public void upStatus(OsApplyVo osApplyVo) throws InterruptedException {//審核
        OsApply osApply = new OsApply();
        OsApplyFloatip floatip = new OsApplyFloatip();
        OsApplyDisk disk = new OsApplyDisk();
        String projectId =osApplyVo.getProjectId();
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack = new Openstack4jNova(openstack4jEntity);
        Openstack4jProject openstack4jProject = new Openstack4jProject();
        JCloudsNova jCloudsNova = new JCloudsNova(openstack4jEntity);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);

        if(osApplyVo.getOptionsType().equals("1")){//审批通过
            if(osApplyVo.getApplyType().equals("1")){//審批vm
                osApply.setId(osApplyVo.getId());
                osApply.setStatus("1");
                if(StringUtils.isNotEmpty(osApplyVo.getVmId())){//更新vm
                    String vmid = osApplyVo.getVmId();
                    String flavorId = osApplyVo.getFlavorId();
                    openstack.updateServer(vmid,flavorId);//更新服务
                   /* int time = 0;
                    while(time<10){
                        Thread.sleep(500);
                        time++;
                        Server vm = openstack4jNova.getServerById(vmid);
                        if(vm.getFlavorId().equals(flavorId)){
                            openstack.confirm(vmid);//确认更新
                            break;
                        }
                    }*/
                }else{ //审批通过创建服务
                    String networkId = osApplyVo.getNetworkId();
                    String securityName = osApplyVo.getSecurityName();
                    String imgId = osApplyVo.getImgId();
                    String isDelete = osApplyVo.getIsDelete();
                    String instanceName = osApplyVo.getInstanceName();
                    String flavorId = osApplyVo.getFlavorId();
                    Flavor flavor = jCloudsNova.getFlavor(flavorId);
                    String size = String.valueOf(flavor.getDisk());
                    //生成vmid
                    String vmid =openstack.vmId(networkId,securityName,imgId,size,isDelete,instanceName,flavorId);
                    osApply.setVmId(vmid);
                }
                iOsApplyService.updateById(osApply);
            }else if(osApplyVo.getApplyType().equals("2")){//審批ip
                floatip.setId(osApplyVo.getId());
                floatip.setStatus("1");
                String floatNetworkId = osApplyVo.getFloatNetworkId();
                String subnetId = osApplyVo.getSubnetId();
                //生成浮動ip
                String floatingIp =  openstack.floatingIP(floatNetworkId,subnetId);
                String ip = floatingIp;
                floatip.setFloatIp(ip);
                osApplyFloatipService.updateById(floatip);
            }else if(osApplyVo.getApplyType().equals("3")){//審批磁盘
                disk.setId(osApplyVo.getId());
                disk.setStatus("1");
                if(StringUtils.isNotEmpty(osApplyVo.getDiskId())){//更新磁盘
                    String diskId = osApplyVo.getDiskId();
                    int size = Integer.parseInt(osApplyVo.getSize());
                    openstack4jNova.upVolume(diskId,size);
                }else{//创建磁盘
                    String diskname = osApplyVo.getDiskName();
                    String diskrepresent = osApplyVo.getDiskrepresent();
                    int size = Integer.parseInt(osApplyVo.getSize());
                    //生成磁盤
                    Volume volume = openstack.createVolume(diskname,diskrepresent,size);
                    disk.setDiskId(volume.getId());
                }
                osApplyDiskService.updateById(disk);
            }

        }else if(osApplyVo.getOptionsType().equals("0")){//审批拒绝
            if(osApplyVo.getApplyType().equals("1")) {//vm
                osApply.setId(osApplyVo.getId());
                osApply.setStatus("2");
                iOsApplyService.updateById(osApply);
            }else if(osApplyVo.getApplyType().equals("2")) {//ip
                floatip.setId(osApplyVo.getId());
                floatip.setStatus("2");
                osApplyFloatipService.updateById(floatip);
            }else if(osApplyVo.getApplyType().equals("3")) {//磁盘
                disk.setId(osApplyVo.getId());
                disk.setStatus("2");
                osApplyDiskService.updateById(disk);
            }

        }

    }
    //審核之後刷新狀態
    @Override
    public void getStatus(OsOptionVo osOptionVo) throws InterruptedException {
        int time = 0;
        String projectId = osOptionVo.getProjectId();
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        String applyId = osOptionVo.getApplyId();
        String applyType = osOptionVo.getApplyType();
        String id = osOptionVo.getId();
        String options = osOptionVo.getOptions();
        OsOption osOptions = new OsOption();
        while (time < 10) {
            Thread.sleep(500);
            time++;
            OsOptionVo osOptionVos = osOptionMapper.getProjectId(applyId, applyType);
            String signId = osOptionVos.getSignId();//生成的id||ip
            String adjustType = osOptionVos.getAdjustType();//調整後的類型大小
            if (signId != null) {
                if (options.equals("2")) {//调整的刷新狀態

                    if(applyType.equals("1")){//vm調整
                        Server vm = openstack4jNova.getServerById(signId);
                        if (vm.getFlavorId().equals(adjustType)) {
                            Thread.sleep(1000);
                            openstack4jNova.confirm(signId);//确认更新
                            osOptions.setId(id);
                            osOptions.setOptionsType("1");
                            osOptionService.updateById(osOptions);
                            break;
                        }
                    }else if(applyType.equals("3")){//磁盘调整大小验证
                        Volume volume = openstack4jNova.getVolume(signId);
                        int size = Integer.parseInt(adjustType);
                        if(volume.getSize()==size){
                            osOptions.setId(id);
                            osOptions.setOptionsType("1");
                            osOptionService.updateById(osOptions);
                            break;
                        }
                    }
                } else {//審核的刷新狀態
                    osOptions.setId(id);
                    osOptions.setOptionsType("1");
                    osOptionService.updateById(osOptions);
                    break;
                }

            }
        }
    }

    @Override
    public String getProjectId(String applyId,String applyType){
        OsOptionVo options = osOptionMapper.getProjectId(applyId,applyType);
            return options.getProjectId();
    }
    @Override
    public void deleteOption(String applyId,String applyType){
        osOptionMapper.deleteOption(applyId,applyType);
    }
}
