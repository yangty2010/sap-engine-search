package com.neusoft.saca.snap.file.domain.file.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.neusoft.saca.snap.infrastructure.util.DateUtil;

/**
 * 通用文件实体，用来保存系统中上传的文件、附件等具有文件性质的数据信息
 * 
 * @author yan
 * 
 */
@Entity
@Table(name = "file")
public class FileEntry implements java.io.Serializable {
	private static final long serialVersionUID = -6578620298600603147L;

	/**
	 * 唯一性id
	 */
	@Id
	@Column(name = "id", nullable = false, unique = true, length = 36)
	private String id;

	/**
	 * 文件名称
	 */
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	/**
	 * 文件大小
	 */
	@Column(name = "size_in_bytes")
	private long sizeInBytes;

	/**
	 * 文件上传时间
	 */
	@Column(name = "upload_time", nullable = false)
	private Date uploadTime;

	/**
	 * 文件类型
	 */
	@Column(name = "type", nullable = false, length = 50)
	private String type;
	
	/**
	 * 获取文件的类型大写
	 * 
	 * @return
	 */
	@Transient
	public String getUpperCaseType() {
		return type.toUpperCase();
	}

	/**
	 * 获取上传时间的字符串
	 * 
	 * @return
	 */
	@Transient
	public String getFriendlyUploadTime() {
		return DateUtil.convertToString(uploadTime, "yyyy-MM-dd");
	}

	/**
	 * 文件发布者
	 */
	@Column(name = "publisher", nullable = false, length = 50)
	private String publisher;

	/**
	 * 文件发布者姓名
	 */
	@Column(name = "publisher_name", nullable = false, length = 50)
	private String publisherName;

	/**
	 * 文件发布区域id
	 */
	@Column(name = "scope_id", nullable = false, length = 36)
	private String scopeId;

	/**
	 * 文件发布区域名称
	 */
	@Column(name = "scope_name", nullable = false, length = 50)
	private String scopeName;

	/**
	 * 文件发布区域类型
	 */
	@Column(name = "scope_type", nullable = false, length = 50)
	private String scopeType;

	/**
	 * 是否在知识库中显示，可以为空
	 */
	@Column(name = "in_knowledge_base", nullable = true)
	private boolean inKnowledgeBase;

	/**
	 * 文件描述
	 */
	@Column(name = "description", nullable = true)
	private String description;

	/**
	 * 文件隐私性
	 */
	@Column(name = "privacy")
	private int privacy;

	/**
	 * 文件可见成员
	 */
	@Column(name = "privacy_member", length = 5000)
	private String privacyMember;

	/**
	 * 文档所在组织id，包括群组、部门id
	 */
	@Column(name = "org_id", length = 36)
	private String orgId;

	/**
	 * 评级分数
	 */
	@Column(name = "rating")
	private float rating;

	/**
	 * 文件是否可预览 
	 * 一共三个值："0","1","2",分别代表：不可预览，疑似不可预览，可预览，
	 * 其中疑似不可预览需要再进行确认，再转换为0和2两个状态
	 */
	@Column(name = "preview")
	private String preview;
	
	@Column(name = "tags", length = 5000)
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

	/**
	 * 获取文档可见成员
	 * 
	 * @return
	 */
	@Transient
	public List<String> getPrivacyMembers() {
		List<String> list = new ArrayList<String>();

		if (StringUtils.isNotBlank(privacyMember)) {
			String[] members = privacyMember.split(",");
			for (String member : members) {
				if (StringUtils.isNotBlank(member)) {
					list.add(member);
				}
			}
		}

		return list;
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
