package com.neusoft.saca.snap.file.app.ticket.controller;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.constant.TicketConstant;
import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;

/**
 * 
 * @author YANGTY-SAP
 * 
 */
@Controller
@RequestMapping("ticket")
public class TicketCreateController {

	@Autowired
	private TicketManageFacade ticketManageFacade;


	/**
	 * 判断redis中是否有fileKey，如果存在说明pdf转化已成功
	 * 
	 * @param fileid
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createRedisValue(@RequestParam String fileId) {
		JSONObject result = new JSONObject();
		
		ticketManageFacade.createTicket(fileId, fileId, TicketConstant.TICKET_TYPE_SEARCH);
		
		result.put("success", true);
		
		return result;
	}

}
