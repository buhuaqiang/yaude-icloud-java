package com.yaude.icloud.openstack.service;

import com.yaude.icloud.openstack.entity.OsUserProject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;

import java.util.List;

/**
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
public interface IOsUserProjectService extends IService<OsUserProject> {

    List<OsUserProjectVo> getProjectList(OsUserProjectVo osUserProjectVo);

    boolean updateUserProject(OsUserProjectVo osUserProjectVo);

}
