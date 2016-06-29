package com.neusoft.saca.snap.search.vo;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.neusoft.saca.snap.infrastructure.search.vo.SearchableResourceBean;
import com.neusoft.saca.snap.search.tools.constant.AppSearchConstant;

public class SearchableFileBean extends SearchableResourceBean {
	@Field
	private String userId;// 发布者id
	@Field
	private String userName;// 发布者姓名
	@Field
	private String fileName; // 文档名称
	@Field
	private String description; // 文档描述
	@Field
	private String[] tags; // 标签
	@Field
	private String content;// 动态内容
	@Field
	private Date createTime; // 创建日期
	@Field
	private Date uploadTime; // 上传日期
	@Field
	private String scopeId;// 发布区域id
	@Field
	private String scopeName;// 发布区域名称
	@Field
	private String scopeType;// 发布区域类型
	@Field
	private String scopePrivacy;// 发布区域隐私性
	@Field
	private String[] scopeMembers;// 发布区域的成员

	@Field
	private int resourcePrivacy;// 资源的隐私性
	@Field
	private String[] resourceMembers;// 资源的可见成员
	@Field
	private String orgId;// 文件的组织id
	@Field
	private float rating;

	public int getResourcePrivacy() {
		return resourcePrivacy;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public void setResourcePrivacy(int resourcePrivacy) {
		this.resourcePrivacy = resourcePrivacy;
	}

	public String[] getResourceMembers() {
		return resourceMembers;
	}

	public void setResourceMembers(String[] resourceMembers) {
		this.resourceMembers = resourceMembers;
	}

	public SearchableFileBean() {
		super();
		super.setResourceType(AppSearchConstant.RESOURCE_TYPE_FILE);
	}

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

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getScopePrivacy() {
		return scopePrivacy;
	}

	public void setScopePrivacy(String scopePrivacy) {
		this.scopePrivacy = scopePrivacy;
	}

	public String[] getScopeMembers() {
		return scopeMembers;
	}

	public void setScopeMembers(String[] scopeMembers) {
		this.scopeMembers = scopeMembers;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

}
