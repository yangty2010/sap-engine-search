package com.neusoft.saca.snap.search.tools.assembles;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;
import com.neusoft.saca.snap.infrastructure.constant.PrivacyConstant;
import com.neusoft.saca.snap.search.tools.SearchUtils;
import com.neusoft.saca.snap.search.tools.constant.AppSearchConstant;
import com.neusoft.saca.snap.search.vo.SearchableFileBean;

/**
 * @author YANGTY-TSD
 */
@Service
public class SearchAssembles {

	@Autowired
	private SearchUtils utils;
	
	/**
	 * 如果有查询类型，根据查询类型构造查询语句
	 * @param searchType
	 * @param currentUserId
	 * @param keyWords
	 * @param orgIds
	 * @return
	 */
	public String assembleQueryStr(String searchType, String currentUserId, String[] keyWords, String[] orgIds){
		
		String queryStr = "";
		//1 如果搜索类型不为空
		if (StringUtils.isNotBlank(searchType)) {
			queryStr = this.assembleQueryStringBySearchType(currentUserId,
					keyWords, searchType, AppSearchConstant.RESOURCE_TYPE_FILE,
					orgIds);
		//2 如果搜索类型为空
		} else {
			queryStr = this.assembleQueryString(currentUserId, keyWords,
					AppSearchConstant.RESOURCE_TYPE_FILE, orgIds);
		}
		
		//3 返回查询语句
		return queryStr;
	}
	
	/**
	 * 
	 * @param currentUserId
	 * @param query
	 * @param orgId
	 * @return
	 */
	public String assembleFileQueryString(String currentUserId, String[] query, String orgId) {
		StringBuilder queryString = new StringBuilder();
		String queryStr = "";
		for (int i = 0; i < query.length; i++) {
			queryStr += query[i] + " ";
		}
		queryStr = queryStr.substring(0, queryStr.length() - 1);
		queryString.append("resourceType:file");
		queryString.append(" AND ");
		queryString.append(" ( ");
		queryString.append("fileName:");
		queryString.append(queryStr);
		queryString.append(" OR ");
		queryString.append("description:");
		queryString.append(queryStr);
		queryString.append(" ) ");
		queryString.append(" AND ");
		queryString.append("orgId:");
		queryString.append(orgId);
		queryString.append(" AND ");
		// 添加隐私
		queryString.append("(");
		queryString.append("( ");
		queryString.append("userId:");
		queryString.append(currentUserId);
		queryString.append(")");
		queryString.append(" OR ");
		queryString.append("( resourcePrivacy:");
		queryString.append(PrivacyConstant.PARTIAL_PUBLIC);
		queryString.append(" AND resourceMembers:");
		queryString.append(currentUserId);
		queryString.append(")");
		queryString.append(" OR ");
		queryString.append("resourcePrivacy:");
		queryString.append(PrivacyConstant.PUBLIC);
		queryString.append(" )");

		return queryString.toString();
	}
	
	public StringBuilder buildUserQueryStr(String nameFragment, String company,String scopeId,String scopeType) {
		
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("resourceType:");
		strBuilder.append("user");
		strBuilder.append(" AND ");
		strBuilder.append("( userName:");
		strBuilder.append(nameFragment);
		strBuilder.append(" OR ");
		strBuilder.append("pinyin:");
		strBuilder.append(nameFragment);
		strBuilder.append(")");
		if (StringUtils.isNotBlank(company)) {
			strBuilder.append(" AND ");
			strBuilder.append("company:");
			strBuilder.append(company);
		}
		if(StringUtils.isNotBlank(scopeType)&&StringUtils.isNotBlank(scopeId)){
			strBuilder.append(" AND ");
			strBuilder.append(scopeType);
			strBuilder.append(":");
			strBuilder.append(scopeId);			
		}
		
		return strBuilder;
	}
	
	/**
	 * 搜索类型为“话题”topic或者“标签”tag
	 * @param query
	 * @param searchType
	 * @param resourceType
	 * @return
	 */
	public String assembleQueryStringBySearchType(String currentUserId, String[] query, String searchType,
			String resourceType, String[] orgIds) {
		StringBuilder queryString = new StringBuilder();
		
		String queryCriteria1d0 = this.assembleKeywords(query, 1.0f);
		
		queryString = this.assembleSubQueryString(currentUserId, queryString, resourceType, searchType+"s", queryCriteria1d0, orgIds);
		
		return queryString.toString();
	}
	
	/**
	 * TODO 优化
	 * 
	 * @param query
	 * @param resourceType
	 * @return
	 */
	public String assembleQueryString(String currentUserId, String[] query, String resourceType, String[] orgIds) {
		
		StringBuilder queryString = new StringBuilder();
		//1 文件名权重为2.0
		String queryCriteria2d0 = this.assembleKeywords(query, 2.0f);
		queryString = this.assembleSubQueryString(currentUserId, queryString, resourceType, "fileName", queryCriteria2d0,
				orgIds);
		//2 标签权重为2.0
		queryString = this.assembleSubQueryString(currentUserId, queryString, resourceType, "tags", queryCriteria2d0,
				orgIds);
		//3 描述内容权重为1.0
		String queryCriteria1d0 = this.assembleKeywords(query, 1.0f);
		queryString = this.assembleSubQueryString(currentUserId, queryString, resourceType, "description",
				queryCriteria1d0, orgIds);
		//4 文档内容权重为1.0
		queryString = this.assembleSubQueryString(currentUserId, queryString, resourceType, "content", queryCriteria1d0,
				orgIds);
		//5 返回
		return queryString.toString();
	}
	
