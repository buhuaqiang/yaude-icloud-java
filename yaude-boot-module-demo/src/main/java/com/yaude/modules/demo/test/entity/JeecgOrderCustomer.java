package com.yaude.modules.demo.test.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 訂單客戶
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Data
@TableName("jeecg_order_customer")
public class JeecgOrderCustomer implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主鍵*/
    @TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**客戶名*/
	@Excel(name="客戶名字",width=15)
	private String name;
	/**性別*/
	private String sex;
	/**身份證號碼*/
	@Excel(name="身份證號碼",width=15)
	private String idcard;
	/**身份證掃描件*/
	private String idcardPic;
	/**電話1*/
	@Excel(name="電話",width=15)
	private String telphone;
	/**外鍵*/
	private String orderId;
	/**創建人*/
	private String createBy;
	/**創建時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**修改人*/
	private String updateBy;
	/**修改時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
}
