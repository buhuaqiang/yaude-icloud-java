package com.yaude.config.shiro;

import com.yaude.config.shiro.filters.CustomShiroFilterFactoryBean;
import com.yaude.config.shiro.filters.JwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisClusterManager;
import org.crazycake.shiro.RedisManager;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.util.oConvertUtils;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.*;

/**
 * @author: Scott
 * @date: 2018/2/7
 * @description: shiro 配置類
 */

@Slf4j
@Configuration
public class ShiroConfig {

    @Value("${jeecg.shiro.excludeUrls}")
    private String excludeUrls;
    @Resource
    LettuceConnectionFactory lettuceConnectionFactory;
    @Autowired
    private Environment env;


    /**
     * Filter Chain定義說明
     *
     * 1、一個URL可以配置多個Filter，使用逗號分隔
     * 2、當設置多個過濾器時，全部驗證通過，才視為通過
     * 3、部分過濾器可指定參數，如perms，roles
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        CustomShiroFilterFactoryBean shiroFilterFactoryBean = new CustomShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 攔截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        if(oConvertUtils.isNotEmpty(excludeUrls)){
            String[] permissionUrl = excludeUrls.split(",");
            for(String url : permissionUrl){
                filterChainDefinitionMap.put(url,"anon");
            }
        }
        // 配置不會被攔截的鏈接 順序判斷
        filterChainDefinitionMap.put("/license/**", "anon");//license測試
        filterChainDefinitionMap.put("/sys/cas/client/validateLogin", "anon"); //cas驗證登錄
        filterChainDefinitionMap.put("/sys/randomImage/**", "anon"); //登錄驗證碼接口排除
        filterChainDefinitionMap.put("/sys/checkCaptcha", "anon"); //登錄驗證碼接口排除
        filterChainDefinitionMap.put("/sys/login", "anon"); //登錄接口排除
        filterChainDefinitionMap.put("/sys/mLogin", "anon"); //登錄接口排除
        filterChainDefinitionMap.put("/sys/logout", "anon"); //登出接口排除
        filterChainDefinitionMap.put("/sys/thirdLogin/**", "anon"); //第三方登錄
        filterChainDefinitionMap.put("/sys/getEncryptedString", "anon"); //獲取加密串
        filterChainDefinitionMap.put("/sys/sms", "anon");//短信驗證碼
        filterChainDefinitionMap.put("/sys/phoneLogin", "anon");//手機登錄
        filterChainDefinitionMap.put("/sys/user/checkOnlyUser", "anon");//校驗用戶是否存在
        filterChainDefinitionMap.put("/sys/user/register", "anon");//用戶注冊
        filterChainDefinitionMap.put("/sys/user/phoneVerification", "anon");//用戶忘記密碼驗證手機號
        filterChainDefinitionMap.put("/sys/user/passwordChange", "anon");//用戶更改密碼
        filterChainDefinitionMap.put("/auth/2step-code", "anon");//登錄驗證碼
        filterChainDefinitionMap.put("/sys/common/static/**", "anon");//圖片預覽 &下載文件不限制token
        filterChainDefinitionMap.put("/sys/common/pdf/**", "anon");//pdf預覽
        filterChainDefinitionMap.put("/generic/**", "anon");//pdf預覽需要文件
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/**/*.js", "anon");
        filterChainDefinitionMap.put("/**/*.css", "anon");
        filterChainDefinitionMap.put("/**/*.html", "anon");
        filterChainDefinitionMap.put("/**/*.svg", "anon");
        filterChainDefinitionMap.put("/**/*.pdf", "anon");
        filterChainDefinitionMap.put("/**/*.jpg", "anon");
        filterChainDefinitionMap.put("/**/*.png", "anon");
        filterChainDefinitionMap.put("/**/*.ico", "anon");

        // update-begin--Author:sunjianlei Date:20190813 for：排除字體格式的后綴
        filterChainDefinitionMap.put("/**/*.ttf", "anon");
        filterChainDefinitionMap.put("/**/*.woff", "anon");
        filterChainDefinitionMap.put("/**/*.woff2", "anon");
        // update-begin--Author:sunjianlei Date:20190813 for：排除字體格式的后綴

        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger**/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");

        filterChainDefinitionMap.put("/sys/annountCement/show/**", "anon");

        //積木報表排除
        filterChainDefinitionMap.put("/jmreport/**", "anon");
        filterChainDefinitionMap.put("/**/*.js.map", "anon");
        filterChainDefinitionMap.put("/**/*.css.map", "anon");
        //大屏模板例子
        filterChainDefinitionMap.put("/test/bigScreen/**", "anon");

