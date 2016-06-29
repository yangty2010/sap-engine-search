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
public class DirectoryObtainTreeController {

	@Autowired
	private DirectoryFacade directoryFacade;

	/**
	 * 获取目录路树结构
	 * 
	 * @param rootDirectory
	 * @return
	 */
	@RequestMapping(value = "obtain/tree", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainDirectoryTree(@RequestParam String rootDirectory) {
		JSONObject result = new JSONObject();
		List<DirectoryEntry> dirs = directoryFacade.obtainDirectoryTree(rootDirectory);
		result.put("success", true);
		result.put("dirs", dirs);
		return result;
	}

}
