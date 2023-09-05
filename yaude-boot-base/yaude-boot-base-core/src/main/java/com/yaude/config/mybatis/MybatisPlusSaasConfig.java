package com.yaude.config.mybatis;

import java.util.ArrayList;
import java.util.List;

import com.yaude.common.util.oConvertUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * 單數據源配置（jeecg.datasource.open = false時生效）
 * @Author zhoujf
 *
 */
@Configuration
@MapperScan(value={"com.yaude.modules.**.mapper*","com.yaude.icloud.**.mapper*"})
public class MybatisPlusSaasConfig {
    /**
     * tenant_id 字段名
     */
    private static final String TENANT_FIELD_NAME = "tenant_id";
    /**
     * 哪些表需要做多租戶 表需要添加一個字段 tenant_id
     */
    private static final List<String> tenantTable = new ArrayList<String>();

    static {
        tenantTable.add("demo");
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                String tenant_id = oConvertUtils.getString(TenantContext.getTenant(),"0");
                return new LongValue(tenant_id);
            }

            @Override
            public String getTenantIdColumn(){
                return TENANT_FIELD_NAME;
            }

            // 返回 true 表示不走租戶邏輯
            @Override
            public boolean ignoreTable(String tableName) {
                for(String temp: tenantTable){
                    if(temp.equalsIgnoreCase(tableName)){
                        return false;
                    }
                }
                return true;
            }
        }));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

//    /**
//     * 下個版本會刪除，現在為了避免緩存出現問題不得不配置
//     * @return
//     */
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> configuration.setUseDeprecatedExecutor(false);
//    }
//    /**
//     * mybatis-plus SQL執行效率插件【生產環境可以關閉】
//     */
//    @Bean
//    public PerformanceInterceptor performanceInterceptor() {
//        return new PerformanceInterceptor();
//    }

}
