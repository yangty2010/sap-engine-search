package com.neusoft.saca.snap.search.vo;

import org.apache.solr.client.solrj.beans.Field;

import com.neusoft.saca.snap.infrastructure.search.vo.SearchedResourceBean;

public class MatchedUser extends SearchedResourceBean {
	public static final String[] MATCHED_USER_FIELDS = new String[] { "id", "userId", "userName",	"company" };

	@Field
	private String userId;
	@Field
	private String userName;
	@Field
	private String company;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
