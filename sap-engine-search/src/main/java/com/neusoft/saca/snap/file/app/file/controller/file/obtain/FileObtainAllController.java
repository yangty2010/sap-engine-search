package com.neusoft.saca.snap.file.app.file.controller.file.obtain;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 
 * @author YANGTY-TSD
 * 
 */
@Controller
@RequestMapping("file")
public class FileObtainAllController {

	@Autowired
	private FileDomainService fileDomainService;

	/**
	 * 获取文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "obtain/all", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainFile(@RequestParam int pageNum) {

		JSONObject result = new JSONObject();
		
		Page<FileEntry> files = fileDomainService.obtainFiles(pageNum, 50);

		if (files.hasContent()) {
			result.put("success", true);
			result.put("files", files.getContent());
			result.put("amount", files.getTotalElements());
		} else {
			result.put("success", false);
		}

		return result;
	}

}
