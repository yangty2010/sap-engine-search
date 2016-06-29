package com.neusoft.saca.snap.search.controller.index;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.search.application.SearchIndexService;

/**
 * 
 * @author yangty-tsd
 *
 */
@Controller
@RequestMapping("search")
public class SearchIndexsController {

	@Autowired
	private SearchIndexService searchIndexService;
	
	/**
	 * 为多个文件的内容建立索引
	 * @param fileIds
	 * @return
	 */
	@RequestMapping(value="indexes")
	@ResponseBody
	public JSONObject indexFiles(@RequestParam List<String> fileIds){
		JSONObject jsonObject=new JSONObject();
		searchIndexService.indexMultiFileContent(fileIds);
		jsonObject.put("code", "0");
		return jsonObject;
	}
	
}
