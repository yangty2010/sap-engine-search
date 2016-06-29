package com.neusoft.saca.snap.file.app.file.controller.directory.obtain;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.application.DirectoryService;
import com.neusoft.saca.snap.file.domain.directory.repository.DirectoryJpaRepo;
import com.neusoft.saca.snap.file.domain.file.api.FileDomainConstant;
import com.neusoft.saca.snap.file.domain.file.application.FileDomainService;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryObtainFilesController {

	@Autowired
	private DirectoryFacade directoryFacade;

	@Autowired
	private DirectoryService directoryService;

	@Autowired
	private DirectoryJpaRepo directoryJpaRepo;

	@Autowired
	private FileDomainService fileFacade;
	
	private static final String DEFAULT_SORT_CONDITION = "uploadTime";// 默认排序条件：上传时间

	/**
	 * 文件排序条件
	 */
	@SuppressWarnings("serial")
	private static final List<String> registeredFileSortCondition = new ArrayList<String>() {

		{
			add("uploadTime");// 上传时间
			add("finalScore");// 评级 TODO 抛弃
			add("rating");// 评级
		}
	};



	/**
	 * 获取指定目录下的文件列表，带排序
	 * 
	 * @param directory
	 * @param sortCondition
	 * @param sortOrder
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value = "obtain/files", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject obtainFiles(@RequestParam String directory,
			@RequestParam String sortCondition, @RequestParam String sortOrder,
			@RequestParam Integer page, @RequestParam Integer size) {
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
		Sort sort = convert2Sort(sortCondition, sortOrder);
		Page<FileEntry> files = fileFacade.obtainFilesByScopeId(directory,
				sort, page, size);
		if (files != null) {
			result.put("success", true);
			result.put("files", files);
		} else {
			result.put("success", false);
			result.put("msg", "文件不存在！");
		}
		return result;
	}
	
	private Sort convert2Sort(String sortCondition, String sortOrder) {
		String condition = DEFAULT_SORT_CONDITION;
		if (StringUtils.isNotBlank(sortCondition)
				&& registeredFileSortCondition.contains(sortCondition)) {
			condition = sortCondition;
		}
		Direction direction = Direction.DESC;
		if (StringUtils.isNotBlank(sortOrder)
				&& "asc".equalsIgnoreCase(sortOrder)) {
			direction = Direction.ASC;
		}
		Sort sort = new Sort(direction, condition);
		return sort;
	}


}
