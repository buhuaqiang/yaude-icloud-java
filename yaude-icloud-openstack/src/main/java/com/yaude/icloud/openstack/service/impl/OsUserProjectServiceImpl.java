package com.yaude.icloud.openstack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.yaude.icloud.openstack.mapper.OsUserProjectMapper;
import com.yaude.icloud.openstack.service.IOsUserProjectService;
import com.yaude.icloud.openstack.utils.Openstack4jProject;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.model.identity.v3.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Service
public class OsUserProjectServiceImpl extends ServiceImpl<OsUserProjectMapper, OsUserProject> implements IOsUserProjectService {

    @Autowired
    private OsUserProjectMapper osUserProjectMapper;

    //获取项目列表
    @Override
    public List<OsUserProjectVo> getProjectList(OsUserProjectVo userProjectVo) {
        QueryWrapper<OsUserProject> queryWrapper = null;
        Openstack4jProject openstack4JProject = new Openstack4jProject();
        List<Project> projectList = (List<Project>) openstack4JProject.getProjectList();

        //根據項目名稱篩選                                                                                            名稱篩選
        if(StringUtils.isNotEmpty(userProjectVo.getProjectName())){
            projectList =  projectList.stream().filter(se -> se.getName().equals(userProjectVo.getProjectName())).collect(Collectors.toList());
        }
        List<OsUserProjectVo>  osUserProjectVos = new ArrayList<>();
        OsUserProjectVo osUserProjectVo = null;
        List<OsUserProjectVo> list = null;
        Project project = null;
        String projectUserNames = "";
        for (int i = 0; i < projectList.size(); i++) {
            project = projectList.get(i);
            list = osUserProjectMapper.queryUserProjectList(project.getId());
            projectUserNames = "";
            ArrayList isAdminKeys = new ArrayList();
            if(list!=null){
                for (OsUserProjectVo oup:list) {
                    projectUserNames+= oup.getUsername()+",";
                    if(oup.getIsAdmin()==1){
                        isAdminKeys.add(oup.getUserId());
                    }
                }
            }
            osUserProjectVo = new OsUserProjectVo();
            osUserProjectVo.setProjectName(project.getName()); //项目名称
            osUserProjectVo.setDescription(project.getDescription()); //描述
            osUserProjectVo.setProjectId(project.getId()); //项目ID
            osUserProjectVo.setDomainName(project.getParentId());
            osUserProjectVo.setEnabled(project.isEnabled());
            osUserProjectVo.setIsAdminKeys(isAdminKeys);
            if(projectUserNames.length()>1)
            osUserProjectVo.setProjectUserNames(projectUserNames.substring(0,projectUserNames.length()-1));
            osUserProjectVos.add(osUserProjectVo);
        }
        return osUserProjectVos;
    }


    @Override
    public boolean updateUserProject(OsUserProjectVo osUserProjectVo) {
        List<OsUserProject> osUserProjects = new ArrayList<>();
        OsUserProject osUserProject = null;
        QueryWrapper<OsUserProject> queryWrapper = null;
        if(osUserProjectVo.getProjectUserIds().size()>0){
            queryWrapper = new QueryWrapper<OsUserProject>();
            queryWrapper.eq("project_id",osUserProjectVo.getProjectId());
            this.remove(queryWrapper);
            for (int i = 0; i <osUserProjectVo.getProjectUserIds().size() ; i++) {
                osUserProject = new OsUserProject();
                osUserProject.setUserId(osUserProjectVo.getProjectUserIds().get(i).toString());
                osUserProject.setProjectId(osUserProjectVo.getProjectId());
                osUserProject.setDomainName(osUserProjectVo.getDomainName());
                osUserProject.setIsAdmin(0);
                if(osUserProjectVo.getIsAdminKeys().size()>0){
                    for (Object key:osUserProjectVo.getIsAdminKeys()
                         ) {
                        if(key.toString().equals(osUserProject.getUserId())){
                            osUserProject.setIsAdmin(1);
                        }
                    }
                }
                osUserProjects.add(osUserProject);
            }
            this.saveBatch(osUserProjects);
        }
        return true;
    }
}
