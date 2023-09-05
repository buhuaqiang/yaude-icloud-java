package com.yaude.modules.demo.test.service;

import java.util.List;

import com.yaude.modules.demo.test.entity.JeecgOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 訂單機票
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface IJeecgOrderTicketService extends IService<JeecgOrderTicket> {
	
	public List<JeecgOrderTicket> selectTicketsByMainId(String mainId);
}
