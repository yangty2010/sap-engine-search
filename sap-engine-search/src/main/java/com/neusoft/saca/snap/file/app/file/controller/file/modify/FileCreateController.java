package com.neusoft.saca.snap.file.app.file.controller.file.modify;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.FileCreateDto;
import com.neusoft.saca.snap.file.app.file.dto.FilesCreateDto;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 
 * @author yangty-tsd
 * 
 */
@Controller
@RequestMapping("file")
public class FileCreateController {

	@Autowired
	private FileDomainService fileDomainService;


	/**
	 * 创建文件
	 * 
	 * @param fileCreateDto
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createFile(@RequestBody FileCreateDto fileCreateDto) {
		JSONObject result = new JSONObject();
		FileEntry fileEntry = fileDomainService.create(fileCreateDto);
		result.put("success", true);
		result.put("file", fileEntry);
		return result;
	}

	/**
	 * 创建多文件
	 * 
	 * @param fileCreateDto
	 * @return
	 */
	@RequestMapping(value = "create/batch", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createFiles(@RequestBody FilesCreateDto filesCreateDto) {
		JSONObject result = new JSONObject();
		List<FileEntry> files = fileDomainService.create(filesCreateDto);
		result.put("success", true);
		result.put("files", files);
		return result;
	}

	

}
