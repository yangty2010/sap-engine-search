package com.neusoft.saca.snap.file.domain.directory.vo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "directory")
public class DirectoryEntry implements Serializable, Comparable<DirectoryEntry> {

	private static final long serialVersionUID = -600941851415653185L;

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

	public DirectoryEntry() {
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "order_number", nullable = false)
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Column(name = "file_amount", nullable = false)
	public int getFileAmount() {
		return fileAmount;
	}

	public void setFileAmount(int fileAmount) {
		this.fileAmount = fileAmount;
	}

	@Column(name = "parent", nullable = false, length = 36)
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@Column(name = "root_parent", nullable = false, length = 36)
	public String getRootParent() {
		return rootParent;
	}

	public void setRootParent(String rootParent) {
		this.rootParent = rootParent;
	}

	@Column(name = "privacy", nullable = true)
	public Integer getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Integer privacy) {
		this.privacy = privacy;
	}

	@Column(name = "privacy_member", length = 5000)
	public String getPrivacyMember() {
		return privacyMember;
	}

	public void setPrivacyMember(String privacyMember) {
		this.privacyMember = privacyMember;
	}

	@Column(name = "privacy_group", length = 5000)
	public String getPrivacyGroup() {
		return privacyGroup;
	}

	public void setPrivacyGroup(String privacyGroup) {
		this.privacyGroup = privacyGroup;
	}

	@Column(name = "type", nullable = true)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "creator", nullable = true)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
	@Column(name = "create_date", nullable = true)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public int compareTo(DirectoryEntry o) {
		if (!this.parent.equals(o.getParent())) {
			return 0;
		}
		if (this.order > o.getOrder()) {
			return 1;
		} else if (this.order < o.getOrder()) {
			return -1;
		}
		return 0;
	}

}
