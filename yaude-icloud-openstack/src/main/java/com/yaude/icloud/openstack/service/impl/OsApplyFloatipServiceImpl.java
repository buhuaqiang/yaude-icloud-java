package com.yaude.icloud.openstack.service.impl;

import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.yaude.icloud.openstack.entity.OsApplyFloatip;
import com.yaude.icloud.openstack.entity.OsOption;
import com.yaude.icloud.openstack.mapper.OsApplyFloatipMapper;
import com.yaude.icloud.openstack.mapper.OsApplyMapper;
import com.yaude.icloud.openstack.mapper.OsOptionMapper;
import com.yaude.icloud.openstack.service.IOsApplyFloatipService;
import com.yaude.icloud.openstack.utils.Openstack4jEntity;
import com.yaude.icloud.openstack.utils.Openstack4jNova;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyDiskVo;
import com.yaude.icloud.openstack.vo.OsApplyFloatipVo;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.block.VolumeType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 浮动ip申请明细档
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
@Service
public class OsApplyFloatipServiceImpl extends ServiceImpl<OsApplyFloatipMapper, OsApplyFloatip> implements IOsApplyFloatipService {

    @Autowired
    private OsApplyFloatipMapper osApplyFloatipMapper;
    @Autowired
    private OsOptionMapper osOptionMapper;

    @Override
    public List<OsApplyFloatipVo> getOsApplyFloatList(List<OsApplyFloatip> osApplyFloatips){
        List<OsApplyFloatipVo>  osApplyFloatipVos = new ArrayList<>();
        for (OsApplyFloatip osApplyFloatip :osApplyFloatips) {
            OsApplyFloatipVo osApplyFloatipVo = new OsApplyFloatipVo();
            BeanUtils.copyProperties(osApplyFloatip,osApplyFloatipVo);
            //浮動ip的申請狀態不為空並且狀態為1或者2 (1為成功，2為失敗)
            if(StringUtils.isNotEmpty(osApplyFloatip.getStatus()) &&(osApplyFloatip.getStatus().equals("1") || osApplyFloatip.getStatus().equals("2"))){
                OsOption optionTexts = osOptionMapper.getText(osApplyFloatip.getId());
                String optionText = optionTexts.getOptionsText();//獲取審核意見
                osApplyFloatipVo.setOptionsText(optionText);
            }

            osApplyFloatipVos.add(osApplyFloatipVo) ;
        }
        return osApplyFloatipVos;
    }

    @Override
    public List<OsApplyFloatipVo> getNetwork(String projectId) {//獲取網絡
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<? extends Network> networks = openstack4jNova.networks();
        List<OsApplyFloatipVo>  osApplyFloatips = new ArrayList<>();
        OsApplyFloatipVo osApplyFloatip = null;
        Network network = null;
        for (int i = 0; i < networks.size(); i++) {
            network = networks.get(i);
            if(network.isRouterExternal()){
                osApplyFloatip = new OsApplyFloatipVo();
                osApplyFloatip.setNetworkName(network.getName());
                osApplyFloatip.setNetworkId(network.getId());
                osApplyFloatips.add(osApplyFloatip);
            }

        }
        return osApplyFloatips;
    }

    @Override
    public List<OsApplyFloatipVo> getSubnets(String networkId,String projectId) {//獲取子網
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<OsApplyFloatipVo>  osApplyFloatips = new ArrayList<>();
        Network network = openstack4jNova.network(networkId);
        OsApplyFloatipVo osApplyFloatip = null;
        List<String> SubnetIds = network.getSubnets();
        for (int i=0;i<SubnetIds.size();i++){
            osApplyFloatip = new OsApplyFloatipVo();
            Subnet subnet = openstack4jNova.subnet(SubnetIds.get(i));
            osApplyFloatip.setSubnetId(SubnetIds.get(i));
            osApplyFloatip.setSubnetName(subnet.getName());
            osApplyFloatips.add(osApplyFloatip);
        }
        return osApplyFloatips;
    }

   /* @Override
    public List<OsApplyFloatipVo> getFloatip(String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        Openstack4jNova openstack4jNova = new Openstack4jNova(openstack4jEntity);
        List<OsApplyFloatipVo>  osApplyFloatips = new ArrayList<>();
        FloatingIP floatingIP = openstack4jNova.floatingIP();
        OsApplyFloatipVo osApplyFloatip = new OsApplyFloatipVo();
        osApplyFloatip.setFloatIp(floatingIP.getFloatingIpAddress());
        osApplyFloatips.add(osApplyFloatip);
        return osApplyFloatips;
    }
*/

    @Override
    public  int getStatus(String id){
        int counts = osApplyFloatipMapper.getStatus(id);
        return  counts;
    }

    @Override
    public  int getCountStatus(List<String> ids){
        int counts = osApplyFloatipMapper.getCountStatus(ids);
        return  counts;
    }




}
