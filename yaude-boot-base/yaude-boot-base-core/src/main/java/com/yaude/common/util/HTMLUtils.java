package com.yaude.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * HTML 工具類
 */
public class HTMLUtils {

    /**
     * 獲取HTML內的文本，不包含標簽
     *
     * @param html HTML 代碼
     */
    public static String getInnerText(String html) {
        if (StringUtils.isNotBlank(html)) {
            //去掉 html 的標簽
            String content = html.replaceAll("</?[^>]+>", "");
            // 將多個空格合并成一個空格
            content = content.replaceAll("(&nbsp;)+", "&nbsp;");
            // 反向轉義字符
            content = HtmlUtils.htmlUnescape(content);
            return content.trim();
        }
        return "";
    }

}
