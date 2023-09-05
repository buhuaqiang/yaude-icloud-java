package com.yaude.modules.system.vo;

import com.yaude.modules.system.entity.SysDictItem;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

@Data
public class SysDictPage {

    /**
     * 主鍵
     */
    private String id;
    /**
     * 字典名稱
     */
    @Excel(name = "字典名稱", width = 20)
    private String dictName;

    /**
     * 字典編碼
     */
    @Excel(name = "字典編碼", width = 30)
    private String dictCode;
    /**
     * 刪除狀態
     */
    private Integer delFlag;
    /**
     * 描述
     */
    @Excel(name = "描述", width = 30)
    private String description;

    @ExcelCollection(name = "字典列表")
    private List<SysDictItem> sysDictItemList;

}
