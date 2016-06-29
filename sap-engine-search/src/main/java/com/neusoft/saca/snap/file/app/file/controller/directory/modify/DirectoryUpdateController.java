package com.neusoft.saca.snap.file.app.file.controller.directory.modify;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.dto.DirectoryUpdateDto;
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
public class DirectoryUpdateController {

	@Autowired
	private DirectoryFacade directoryFacade;

	/**
	 * 修改目录
	 * 
	 * @param directoryDto
	 * @return
	 */
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject update(@RequestBody DirectoryUpdateDto directoryUpdateDto) {
		JSONObject result = new JSONObject();
		DirectoryEntry directoryEntry = directoryFacade
				.obtainDirectory(directoryUpdateDto.getId());
		BeanUtil.copyBeanProperties(directoryEntry, directoryUpdateDto);
		directoryFacade.update(directoryEntry);
		result.put("success", true);
		return result;
	}

}
