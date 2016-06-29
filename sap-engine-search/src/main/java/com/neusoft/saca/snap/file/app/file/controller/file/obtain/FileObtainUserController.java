package com.neusoft.saca.snap.file.app.file.controller.file.obtain;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.utils.FileUtils;
import com.neusoft.saca.snap.file.domain.file.api.FileDomainConstant;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 
 * @author yangty-tsd
 * 
 */
@Controller
@RequestMapping("file")
public class FileObtainUserController {

	@Autowired
	private FileDomainService fileDomainService;

	/**
	 * 获取个人文件
	 * 
	 * @param publisher
	 * @param sortCondition
	 * @param sortOrder
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value = "obtain/user", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainUserFiles(@RequestParam String publisher, @RequestParam String sortCondition,
			@RequestParam String sortOrder, @RequestParam Integer page, @RequestParam Integer size) {
		JSONObject result = new JSONObject();
		/**
		 * 规范分页、排序条件
		 */
		if (page == null || page < 0) {
			page = Integer.valueOf(1);
		}
		if (size == null || size < 0) {
			size = Integer.valueOf(FileDomainConstant.PAGE_SIZE_IN_DIRECTORY);
		}
		Sort sort = FileUtils.convert2Sort(sortCondition, sortOrder);
		Page<FileEntry> files = fileDomainService.obtainFilesByPublisher(publisher, sort, page, size);
		if (files != null) {
			result.put("success", true);
			result.put("files", files.getContent());
			result.put("amount", files.getTotalElements());
		} else {
			result.put("success", false);
			result.put("msg", "文件不存在！");
		}
		return result;
	}


}
