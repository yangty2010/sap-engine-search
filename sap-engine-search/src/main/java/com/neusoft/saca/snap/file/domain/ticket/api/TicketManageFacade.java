package com.neusoft.saca.snap.file.domain.ticket.api;

import com.neusoft.saca.snap.file.domain.ticket.vo.TicketValue;

/**
 *  ticket管理服务接口
 * @author yan
 *
 */
public interface TicketManageFacade {

	/**
	 * 创建一个Ticket
	 * @param key 
	 * @param value
	 * @param type ticket类型，如"attachment","file"
	 * @return 
	 */
	public void createTicket(String key,String value,String type);
	
	/**
	 * 创建一个Ticket
	 * @param key 
	 * @param value
	 * @param type ticket类型，如"attachment","file"
	 * @param expireTimeInSeconds 过期时间，以秒为单位 -1表示不过期
	 * @return 
	 */
	public void createTicket(String key,String value,String type,long expireTimeInSeconds);
	
	/**
	 * 根据key获取ticket的value
	 * @param key
	 * @param type
	 * @return TicketValue 获取不到时返回null
	 */
	public TicketValue obtainValue(String key,String type);
	
}
