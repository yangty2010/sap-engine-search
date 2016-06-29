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

import com.neusoft.saca.snap.file.app.file.dto.DirectoriesCreateDto;
import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryCreateBatchController {

	@Autowired
	private DirectoryFacade directoryFacade;


	/**
	 * 批量创建目录
	 * 
	 * @param directoryDto
	 * @return
	 */
	@RequestMapping(value = "create/batch", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject create(@RequestBody DirectoriesCreateDto directoriesCreateDto) {
		JSONObject result = new JSONObject();
		String dirsJson = directoriesCreateDto.getDirsJson();
		JSONArray jsonArray = JSONArray.fromObject(dirsJson);
		List<DirectoryEntry> directoryEntries = new ArrayList<DirectoryEntry>();
		for (Object dirObject : jsonArray) {
			DirectoryEntry directoryEntry = (DirectoryEntry) JSONObject.toBean(
					(JSONObject) dirObject, DirectoryEntry.class);
			directoryEntries.add(directoryEntry);
		}
		directoryFacade.batchCreate(directoryEntries);
		result.put("success", true);
		return result;
	}
}
