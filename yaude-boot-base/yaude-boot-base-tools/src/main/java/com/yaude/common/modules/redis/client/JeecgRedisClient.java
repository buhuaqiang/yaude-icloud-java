package com.yaude.common.modules.redis.client;

import com.yaude.common.constant.GlobalConstants;
import com.yaude.common.base.BaseMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * redis客戶端
 */
@Configuration
public class JeecgRedisClient {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 發送消息
     *
     * @param handlerName
     * @param params
     */
    public void sendMessage(String handlerName, BaseMap params) {
        params.put(GlobalConstants.HANDLER_NAME, handlerName);
        redisTemplate.convertAndSend(GlobalConstants.REDIS_TOPIC_NAME, params);
    }


}
