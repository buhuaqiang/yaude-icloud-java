package com.yaude.modules.demo.test.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.yaude.modules.demo.test.entity.JeecgOrderCustomer;
import com.yaude.modules.demo.test.entity.JeecgOrderMain;
import com.yaude.modules.demo.test.entity.JeecgOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 訂單
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface IJeecgOrderMainService extends IService<JeecgOrderMain> {

	/**
	 * 添加一對多
	 * 
	 */
	public void saveMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList) ;
	
	/**
	 * 修改一對多
	 * 
	 */
	public void updateMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList);
	
	/**
	 * 刪除一對多
	 * @param jformOrderMain
	 */
	public void delMain(String id);
	
	/**
	 * 批量刪除一對多
	 * @param jformOrderMain
	 */
	public void delBatchMain(Collection<? extends Serializable> idList);

	public void updateCopyMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList);
}
