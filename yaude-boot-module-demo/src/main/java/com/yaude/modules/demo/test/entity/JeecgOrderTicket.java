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
 * @Description: 訂單機票
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Data
@TableName("jeecg_order_ticket")
public class JeecgOrderTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主鍵*/
    @TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**航班號*/
	@Excel(name="航班號",width=15)
	private String ticketCode;
	/**航班時間*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Excel(name="航班時間",width=15,format = "yyyy-MM-dd")
	private java.util.Date tickectDate;
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
