package com.neusoft.saca.snap.file.app.file.controller.directory.modify;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.DirectoryCreateDto;
import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;
import com.neusoft.saca.snap.infrastructure.util.BeanUtil;

/**
 * 
 * @author YANGTY-TSD
 *
 */
@Controller
@RequestMapping("directory")
public class DirectoryCreateController {

	@Autowired
	private DirectoryFacade directoryFacade;

	/**
	 * 创建目录
	 * 
	 * @param directoryDto
	 * @return
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject create(@RequestBody DirectoryCreateDto directoryCreateDto) {
		JSONObject result = new JSONObject();
		DirectoryEntry directoryEntry = new DirectoryEntry();
		BeanUtil.copyBeanProperties(directoryEntry, directoryCreateDto);
		directoryFacade.create(directoryEntry);
		result.put("success", true);
		return result;
	}

}
