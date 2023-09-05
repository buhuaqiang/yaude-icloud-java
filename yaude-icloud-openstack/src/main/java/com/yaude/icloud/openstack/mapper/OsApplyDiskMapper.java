package com.yaude.icloud.openstack.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 磁盘申请明细档
 * @Author: jeecg-boot
 * @Date:   2021-10-07
 * @Version: V1.0
 */
public interface OsApplyDiskMapper extends BaseMapper<OsApplyDisk> {

    int getStatus(String id);

    int getCountStatus(@Param("ids") List<String> ids);
}
