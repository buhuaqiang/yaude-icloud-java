package com.yaude.modules.message.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.yaude.common.base.BaseMap;
import com.yaude.common.modules.redis.client.JeecgRedisClient;
import com.yaude.common.constant.WebsocketConst;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author scott
 * @Date 2019/11/29 9:41
 * @Description: 此注解相當于設置訪問URL
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}") //此注解相當于設置訪問URL
public class WebSocket {

    private Session session;

    private String userId;

    private static final String REDIS_TOPIC_NAME = "socketHandler";

    @Resource
    private JeecgRedisClient jeecgRedisClient;

    /**
     * 緩存 webSocket連接到單機服務class中（整體方案支持集群）
     */
    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();
    private static Map<String, Session> sessionPool = new HashMap<String, Session>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.put(userId, session);
            log.info("【websocket消息】有新的連接，總數為:" + webSockets.size());
        } catch (Exception e) {
        }
    }

    @OnClose
    public void onClose() {
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            log.info("【websocket消息】連接斷開，總數為:" + webSockets.size());
        } catch (Exception e) {
        }
    }


    /**
     * 服務端推送消息
     *
     * @param userId
     * @param message
     */
    public void pushMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            try {
                log.info("【websocket消息】 單點消息:" + message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 服務器端推送消息
     */
    public void pushMessage(String message) {
        try {
            webSockets.forEach(ws -> ws.session.getAsyncRemote().sendText(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnMessage
    public void onMessage(String message) {
        //todo 現在有個定時任務刷，應該去掉
        log.debug("【websocket消息】收到客戶端消息:" + message);
        JSONObject obj = new JSONObject();
        //業務類型
        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHECK);
        //消息內容
        obj.put(WebsocketConst.MSG_TXT, "心跳響應");
        for (WebSocket webSocket : webSockets) {
            webSocket.pushMessage(message);
        }
    }

    /**
     * 后臺發送消息到redis
     *
     * @param message
     */
    public void sendMessage(String message) {
        log.info("【websocket消息】廣播消息:" + message);
        BaseMap baseMap = new BaseMap();
        baseMap.put("userId", "");
        baseMap.put("message", message);
        jeecgRedisClient.sendMessage(REDIS_TOPIC_NAME, baseMap);
    }

    /**
     * 此為單點消息
     *
     * @param userId
     * @param message
     */
    public void sendMessage(String userId, String message) {
        BaseMap baseMap = new BaseMap();
        baseMap.put("userId", userId);
        baseMap.put("message", message);
        jeecgRedisClient.sendMessage(REDIS_TOPIC_NAME, baseMap);
    }

    /**
     * 此為單點消息(多人)
     *
     * @param userIds
     * @param message
     */
    public void sendMessage(String[] userIds, String message) {
        for (String userId : userIds) {
            sendMessage(userId, message);
        }
    }

}