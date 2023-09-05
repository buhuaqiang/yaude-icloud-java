package com.yaude.modules.system.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.yaude.common.constant.CacheConstant;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.util.JwtUtil;
import com.yaude.common.system.vo.LoginUser;
import com.yaude.common.util.RedisUtil;
import com.yaude.modules.base.service.BaseCommonService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.api.ISysBaseAPI;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.service.ISysUserService;
import com.yaude.modules.system.vo.SysUserOnlineVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 在線用戶
 * @Author: chenli
 * @Date: 2020-06-07
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/online")
@Slf4j
public class SysUserOnlineController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public ISysUserService userService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Resource
    private BaseCommonService baseCommonService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<Page<SysUserOnlineVO>> list(@RequestParam(name="username", required=false) String username, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
        SysUserOnlineVO online;
        List<SysUserOnlineVO> onlineList = new ArrayList<SysUserOnlineVO>();
        for (String key : keys) {
            online = new SysUserOnlineVO();
            String token = (String) redisUtil.get(key);
            if (!StringUtils.isEmpty(token)){
                online.setToken(token);
                LoginUser loginUser = sysBaseAPI.getUserByName(JwtUtil.getUsername(token));
                BeanUtils.copyProperties(loginUser, online);
                if (StringUtils.isNotEmpty(username)) {
                    if (StringUtils.equals(username, online.getUsername())) {
                        onlineList.add(online);
                    }
                } else {
                    onlineList.add(online);
                }
            }
        }

        Page<SysUserOnlineVO> page = new Page<SysUserOnlineVO>(pageNo, pageSize);
        int count = onlineList.size();
        List<SysUserOnlineVO> pages = new ArrayList<>();
        //計算當前頁第一條數據的下標
        int currId = pageNo>1 ? (pageNo-1)*pageSize:0;
        for (int i=0; i<pageSize && i<count - currId;i++){
            pages.add(onlineList.get(currId+i));
        }
        page.setSize(pageSize);
        page.setCurrent(pageNo);
        page.setTotal(count);
        //計算分頁總頁數
        page.setPages(count %10 == 0 ? count/10 :count/10+1);
        page.setRecords(pages);

        Collections.reverse(onlineList);
        onlineList.removeAll(Collections.singleton(null));
        Result<Page<SysUserOnlineVO>> result = new Result<Page<SysUserOnlineVO>>();
        result.setSuccess(true);
        result.setResult(page);
        return result;
    }

    /**
     * 強退用戶
     */
    @RequestMapping(value = "/forceLogout",method = RequestMethod.POST)
    public Result<Object> forceLogout(@RequestBody SysUserOnlineVO online) {
        //用戶退出邏輯
        if(oConvertUtils.isEmpty(online.getToken())) {
            return Result.error("退出登錄失敗！");
        }
        String username = JwtUtil.getUsername(online.getToken());
        LoginUser sysUser = sysBaseAPI.getUserByName(username);
        if(sysUser!=null) {
            baseCommonService.addLog("強制: "+sysUser.getRealname()+"退出成功！", CommonConstant.LOG_TYPE_1, null,sysUser);
            log.info(" 強制  "+sysUser.getRealname()+"退出成功！ ");
            //清空用戶登錄Token緩存
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + online.getToken());
            //清空用戶登錄Shiro權限緩存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用戶的緩存信息（包括部門信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            //調用shiro的logout
            SecurityUtils.getSubject().logout();
            return Result.ok("退出登錄成功！");
        }else {
            return Result.error("Token無效!");
        }
    }
}
