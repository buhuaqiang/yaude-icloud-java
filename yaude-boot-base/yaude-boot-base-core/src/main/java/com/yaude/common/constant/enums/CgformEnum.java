package com.yaude.common.constant.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * online表單枚舉 代碼生成器用到
 */
public enum CgformEnum {

    /**
     * 單表
     */
    ONE(1, "one", "/jeecg/code-template-online", "default.one", "經典風格"),
    /**
     * 多表
     */
    MANY(2, "many", "/jeecg/code-template-online", "default.onetomany", "經典風格"),
    /**
     * 多表
     */
    ERP(2, "erp", "/jeecg/code-template-online", "erp.onetomany", "ERP風格"),
    /**
     * 多表（jvxe風格）
     *  */
    JVXE_TABLE(2, "jvxe", "/jeecg/code-template-online", "jvxe.onetomany", "JVXE風格"),
    /**
     * 多表（內嵌子表風格）
     */
    INNER_TABLE(2, "innerTable", "/jeecg/code-template-online", "inner-table.onetomany", "內嵌子表風格"),
    /**
     * 多表（tab風格）
     *  */
    TAB(2, "tab", "/jeecg/code-template-online", "tab.onetomany", "Tab風格"),
    /**
     * 樹形列表
     */
    TREE(3, "tree", "/jeecg/code-template-online", "default.tree", "樹形列表");

    /**
     * 類型 1/單表 2/一對多 3/樹
     */
    int type;
    /**
     * 編碼標識
     */
    String code;
    /**
     * 代碼生成器模板路徑
     */
    String templatePath;
    /**
     * 代碼生成器模板路徑
     */
    String stylePath;
    /**
     * 模板風格名稱
     */
    String note;

    /**
     * 構造器
     *
     * @param type 類型 1/單表 2/一對多 3/樹
     * @param code 模板編碼
     * @param templatePath  模板路徑
     * @param stylePath  模板子路徑
     * @param note
     */
    CgformEnum(int type, String code, String templatePath, String stylePath, String note) {
        this.type = type;
        this.code = code;
        this.templatePath = templatePath;
        this.stylePath = stylePath;
        this.note = note;
    }

    /**
     * 根據code獲取模板路徑
     *
     * @param code
     * @return
     */
    public static String getTemplatePathByConfig(String code) {
        return getCgformEnumByConfig(code).templatePath;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getStylePath() {
        return stylePath;
    }

    public void setStylePath(String stylePath) {
        this.stylePath = stylePath;
    }

    /**
     * 根據code找枚舉
     *
     * @param code
     * @return
     */
    public static CgformEnum getCgformEnumByConfig(String code) {
        for (CgformEnum e : CgformEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根據類型找所有
     *
     * @param type
     * @return
     */
    public static List<Map<String, Object>> getJspModelList(int type) {
        List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
        for (CgformEnum e : CgformEnum.values()) {
            if (e.type == type) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("code", e.code);
                map.put("note", e.note);
                ls.add(map);
            }
        }
        return ls;
    }


}
