package com.yaude.common.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.constant.enums.ModuleType;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.modules.base.service.BaseCommonService;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import com.yaude.common.api.dto.LogDTO;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.IPUtils;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * 系統日志，切面處理類
 *
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2018年1月14日
 */
@Aspect
@Component
public class AutoLogAspect {

    @Resource
    private BaseCommonService baseCommonService;

    @Pointcut("@annotation(com.yaude.common.aspect.annotation.AutoLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //執行方法
        Object result = point.proceed();
        //執行時長(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time, result);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object obj) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogDTO dto = new LogDTO();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if(syslog != null){
            //update-begin-author:taoyan date:
            String content = syslog.value();
            if(syslog.module()== ModuleType.ONLINE){
                content = getOnlineLogContent(obj, content);
            }
            //注解上的描述,操作日志內容
            dto.setLogType(syslog.logType());
            dto.setLogContent(content);
        }

        //請求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        dto.setMethod(className + "." + methodName + "()");


        //設置操作類型
        if (dto.getLogType() == CommonConstant.LOG_TYPE_2) {
            dto.setOperateType(getOperateType(methodName, syslog.operateType()));
        }

        //獲取request
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        //請求的參數
        dto.setRequestParam(getReqestParams(request,joinPoint));
        //設置IP地址
        dto.setIp(IPUtils.getIpAddr(request));
        //獲取登錄用戶信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(sysUser!=null){
            dto.setUserid(sysUser.getUsername());
            dto.setUsername(sysUser.getRealname());

        }
        //耗時
        dto.setCostTime(time);
        dto.setCreateTime(new Date());
        //保存系統日志
        baseCommonService.addLog(dto);
    }


    /**
     * 獲取操作類型
     */
    private int getOperateType(String methodName,int operateType) {
        if (operateType > 0) {
            return operateType;
        }
        if (methodName.startsWith("list")) {
            return CommonConstant.OPERATE_TYPE_1;
        }
        if (methodName.startsWith("add")) {
            return CommonConstant.OPERATE_TYPE_2;
        }
        if (methodName.startsWith("edit")) {
            return CommonConstant.OPERATE_TYPE_3;
        }
        if (methodName.startsWith("delete")) {
            return CommonConstant.OPERATE_TYPE_4;
        }
        if (methodName.startsWith("import")) {
            return CommonConstant.OPERATE_TYPE_5;
        }
        if (methodName.startsWith("export")) {
            return CommonConstant.OPERATE_TYPE_6;
        }
        return CommonConstant.OPERATE_TYPE_1;
    }

    /**
     * @Description: 獲取請求參數
     * @author: scott
     * @date: 2020/4/16 0:10
     * @param request:  request
     * @param joinPoint:  joinPoint
     * @Return: java.lang.String
     */
    private String getReqestParams(HttpServletRequest request, JoinPoint joinPoint) {
        String httpMethod = request.getMethod();
        String params = "";
        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            Object[] paramsArray = joinPoint.getArgs();
            // java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
            //  https://my.oschina.net/mengzhang6/blog/2395893
            Object[] arguments  = new Object[paramsArray.length];
            for (int i = 0; i < paramsArray.length; i++) {
                if (paramsArray[i] instanceof BindingResult || paramsArray[i] instanceof ServletRequest || paramsArray[i] instanceof ServletResponse || paramsArray[i] instanceof MultipartFile) {
                    //ServletRequest不能序列化，從入參里排除，否則報異常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                    //ServletResponse不能序列化 從入參里排除，否則報異常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                    continue;
                }
                arguments[i] = paramsArray[i];
            }
            //update-begin-author:taoyan date:20200724 for:日志數據太長的直接過濾掉
            PropertyFilter profilter = new PropertyFilter() {
                @Override
                public boolean apply(Object o, String name, Object value) {
                    if(value!=null && value.toString().length()>500){
                        return false;
                    }
                    return true;
                }
            };
            params = JSONObject.toJSONString(arguments, profilter);
            //update-end-author:taoyan date:20200724 for:日志數據太長的直接過濾掉
        } else {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 請求的方法參數值
            Object[] args = joinPoint.getArgs();
            // 請求的方法參數名稱
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    params += "  " + paramNames[i] + ": " + args[i];
                }
            }
        }
        return params;
    }

    /**
     * online日志內容拼接
     * @param obj
     * @param content
     * @return
     */
    private String getOnlineLogContent(Object obj, String content){
        if (Result.class.isInstance(obj)){
            Result res = (Result)obj;
            String msg = res.getMessage();
            String tableName = res.getOnlTable();
            if(oConvertUtils.isNotEmpty(tableName)){
                content+=",表名:"+tableName;
            }
            if(res.isSuccess()){
                content+= ","+(oConvertUtils.isEmpty(msg)?"操作成功":msg);
            }else{
                content+= ","+(oConvertUtils.isEmpty(msg)?"操作失敗":msg);
            }
        }
        return content;
    }


    /*    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object obj) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if(syslog != null){
            //update-begin-author:taoyan date:
            String content = syslog.value();
            if(syslog.module()== ModuleType.ONLINE){
                content = getOnlineLogContent(obj, content);
            }
            //注解上的描述,操作日志內容
            sysLog.setLogContent(content);
            sysLog.setLogType(syslog.logType());
        }

        //請求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");


        //設置操作類型
        if (sysLog.getLogType() == CommonConstant.LOG_TYPE_2) {
            sysLog.setOperateType(getOperateType(methodName, syslog.operateType()));
        }

        //獲取request
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        //請求的參數
        sysLog.setRequestParam(getReqestParams(request,joinPoint));

        //設置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));

        //獲取登錄用戶信息
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if(sysUser!=null){
            sysLog.setUserid(sysUser.getUsername());
            sysLog.setUsername(sysUser.getRealname());

        }
        //耗時
        sysLog.setCostTime(time);
        sysLog.setCreateTime(new Date());
        //保存系統日志
        sysLogService.save(sysLog);
    }*/
}
