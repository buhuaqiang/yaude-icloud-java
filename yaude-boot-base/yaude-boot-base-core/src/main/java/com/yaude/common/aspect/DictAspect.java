package com.yaude.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaude.common.aspect.annotation.Dict;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.vo.DictModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 字典aop類
 * @Author: dangzhenghui
 * @Date: 2019-3-17 21:50
 * @Version: 1.0
 */
@Aspect
@Component
@Slf4j
public class DictAspect {

    @Autowired
    private CommonAPI commonAPI;
    @Autowired
    public RedisTemplate redisTemplate;

    // 定義切點Pointcut
    @Pointcut("execution(public * org.jeecg.modules..*.*Controller.*(..)) ||" +
            " execution(public * com.yaude.modules..*.*Controller.*(..)) ||"+
            " execution(public * com.yaude..*.*Controller.*(..)) ")
    public void excudeService() {
    }

    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    	long time1=System.currentTimeMillis();	
        Object result = pjp.proceed();
        long time2=System.currentTimeMillis();
        log.debug("獲取JSON數據 耗時："+(time2-time1)+"ms");
        long start=System.currentTimeMillis();
        this.parseDictText(result);
        long end=System.currentTimeMillis();
        log.debug("注入字典到JSON數據  耗時"+(end-start)+"ms");
        return result;
    }

    /**
     * 本方法針對返回對象為Result 的IPage的分頁列表數據進行動態字典注入
     * 字典注入實現 通過對實體類添加注解@dict 來標識需要的字典內容,字典分為單字典code即可 ，table字典 code table text配合使用與原來jeecg的用法相同
     * 示例為SysUser   字段為sex 添加了注解@Dict(dicCode = "sex") 會在字典服務立馬查出來對應的text 然后在請求list的時候將這個字典text，已字段名稱加_dictText形式返回到前端
     * 例輸入當前返回值的就會多出一個sex_dictText字段
     * {
     *      sex:1,
     *      sex_dictText:"男"
     * }
     * 前端直接取值sext_dictText在table里面無需再進行前端的字典轉換了
     *  customRender:function (text) {
     *               if(text==1){
     *                 return "男";
     *               }else if(text==2){
     *                 return "女";
     *               }else{
     *                 return text;
     *               }
     *             }
     *             目前vue是這么進行字典渲染到table上的多了就很麻煩了 這個直接在服務端渲染完成前端可以直接用
     * @param result
     */
    private void parseDictText(Object result) {
        if (result instanceof Result) {
            if (((Result) result).getResult() instanceof IPage) {
                List<JSONObject> items = new ArrayList<>();

                //step.1 篩選出加了 Dict 注解的字段列表
                List<Field> dictFieldList = new ArrayList<>();
                // 字典數據列表， key = 字典code，value=數據列表
                Map<String, List<String>> dataListMap = new HashMap<>();

                for (Object record : ((IPage) ((Result) result).getResult()).getRecords()) {
                    ObjectMapper mapper = new ObjectMapper();
                    String json="{}";
                    try {
                        //解決@JsonFormat注解解析不了的問題詳見SysAnnouncement類的@JsonFormat
                         json = mapper.writeValueAsString(record);
                    } catch (JsonProcessingException e) {
                        log.error("json解析失敗"+e.getMessage(),e);
                    }
                    JSONObject item = JSONObject.parseObject(json);
                    //update-begin--Author:scott -- Date:20190603 ----for：解決繼承實體字段無法翻譯問題------
                    //for (Field field : record.getClass().getDeclaredFields()) {
                    // 遍歷所有字段，把字典Code取出來，放到 map 里
                    for (Field field : oConvertUtils.getAllFields(record)) {
                        String value = item.getString(field.getName());
                        if (oConvertUtils.isEmpty(value)) {
                            continue;
                        }
                    //update-end--Author:scott  -- Date:20190603 ----for：解決繼承實體字段無法翻譯問題------
                        if (field.getAnnotation(Dict.class) != null) {
                            if (!dictFieldList.contains(field)) {
                                dictFieldList.add(field);
                            }
                            String code = field.getAnnotation(Dict.class).dicCode();
                            String text = field.getAnnotation(Dict.class).dicText();
                            String table = field.getAnnotation(Dict.class).dictTable();

                            List<String> dataList;
                            String dictCode = code;
                            if (!StringUtils.isEmpty(table)) {
                                dictCode = String.format("%s,%s,%s", table, text, code);
                            }
                            dataList = dataListMap.computeIfAbsent(dictCode, k -> new ArrayList<>());
                            this.listAddAllDeduplicate(dataList, Arrays.asList(value.split(",")));
                        }
                        //date類型默認轉換string格式化日期
                        if (field.getType().getName().equals("java.util.Date")&&field.getAnnotation(JsonFormat.class)==null&&item.get(field.getName())!=null){
                            SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                        }
                    }
                    items.add(item);
                }

                //step.2 調用翻譯方法，一次性翻譯
                Map<String, List<DictModel>> translText = this.translateAllDict(dataListMap);

                //step.3 將翻譯結果填充到返回結果里
                for (JSONObject record : items) {
                    for (Field field : dictFieldList) {
                        String code = field.getAnnotation(Dict.class).dicCode();
                        String text = field.getAnnotation(Dict.class).dicText();
                        String table = field.getAnnotation(Dict.class).dictTable();

                        String fieldDictCode = code;
                        if (!StringUtils.isEmpty(table)) {
                            fieldDictCode = String.format("%s,%s,%s", table, text, code);
                        }

                        String value = record.getString(field.getName());
                        if (oConvertUtils.isNotEmpty(value)) {
                            List<DictModel> dictModels = translText.get(fieldDictCode);
                            if(dictModels==null || dictModels.size()==0){
                                continue;
                            }

                            String textValue = this.translDictText(dictModels, value);
                            log.debug(" 字典Val : " + textValue);
                            log.debug(" __翻譯字典字段__ " + field.getName() + CommonConstant.DICT_TEXT_SUFFIX + "： " + textValue);

                            // TODO-sun 測試輸出，待刪
                            log.debug(" ---- dictCode: " + fieldDictCode);
                            log.debug(" ---- value: " + value);
                            log.debug(" ----- text: " + textValue);
                            log.debug(" ---- dictModels: " + JSON.toJSONString(dictModels));

                            record.put(field.getName() + CommonConstant.DICT_TEXT_SUFFIX, textValue);
                        }
                    }
                }

                ((IPage) ((Result) result).getResult()).setRecords(items);
            }

        }
    }

    /**
     * list 去重添加
     */
    private void listAddAllDeduplicate(List<String> dataList, List<String> addList) {
        // 篩選出dataList中沒有的數據
        List<String> filterList = addList.stream().filter(i -> !dataList.contains(i)).collect(Collectors.toList());
        dataList.addAll(filterList);
    }

    /**
     * 一次性把所有的字典都翻譯了
     * 1.  所有的普通數據字典的所有數據只執行一次SQL
     * 2.  表字典相同的所有數據只執行一次SQL
     * @param dataListMap
     * @return
     */
    private Map<String, List<DictModel>> translateAllDict(Map<String, List<String>> dataListMap) {
        // 翻譯后的字典文本，key=dictCode
        Map<String, List<DictModel>> translText = new HashMap<>();
        // 需要翻譯的數據（有些可以從redis緩存中獲取，就不走數據庫查詢）
        List<String> needTranslData = new ArrayList<>();
        //step.1 先通過redis中獲取緩存字典數據
        for (String dictCode : dataListMap.keySet()) {
            List<String> dataList = dataListMap.get(dictCode);
            if (dataList.size() == 0) {
                continue;
            }
            // 表字典需要翻譯的數據
            List<String> needTranslDataTable = new ArrayList<>();
            for (String s : dataList) {
                String data = s.trim();
                if (data.length() == 0) {
                    continue; //跳過循環
                }
                if (dictCode.contains(",")) {
                    String keyString = String.format("sys:cache:dictTable::SimpleKey [%s,%s]", dictCode, data);
                    if (redisTemplate.hasKey(keyString)) {
                        try {
                            String text = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                            List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                            list.add(new DictModel(data, text));
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    } else if (!needTranslDataTable.contains(data)) {
                        // 去重添加
                        needTranslDataTable.add(data);
                    }
                } else {
                    String keyString = String.format("sys:cache:dict::%s:%s", dictCode, data);
                    if (redisTemplate.hasKey(keyString)) {
                        try {
                            String text = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                            List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                            list.add(new DictModel(data, text));
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    } else if (!needTranslData.contains(data)) {
                        // 去重添加
                        needTranslData.add(data);
                    }
                }

            }
            //step.2 調用數據庫翻譯表字典
            if (needTranslDataTable.size() > 0) {
                String[] arr = dictCode.split(",");
                String table = arr[0], text = arr[1], code = arr[2];
                String values = String.join(",", needTranslDataTable);
                log.info("translateDictFromTableByKeys.dictCode:" + dictCode);
                log.info("translateDictFromTableByKeys.values:" + values);
                List<DictModel> texts = commonAPI.translateDictFromTableByKeys(table, text, code, values);
                log.info("translateDictFromTableByKeys.result:" + texts);
                List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                list.addAll(texts);

                // 做 redis 緩存
                for (DictModel dict : texts) {
                    String redisKey = String.format("sys:cache:dictTable::SimpleKey [%s,%s]", dictCode, dict.getValue());
                    try {
                        redisTemplate.opsForValue().set(redisKey, dict.getText());
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
        }

        //step.3 調用數據庫進行翻譯普通字典
        if (needTranslData.size() > 0) {
            List<String> dictCodeList = Arrays.asList(dataListMap.keySet().toArray(new String[]{}));
            // 將不包含逗號的字典code篩選出來，因為帶逗號的是表字典，而不是普通的數據字典
            List<String> filterDictCodes = dictCodeList.stream().filter(key -> !key.contains(",")).collect(Collectors.toList());
            String dictCodes = String.join(",", filterDictCodes);
            String values = String.join(",", needTranslData);
            log.info("translateManyDict.dictCodes:" + dictCodes);
            log.info("translateManyDict.values:" + values);
            Map<String, List<DictModel>> manyDict = commonAPI.translateManyDict(dictCodes, values);
            log.info("translateManyDict.result:" + manyDict);
            for (String dictCode : manyDict.keySet()) {
                List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                List<DictModel> newList = manyDict.get(dictCode);
                list.addAll(newList);

                // 做 redis 緩存
                for (DictModel dict : newList) {
                    String redisKey = String.format("sys:cache:dict::%s:%s", dictCode, dict.getValue());
                    try {
                        redisTemplate.opsForValue().set(redisKey, dict.getText());
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
        }
        return translText;
    }

    /**
     * 字典值替換文本
     *
     * @param dictModels
     * @param values
     * @return
     */
    private String translDictText(List<DictModel> dictModels, String values) {
        List<String> result = new ArrayList<>();

        // 允許多個逗號分隔，允許傳數組對象
        String[] splitVal = values.split(",");
        for (String val : splitVal) {
            String dictText = val;
            for (DictModel dict : dictModels) {
                if (val.equals(dict.getValue())) {
                    dictText = dict.getText();
                    break;
                }
            }
            result.add(dictText);
        }
        return String.join(",", result);
    }

    /**
     *  翻譯字典文本
     * @param code
     * @param text
     * @param table
     * @param key
     * @return
     */
    @Deprecated
    private String translateDictValue(String code, String text, String table, String key) {
    	if(oConvertUtils.isEmpty(key)) {
    		return null;
    	}
        StringBuffer textValue=new StringBuffer();
        String[] keys = key.split(",");
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 key : "+ k);
            if (k.trim().length() == 0) {
                continue; //跳過循環
            }
            //update-begin--Author:scott -- Date:20210531 ----for： !56 優化微服務應用下存在表字段需要字典翻譯時加載緩慢問題-----
            if (!StringUtils.isEmpty(table)){
                log.info("--DictAspect------dicTable="+ table+" ,dicText= "+text+" ,dicCode="+code);
                String keyString = String.format("sys:cache:dictTable::SimpleKey [%s,%s,%s,%s]",table,text,code,k.trim());
                    if (redisTemplate.hasKey(keyString)){
                    try {
                        tmpValue = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }
                }else {
                    tmpValue= commonAPI.translateDictFromTable(table,text,code,k.trim());
                }
            }else {
                String keyString = String.format("sys:cache:dict::%s:%s",code,k.trim());
                if (redisTemplate.hasKey(keyString)){
                    try {
                        tmpValue = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                    } catch (Exception e) {
                       log.warn(e.getMessage());
                    }
                }else {
                    tmpValue = commonAPI.translateDict(code, k.trim());
                }
            }
            //update-end--Author:scott -- Date:20210531 ----for： !56 優化微服務應用下存在表字段需要字典翻譯時加載緩慢問題-----

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }

        }
        return textValue.toString();
    }

}
