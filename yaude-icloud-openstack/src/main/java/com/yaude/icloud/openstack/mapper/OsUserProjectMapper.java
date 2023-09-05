package com.yaude.icloud.openstack.mapper;

import java.util.List;

import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import org.apache.ibatis.annotations.Param;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
public interface OsUserProjectMapper extends BaseMapper<OsUserProject> {

    /**
     *  查询项目下的用户详情
     * @param projectId
     * @return
     */
    List<OsUserProjectVo> queryUserProjectList(@Param("projectId") String projectId);

    List<OsUserProjectVo> getProject(@Param("userId") String userId);
}
