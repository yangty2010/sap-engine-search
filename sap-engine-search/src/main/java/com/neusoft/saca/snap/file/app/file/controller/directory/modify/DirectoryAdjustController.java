package com.neusoft.saca.snap.file.app.file.controller.directory.modify;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.DirectoryAdjustDto;
import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryAdjustController {

	@Autowired
	private DirectoryFacade directoryFacade;

	/**
	 * 调整目录结构
	 * 
	 * @param directoryDto
	 * @return
	 */
	@RequestMapping(value = "adjust", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject adjustDirectory(
			@RequestBody DirectoryAdjustDto directoryAdjustDto) {
		JSONObject result = new JSONObject();

		List<DirectoryEntry> directoryEntries = new ArrayList<DirectoryEntry>();
		JSONArray array = JSONArray.fromObject(directoryAdjustDto
				.getDirectories());
		for (int i = 0; i < array.size(); i++) {
			JSONObject directoryJson = array.getJSONObject(i);
			DirectoryEntry directoryEntry = (DirectoryEntry) JSONObject.toBean(
					directoryJson, DirectoryEntry.class);
			directoryEntry.setParent(directoryAdjustDto.getParent());
			directoryEntry.setOrder(i + 1);
			directoryEntries.add(directoryEntry);
		}

		directoryFacade.adjustDirectory(directoryAdjustDto.getParent(),
				directoryEntries);
		result.put("success", true);
		return result;
	}

}
