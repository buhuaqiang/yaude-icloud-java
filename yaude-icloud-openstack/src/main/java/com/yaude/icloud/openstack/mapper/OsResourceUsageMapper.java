package com.yaude.icloud.openstack.mapper;

import java.util.List;

import com.yaude.icloud.openstack.vo.OsOptionVo;
import com.yaude.icloud.openstack.vo.OsResourceUsageVo;
import org.apache.ibatis.annotations.Param;
import com.yaude.icloud.openstack.entity.OsResourceUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 资源用量表
 * @Author: jeecg-boot
 * @Date:   2021-10-21
 * @Version: V1.0
 */
public interface OsResourceUsageMapper extends BaseMapper<OsResourceUsage> {

    List<OsResourceUsageVo> getMonthCountInfo(@Param("projectId") String projectId,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    List<OsResourceUsageVo> getYearCountInfo(@Param("projectId") String projectId,@Param("startYear") String startMonth,@Param("endYear") String endMonth);

}
