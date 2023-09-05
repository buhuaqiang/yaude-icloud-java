package com.yaude.common.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * @author 張代浩
 * @desc 通過反射來動態調用get 和 set 方法
 */
@Slf4j
public class ReflectHelper {

    private Class cls;

    /**
     * 傳過來的對象
     */
    private Object obj;

    /**
     * 存放get方法
     */
    private Hashtable<String, Method> getMethods = null;
    /**
     * 存放set方法
     */
    private Hashtable<String, Method> setMethods = null;

    /**
     * 定義構造方法 -- 一般來說是個pojo
     *
     * @param o 目標對象
     */
    public ReflectHelper(Object o) {
        obj = o;
        initMethods();
    }

    /**
     * @desc 初始化
     */
    public void initMethods() {
        getMethods = new Hashtable<String, Method>();
        setMethods = new Hashtable<String, Method>();
        cls = obj.getClass();
        Method[] methods = cls.getMethods();
        // 定義正則表達式，從方法中過濾出getter / setter 函數.
        String gs = "get(\\w+)";
        Pattern getM = Pattern.compile(gs);
        String ss = "set(\\w+)";
        Pattern setM = Pattern.compile(ss);
        // 把方法中的"set" 或者 "get" 去掉
        String rapl = "$1";
        String param;
        for (int i = 0; i < methods.length; ++i) {
            Method m = methods[i];
            String methodName = m.getName();
            if (Pattern.matches(gs, methodName)) {
                param = getM.matcher(methodName).replaceAll(rapl).toLowerCase();
                getMethods.put(param, m);
            } else if (Pattern.matches(ss, methodName)) {
                param = setM.matcher(methodName).replaceAll(rapl).toLowerCase();
                setMethods.put(param, m);
            } else {
                // logger.info(methodName + " 不是getter,setter方法！");
            }
        }
    }

    /**
     * @desc 調用set方法
     */
    public boolean setMethodValue(String property, Object object) {
        Method m = setMethods.get(property.toLowerCase());
        if (m != null) {
            try {
                // 調用目標類的setter函數
                m.invoke(obj, object);
                return true;
            } catch (Exception ex) {
                log.info("invoke getter on " + property + " error: " + ex.toString());
                return false;
            }
        }
        return false;
    }

    /**
     * @desc 調用set方法
     */
    public Object getMethodValue(String property) {
        Object value = null;
        Method m = getMethods.get(property.toLowerCase());
        if (m != null) {
            try {
                /*
                 * 調用obj類的setter函數
                 */
                value = m.invoke(obj, new Object[]{});

            } catch (Exception ex) {
                log.info("invoke getter on " + property + " error: " + ex.toString());
            }
        }
        return value;
    }

    /**
     * 把map中的內容全部注入到obj中
     *
     * @param data
     * @return
     */
    public Object setAll(Map<String, Object> data) {
        if (data == null || data.keySet().size() <= 0) {
            return null;
        }
        for (Entry<String, Object> entry : data.entrySet()) {
            this.setMethodValue(entry.getKey(), entry.getValue());
        }
        return obj;
    }

    /**
     * 把map中的內容全部注入到obj中
     *
     * @param o
     * @param data
     * @return
     */
    public static Object setAll(Object o, Map<String, Object> data) {
        ReflectHelper reflectHelper = new ReflectHelper(o);
        reflectHelper.setAll(data);
        return o;
    }

    /**
     * 把map中的內容全部注入到新實例中
     *
     * @param clazz
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T setAll(Class<T> clazz, Map<String, Object> data) {
        T o = null;
        try {
            o = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            o = null;
            return o;
        }
        return (T) setAll(o, data);
    }

    /**
     * 根據傳入的class將mapList轉換為實體類list
     *
     * @param mapist
     * @param clazz
     * @return
     */
    public static <T> List<T> transList2Entrys(List<Map<String, Object>> mapist, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        if (mapist != null && mapist.size() > 0) {
            for (Map<String, Object> data : mapist) {
                list.add(ReflectHelper.setAll(clazz, data));
            }
        }
        return list;
    }

    /**
     * 根據屬性名獲取屬性值
     */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 獲取屬性值
     */
    public static Object getFieldVal(String fieldName, Object o) {
        try {
            // 暴力反射獲取屬性
            Field filed = o.getClass().getDeclaredField(fieldName);
            // 設置反射時取消Java的訪問檢查，暴力訪問
            filed.setAccessible(true);
            Object val = filed.get(o);
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 獲取屬性名數組
     */
    public static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            //log.info(fields[i].getType());
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 獲取屬性類型(type)，屬性名(name)，屬性值(value)的map組成的list
     */
    public static List<Map> getFiledsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        List<Map> list = new ArrayList<Map>();
        Map<String, Object> infoMap = null;
        for (int i = 0; i < fields.length; i++) {
            infoMap = new HashMap<String, Object>();
            infoMap.put("type", fields[i].getType().toString());
            infoMap.put("name", fields[i].getName());
            infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
            list.add(infoMap);
        }
        return list;
    }

    /**
     * 獲取對象的所有屬性值，返回一個對象數組
     */
    public static Object[] getFiledValues(Object o) {
        String[] fieldNames = getFiledName(o);
        Object[] value = new Object[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            value[i] = getFieldValueByName(fieldNames[i], o);
        }
        return value;
    }

}