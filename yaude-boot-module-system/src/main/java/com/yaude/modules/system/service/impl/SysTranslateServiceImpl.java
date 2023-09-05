package com.yaude.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaude.common.constant.CacheConstant;
import org.apache.commons.lang3.StringUtils;
import com.yaude.modules.system.entity.SysTranslate;
import com.yaude.modules.system.mapper.SysTranslateMapper;
import com.yaude.modules.system.service.ISysTranslateService;
import com.yaude.modules.system.vo.SysTranslateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 多語言
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
@Service
public class SysTranslateServiceImpl extends ServiceImpl<SysTranslateMapper, SysTranslate> implements ISysTranslateService {

    @Autowired
    private SysTranslateMapper sysTranslateMapper;

    @Override
    @Cacheable(value = CacheConstant.SYS_TRANSLATE_ALLTABLE_CACHE)
    public List<SysTranslate> queryAllSysTranslateItems() {
        LambdaQueryWrapper<SysTranslate> queryWrapper = new LambdaQueryWrapper<SysTranslate>();
        List<SysTranslate> sysTranslateList = sysTranslateMapper.selectList(queryWrapper);
        log.debug("-------登錄加載多語言配置-----" + sysTranslateList.toString());
        return sysTranslateList;
    }

    @Override
    public List<Map<String, String>>  queryAllList(SysTranslateVO sysTranslateVo) {
        if(sysTranslateVo.getRelateTable()!=null&&!sysTranslateVo.getRelateTable().equals("")){
            String cloumnName = "";
            if(sysTranslateVo.getRelateTable().equals("testad")){
                cloumnName = "text";
            }else if(sysTranslateVo.getRelateTable().equals("sys_permission")){
                cloumnName = "name";
            } else if(sysTranslateVo.getRelateTable().equals("sys_role")) {
                cloumnName = "role_name";
            }else if(sysTranslateVo.getRelateTable().equals("sys_dict_item")) {
                cloumnName = "item_text";
            }
            if(!StringUtils.isEmpty(sysTranslateVo.getKeyword())){
                return sysTranslateMapper.queryAllListByKeyword(sysTranslateVo.getRelateTable(),cloumnName,sysTranslateVo.getKeyword());
            }
            return sysTranslateMapper.queryAllList(sysTranslateVo.getRelateTable(),cloumnName);
        }
        return null;
    }
}
