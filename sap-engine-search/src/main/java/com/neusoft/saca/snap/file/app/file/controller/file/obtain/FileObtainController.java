package com.neusoft.saca.snap.file.app.file.controller.file.obtain;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
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
public class FileObtainController {

	@Autowired
	private FileDomainService fileDomainService;


	/**
	 * 获取文件
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "obtain", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainFile(@RequestParam String id) {
		JSONObject result = new JSONObject();
		FileEntry fileEntry = fileDomainService.obtain(id);
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
