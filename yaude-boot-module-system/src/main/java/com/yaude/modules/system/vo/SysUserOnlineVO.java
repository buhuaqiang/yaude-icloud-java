package com.yaude.modules.system.vo;

import java.util.Date;

import com.yaude.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 *
 * @Author: chenli
 * @Date: 2020-06-07
 * @Version: V1.0
 */
@Data
public class SysUserOnlineVO {
    /**
     * 會話id
     */
    private String id;

    /**
     * 會話編號
     */
    private String token;

    /**
     * 用戶名
     */
    private String username;

    /**
     * 用戶名
     */
    private String realname;

    /**
     * 頭像
     */
    private String avatar;

    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性別（1：男 2：女）
     */
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 手機號
     */
    private String phone;
}
