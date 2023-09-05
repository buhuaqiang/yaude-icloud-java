package com.yaude.modules.demo.test.vo;

import java.util.List;

import com.yaude.modules.demo.test.entity.JeecgOrderCustomer;
import com.yaude.modules.demo.test.entity.JeecgOrderTicket;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import lombok.Data;

@Data
public class JeecgOrderMainPage {
	
	/**主鍵*/
	private String id;
	/**訂單號*/
	@Excel(name="訂單號",width=15)
	private String orderCode;
	/**訂單類型*/
	private String ctype;
	/**訂單日期*/
	@Excel(name="訂單日期",width=15,format = "yyyy-MM-dd")
	private java.util.Date orderDate;
	/**訂單金額*/
	@Excel(name="訂單金額",width=15)
	private Double orderMoney;
	/**訂單備注*/
	private String content;
	/**創建人*/
	private String createBy;
	/**創建時間*/
	private java.util.Date createTime;
	/**修改人*/
	private String updateBy;
	/**修改時間*/
	private java.util.Date updateTime;
	
	@ExcelCollection(name="客戶")
	private List<JeecgOrderCustomer> jeecgOrderCustomerList;
	@ExcelCollection(name="機票")
	private List<JeecgOrderTicket> jeecgOrderTicketList;
	
}
