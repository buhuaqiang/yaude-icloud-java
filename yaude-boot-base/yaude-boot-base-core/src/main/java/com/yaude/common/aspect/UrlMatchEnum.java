package com.yaude.common.aspect;

/**
 * @Author scott
 * @Date 2020/1/14 13:36
 * @Description: 請求URL與菜單路由URL轉換規則（方便于采用菜單路由URL來配置數據權限規則）
 */
public enum UrlMatchEnum {
    CGFORM_DATA("/online/cgform/api/getData/", "/online/cgformList/"),
    CGFORM_EXCEL_DATA("/online/cgform/api/exportXls/", "/online/cgformList/"),
    CGFORM_TREE_DATA("/online/cgform/api/getTreeData/", "/online/cgformList/"),
    CGREPORT_DATA("/online/cgreport/api/getColumnsAndData/", "/online/cgreport/"),
    CGREPORT_EXCEL_DATA("/online/cgreport/api/exportXls/", "/online/cgreport/");


    UrlMatchEnum(String url, String match_url) {
        this.url = url;
        this.match_url = match_url;
    }

    /**
     * Request 請求 URL前綴
     */
    private String url;
    /**
     * 菜單路由 URL前綴 (對應菜單路徑)
     */
    private String match_url;

    /**
     * 根據req url 獲取到菜單配置路徑（前端頁面路由URL）
     *
     * @param url
     * @return
     */
    public static String getMatchResultByUrl(String url) {
        //獲取到枚舉
        UrlMatchEnum[] values = UrlMatchEnum.values();
        //加強for循環進行遍歷操作
        for (UrlMatchEnum lr : values) {
            //如果遍歷獲取的type和參數type一致
            if (url.indexOf(lr.url) != -1) {
                //返回type對象的desc
                return url.replace(lr.url, lr.match_url);
            }
        }
        return null;
    }


//    public static void main(String[] args) {
//        /**
//         * 比如request真實請求URL: /online/cgform/api/getData/81fcf7d8922d45069b0d5ba983612d3a
//         * 轉換匹配路由URL后（對應配置的菜單路徑）:/online/cgformList/81fcf7d8922d45069b0d5ba983612d3a
//         */
//        System.out.println(UrlMatchEnum.getMatchResultByUrl("/online/cgform/api/getData/81fcf7d8922d45069b0d5ba983612d3a"));
//    }
}