package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.entity.SysPosition;

/**
 * @Description: 職務表
 * @Author: jeecg-boot
 * @Date: 2019-09-19
 * @Version: V1.0
 */
public interface ISysPositionService extends IService<SysPosition> {

    /**
     * 通過code查詢
     */
    SysPosition getByCode(String code);

}
