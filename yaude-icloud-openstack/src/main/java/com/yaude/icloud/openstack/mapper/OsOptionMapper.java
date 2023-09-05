package com.yaude.icloud.openstack.mapper;

import java.util.List;

import com.yaude.icloud.openstack.vo.OsOptionVo;
import org.apache.ibatis.annotations.Param;
import com.yaude.icloud.openstack.entity.OsOption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * @Description: 審核意見細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface OsOptionMapper extends BaseMapper<OsOption> {

    OsOption  getText(@Param("id") String id);
    List<OsOptionVo>  getApply(@Param("id") String id,String userId);

    OsOptionVo getProjectId(String applyId,String applyType);

    void deleteOption(String applyId,String applyType);
}
