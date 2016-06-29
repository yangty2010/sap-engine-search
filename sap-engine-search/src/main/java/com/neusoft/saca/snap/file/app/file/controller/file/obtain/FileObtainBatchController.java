package com.neusoft.saca.snap.file.app.file.controller.file.obtain;

import java.util.List;

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
 * @author yangty-tsd
 * 
 */
@Controller
@RequestMapping("file")
public class FileObtainBatchController {

	@Autowired
	private FileDomainService fileDomainService;


	/**
	 * 根据id列表获取文件列表
	 * 
	 * @param fileIds
	 * @return
	 */
	@RequestMapping(value = "obtain/batch", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainFiles(@RequestParam List<String> fileIds) {
		JSONObject result = new JSONObject();
		List<FileEntry> files = fileDomainService.obtainFilesByIds(fileIds);
		if (files != null) {
			result.put("success", true);
			result.put("files", files);
		} else {
			result.put("success", false);
			result.put("msg", "文件不存在！");
		}
		return result;
	}

}
