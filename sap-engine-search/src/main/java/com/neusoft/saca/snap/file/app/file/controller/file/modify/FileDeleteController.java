package com.neusoft.saca.snap.file.app.file.controller.file.modify;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.FileDeleteDto;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 
 * @author yangty-tsd
 * 
 */
@Controller
@RequestMapping("file")
public class FileDeleteController {

	@Autowired
	private FileDomainService fileDomainService;


	/**
	 * 删除文件
	 * 
	 * @param fileDeleteDto
	 * @return
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteFile(@RequestBody FileDeleteDto fileDeleteDto) {
		JSONObject result = new JSONObject();
		FileEntry fileEntry = fileDomainService.delete(fileDeleteDto.getFileId());
		result.put("success", true);
		result.put("file", fileEntry);
		return result;
	}

	

}
