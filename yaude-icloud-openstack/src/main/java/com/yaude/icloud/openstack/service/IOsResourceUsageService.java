package com.yaude.icloud.openstack.service;

import com.yaude.icloud.openstack.entity.OsResourceUsage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsResourceUsageVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 资源用量表
 * @Author: jeecg-boot
 * @Date:   2021-10-21
 * @Version: V1.0
 */
public interface IOsResourceUsageService extends IService<OsResourceUsage> {

    List<OsResourceUsageVo> getMonthCountInfo(@Param("projectId") String projectId,@Param("startMonth") String startMonth,@Param("endMonth") String endMonth);

    List<OsResourceUsageVo> getYearCountInfo(@Param("projectId") String projectId,@Param("startYear") String startYear,@Param("endYear") String endYear);

}
