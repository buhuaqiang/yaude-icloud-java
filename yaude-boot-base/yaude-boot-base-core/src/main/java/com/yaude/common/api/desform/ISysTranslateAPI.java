package com.yaude.common.api.desform;

import com.yaude.common.system.vo.DictModel;

import java.util.List;

/**
 * 表單設計器【System】翻譯API接口
 *
 * @author sunjianlei
 */
public interface ISysTranslateAPI {

    /**
     * 查詢分類字典翻譯
     */
    List<String> categoryLoadDictItem(String ids);

    /**
     * 根據字典code加載字典text
     *
     * @param dictCode 順序：tableName,text,code
     * @param keys     要查詢的key
     * @return
     */
    List<String> dictLoadDictItem(String dictCode, String keys);

    /**
     * 獲取字典數據
     *
     * @param dictCode 順序：tableName,text,code
     * @param dictCode 要查詢的key
     * @return
     */
    List<DictModel> dictGetDictItems(String dictCode);

    /**
     * 【JSearchSelectTag下拉搜索組件專用接口】
     * 大數據量的字典表 走異步加載  即前端輸入內容過濾數據
     *
     * @param dictCode 字典code格式：table,text,code
     * @return
     */
    List<DictModel> dictLoadDict(String dictCode, String keyword, Integer pageSize);

}
