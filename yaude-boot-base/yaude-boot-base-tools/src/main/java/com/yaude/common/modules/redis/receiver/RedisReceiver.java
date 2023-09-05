package com.yaude.common.modules.redis.receiver;


import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import com.yaude.common.base.BaseMap;
import com.yaude.common.constant.GlobalConstants;
import com.yaude.common.modules.redis.listener.JeecgRedisListerer;
import com.yaude.common.util.SpringContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author zyf
 */
@Component
@Data
public class RedisReceiver {


    /**
     * 接受消息并調用業務邏輯處理器
     *
     * @param params
     */
    public void onMessage(BaseMap params) {
        Object handlerName = params.get(GlobalConstants.HANDLER_NAME);
        JeecgRedisListerer messageListener = SpringContextHolder.getHandler(handlerName.toString(), JeecgRedisListerer.class);
        if (ObjectUtil.isNotEmpty(messageListener)) {
            messageListener.onMessage(params);
        }
    }

}
