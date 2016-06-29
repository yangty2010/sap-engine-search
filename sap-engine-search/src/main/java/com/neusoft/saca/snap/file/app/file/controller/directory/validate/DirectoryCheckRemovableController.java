package com.neusoft.saca.snap.file.app.file.controller.directory.validate;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryCheckRemovableController {

	@Autowired
	private DirectoryFacade directoryFacade;


	/**
	 * 检查目录是否能够被删除
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "check/removable", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject checkDirectoryRemovable(@RequestParam String directoryId) {
		JSONObject result = new JSONObject();
		boolean checkFlag = directoryFacade.checkDirectoryRemovable(directoryId);
		result.put("success", true);
		result.put("removable", checkFlag);
		return result;
	}

}
