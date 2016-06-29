package com.neusoft.saca.snap.search.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.saca.snap.infrastructure.search.application.SearchService;
import com.neusoft.saca.snap.infrastructure.search.vo.SearchResult;
import com.neusoft.saca.snap.infrastructure.search.vo.SearchedResourceBean;
import com.neusoft.saca.snap.search.tools.SearchDocumentObjectBinder;
import com.neusoft.saca.snap.search.tools.SearchUtils;
import com.neusoft.saca.snap.search.tools.assembles.SearchAssembles;
import com.neusoft.saca.snap.search.tools.constant.AppSearchConstant;
import com.neusoft.saca.snap.search.vo.MatchedUser;
import com.neusoft.saca.snap.search.vo.SearchedFileBean;
/**
 * 
 * @author YANGTY-TSD
 *
 */
@Service
public class AppSearchFacade {
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private SearchUtils utils;
	
	@Autowired
	private SearchAssembles assembles;

	/**
	 * 
	 * @param currentUserId
	 * @param keyWords
	 * @param page
	 * @param pageSize
	 * @param searchType
	 * @param orgIdList
	 * @return
	 */
	public SearchResult searchFileNumber(String currentUserId, String[] keyWords, int page, int pageSize, String searchType,
			List<String> orgIdList) {
		
		//1 判断校验
		if (keyWords == null) {
			throw new IllegalArgumentException("传入的keyWords不能为空");
		}
		
		//2 获取组织id
		String[] orgIds = utils.getOrgids(orgIdList);
		
		
		//3 获取搜索查询语句
		String queryStr = assembles.assembleQueryStr(searchType, currentUserId, keyWords, orgIds);
		
		
		//4 获取搜索结果
		SearchResult result = searchService.search(keyWords, queryStr, page, pageSize,
				SearchDocumentObjectBinder.obtainBinderMap(), SearchedFileBean.RETURN_FIELDS, "fileName", "tags",
				"content", "description");
		
		//5 返回结果
		return result;
	}
	
	/**
	 * 
	 * @param currentUserId
	 * @param keyWords
	 * @param page
	 * @param pageSize
	 * @param searchType
	 * @param orgIdList
	 * @return
	 */
	public SearchResult search(String currentUserId, String[] keyWords, int page, int pageSize, String searchType,
			List<String> orgIdList) {
		
		//1 获取搜索结果
		SearchResult result = this.searchFileNumber(currentUserId, keyWords, page, pageSize, searchType, orgIdList);
		
		//2 抽取标签高亮
		if (result.getTotalCount()>0) {
			result = SearchUtils.extractHighlightTags(result);
		}
		
		//3 返回结果
		return result;
	}


	/**
	 * 
	 * @param currentUserId
	 * @param orgId
	 * @param keyWords
	 * @param page
	 * @param pageSize
	 * @param sortCondition
	 * @param sortOrder
	 * @return
	 */
	public SearchResult searchFile(String currentUserId, String orgId, String[] keyWords, int page, int pageSize,
			String sortCondition, String sortOrder) {
		//1 判断校验
		if (keyWords == null) {
			throw new IllegalArgumentException("传入的keyWords不能为空");
		}
		
		//2 装配搜索查询语句
		String queryStr = assembles.assembleFileQueryString(currentUserId, keyWords, orgId);
		
		//3 获取搜索结果
		SearchResult result = searchService.search(queryStr, page, pageSize, sortCondition, sortOrder,
				SearchDocumentObjectBinder.obtainBinderMap(), SearchedFileBean.RETURN_FIELDS);
		
		
		return result;
	}
	
	public List<MatchedUser> obtainAllMatchedUsers(String nameFragment){
		if (StringUtils.isBlank(nameFragment)) {
			throw new IllegalArgumentException("要查询的姓名不能为空！");
		}
		String company = null;
		String scopeId = null;
		String scopeType = null;
		
		List<MatchedUser> resultUsers = new ArrayList<MatchedUser>();

		String queryStr = assembles.buildUserQueryStr(nameFragment, company, scopeId, scopeType).toString();
		
		@SuppressWarnings("unchecked")
		List<SearchedResourceBean> matchedUsers = (List<SearchedResourceBean>) searchService.searchFastSeek(queryStr,
				AppSearchConstant.USER_SEARCH_SIZE, MatchedUser.MATCHED_USER_FIELDS, MatchedUser.class);

		if (matchedUsers != null && matchedUsers.size() > 0) {
			for (SearchedResourceBean item : matchedUsers) {
				MatchedUser matchedUser = (MatchedUser) item;
				resultUsers.add(matchedUser);
			}
		}
		return resultUsers;		
	}


}
