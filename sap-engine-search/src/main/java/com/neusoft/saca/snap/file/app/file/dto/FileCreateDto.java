package com.neusoft.saca.snap.file.app.file.dto;

import java.util.Date;

public class FileCreateDto {

	/**
	 * 唯一性id
	 */
	private String id;

	/**
	 * 文件名称
	 */
	private String name;

	/**
	 * 文件大小
	 */
	private long sizeInBytes;

	/**
	 * 文件上传时间
	 */
	private Date uploadTime;

	/**
	 * 文件类型
	 */
	private String type;

	/**
	 * 文件发布者
	 */
	private String publisher;

	/**
	 * 文件发布者姓名
	 */
	private String publisherName;

	/**
	 * 文件发布区域id
	 */
	private String scopeId;

	/**
	 * 文件发布区域名称
	 */
	private String scopeName;

	/**
	 * 文件发布区域类型
	 */
	private String scopeType;

	/**
	 * 是否在知识库中显示，可以为空
	 */
	private boolean inKnowledgeBase;

	/**
	 * 文件描述
	 */
	private String description;

	/**
	 * 文件隐私性
	 */
	private int privacy;

	/**
	 * 文件可见成员
	 */
	private String privacyMember;

	/**
	 * 文档所在组织id，包括群组、部门id
	 */
	private String orgId;

	/**
	 * 评级分数
	 */
	private float rating;

	/**
	 * 文件是否可预览 
	 * 一共三个值："0","1","2",分别代表：不可预览，疑似不可预览，可预览，
	 * 其中疑似不可预览需要再进行确认，再转换为0和2两个状态
	 */
	private String preview;

	private String tags;

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	/**
	 * 文件是否可预览 
	 * 一共三个值："0","1","2",分别代表：不可预览，疑似不可预览，可预览，
	 * 其中疑似不可预览需要再进行确认，再转换为0和2两个状态
	 */
	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getPrivacyMember() {
		return privacyMember;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public void setPrivacyMember(String privacyMember) {
		this.privacyMember = privacyMember;
	}

	public int getPrivacy() {
		return privacy;
	}

	public void setPrivacy(int privacy) {
		this.privacy = privacy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getInKnowledgeBase() {
		return inKnowledgeBase;
	}

	public void setInKnowledgeBase(boolean inKnowledgeBase) {
		this.inKnowledgeBase = inKnowledgeBase;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getScopeId() {
		return scopeId;
	}

	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}
}
