package com.yaude.icloud.openstack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.UUIDGenerator;
import com.yaude.common.util.oConvertUtils;
import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsKeyPairs;
import com.yaude.icloud.openstack.entity.OsOption;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.yaude.icloud.openstack.mapper.OsApplyMapper;
import com.yaude.icloud.openstack.mapper.OsKeyPairsMapper;
import com.yaude.icloud.openstack.mapper.OsOptionMapper;
import com.yaude.icloud.openstack.mapper.OsUserProjectMapper;
import com.yaude.icloud.openstack.service.IOsApplyService;
import com.yaude.icloud.openstack.utils.JCloudsNeutron;
import com.yaude.icloud.openstack.utils.JCloudsNova;
import com.yaude.icloud.openstack.utils.Openstack4jEntity;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.openstack4j.model.identity.v3.Project;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;


import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
@Service
public class OsApplyServiceImpl extends ServiceImpl<OsApplyMapper, OsApply> implements IOsApplyService {
    @Autowired
    private OsApplyMapper osApplyMapper;

    @Autowired
    private OsOptionMapper osOptionMapper;
    @Autowired
    private OsUserProjectMapper osUserProjectMapper;
    @Autowired
    private OsKeyPairsMapper osKeyPairsMapper;



    @Override
    public List<OsApplyVo> getOsApplyVoListByOsApply(List<OsApply> osApplys){//根據實例id查詢信息
       // Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        List<OsApplyVo>  osApplyVos = new ArrayList<>();
        for (OsApply osApply :osApplys) {
            OsApplyVo osApplyVo = new OsApplyVo();
            BeanUtils.copyProperties(osApply,osApplyVo);
            //實例申請狀態不為空並且狀態為1或者2 (1為成功，2為失敗)
            if(StringUtils.isNotEmpty(osApply.getStatus()) &&(osApply.getStatus().equals("1") || osApply.getStatus().equals("2"))){
                OsOption optionTexts = osOptionMapper.getText(osApply.getId());
                String optionText = optionTexts.getOptionsText();//獲取審核意見
                osApplyVo.setOptionsText(optionText);
            }

            Date endtime = osApply.getEndTime();
            long dates = daysBetween(endtime);
            osApplyVo.setSectionTime(dates+"天");


            osApplyVos.add(osApplyVo);
        }

        return osApplyVos;
    }

    public static long daysBetween(Date endTime)  {//計算剩餘天數
        try{
            Date date = new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            date=sdf.parse(sdf.format(date));
            endTime=sdf.parse(sdf.format(endTime));
            //Date smdate=sdf.parse("date");
            //Date  bdate=sdf.parse("endTime");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long time1 = cal.getTimeInMillis();
            cal.setTime(endTime);
            long time2 = cal.getTimeInMillis();
            long between_days=(time2-time1)/(1000*3600*24);
            return between_days;

        }catch (Exception e){
           return 0;
        }

    }





