package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yaude.common.aspect.annotation.Dict;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 系統通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Data
@TableName("sys_announcement")
public class SysAnnouncement implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private java.lang.String id;
    /**
     * 標題
     */
    @Excel(name = "標題", width = 15)
    private java.lang.String titile;
    /**
     * 內容
     */
    @Excel(name = "內容", width = 30)
    private java.lang.String msgContent;
    /**
     * 開始時間
     */
    @Excel(name = "開始時間", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date startTime;
    /**
     * 結束時間
     */
    @Excel(name = "結束時間", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date endTime;
    /**
     * 發布人
     */
    @Excel(name = "發布人", width = 15)
    private java.lang.String sender;
    /**
     * 優先級（L低，M中，H高）
     */
    @Excel(name = "優先級", width = 15, dicCode = "priority")
    @Dict(dicCode = "priority")
    private java.lang.String priority;
    
    /**
     * 消息類型1:通知公告2:系統消息
     */
    @Excel(name = "消息類型", width = 15, dicCode = "msg_category")
    @Dict(dicCode = "msg_category")
    private java.lang.String msgCategory;
    /**
     * 通告對象類型（USER:指定用戶，ALL:全體用戶）
     */
    @Excel(name = "通告對象類型", width = 15, dicCode = "msg_type")
    @Dict(dicCode = "msg_type")
    private java.lang.String msgType;
    /**
     * 發布狀態（0未發布，1已發布，2已撤銷）
     */
    @Excel(name = "發布狀態", width = 15, dicCode = "send_status")
    @Dict(dicCode = "send_status")
    private java.lang.String sendStatus;
    /**
     * 發布時間
     */
    @Excel(name = "發布時間", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date sendTime;
    /**
     * 撤銷時間
     */
    @Excel(name = "撤銷時間", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date cancelTime;
    /**
     * 刪除狀態（0，正常，1已刪除）
     */
    private java.lang.String delFlag;
    /**
     * 創建人
     */
    private java.lang.String createBy;
    /**
     * 創建時間
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    private java.lang.String updateBy;
    /**
     * 更新時間
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**
     * 指定用戶
     **/
    private java.lang.String userIds;
    /**
     * 業務類型(email:郵件 bpm:流程)
     */
    private java.lang.String busType;
    /**
     * 業務id
     */
    private java.lang.String busId;
    /**
     * 打開方式 組件：component 路由：url
     */
    private java.lang.String openType;
    /**
     * 組件/路由 地址
     */
    private java.lang.String openPage;
    /**
     * 摘要
     */
    private java.lang.String msgAbstract;
    /**
     * 釘釘task_id，用于撤回消息
     */
    private java.lang.String dtTaskId;
}
