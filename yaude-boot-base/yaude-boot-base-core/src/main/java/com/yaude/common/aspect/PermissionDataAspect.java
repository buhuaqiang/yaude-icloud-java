package com.yaude.common.aspect;

import com.yaude.common.aspect.annotation.PermissionData;
import com.yaude.common.system.util.JeecgDataAutorUtils;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.SysPermissionDataRuleModel;
import com.yaude.common.system.vo.SysUserCacheInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.util.SpringContextUtils;
import com.yaude.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 數據權限切面處理類
 *  當被請求的方法有注解PermissionData時,會在往當前request中寫入數據權限信息
 * @Date 2019年4月10日
 * @Version: 1.0
 */
@Aspect
@Component
@Slf4j
public class PermissionDataAspect {

    @Autowired
    private CommonAPI commonAPI;

    @Pointcut("@annotation(com.yaude.common.aspect.annotation.PermissionData)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object arround(ProceedingJoinPoint point) throws  Throwable{
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        PermissionData pd = method.getAnnotation(PermissionData.class);
        String component = pd.pageComponent();

        String requestMethod = request.getMethod();
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        requestPath = filterUrl(requestPath);
        log.debug("攔截請求 >> "+requestPath+";請求類型 >> "+requestMethod);
        String username = JwtUtil.getUserNameByToken(request);
        //查詢數據權限信息
        //TODO 微服務情況下也得支持緩存機制
        List<SysPermissionDataRuleModel> dataRules = commonAPI.queryPermissionDataRule(component, requestPath, username);
        if(dataRules!=null && dataRules.size()>0) {
            //臨時存儲
            JeecgDataAutorUtils.installDataSearchConditon(request, dataRules);
            //TODO 微服務情況下也得支持緩存機制
            SysUserCacheInfo userinfo = commonAPI.getCacheUser(username);
            JeecgDataAutorUtils.installUserInfo(request, userinfo);
        }
        return  point.proceed();
    }

    private String filterUrl(String requestPath){
        String url = "";
        if(oConvertUtils.isNotEmpty(requestPath)){
            url = requestPath.replace("\\", "/");
            url = url.replace("//", "/");
            if(url.indexOf("//")>=0){
                url = filterUrl(url);
            }
			/*if(url.startsWith("/")){
				url=url.substring(1);
			}*/
        }
        return url;
    }

    /**
     * 獲取請求地址
     * @param request
     * @return
     */
    private String getJgAuthRequsetPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String requestPath = request.getRequestURI();
        if(oConvertUtils.isNotEmpty(queryString)){
            requestPath += "?" + queryString;
        }
        if (requestPath.indexOf("&") > -1) {// 去掉其他參數(保留一個參數) 例如：loginController.do?login
            requestPath = requestPath.substring(0, requestPath.indexOf("&"));
        }
        if(requestPath.indexOf("=")!=-1){
            if(requestPath.indexOf(".do")!=-1){
                requestPath = requestPath.substring(0,requestPath.indexOf(".do")+3);
            }else{
                requestPath = requestPath.substring(0,requestPath.indexOf("?"));
            }
        }
        requestPath = requestPath.substring(request.getContextPath().length() + 1);// 去掉項目路徑
        return filterUrl(requestPath);
    }

    private boolean moHuContain(List<String> list,String key){
        for(String str : list){
            if(key.contains(str)){
                return true;
            }
        }
        return false;
    }


}
