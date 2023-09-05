package com.yaude.icloud.openstack.mapper;

import java.util.List;

import com.yaude.icloud.openstack.vo.OsApplyVo;
import org.apache.ibatis.annotations.Param;
import com.yaude.icloud.openstack.entity.OsApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
public interface OsApplyMapper extends BaseMapper<OsApply> {

    int getStatus(String id);

    int getCountStatus(@Param("ids") List<String> ids);
}
