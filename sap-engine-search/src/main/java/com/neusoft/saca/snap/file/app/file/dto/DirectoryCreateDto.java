package com.neusoft.saca.snap.file.app.file.dto;

import java.util.Date;

public class DirectoryCreateDto {

	/**
	 * 唯一性标识
	 */
	private String id;

	/**
	 * 目录名称
	 */
	private String name;

	/**
	 * 目录顺序
	 */
	private int order;

	/**
	 * 目录下文件数量
	 */
	private int fileAmount;

	/**
	 * 父级目录id
	 */
	private String parent;

	/**
	 * 根节点（ROOT下一级）
	 */
	private String rootParent;

	private Integer privacy;

	private String privacyMember;

	private String privacyGroup;

	private Integer type;

	private String creator;

	private Date createDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getFileAmount() {
		return fileAmount;
	}

	public void setFileAmount(int fileAmount) {
		this.fileAmount = fileAmount;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getRootParent() {
		return rootParent;
	}

	public void setRootParent(String rootParent) {
		this.rootParent = rootParent;
	}

	public Integer getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Integer privacy) {
		this.privacy = privacy;
	}

	public String getPrivacyMember() {
		return privacyMember;
	}

	public void setPrivacyMember(String privacyMember) {
		this.privacyMember = privacyMember;
	}

	public String getPrivacyGroup() {
		return privacyGroup;
	}

	public void setPrivacyGroup(String privacyGroup) {
		this.privacyGroup = privacyGroup;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
