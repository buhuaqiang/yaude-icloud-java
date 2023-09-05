package com.yaude.icloud.openstack.service;

import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsApplyDiskVo;
import com.yaude.icloud.openstack.vo.OsApplyVo;

import java.util.List;

/**
 * @Description: 磁盘申请明细档
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
public interface IOsApplyDiskService extends IService<OsApplyDisk> {

    List<OsApplyDisk> getNetworkType(String projectId);

    List<OsApplyDiskVo> getImg(String projectId);
    List<OsApplyDiskVo> getSnapshot(String projectId);
    List<OsApplyDiskVo> getVolume(String projectId);

    List<OsApplyDiskVo> getOsApplyDiskList(List<OsApplyDisk> osApplyDiskList);

    int getStatus(String id);
    int getCountStatus(List<String> ids);

}
