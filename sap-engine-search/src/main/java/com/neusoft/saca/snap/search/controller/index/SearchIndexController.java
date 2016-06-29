package com.neusoft.saca.snap.search.controller.index;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.search.application.SearchIndexService;

/**
 * 
 * @author yangty-tsd
 *
 */
@Controller
@RequestMapping("search")
public class SearchIndexController {

	@Autowired
	private SearchIndexService searchIndexService;
	
	/**
	 * 为文件建立索引
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value="index",method=RequestMethod.POST)
	public @ResponseBody JSONObject indexFile(@RequestBody String fileId){
		JSONObject jsonObject=new JSONObject();
		searchIndexService.indexFileContent(fileId);
		jsonObject.put("code", "0");
		return jsonObject;
	}
	
}
