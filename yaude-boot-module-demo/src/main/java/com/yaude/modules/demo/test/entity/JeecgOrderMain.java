package com.yaude.modules.demo.test.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 訂單
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Data
@TableName("jeecg_order_main")
public class JeecgOrderMain implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主鍵*/
    @TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**訂單號*/
	private String orderCode;
	/**訂單類型*/
	private String ctype;
	/**訂單日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date orderDate;
	/**訂單金額*/
	private Double orderMoney;
	/**訂單備注*/
	private String content;
	/**創建人*/
	private String createBy;
	/**創建時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**修改人*/
	private String updateBy;
	/**修改時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
