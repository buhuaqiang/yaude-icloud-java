package com.yaude.modules.system.service.impl.desform;

import com.yaude.common.system.vo.DictModel;
import com.yaude.common.api.desform.ISysTranslateAPI;
import com.yaude.modules.system.service.ISysCategoryService;
import com.yaude.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 表單設計器翻譯API接口（system實現類）
 *
 * @author sunjianlei
 */
@Component
public class SysTranslateAPIImpl implements ISysTranslateAPI {

    @Autowired
    ISysCategoryService sysCategoryService;
    @Autowired
    ISysDictService sysDictService;

    @Override
    public List<String> categoryLoadDictItem(String ids) {
        return sysCategoryService.loadDictItem(ids, false);
    }

    @Override
    public List<String> dictLoadDictItem(String dictCode, String keys) {
        String[] params = dictCode.split(",");
        return sysDictService.queryTableDictByKeys(params[0], params[1], params[2], keys, false);
    }

    @Override
    public List<DictModel> dictGetDictItems(String dictCode) {
        List<DictModel> ls = sysDictService.getDictItems(dictCode);
        if (ls == null) {
            ls = new ArrayList<>();
        }
        return ls;
    }

    @Override
    public List<DictModel> dictLoadDict(String dictCode, String keyword, Integer pageSize) {
        return sysDictService.loadDict(dictCode, keyword, pageSize);
    }

}
