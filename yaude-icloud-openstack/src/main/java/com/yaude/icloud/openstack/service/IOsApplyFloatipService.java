package com.yaude.icloud.openstack.service;

import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.yaude.icloud.openstack.entity.OsApplyFloatip;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsApplyDiskVo;
import com.yaude.icloud.openstack.vo.OsApplyFloatipVo;

import java.util.List;

/**
 * @Description: 浮动ip申请明细档
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
public interface IOsApplyFloatipService extends IService<OsApplyFloatip> {
    List<OsApplyFloatipVo> getNetwork(String projectId);
    List<OsApplyFloatipVo> getSubnets(String networkId,String projectId);
//    List<OsApplyFloatipVo> getFloatip(String projectId);

    List<OsApplyFloatipVo> getOsApplyFloatList(List<OsApplyFloatip> osApplyFloatipList);

    int getStatus(String id);
    int getCountStatus(List<String> ids);
}
