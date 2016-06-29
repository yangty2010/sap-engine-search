package com.neusoft.saca.snap.file.app.image.dto;

import java.util.ArrayList;
import java.util.List;

public class DiscussionGroupAvatarCreateDto {

	String discussionGroupId;
	List<String> userIdList = new ArrayList<String>();
	public String getDiscussionGroupId() {
		return discussionGroupId;
	}
	public void setDiscussionGroupId(String discussionGroupId) {
		this.discussionGroupId = discussionGroupId;
	}
	public List<String> getUserIdList() {
		return userIdList;
	}
	public void setUserIdList(List<String> userIdList) {
		this.userIdList = userIdList;
	}
	
}
