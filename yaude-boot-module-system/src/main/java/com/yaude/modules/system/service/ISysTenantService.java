package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.entity.SysTenant;

import java.util.Collection;
import java.util.List;

public interface ISysTenantService extends IService<SysTenant> {

    /**
     * 查詢有效的租戶
     *
     * @param idList
     * @return
     */
    List<SysTenant> queryEffectiveTenant(Collection<String> idList);

    /**
     * 返回某個租戶被多少個用戶引用了
     *
     * @param id
     * @return
     */
    int countUserLinkTenant(String id);

    /**
     * 根據ID刪除租戶，會判斷是否已被引用
     *
     * @param id
     * @return
     */
    boolean removeTenantById(String id);

}