	/**
	 * 调节查询关键字的权重
	 * 
	 * @param query
	 * @param boostFactor
	 * @return
	 */
	public String assembleKeywords(String[] querys, float boostFactor) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < querys.length; i++) {
			sb.append("\"").append(querys[i]).append("\"").append("^").append(boostFactor).append(" ");
		}
		// 非最后一个需要添加一个空格
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param currentUserId
	 * @param queryString
	 * @param resourceType
	 * @param field
	 * @param criteria
	 * @param orgIds
	 * @return
	 */
	private StringBuilder assembleSubQueryString(String currentUserId, StringBuilder queryString, String resourceType,
			String field, String criteria, String[] orgIds) {
		if (queryString.length() != 0) {
			queryString.append(" OR ");
		}
		queryString.append("(");
		if (StringUtils.isNotBlank(resourceType)) {// resourceType为空
			queryString.append("resourceType:");
			queryString.append(resourceType);
			queryString.append(" AND ");
		}
		queryString.append(field + ":(");
		queryString.append(criteria);
		queryString.append(")");

		/**
		 * 增加隐私搜索
		 */
		queryString.append(" AND ");
		
		this.assembleSubQueryWithPrivacy(currentUserId, queryString, orgIds);

		queryString.append(")");
		return queryString;
	}
	
	/**
	 * 带有隐私的搜索语句构建
	 * 
	 * @param currentUserId
	 *            当前查询用户
	 * @param queryString
	 * @param orgIds
	 *            用户所加入的所有组织（群组、部门以及其他）
	 * @return
	 */
	public StringBuilder assembleSubQueryWithPrivacy(String currentUserId, StringBuilder queryString, String[] orgIds) {

		/**
		 * ( (userId: currentUserId) OR (resourcePrivacy:1 AND resourceMembers:
		 * currentUserId) OR (resourcePrivacy:2 AND orgId: (orgId[0] OR
		 * orgId[1] OR ...)) OR resourcePrivacy:3 )
		 */
		queryString.append("(");

		queryString.append("( ");
		queryString.append("userId:");
		queryString.append(currentUserId);
		queryString.append(")");

		queryString.append(" OR ");

		queryString.append("( resourcePrivacy:");
		queryString.append(PrivacyConstant.PARTIAL_PUBLIC);
		queryString.append(" AND resourceMembers:");
		queryString.append(currentUserId);
		queryString.append(")");

		queryString.append(" OR ");
		if (orgIds != null && orgIds.length > 0) {
			queryString.append("( resourcePrivacy:");
			queryString.append(PrivacyConstant.ORG_PUBLIC);
			queryString.append(" AND orgId:");

			queryString.append("(");

			for (String orgId : orgIds) {
				queryString.append(" ");
				queryString.append(orgId);
			}

			queryString.append(")");
			queryString.append(")");

			queryString.append(" OR ");
		}

		queryString.append("resourcePrivacy:");
		queryString.append(PrivacyConstant.PUBLIC);

		queryString.append(" )");
		return queryString;
	}
	
	/**
	 * 装配filebean
	 * @param file
	 * @return
	 */
	public SearchableFileBean assembleFileBean(FileEntry file){
		SearchableFileBean searchableFileBean = new SearchableFileBean();
		
		String fileId = file.getId();
		searchableFileBean.setId(fileId);
		searchableFileBean.setFileName(file.getName());
		searchableFileBean.setDescription(file.getDescription());
		searchableFileBean.setOrgId(file.getOrgId());
		searchableFileBean.setCreateTime(file.getUploadTime());
		searchableFileBean.setScopeId(file.getScopeId());
		searchableFileBean.setScopeName(file.getScopeName());
		searchableFileBean.setScopeType(file.getScopeType());
		String tagsStr = file.getTags();
		if (StringUtils.isNotBlank(tagsStr)) {
			searchableFileBean.setTags(tagsStr.split(","));
		}
		searchableFileBean.setResourcePrivacy(file.getPrivacy());// 设置隐私
		searchableFileBean.setUserId(file.getPublisher());
		searchableFileBean.setUserName(file.getPublisherName());
		searchableFileBean.setRating(file.getRating());
		searchableFileBean.setUploadTime(file.getUploadTime());
		
		return searchableFileBean;
	}
	
	/**
	 * 装配filebean,按照指定成员可见
	 * @param file
	 * @return
	 */
	public SearchableFileBean assembleFileBeanForPrivacy(FileEntry file){
		SearchableFileBean fileBean = this.assembleFileBean(file);
		
		// 指定成员可见，需要把指定成员建立索引
		if (file.getPrivacy() == PrivacyConstant.PARTIAL_PUBLIC) {
			String memberStr = file.getPrivacyMember();
			if (StringUtils.isNotBlank(memberStr)) {
				String[] members = memberStr.trim().split(",");
				fileBean.setResourceMembers(members);
			}
		}
		
		return fileBean;		
	}
	
}
