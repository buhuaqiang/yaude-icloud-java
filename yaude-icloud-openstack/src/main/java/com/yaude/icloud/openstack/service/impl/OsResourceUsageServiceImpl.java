package com.yaude.icloud.openstack.service.impl;

import com.yaude.icloud.openstack.entity.OsResourceUsage;
import com.yaude.icloud.openstack.mapper.OsResourceUsageMapper;
import com.yaude.icloud.openstack.service.IOsResourceUsageService;
import com.yaude.icloud.openstack.vo.OsResourceUsageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 资源用量表
 * @Author: jeecg-boot
 * @Date:   2021-10-21
 * @Version: V1.0
 */
@Service
public class OsResourceUsageServiceImpl extends ServiceImpl<OsResourceUsageMapper, OsResourceUsage> implements IOsResourceUsageService {

    @Autowired
    private OsResourceUsageMapper osResourceUsageMapper;

    @Override
    public List<OsResourceUsageVo> getMonthCountInfo(String projectId,String startMonth,String endMonth) {
        List<OsResourceUsageVo> monthCountInfo = osResourceUsageMapper.getMonthCountInfo(projectId, startMonth, endMonth);
        return monthCountInfo;
    }

    @Override
    public List<OsResourceUsageVo> getYearCountInfo(String projectId,String startYear,String endYear) {
        List<OsResourceUsageVo> monthCountInfo = osResourceUsageMapper.getYearCountInfo(projectId, startYear, endYear);
        return monthCountInfo;
    }
}
