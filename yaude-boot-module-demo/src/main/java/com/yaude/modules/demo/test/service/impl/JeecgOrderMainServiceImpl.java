package com.yaude.modules.demo.test.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.yaude.modules.demo.test.entity.JeecgOrderCustomer;
import com.yaude.modules.demo.test.entity.JeecgOrderMain;
import com.yaude.modules.demo.test.entity.JeecgOrderTicket;
import com.yaude.modules.demo.test.mapper.JeecgOrderCustomerMapper;
import com.yaude.modules.demo.test.mapper.JeecgOrderMainMapper;
import com.yaude.modules.demo.test.mapper.JeecgOrderTicketMapper;
import com.yaude.modules.demo.test.service.IJeecgOrderMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 訂單
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Service
public class JeecgOrderMainServiceImpl extends ServiceImpl<JeecgOrderMainMapper, JeecgOrderMain> implements IJeecgOrderMainService {

    @Autowired
    private JeecgOrderMainMapper jeecgOrderMainMapper;
    @Autowired
    private JeecgOrderCustomerMapper jeecgOrderCustomerMapper;
    @Autowired
    private JeecgOrderTicketMapper jeecgOrderTicketMapper;

    @Override
    @Transactional
    public void saveMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList) {
        jeecgOrderMainMapper.insert(jeecgOrderMain);
        if (jeecgOrderCustomerList != null) {
            for (JeecgOrderCustomer entity : jeecgOrderCustomerList) {
                entity.setOrderId(jeecgOrderMain.getId());
                jeecgOrderCustomerMapper.insert(entity);
            }
        }
        if (jeecgOrderTicketList != null) {
            for (JeecgOrderTicket entity : jeecgOrderTicketList) {
                entity.setOrderId(jeecgOrderMain.getId());
                jeecgOrderTicketMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void updateMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList) {
        jeecgOrderMainMapper.updateById(jeecgOrderMain);

        //1.先刪除子表數據
        jeecgOrderTicketMapper.deleteTicketsByMainId(jeecgOrderMain.getId());
        jeecgOrderCustomerMapper.deleteCustomersByMainId(jeecgOrderMain.getId());

        //2.子表數據重新插入
        if (jeecgOrderCustomerList != null) {
            for (JeecgOrderCustomer entity : jeecgOrderCustomerList) {
                entity.setOrderId(jeecgOrderMain.getId());
                jeecgOrderCustomerMapper.insert(entity);
            }
        }
        if (jeecgOrderTicketList != null) {
            for (JeecgOrderTicket entity : jeecgOrderTicketList) {
                entity.setOrderId(jeecgOrderMain.getId());
                jeecgOrderTicketMapper.insert(entity);
            }
        }
    }

    /**
     * 一對多維護邏輯改造  LOWCOD-315
     * @param jeecgOrderMain
     * @param jeecgOrderCustomerList
     * @param jeecgOrderTicketList
     */
    @Override
    @Transactional
    public void updateCopyMain(JeecgOrderMain jeecgOrderMain, List<JeecgOrderCustomer> jeecgOrderCustomerList, List<JeecgOrderTicket> jeecgOrderTicketList) {
        jeecgOrderMainMapper.updateById(jeecgOrderMain);

        // 循環前臺傳過來的數據
        for (JeecgOrderTicket ticket:jeecgOrderTicketList){
            // 先查詢子表數據庫
            JeecgOrderTicket orderTicket = jeecgOrderTicketMapper.selectById(ticket.getId());
            if(orderTicket == null){
                // 當傳過來的id數據庫不存在時，說明數據庫沒有，走新增邏輯
                ticket.setOrderId(jeecgOrderMain.getId());
                jeecgOrderTicketMapper.insert(ticket);
                break;
            }
            if(orderTicket.getId().equals(ticket.getId())){
                // 傳過來的id和數據庫id一至時，說明數據庫存在該數據，走更新邏輯
                jeecgOrderTicketMapper.updateById(ticket);
            }
        }
        for (JeecgOrderCustomer customer:jeecgOrderCustomerList){
            // 先查詢子表數據庫
            JeecgOrderCustomer customers = jeecgOrderCustomerMapper.selectById(customer.getId());
            if(customers == null){
                // 當傳過來的id數據庫不存在時，說明數據庫沒有，走新增邏輯
                customer.setOrderId(jeecgOrderMain.getId());
                jeecgOrderCustomerMapper.insert(customer);
                break;
            }
            if(customers.getId().equals(customer.getId())){
                //TODO 傳過來的id和數據庫id一至時，說明數據庫存在該數據，走更新邏輯
                jeecgOrderCustomerMapper.updateById(customer);
            }
        }
        // 當跟新和刪除之后取差集， 當傳過來的id不存在，而數據庫存在時，說明已刪除，走刪除邏輯
        List<JeecgOrderTicket> jeecgOrderTickets = jeecgOrderTicketMapper.selectTicketsByMainId(jeecgOrderMain.getId());
        List<JeecgOrderTicket> collect = jeecgOrderTickets.stream()
                .filter(item -> !jeecgOrderTicketList.stream()
                .map(e -> e.getId())
                .collect(Collectors.toList())
                .contains(item.getId()))
                .collect(Collectors.toList());
        // for循環刪除id
        for (JeecgOrderTicket ticket:collect){
            jeecgOrderTicketMapper.deleteById(ticket.getId());
        }

        List<JeecgOrderCustomer> jeecgOrderCustomers = jeecgOrderCustomerMapper.selectCustomersByMainId(jeecgOrderMain.getId());
        List<JeecgOrderCustomer> customersCollect = jeecgOrderCustomers.stream()
                .filter(item -> !jeecgOrderCustomerList.stream()
                        .map(e -> e.getId())
                        .collect(Collectors.toList())
                        .contains(item.getId()))
                .collect(Collectors.toList());
        //TODO for循環刪除id
        for (JeecgOrderCustomer c:customersCollect){
            jeecgOrderCustomerMapper.deleteById(c.getId());
        }
    }
	@Override
	@Transactional
	public void delMain(String id) {
		jeecgOrderMainMapper.deleteById(id);
		jeecgOrderTicketMapper.deleteTicketsByMainId(id);
		jeecgOrderCustomerMapper.deleteCustomersByMainId(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			jeecgOrderMainMapper.deleteById(id);
			jeecgOrderTicketMapper.deleteTicketsByMainId(id.toString());
			jeecgOrderCustomerMapper.deleteCustomersByMainId(id.toString());
		}
	}

}
