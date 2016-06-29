package com.neusoft.saca.snap.file.app.file.controller.directory.obtain;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryPathVo;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryObtainPathListController {

	@Autowired
	private DirectoryFacade directoryFacade;

	
	/**
	 * 获取目录路径列表
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "obtain/path/list", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainPathList(@RequestParam String directoryId) {
		JSONObject result = new JSONObject();
		List<DirectoryPathVo> paths = directoryFacade
				.obtainPathList(directoryId);
		result.put("success", true);
		result.put("paths", paths);
		return result;
	}

}
