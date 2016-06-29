package com.neusoft.saca.snap.search.controller.search;

import java.util.List;

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
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("search")
public class SearchFileNumberController {

	@Autowired
	private AppSearchFacade facade;

	/**
	 * 根据资源类型和搜索关键字搜索
	 * 
	 * @param currentUserId
	 * @param keyWords
	 *            搜索关键字的数组, 不能传入null
	 * @param page
	 *            页码数，从1开始
	 * @param pageSize
	 * @param searchType
	 *            要查询的类型：标签或者空，空表示正常搜索
	 * @return
	 */
	@RequestMapping(value = "file/number", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject searchFileFacet(@RequestParam String currentUserId, @RequestParam String[] keyWords,
			@RequestParam(required = false) String searchType, @RequestParam List<String> orgIds) {
		//1 声明定义变量
		JSONObject jsonObject = new JSONObject();
		
		//2 根据搜索类型（null,tag,topic）查询文档类型的结果集
		SearchResult searchResult = facade.searchFileNumber(currentUserId, keyWords, 1, 1, searchType, orgIds);
		
		//3 返回结果
		jsonObject.put("success", true);
		jsonObject.put("number", searchResult.getTotalCount());
		return jsonObject;
	}
	
}
