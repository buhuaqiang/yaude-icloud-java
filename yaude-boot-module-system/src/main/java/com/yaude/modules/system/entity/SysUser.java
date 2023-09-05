package com.yaude.modules.system.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yaude.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用戶表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 登錄賬號
     */
    @Excel(name = "登錄賬號", width = 15)
    private String username;

    /**
     * 真實姓名
     */
    @Excel(name = "真實姓名", width = 15)
    private String realname;

    /**
     * 密碼
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * md5密碼鹽
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * 頭像
     */
    @Excel(name = "頭像", width = 15,type = 2)
    private String avatar;

    /**
     * 生日
     */
    @Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性別（1：男 2：女）
     */
    @Excel(name = "性別", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 電子郵件
     */
    @Excel(name = "電子郵件", width = 15)
    private String email;

    /**
     * 電話
     */
    @Excel(name = "電話", width = 15)
    private String phone;

    /**
     * 部門code(當前選擇登錄部門)
     */
    private String orgCode;

    /**部門名稱*/
    private transient String orgCodeTxt;

    /**
     * 狀態(1：正常  2：凍結 ）
     */
    @Excel(name = "狀態", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 刪除狀態（0，正常，1已刪除）
     */
    @Excel(name = "刪除狀態", width = 15,dicCode="del_flag")
    @TableLogic
    private Integer delFlag;

    /**
     * 工號，唯一鍵
     */
    @Excel(name = "工號", width = 15)
    private String workNo;

    /**
     * 職務，關聯職務表
     */
    @Excel(name = "職務", width = 15)
    @Dict(dictTable ="sys_position",dicText = "name",dicCode = "code")
    private String post;

    /**
     * 座機號
     */
    @Excel(name = "座機號", width = 15)
    private String telephone;

    /**
     * 創建人
     */
    private String createBy;

    /**
     * 創建時間
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新時間
     */
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

    /**
     * 身份（0 普通成員 1 上級）
     */
    @Excel(name="（1普通成員 2上級）",width = 15)
    private Integer userIdentity;

    /**
     * 負責部門
     */
    @Excel(name="負責部門",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String departIds;

    /**
     * 多租戶id配置，編輯用戶的時候設置
     */
    private String relTenantIds;

    /**設備id uniapp推送用*/
    private String clientId;
}
