package com.neusoft.saca.snap.file.app.file.controller.file.modify;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.FileUpdateDto;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 
 * @author yangty-tsd
 * 
 */
@Controller
@RequestMapping("file")
public class FileUpdateController {

	@Autowired
	private FileDomainService fileDomainService;

	/**
	 * 更新文件
	 * 
	 * @param fileCreateDto
	 * @return
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject update(@RequestBody FileUpdateDto fileUpdateDto) {
		JSONObject result = new JSONObject();
		FileEntry file = fileDomainService.update(fileUpdateDto);
		if (file != null) {
			result.put("success", true);
			result.put("file", file);
		} else {
			result.put("success", false);
			result.put("msg", "更新失败，不存在id为" + fileUpdateDto.getId() + "的文件！");
		}
		return result;
	}


	/**
	 * 更新文件预览信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "update/preview", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject updatePdfPreview(@RequestParam String id) {
		JSONObject result = new JSONObject();
		FileEntry fileEntry = fileDomainService.updatePdfPreview(id);
		if (fileEntry != null) {
			result.put("success", true);
			result.put("file", fileEntry);
		} else {
			result.put("success", false);
			result.put("msg", "文件不存在！");
		}
		return result;
	}
	

}
