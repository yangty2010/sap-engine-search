package com.neusoft.saca.snap.file.app.file.controller.file.modify;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.FileMoveDto;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;

/**
 * 
 * @author yangty-tsd
 * 
 */
@Controller
@RequestMapping("file")
public class FileMoveController {

	@Autowired
	private FileDomainService fileDomainService;


	/**
	 * 移动文件
	 * 
	 * @param fileMoveDto
	 * @return
	 */
	@RequestMapping(value = "move", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject moveFile(@RequestBody FileMoveDto fileMoveDto) {
		JSONObject result = new JSONObject();
		fileDomainService.updateFileScope(fileMoveDto.getFileId(), fileMoveDto.getDirId(), fileMoveDto.getDirName());
		result.put("success", true);
		return result;
	}


}
