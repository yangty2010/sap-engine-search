package com.neusoft.saca.snap.search.vo;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import com.neusoft.saca.snap.infrastructure.search.vo.SearchedResourceBean;
import com.neusoft.saca.snap.infrastructure.search.vo.TagHighlight;
import com.neusoft.saca.snap.search.tools.constant.AppSearchConstant;

/**
 * 被搜索到的file资源bean
 * 
 * @author yanzhx
 * 
 */
public class SearchedFileBean extends SearchedResourceBean {
	public static final String[] RETURN_FIELDS = new String[] { "content", "id", "resourceType", "userId",
			"userName", "fileName", "description", "tags", "createTime", "uploadTime", "scopeId", "scopeType",
			"scopeName", "orgId", "rating" };
	@Field
	private String content;// 动态内容，可以为空
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
	private String orgId;// 文件发布的组织id(部门、群组、知识库id、动态中的为open)
	@Field
	private float rating;

	private List<TagHighlight> tagHighlights;// 高亮标签

	public SearchedFileBean() {
		super();
		super.setResourceType(AppSearchConstant.RESOURCE_TYPE_FILE);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String[] getTags() {
		return tags;
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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
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

	public List<TagHighlight> getTagHighlights() {
		return tagHighlights;
	}

	public void setTagHighlights(List<TagHighlight> tagHighlights) {
		this.tagHighlights = tagHighlights;
	}

}
