package com.neusoft.saca.snap.file.app.file.controller.directory.modify;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.DirectoryDeleteDto;
import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryDeleteSingleController {

	@Autowired
	private DirectoryFacade directoryFacade;


	/**
	 * 获取指定目录信息
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "delete/single", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteDirectory(
			@RequestBody DirectoryDeleteDto directoryDeleteDto) {
		JSONObject result = new JSONObject();
		boolean rtn = directoryFacade.deleteDirectory(directoryDeleteDto
				.getDirectoryId());
		result.put("success", true);
		return result;
	}

}
