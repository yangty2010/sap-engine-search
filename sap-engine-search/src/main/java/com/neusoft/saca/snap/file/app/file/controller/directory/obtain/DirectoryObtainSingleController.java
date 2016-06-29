package com.neusoft.saca.snap.file.app.file.controller.directory.obtain;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryObtainSingleController {

	@Autowired
	private DirectoryFacade directoryFacade;

	/**
	 * 获取指定目录信息
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "obtain/single", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainDirectory(@RequestParam String directoryId) {
		JSONObject result = new JSONObject();
		DirectoryEntry directoryEntry = directoryFacade
				.obtainDirectory(directoryId);
		if (directoryEntry != null) {
			result.put("success", true);
			result.put("dir", directoryEntry);
		} else {
			result.put("success", false);
			result.put("msg", "目录不存在！");
		}
		return result;
	}

	
}
