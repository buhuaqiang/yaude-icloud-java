package com.yaude.common.modules.redis.listener;

import com.yaude.common.base.BaseMap;

/**
 * 自定義消息監聽
 */
public interface JeecgRedisListerer {

    void onMessage(BaseMap message);

}
