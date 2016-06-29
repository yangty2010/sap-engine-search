package com.neusoft.saca.snap.search.controller.index;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.search.application.AppSearchableResourceIndexUpdateService;

/**
 * 
 * @author yangty-tsd
 *
 */
@Controller
@RequestMapping("search")
public class SearchIndexRebuildController {

	@Autowired
	private AppSearchableResourceIndexUpdateService appSearchableResourceIndexUpdateService;

		
	/**
	 * 重建文件索引
	 * @return
	 */
	@RequestMapping(value = "file/index/rebuild", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject indexRebuild() {
		JSONObject jsonObject = new JSONObject();
		appSearchableResourceIndexUpdateService.rebuildFilesIndex();
		jsonObject.put("success", true);
		return jsonObject;
	}
}
