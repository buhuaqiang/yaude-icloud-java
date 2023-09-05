package com.yaude.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.yaude.modules.system.entity.SysTranslate;

import java.util.List;
import java.util.Map;

/**
 * @Description: 多語言
 * @Author: jeecg-boot
 * @Date:   2021-08-23
 * @Version: V1.0
 */
public interface SysTranslateMapper extends BaseMapper<SysTranslate> {

    //通過表名+字段名查詢多語言配置
    @Deprecated
    @Select("select t.id as \"relateId\",\"${table}\" as \"relateTable\",t.${key} as \"text\",s.id,s.chinese,s.taiwan,s.english " +
            " from ${table}  t left join sys_translate s on s.relate_id =t.id and s.relate_table = \"${table}\"")
    public List<Map<String, String>> queryAllList(@Param("table") String table, @Param("key") String key);

    //通過表名+字段名+關鍵字查詢多語言配置
    @Deprecated
    @Select("select t.id as \"relateId\",\"${table}\" as \"relateTable\",t.${key} as \"text\",s.id,s.chinese,s.taiwan,s.english " +
            " from ${table}  t left join sys_translate s on s.relate_id =t.id and s.relate_table = \"${table}\" where t.${key} like \"%${keyword}%\"")
    public List<Map<String, String>> queryAllListByKeyword(@Param("table") String table, @Param("key") String key,@Param("keyword") String keyword);

}
