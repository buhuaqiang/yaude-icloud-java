package com.yaude.icloud.openstack.service.impl;

import com.yaude.common.system.vo.LoginUser;
import com.yaude.icloud.openstack.entity.OsKeyPairs;
import com.yaude.icloud.openstack.mapper.OsKeyPairsMapper;
import com.yaude.icloud.openstack.mapper.OsOptionMapper;
import com.yaude.icloud.openstack.mapper.OsUserProjectMapper;
import com.yaude.icloud.openstack.service.IOsKeyPairsService;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import org.apache.shiro.SecurityUtils;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.identity.v3.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 秘钥对
 * @Author: jeecg-boot
 * @Date:   2021-10-18
 * @Version: V1.0
 */
@Service
public class OsKeyPairsServiceImpl extends ServiceImpl<OsKeyPairsMapper, OsKeyPairs> implements IOsKeyPairsService {
    @Autowired
    private OsKeyPairsMapper osKeyPairsMapper;
    @Autowired
    private OsUserProjectMapper osUserProjectMapper;

   /* @Override
    public List<OsKeyPairs> getUser() {
        List<OsKeyPairs>  osKeyPairses = osKeyPairsMapper.getUser();
        return osKeyPairses;
    }*/
    @Override
    public List<OsKeyPairs> getProjects() {//根據userid獲取項目
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        List<OsUserProjectVo> ProjectIds = osUserProjectMapper.getProject(userId);
        Openstack4jProject openstack4jApi = new Openstack4jProject();
        List<OsKeyPairs>  osKeyPairses = new ArrayList<>();
        OsKeyPairs osKeyPairs = null;
        for(int i = 0; i < ProjectIds.size(); i++){
            osKeyPairs = new OsKeyPairs();
            String projectId = ProjectIds.get(i).getProjectId();
            osKeyPairs.setProjectId(projectId);
            Project project = openstack4jApi.getProjects(projectId);
            if(project==null){
                continue;
            }
            osKeyPairs.setProjectName(project.getName());
            osKeyPairses.add(osKeyPairs);
        }
        return osKeyPairses;
    }
    @Override
    public OsKeyPairs getkey(OsKeyPairs osKeyPairs) {//獲取秘鑰
        Openstack4jProject openstack4jProject = new Openstack4jProject();
        //生成秘鑰
        Keypair keypair = openstack4jProject.createKeypair(osKeyPairs.getKeyName());
        OsKeyPairs  osKeyPairses = new OsKeyPairs();
        osKeyPairses.setKeyName(keypair.getName());
        osKeyPairses.setPublicKey(keypair.getPublicKey());
        osKeyPairses.setPrivateKey(keypair.getPrivateKey());
        osKeyPairses.setFingerprint(keypair.getFingerprint());
       // osKeyPairses.setUserId(osKeyPairs.getUserId());
        //osKeyPairses.setUserName(osKeyPairs.getUserName());
        osKeyPairses.setProjectId(osKeyPairs.getProjectId());
        osKeyPairses.setProjectName(osKeyPairs.getProjectName());
        return osKeyPairses;
    }


}
