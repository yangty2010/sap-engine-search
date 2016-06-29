package com.neusoft.saca.snap.file.domain.ticket.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;
import com.neusoft.saca.snap.file.domain.ticket.vo.TicketValue;

@Service
public class TicketManageFacadeImpl implements TicketManageFacade {

	private static final String KEY="key";
	private static final String VALUE="value";
	private static final String TYPE="type";
	@Autowired
	private RedisTemplate<String, String> template;
	
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOps;
	
	@Override
	@Transactional(readOnly=false)
	public void createTicket(String key, String value, String type) {
		Assert.hasText(key);
		Assert.hasText(value);
		Assert.hasText(type);
		Map<String, String> valueMap=new HashMap<String, String>();
		valueMap.put(KEY, key);
		valueMap.put(TYPE, type);
		valueMap.put(VALUE, value);
		hashOps.putAll(type+":"+key, valueMap);
		template.expire(type+":"+key, 60l, TimeUnit.SECONDS);
	}

	@Override
	@Transactional(readOnly=true)
	public TicketValue obtainValue(String key,String type) {
		Assert.hasText(key);
		Assert.hasText(type);
		String realKey=type+":"+key;
		Map<String, String> valueMap=hashOps.entries(realKey);
		
		if (valueMap==null||!valueMap.containsKey(KEY)) {
			return null;
		}else{
			TicketValue ticketValue=new TicketValue();
			ticketValue.setKey(valueMap.get(KEY));
			ticketValue.setType(valueMap.get(TYPE));
			ticketValue.setValue(valueMap.get(VALUE));
			return ticketValue;
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void createTicket(String key, String value, String type, long expireTimeInSeconds) {
		Assert.hasText(key);
		Assert.hasText(value);
		Assert.hasText(type);
		Map<String, String> valueMap=new HashMap<String, String>();
		valueMap.put(KEY, key);
		valueMap.put(TYPE, type);
		valueMap.put(VALUE, value);
		hashOps.putAll(type+":"+key, valueMap);
		if (expireTimeInSeconds>0) {
			template.expire(type+":"+key, expireTimeInSeconds, TimeUnit.SECONDS);		
		}

	}

}
