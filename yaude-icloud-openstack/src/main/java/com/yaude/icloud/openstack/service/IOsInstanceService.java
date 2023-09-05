package com.yaude.icloud.openstack.service;

import com.google.common.collect.ImmutableList;
import com.yaude.icloud.openstack.entity.OsInstance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Limits;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.network.FloatingIP;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.storage.block.Volume;

import java.util.List;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
public interface IOsInstanceService extends IService<OsInstance> {


     List<OsInstanceVo> getInstanceList(OsInstanceVo osInstanceVo);

     OsInstanceVo getServerDetailById(String serverId,String projectId);

     void startInstance(OsInstance osInstance) throws InterruptedException;

     void stopInstance(OsInstance osInstance) throws InterruptedException;

     void rebootInstanceByHARD(OsInstance osInstance) throws InterruptedException;

     void rebootInstanceBySOFT(OsInstance osInstance) throws InterruptedException;

     String getConsoleUrl(String instanceID,String projectId);

     String createSnapshot(OsInstanceVo osInstanceVo);

     void delete(String serverid,String projectId) throws InterruptedException;

     void deleteBatch(List<String> serverids);

     void connectVolume(OsInstanceVo osInstanceVo);

     ActionResponse detachVolume(OsInstanceVo osInstanceVo);

     ActionResponse addFloatingIp(OsInstanceVo osInstanceVo);

     ActionResponse removeFloatingIP(OsInstanceVo osInstanceVo);

     List<Volume> getAvailableVolumes(OsInstanceVo osInstanceVo);

     List<Volume> getInUseVolumes(OsInstanceVo osInstanceVo);

     List<NetFloatingIP> getFloatingIps(OsInstanceVo osInstanceVo);

     Limits getProjectLimits(OsInstanceVo osInstanceVo);

     SimpleTenantUsage getTenantUsage(OsInstanceVo osInstanceVo);
}
