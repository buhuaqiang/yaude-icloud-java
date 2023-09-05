package com.yaude.modules.system.service;

import com.yaude.modules.system.entity.SysTranslate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.vo.SysTranslateVO;

import java.util.List;
import java.util.Map;

/**
 * @Description: 多語言
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
public interface ISysTranslateService extends IService<SysTranslate> {

    public List<SysTranslate> queryAllSysTranslateItems();

    public List<Map<String, String>>  queryAllList(SysTranslateVO sysTranslateVo);
}
