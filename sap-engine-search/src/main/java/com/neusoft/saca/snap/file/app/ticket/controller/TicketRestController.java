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
import com.neusoft.saca.snap.file.domain.ticket.vo.TicketValue;

/**
 * 
 * @author YANGTY-SAP
 * 
 */
@Controller
@RequestMapping("ticket")
public class TicketRestController {

	@Autowired
	private TicketManageFacade ticketManageFacade;


	/**
	 * 判断redis中是否有fileKey，如果存在说明pdf转化已成功
	 * 
	 * @param fileid
	 * @return
	 */
	@RequestMapping(value = "obtain", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainRedisValue(@RequestParam String fileId) {
		JSONObject result = new JSONObject();
		
		TicketValue ticket = ticketManageFacade.obtainValue(fileId, TicketConstant.TICKET_TYPE_SEARCH);
		if(ticket!=null){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		
		return result;
	}

}
