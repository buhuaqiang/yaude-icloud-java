package com.yaude.icloud.openstack.service.impl;

import com.google.common.collect.ImmutableList;
import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.yaude.icloud.openstack.entity.OsOption;
import com.yaude.icloud.openstack.mapper.OsApplyDiskMapper;
import com.yaude.icloud.openstack.mapper.OsOptionMapper;
import com.yaude.icloud.openstack.service.IOsApplyDiskService;
import com.yaude.icloud.openstack.utils.JCloudsNova;
import com.yaude.icloud.openstack.utils.Openstack4jEntity;
import com.yaude.icloud.openstack.utils.Openstack4jNova;
import com.yaude.icloud.openstack.vo.OsApplyDiskVo;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.openstack4j.model.storage.block.VolumeType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 磁盘申请明细档
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
@Service
public class OsApplyDiskServiceImpl extends ServiceImpl<OsApplyDiskMapper, OsApplyDisk> implements IOsApplyDiskService {

    @Autowired
    private OsApplyDiskMapper osApplyDiskMapper;
    @Autowired
    private OsOptionMapper osOptionMapper;

    @Override
    public List<OsApplyDiskVo> getOsApplyDiskList(List<OsApplyDisk> osApplyDisks){
        List<OsApplyDiskVo>  osApplyDiskVos = new ArrayList<>();
        for (OsApplyDisk osApplyDisk :osApplyDisks) {
            OsApplyDiskVo osApplyDiskVo = new OsApplyDiskVo();
            BeanUtils.copyProperties(osApplyDisk,osApplyDiskVo);
            if(StringUtils.isNotEmpty(osApplyDisk.getStatus()) &&(osApplyDisk.getStatus().equals("1") || osApplyDisk.getStatus().equals("2"))){
                OsOption optionTexts = osOptionMapper.getText(osApplyDisk.getId());
                String optionText = optionTexts.getOptionsText();
                osApplyDiskVo.setOptionsText(optionText);
            }
            osApplyDiskVos.add(osApplyDiskVo) ;
        }
        return osApplyDiskVos;
    }

    @Override
    public List<OsApplyDisk> getNetworkType(String projectId) {//获取网络类型
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4JNova = new Openstack4jNova(openstack4jEntity);
        List<VolumeType> VolumeTypes = openstack4JNova.getVolumeType();
        List<OsApplyDisk>  osApplyDisks = new ArrayList<>();
        OsApplyDisk osApplyDisk = null;
        VolumeType volumeType = null;
        for (int i = 0; i < VolumeTypes.size(); i++) {
            volumeType = VolumeTypes.get(i);
            osApplyDisk = new OsApplyDisk();
            osApplyDisk.setType(volumeType.getName());
            osApplyDisks.add(osApplyDisk);
        }
        return osApplyDisks;
    }

    @Override
    public List<OsApplyDiskVo> getImg(String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        JCloudsNova jCloudsNova = new JCloudsNova(openstack4jEntity);
        ImmutableList<Image> images = jCloudsNova.getListImage();
        List<OsApplyDiskVo>  osApplyDiskVos = new ArrayList<>();
        OsApplyDiskVo osApplyDiskVo = null;
        Image image = null;
        for (int i = 0; i < images.size(); i++) {
            image = images.get(i);
            osApplyDiskVo = new OsApplyDiskVo();
            osApplyDiskVo.setImgId(image.getId()); //鏡像ID
            osApplyDiskVo.setImgName(image.getName()); //鏡像名称
            osApplyDiskVos.add(osApplyDiskVo);
        }
        return osApplyDiskVos;
    }
    @Override
    public List<OsApplyDiskVo> getSnapshot(String projectId) {//获取快照
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<? extends VolumeSnapshot> VolumeSnapshots = openstack4jNova.getVolumeSnapshots();
        List<OsApplyDiskVo>  osApplyDiskVos = new ArrayList<>();
        OsApplyDiskVo osApplyDiskVo = null;
        VolumeSnapshot volumeSnapshot = null;
        for (int i = 0; i < VolumeSnapshots.size(); i++) {
            volumeSnapshot = VolumeSnapshots.get(i);
            osApplyDiskVo = new OsApplyDiskVo();
            osApplyDiskVo.setSnapshotId(volumeSnapshot.getId()); //快照id
            osApplyDiskVo.setSnapshotName(volumeSnapshot.getName()); //快照名称
            osApplyDiskVos.add(osApplyDiskVo);
        }
        return osApplyDiskVos;
    }
    @Override
    public List<OsApplyDiskVo> getVolume(String projectId) {//获取卷
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<Volume> Volumes = openstack4jNova.getVolumes();
        List<OsApplyDiskVo>  osApplyDiskVos = new ArrayList<>();
        OsApplyDiskVo osApplyDiskVo = null;
        Volume volume = null;
        for (int i = 0; i < Volumes.size(); i++) {
            volume = Volumes.get(i);
            osApplyDiskVo = new OsApplyDiskVo();
            osApplyDiskVo.setVolumeId(volume.getId()); //卷id
            osApplyDiskVo.setVolumeName(volume.getName()); //卷名称
            osApplyDiskVos.add(osApplyDiskVo);
        }
        return osApplyDiskVos;
    }

    @Override
    public  int getStatus(String id){
        int counts = osApplyDiskMapper.getStatus(id);
        return  counts;
    }

    @Override
    public  int getCountStatus(List<String> ids){
        int counts = osApplyDiskMapper.getCountStatus(ids);
        return  counts;
    }

}
