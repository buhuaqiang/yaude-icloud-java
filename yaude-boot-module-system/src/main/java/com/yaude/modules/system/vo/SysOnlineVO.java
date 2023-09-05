package com.yaude.modules.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yaude.common.aspect.annotation.Dict;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @Author: chenli
 * @Date: 2020-06-07
 * @Version: V1.0
 */
@Data
public class SysOnlineVO {
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
