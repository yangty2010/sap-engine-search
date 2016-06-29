package com.neusoft.saca.snap.file.app.file.controller.file.obtain;

import java.io.IOException;
import java.io.InputStream;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.application.FileService;

/**
 * 
 * @author YANGTY-TSD
 * 
 */
@Controller
@RequestMapping("file")
public class FileObtainPdfController {

	@Autowired
	private FileService fileService;


	/**
	 * 获取PDF文件
	 * 
	 * @param fileId
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "pdf/obtain", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainFilePdf(@RequestParam String fileId) throws IOException {
		JSONObject result = new JSONObject();
		InputStream inputStream = fileService.obtainPdfWithoutDefault(fileId);
		if (inputStream != null) {
			result.put("success", true);
			result.put("inputStream", IOUtils.toString(inputStream, "UTF-8"));
		} else {
			result.put("success", false);
		}
		return result;
	}

}
