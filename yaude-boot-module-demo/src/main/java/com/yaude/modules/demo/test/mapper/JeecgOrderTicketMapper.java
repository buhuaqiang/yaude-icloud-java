package com.yaude.modules.demo.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import com.yaude.modules.demo.test.entity.JeecgOrderTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 訂單機票
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface JeecgOrderTicketMapper extends BaseMapper<JeecgOrderTicket> {

	/**
	 *  通過主表外鍵批量刪除客戶
	 * @param mainId
	 * @return
	 */
    @Delete("DELETE FROM JEECG_ORDER_TICKET WHERE ORDER_ID = #{mainId}")
	public boolean deleteTicketsByMainId(String mainId);
    
    
    @Select("SELECT * FROM JEECG_ORDER_TICKET WHERE ORDER_ID = #{mainId}")
	public List<JeecgOrderTicket> selectTicketsByMainId(String mainId);
}
