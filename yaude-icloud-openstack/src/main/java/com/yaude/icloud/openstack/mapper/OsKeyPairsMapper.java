package com.yaude.icloud.openstack.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.yaude.icloud.openstack.entity.OsKeyPairs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 秘钥对
 * @Author: jeecg-boot
 * @Date:   2021-10-18
 * @Version: V1.0
 */
public interface OsKeyPairsMapper extends BaseMapper<OsKeyPairs> {
    List<OsKeyPairs>  getPrivateKey(String projectId );


}
