package com.yaude.common.util.dynamic.db;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.codegenerate.generate.util.SimpleFormat;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 趙俊夫
 * @version V1.0
 * @Title:FreemarkerHelper
 * @description:Freemarker引擎協助類
 * @date Jul 5, 2013 2:58:29 PM
 */
@Slf4j
public class FreemarkerParseFactory {

    private static final String ENCODE = "utf-8";
    /**
     * 參數格式化工具類
     */
    private static final String MINI_DAO_FORMAT = "DaoFormat";

    /**
     * 文件緩存
     */
    private static final Configuration _tplConfig = new Configuration();
    /**
     * SQL 緩存
     */
    private static final Configuration _sqlConfig = new Configuration();

    private static StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

    // 使用內嵌的(?ms)打開單行和多行模式
    private final static Pattern p = Pattern
            .compile("(?ms)/\\*.*?\\*/|^\\s*//.*?$");

    static {
        _tplConfig.setClassForTemplateLoading(
                new FreemarkerParseFactory().getClass(), "/");
        _tplConfig.setNumberFormat("0.#####################");
        _sqlConfig.setTemplateLoader(stringTemplateLoader);
        _sqlConfig.setNumberFormat("0.#####################");
        //classic_compatible設置，解決報空指針錯誤
        _sqlConfig.setClassicCompatible(true);
    }

    /**
     * 判斷模板是否存在
     *
     * @throws Exception
     */
    public static boolean isExistTemplate(String tplName) throws Exception {
        try {
            Template mytpl = _tplConfig.getTemplate(tplName, "UTF-8");
            if (mytpl == null) {
                return false;
            }
        } catch (Exception e) {
            //update-begin--Author:scott  Date:20180320 for：解決問題 - 錯誤提示sql文件不存在，實際問題是sql freemarker用法錯誤-----
            if (e instanceof ParseException) {
                log.error(e.getMessage(), e.fillInStackTrace());
                throw new Exception(e);
            }
            log.debug("----isExistTemplate----" + e.toString());
            //update-end--Author:scott  Date:20180320 for：解決問題 - 錯誤提示sql文件不存在，實際問題是sql freemarker用法錯誤------
            return false;
        }
        return true;
    }

    /**
     * 解析ftl模板
     *
     * @param tplName 模板名
     * @param paras   參數
     * @return
     */
    public static String parseTemplate(String tplName, Map<String, Object> paras) {
        try {
            log.debug(" minidao sql templdate : " + tplName);
            StringWriter swriter = new StringWriter();
            Template mytpl = _tplConfig.getTemplate(tplName, ENCODE);
            if (paras.containsKey(MINI_DAO_FORMAT)) {
                throw new RuntimeException("DaoFormat 是 minidao 保留關鍵字，不允許使用 ，請更改參數定義！");
            }
            paras.put(MINI_DAO_FORMAT, new SimpleFormat());
            mytpl.process(paras, swriter);
            String sql = getSqlText(swriter.toString());
            paras.remove(MINI_DAO_FORMAT);
            return sql;
        } catch (Exception e) {
            log.error(e.getMessage(), e.fillInStackTrace());
            log.error("發送一次的模板key:{ " + tplName + " }");
            //System.err.println(e.getMessage());
            //System.err.println("模板名:{ "+ tplName +" }");
            throw new RuntimeException("解析SQL模板異常");
        }
    }

    /**
     * 解析ftl
     *
     * @param tplContent 模板內容
     * @param paras      參數
     * @return String 模板解析后內容
     */
    public static String parseTemplateContent(String tplContent,
                                              Map<String, Object> paras) {
        try {
            StringWriter swriter = new StringWriter();
            if (stringTemplateLoader.findTemplateSource("sql_" + tplContent.hashCode()) == null) {
                stringTemplateLoader.putTemplate("sql_" + tplContent.hashCode(), tplContent);
            }
            Template mytpl = _sqlConfig.getTemplate("sql_" + tplContent.hashCode(), ENCODE);
            if (paras.containsKey(MINI_DAO_FORMAT)) {
                throw new RuntimeException("DaoFormat 是 minidao 保留關鍵字，不允許使用 ，請更改參數定義！");
            }
            paras.put(MINI_DAO_FORMAT, new SimpleFormat());
            mytpl.process(paras, swriter);
            String sql = getSqlText(swriter.toString());
            paras.remove(MINI_DAO_FORMAT);
            return sql;
        } catch (Exception e) {
            log.error(e.getMessage(), e.fillInStackTrace());
            log.error("發送一次的模板key:{ " + tplContent + " }");
            //System.err.println(e.getMessage());
            //System.err.println("模板內容:{ "+ tplContent +" }");
            throw new RuntimeException("解析SQL模板異常");
        }
    }

    /**
     * 除去無效字段，去掉注釋 不然批量處理可能報錯 去除無效的等于
     */
    private static String getSqlText(String sql) {
        // 將注釋替換成""
        sql = p.matcher(sql).replaceAll("");
        sql = sql.replaceAll("\\n", " ").replaceAll("\\t", " ")
                .replaceAll("\\s{1,}", " ").trim();
        // 去掉 最后是 where這樣的問題
        if (sql.endsWith("where") || sql.endsWith("where ")) {
            sql = sql.substring(0, sql.lastIndexOf("where"));
        }
        // 去掉where and 這樣的問題
        int index = 0;
        while ((index = StringUtils.indexOfIgnoreCase(sql, "where and", index)) != -1) {
            sql = sql.substring(0, index + 5)
                    + sql.substring(index + 9, sql.length());
        }
        // 去掉 , where 這樣的問題
        index = 0;
        while ((index = StringUtils.indexOfIgnoreCase(sql, ", where", index)) != -1) {
            sql = sql.substring(0, index)
                    + sql.substring(index + 1, sql.length());
        }
        // 去掉 最后是 ,這樣的問題
        if (sql.endsWith(",") || sql.endsWith(", ")) {
            sql = sql.substring(0, sql.lastIndexOf(","));
        }
        return sql;
    }
}