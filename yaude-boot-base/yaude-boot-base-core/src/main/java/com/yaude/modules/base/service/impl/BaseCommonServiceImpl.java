package com.yaude.modules.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.dto.LogDTO;
import com.yaude.modules.base.mapper.BaseCommonMapper;
import com.yaude.modules.base.service.BaseCommonService;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.IPUtils;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class BaseCommonServiceImpl implements BaseCommonService {

    @Resource
    private BaseCommonMapper baseCommonMapper;

    @Override
    public void addLog(LogDTO logDTO) {
        if(oConvertUtils.isEmpty(logDTO.getId())){
            logDTO.setId(String.valueOf(IdWorker.getId()));
        }
        //保存日志（異常捕獲處理，防止數據太大存儲失敗，導致業務失敗）JT-238
        try {
            baseCommonMapper.saveLog(logDTO);
        } catch (Exception e) {
            log.warn(" LogContent length : "+logDTO.getLogContent().length());
            log.warn(e.getMessage());
        }
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operatetype, LoginUser user) {
        LogDTO sysLog = new LogDTO();
        sysLog.setId(String.valueOf(IdWorker.getId()));
        //注解上的描述,操作日志內容
        sysLog.setLogContent(logContent);
        sysLog.setLogType(logType);
        sysLog.setOperateType(operatetype);
        try {
            //獲取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //設置IP地址
            sysLog.setIp(IPUtils.getIpAddr(request));
        } catch (Exception e) {
            sysLog.setIp("127.0.0.1");
        }
        //獲取登錄用戶信息
        if(user==null){
            try {
                user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        if(user!=null){
            sysLog.setUserid(user.getUsername());
            sysLog.setUsername(user.getRealname());
        }
        sysLog.setCreateTime(new Date());
        //保存日志（異常捕獲處理，防止數據太大存儲失敗，導致業務失敗）JT-238
        try {
            baseCommonMapper.saveLog(sysLog);
        } catch (Exception e) {
            log.warn(" LogContent length : "+sysLog.getLogContent().length());
            log.warn(e.getMessage());
        }
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operateType) {
        addLog(logContent, logType, operateType, null);
    }



}
