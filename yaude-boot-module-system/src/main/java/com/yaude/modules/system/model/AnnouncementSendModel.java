package com.yaude.modules.system.model;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @Description: 用戶通告閱讀標記表
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@Data
public class AnnouncementSendModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
	private java.lang.String id;
	/**通告id*/
	private java.lang.String anntId;
	/**用戶id*/
	private java.lang.String userId;
	/**標題*/
	private java.lang.String titile;
	/**內容*/
	private java.lang.String msgContent;
	/**發布人*/
	private java.lang.String sender;
	/**優先級（L低，M中，H高）*/
	private java.lang.String priority;
	/**閱讀狀態*/
	private java.lang.String readFlag;
	/**發布時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date sendTime;
	/**頁數*/
	private java.lang.Integer pageNo;
	/**大小*/
	private java.lang.Integer pageSize;
    /**
     * 消息類型1:通知公告2:系統消息
     */
    private java.lang.String msgCategory;
	/**
	 * 業務id
	 */
	private java.lang.String busId;
	/**
	 * 業務類型
	 */
	private java.lang.String busType;
	/**
	 * 打開方式 組件：component 路由：url
	 */
	private java.lang.String openType;
	/**
	 * 組件/路由 地址
	 */
	private java.lang.String openPage;

	/**
	 * 業務類型查詢（0.非bpm業務）
	 */
	private java.lang.String bizSource;

	/**
	 * 摘要
	 */
	private java.lang.String msgAbstract;

}
