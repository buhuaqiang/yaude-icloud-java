package com.yaude.modules.demo.mock.vxe.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.constant.VXESocketConst;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * vxe WebSocket，用于實現實時無痕刷新的功能
 */
@Slf4j
@Component
@ServerEndpoint("/vxeSocket/{userId}/{pageId}")
public class VXESocket {

    /**
     * 當前 session
     */
    private Session session;
    /**
     * 當前用戶id
     */
    private String userId;
    /**
     * 頁面id，用于標識同一用戶，不同頁面的數據
     */
    private String pageId;
    /**
     * 當前socket唯一id
     */
    private String socketId;

    /**
     * 用戶連接池，包含單個用戶的所有socket連接；
     * 因為一個用戶可能打開多個頁面，多個頁面就會有多個連接；
     * key是userId，value是Map對象；子Map的key是pageId，value是VXESocket對象
     */
    private static Map<String, Map<String, VXESocket>> userPool = new HashMap<>();
    /**
     * 連接池，包含所有WebSocket連接；
     * key是socketId，value是VXESocket對象
     */
    private static Map<String, VXESocket> socketPool = new HashMap<>();

    /**
     * 獲取某個用戶所有的頁面
     */
    public static Map<String, VXESocket> getUserPool(String userId) {
        return userPool.computeIfAbsent(userId, k -> new HashMap<>());
    }

    /**
     * 向當前用戶發送消息
     *
     * @param message 消息內容
     */
    public void sendMessage(String message) {
        try {
            this.session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.error("【vxeSocket】消息發送失敗：" + e.getMessage());
        }
    }

    /**
     * 封裝消息json
     *
     * @param data 消息內容
     */
    public static String packageMessage(String type, Object data) {
        JSONObject message = new JSONObject();
        message.put(VXESocketConst.TYPE, type);
        message.put(VXESocketConst.DATA, data);
        return message.toJSONString();
    }

    /**
     * 向指定用戶的所有頁面發送消息
     *
     * @param userId  接收消息的用戶ID
     * @param message 消息內容
     */
    public static void sendMessageTo(String userId, String message) {
        Collection<VXESocket> values = getUserPool(userId).values();
        if (values.size() > 0) {
            for (VXESocket socketItem : values) {
                socketItem.sendMessage(message);
            }
        } else {
            log.warn("【vxeSocket】消息發送失敗：userId\"" + userId + "\"不存在或未在線！");
        }
    }

    /**
     * 向指定用戶的指定頁面發送消息
     *
     * @param userId  接收消息的用戶ID
     * @param message 消息內容
     */
    public static void sendMessageTo(String userId, String pageId, String message) {
        VXESocket socketItem = getUserPool(userId).get(pageId);
        if (socketItem != null) {
            socketItem.sendMessage(message);
        } else {
            log.warn("【vxeSocket】消息發送失敗：userId\"" + userId + "\"的pageId\"" + pageId + "\"不存在或未在線！");
        }
    }

    /**
     * 向多個用戶的所有頁面發送消息
     *
     * @param userIds 接收消息的用戶ID數組
     * @param message 消息內容
     */
    public static void sendMessageTo(String[] userIds, String message) {
        for (String userId : userIds) {
            VXESocket.sendMessageTo(userId, message);
        }
    }

    /**
     * 向所有用戶的所有頁面發送消息
     *
     * @param message 消息內容
     */
    public static void sendMessageToAll(String message) {
        for (VXESocket socketItem : socketPool.values()) {
            socketItem.sendMessage(message);
        }
    }

    /**
     * websocket 開啟連接
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("pageId") String pageId) {
        try {
            this.userId = userId;
            this.pageId = pageId;
            this.socketId = userId + pageId;
            this.session = session;

            socketPool.put(this.socketId, this);
            getUserPool(userId).put(this.pageId, this);

            log.info("【vxeSocket】有新的連接，總數為:" + socketPool.size());
        } catch (Exception ignored) {
        }
    }

    /**
     * websocket 斷開連接
     */
    @OnClose
    public void onClose() {
        try {
            socketPool.remove(this.socketId);
            getUserPool(this.userId).remove(this.pageId);

            log.info("【vxeSocket】連接斷開，總數為:" + socketPool.size());
        } catch (Exception ignored) {
        }
    }

    /**
     * websocket 收到消息
     */
    @OnMessage
    public void onMessage(String message) {
        // log.info("【vxeSocket】onMessage:" + message);
        JSONObject json;
        try {
            json = JSON.parseObject(message);
        } catch (Exception e) {
            log.warn("【vxeSocket】收到不合法的消息:" + message);
            return;
        }
        String type = json.getString(VXESocketConst.TYPE);
        switch (type) {
            // 心跳檢測
            case VXESocketConst.TYPE_HB:
                this.sendMessage(VXESocket.packageMessage(type, true));
                break;
            // 更新form數據
            case VXESocketConst.TYPE_UVT:
                this.handleUpdateForm(json);
                break;
            default:
                log.warn("【vxeSocket】收到不識別的消息類型:" + type);
                break;
        }


    }

    /**
     * 處理 UpdateForm 事件
     */
    private void handleUpdateForm(JSONObject json) {
        // 將事件轉發給所有人
        JSONObject data = json.getJSONObject(VXESocketConst.DATA);
        VXESocket.sendMessageToAll(VXESocket.packageMessage(VXESocketConst.TYPE_UVT, data));
    }

}
