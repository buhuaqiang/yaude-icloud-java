package com.yaude.modules.message.websocket;

import cn.hutool.core.util.ObjectUtil;
import com.yaude.common.base.BaseMap;
import com.yaude.common.modules.redis.listener.JeecgRedisListerer;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.constant.CommonSendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 監聽消息(采用redis發布訂閱方式發送消息)
 */
@Slf4j
@Component
public class SocketHandler implements JeecgRedisListerer {

    @Autowired
    private WebSocket webSocket;

    @Override
    public void onMessage(BaseMap map) {
        log.info("【SocketHandler消息】Redis Listerer:" + map.toString());

        String userId = map.get("userId");
        String message = map.get("message");
        if (ObjectUtil.isNotEmpty(userId)) {
            webSocket.pushMessage(userId, message);
            //app端消息推送
            webSocket.pushMessage(userId+CommonSendStatus.APP_SESSION_SUFFIX, message);
        } else {
            webSocket.pushMessage(message);
        }

    }
}