        //websocket排除
        filterChainDefinitionMap.put("/websocket/**", "anon");//系統通知和公告
        filterChainDefinitionMap.put("/newsWebsocket/**", "anon");//CMS模塊
        filterChainDefinitionMap.put("/vxeSocket/**", "anon");//JVxeTable無痕刷新示例

        //性能監控  TODO 存在安全漏洞泄露TOEKN（durid連接池也有）
        filterChainDefinitionMap.put("/actuator/**", "anon");

        // 添加自己的過濾器并且取名為jwt
        Map<String, Filter> filterMap = new HashMap<String, Filter>(1);
        //如果cloudServer為空 則說明是單體 需要加載跨域配置【微服務跨域切換】
        Object cloudServer = env.getProperty(CommonConstant.CLOUD_SERVER_KEY);
        filterMap.put("jwt", new JwtFilter(cloudServer==null));
        shiroFilterFactoryBean.setFilters(filterMap);
        // <!-- 過濾鏈定義，從上向下順序執行，一般將/**放在最為下邊
        filterChainDefinitionMap.put("/**", "jwt");

        // 未授權界面返回JSON
        shiroFilterFactoryBean.setUnauthorizedUrl("/sys/common/403");
        shiroFilterFactoryBean.setLoginUrl("/sys/common/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(ShiroRealm myRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm);

        /*
         * 關閉shiro自帶的session，詳情見文檔
         * http://shiro.apache.org/session-management.html#SessionManagement-
         * StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        //自定義緩存實現,使用redis
        securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }

    /**
     * 下面的代碼是添加注解支持
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        /**
         * 解決重復代理問題 github#994
         * 添加前綴判斷 不匹配 任何Advisor
         */
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        defaultAdvisorAutoProxyCreator.setAdvisorBeanNamePrefix("_no_advisor");
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    /**
     * cacheManager 緩存 redis實現
     * 使用的是shiro-redis開源插件
     *
     * @return
     */
    public RedisCacheManager redisCacheManager() {
        log.info("===============(1)創建緩存管理器RedisCacheManager");
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        //redis中針對不同用戶緩存(此處的id需要對應user實體中的id字段,用于唯一標識)
        redisCacheManager.setPrincipalIdFieldName("id");
        //用戶權限信息緩存時間
        redisCacheManager.setExpire(200000);
        return redisCacheManager;
    }

    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis開源插件
     *
     * @return
     */
    @Bean
    public IRedisManager redisManager() {
        log.info("===============(2)創建RedisManager,連接Redis..");
        IRedisManager manager;
        // redis 單機支持，在集群為空，或者集群無機器時候使用 add by jzyadmin@163.com
        if (lettuceConnectionFactory.getClusterConfiguration() == null || lettuceConnectionFactory.getClusterConfiguration().getClusterNodes().isEmpty()) {
            RedisManager redisManager = new RedisManager();
            redisManager.setHost(lettuceConnectionFactory.getHostName());
            redisManager.setPort(lettuceConnectionFactory.getPort());
            redisManager.setDatabase(lettuceConnectionFactory.getDatabase());
            redisManager.setTimeout(0);
            if (!StringUtils.isEmpty(lettuceConnectionFactory.getPassword())) {
                redisManager.setPassword(lettuceConnectionFactory.getPassword());
            }
            manager = redisManager;
        }else{
            // redis集群支持，優先使用集群配置
            RedisClusterManager redisManager = new RedisClusterManager();
            Set<HostAndPort> portSet = new HashSet<>();
            lettuceConnectionFactory.getClusterConfiguration().getClusterNodes().forEach(node -> portSet.add(new HostAndPort(node.getHost() , node.getPort())));
            //update-begin--Author:scott Date:20210531 for：修改集群模式下未設置redis密碼的bug issues/I3QNIC
            if (oConvertUtils.isNotEmpty(lettuceConnectionFactory.getPassword())) {
                JedisCluster jedisCluster = new JedisCluster(portSet, 2000, 2000, 5,
                    lettuceConnectionFactory.getPassword(), new GenericObjectPoolConfig());
                redisManager.setPassword(lettuceConnectionFactory.getPassword());
                redisManager.setJedisCluster(jedisCluster);
            } else {
                JedisCluster jedisCluster = new JedisCluster(portSet);
                redisManager.setJedisCluster(jedisCluster);
            }
            //update-end--Author:scott Date:20210531 for：修改集群模式下未設置redis密碼的bug issues/I3QNIC
            manager = redisManager;
        }
        return manager;
    }

}
