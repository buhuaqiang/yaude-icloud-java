package com.yaude.icloud.openstack.service;

import com.yaude.icloud.openstack.entity.OsKeyPairs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsApplyVo;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 秘钥对
 * @Author: jeecg-boot
 * @Date:   2021-10-18
 * @Version: V1.0
 */
public interface IOsKeyPairsService extends IService<OsKeyPairs> {
    List<OsKeyPairs> getProjects();
    OsKeyPairs getkey(OsKeyPairs osKeyPairs) ;


}