    @Override
    public List<OsApplyVo> getProject() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        List<OsUserProjectVo> ProjectIds = osUserProjectMapper.getProject(userId);
        Openstack4jProject openstack4jApi = new Openstack4jProject();
        List<OsApplyVo>  osApplys = new ArrayList<>();
        OsApplyVo osApplyVo = null;
        for(int i = 0; i < ProjectIds.size(); i++){
            osApplyVo = new OsApplyVo();
            String projectId = ProjectIds.get(i).getProjectId();
            osApplyVo.setProjectId(projectId);
            Project project = openstack4jApi.getProjects(projectId);
            if(project==null){
                continue;
            }
            osApplyVo.setProjectName(project.getName());
            osApplys.add(osApplyVo);
        }
        return osApplys;
    }


    @Override
    public List<OsApplyVo> getImg(String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        JCloudsNova jCloudsNova = new JCloudsNova(openstack4jEntity);
        ImmutableList<Image> images = jCloudsNova.getListImage();
        List<OsApplyVo>  osApplys = new ArrayList<>();
        OsApplyVo osApplyVo = null;
        Image image = null;
        for (int i = 0; i < images.size(); i++) {
            image = images.get(i);
            osApplyVo = new OsApplyVo();
            osApplyVo.setImgId(image.getId()); //鏡像ID
            osApplyVo.setImgName(image.getName()); //鏡像名称
            osApplys.add(osApplyVo);
        }
        return osApplys;
    }
    @Override
    public List<OsApplyVo> getFlavor(String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        JCloudsNova jCloudsNova = new JCloudsNova(openstack4jEntity);
        ImmutableList<Flavor> flavors = jCloudsNova.getListFlavor();
        List<OsApplyVo>  osApplys = new ArrayList<>();
        OsApplyVo osApplyVo = null;
        Flavor flavor = null;
        for (int i = 0; i < flavors.size(); i++) {
            flavor = flavors.get(i);
            osApplyVo = new OsApplyVo();
            osApplyVo.setFlavorId(flavor.getId()); //实例ID
            int ram = flavor.getRam();
            String ramText = "";
            if(ram<1024){
                ramText = ram+"MB";
            }else{
                ramText = ram/1024 + "GB";
            }
            String allName =flavor.getName()+" "+"CPU："+flavor.getVcpus()+"vcpu"+" 記憶體："+ramText +" 硬碟："+flavor.getDisk()+"GB";
            osApplyVo.setFlavorName(allName); //实例名称
            osApplys.add(osApplyVo);
        }
        return osApplys;
    }
    @Override
    public List<OsApply> getSecurity(String projectId) {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        JCloudsNova jCloudsNova = new JCloudsNova(openstack4jEntity);
        FluentIterable<org.jclouds.openstack.nova.v2_0.domain.SecurityGroup> securityGroups = jCloudsNova.getListSecurityGroup();
        List<OsApply>  osApplys = new ArrayList<>();
        OsApply osApply = null;
        SecurityGroup SecurityGroup = null;
        for (int i = 0; i < securityGroups.size(); i++) {
            SecurityGroup = securityGroups.get(i);
            osApply = new OsApply();
            osApply.setSecurityName(SecurityGroup.getName()); //安全组
            osApplys.add(osApply);
        }
        return osApplys;
    }
    @Override
    public List<OsApplyVo> getNetwork(String projectId) {//獲取網絡
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId(projectId);
        JCloudsNeutron jCloudsNeutron = new JCloudsNeutron(openstack4jEntity);
        //String projectId ="04987b0c4ad54494a79f0c41a7fb6c02";
        FluentIterable<Network> networks = jCloudsNeutron.getListNetwork(projectId);
//        List<OsApply>  osApplys = new ArrayList<>();
        List<OsApplyVo>  osApplys = new ArrayList<>();
        OsApplyVo osApplyVo = null;
        Network Network = null;
        for (int i = 0; i < networks.size(); i++) {
            Network = networks.get(i);
            osApplyVo = new OsApplyVo();
            osApplyVo.setNetworkId(Network.getId()); //网络
            osApplyVo.setNetworkName(Network.getName()); //网络
            osApplys.add(osApplyVo);
        }
        return osApplys;
    }

    @Override
    public  List<OsApplyVo> getPrivateKey(String projectId) {//獲取秘鑰
        List<OsApplyVo> osApplyVos = new ArrayList<>();
        List<OsKeyPairs> osKeyPairs = osKeyPairsMapper.getPrivateKey(projectId);
        OsApplyVo osApplyVo = null;
        for(OsKeyPairs KeyPairs : osKeyPairs){
            osApplyVo = new OsApplyVo();
            osApplyVo.setKeypairsId(KeyPairs.getId());
            osApplyVo.setKeyName(KeyPairs.getKeyName());
            osApplyVo.setPrivateKey(KeyPairs.getPrivateKey());
            osApplyVos.add(osApplyVo);
        }
        return osApplyVos;
    }

    @Override
    public  int getStatus(String id){
        int counts = osApplyMapper.getStatus(id);
        return  counts;
    }

    @Override
    public  int getCountStatus(List<String> ids){
        int counts = osApplyMapper.getCountStatus(ids);
        return  counts;
    }


}
