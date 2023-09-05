package com.yaude.common.system.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查詢多個字典時用到
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictModelMany extends DictModel {

    /**
     * 字典code，根據多個字段code查詢時才用到，用于區分不同的字典選項
     */
    private String dictCode;

}
