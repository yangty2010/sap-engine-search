package com.neusoft.saca.snap.file.app.file.controller.directory.obtain;

import java.util.List;

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
public class DirectoryObtainChildrenController {

	@Autowired
	private DirectoryFacade directoryFacade;

	/**
	 * 获取指定目录下的一级子目录
	 * 
	 * @param parent
	 * @return
	 */
	@RequestMapping(value = "obtain/children", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainDirectories(@RequestParam String parent,
			@RequestParam String userId, @RequestParam String groupId) {
		JSONObject result = new JSONObject();
		List<DirectoryEntry> dirs = directoryFacade.obtainDirectories(parent,
				userId, groupId);
		if (dirs != null) {
			result.put("success", true);
			result.put("dirs", dirs);
		} else {
			result.put("success", false);
			result.put("msg", "目录不存在！");
		}
		return result;
	}

	
}
