package com.neusoft.saca.snap.search.controller.search;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.infrastructure.search.vo.SearchResult;
import com.neusoft.saca.snap.search.api.AppSearchFacade;

/**
 * 
 * @author YANTY-TSD
 *
 */
@Controller
@RequestMapping("search")
public class SearchFileScopeController {

	@Autowired
	private AppSearchFacade facade;
	

	/**
	 * 搜索文档，带有隐私
	 * 
	 * @param currentUserId
	 * @param scopeId
	 *            搜索区域id，群组id、
	 * @param keyWords
	 * @param page
	 * @param pageSize
	 * @param sortCondition
	 * @param sortOrder
	 * @return
	 */
	@RequestMapping(value = "file/scope", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject searchFile(@RequestParam String currentUserId, @RequestParam String scopeId,
			@RequestParam String[] keyWords, @RequestParam Integer page, @RequestParam Integer pageSize,
			@RequestParam String sortCondition, @RequestParam String sortOrder) {
		//1 声明定义变量
		JSONObject jsonObject = new JSONObject();
		
		//2 根据隐私查询文档类型的结果集
		SearchResult searchResult = facade.searchFile(currentUserId, scopeId, keyWords, page, pageSize,
				sortCondition, sortOrder);
		
		//3 返回结果
		jsonObject.put("success", true);
		jsonObject.put("searchResult", searchResult);
		return jsonObject;
	}
	
}
