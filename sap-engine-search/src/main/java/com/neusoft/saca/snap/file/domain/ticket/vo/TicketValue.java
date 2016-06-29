package com.neusoft.saca.snap.file.domain.ticket.vo;

import java.io.Serializable;

public class TicketValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4679861258220845888L;

	private String type;
	private String key;
	private String value;

	public TicketValue() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
