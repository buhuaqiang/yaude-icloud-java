package com.yaude.modules.message.util;

import com.yaude.modules.message.entity.SysMessage;
import com.yaude.modules.message.entity.SysMessageTemplate;
import com.yaude.modules.message.handle.enums.SendMsgStatusEnum;
import com.yaude.modules.message.service.ISysMessageService;
import com.yaude.modules.message.service.ISysMessageTemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息生成工具
 */

@Component
public class PushMsgUtil {

    @Autowired
    private ISysMessageService sysMessageService;

    @Autowired
    private ISysMessageTemplateService sysMessageTemplateService;

    @Autowired
    private Configuration freemarkerConfig;
    /**
     * @param msgType 消息類型 1短信 2郵件 3微信
     * @param templateCode    消息模板碼
     * @param map     消息參數
     * @param sentTo  接收消息方
     */
    public boolean sendMessage(String msgType, String templateCode, Map<String, String> map, String sentTo) {
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        SysMessage sysMessage = new SysMessage();
        if (sysSmsTemplates.size() > 0) {
            SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
            sysMessage.setEsType(msgType);
            sysMessage.setEsReceiver(sentTo);
            //模板標題
            String title = sysSmsTemplate.getTemplateName();
            //模板內容
            String content = sysSmsTemplate.getTemplateContent();
            StringWriter stringWriter = new StringWriter();
            Template template = null;
            try {
                template = new Template("SysMessageTemplate", content, freemarkerConfig);
                template.process(map, stringWriter);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (TemplateException e) {
                e.printStackTrace();
                return false;
            }
            content = stringWriter.toString();
            sysMessage.setEsTitle(title);
            sysMessage.setEsContent(content);
            sysMessage.setEsParam(JSONObject.toJSONString(map));
            sysMessage.setEsSendTime(new Date());
            sysMessage.setEsSendStatus(SendMsgStatusEnum.WAIT.getCode());
            sysMessage.setEsSendNum(0);
            if(sysMessageService.save(sysMessage)) {
				return true;
			}
        }
        return false;
    }

}
