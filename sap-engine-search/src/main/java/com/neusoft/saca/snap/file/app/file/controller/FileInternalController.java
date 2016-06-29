package com.neusoft.saca.snap.file.app.file.controller;

import java.util.List;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

/**
 * 文件服务器提供的内部服务接口，只对业务服务器开放
 * 
 * @author yan
 * 
 */
@Controller
@RequestMapping("fileinternal")
public class FileInternalController {

	@Autowired
	private FileService fileService;
	@Autowired
	private TicketManageFacade ticketManageFacade;

	/**
	 * 获取文件的ticket
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value="ticket")
	public @ResponseBody
	JSONObject obtainTicket(@RequestParam String fileId) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		String ticketKey = UUID.randomUUID().toString();
		ticketManageFacade.createTicket(ticketKey, fileId, "file");
		jsonObject.put("code", "0");
		JSONObject data = new JSONObject();
		data.put("ticket", ticketKey);
		jsonObject.put("data", data);
		return jsonObject;
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject deleteFile(@RequestParam String fileId) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		fileService.delete(fileId);
		jsonObject.put("code", "0");
		return jsonObject;
	}

	/**
	 * 删除多个文件
	 * 
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "batchdelete", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject deleteFiles(@RequestParam List<String> fileIds) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		fileService.delete(fileIds);
		jsonObject.put("code", "0");
		return jsonObject;
	}

	/**
	 * 将上传的文件状态置为“绑定”，即业务中存在对此文件的引用
	 * 
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "attach", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject attachFile(@RequestParam String fileId) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		fileService.attachFile(fileId);
		jsonObject.put("code", "0");
		return jsonObject;
	}

	/**
	 * 将上传的文件状态置为“绑定”，即业务中存在对此文件的引用
	 * 
	 * @param fileIds
	 * @return
	 */
	@RequestMapping(value = "batchattach", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject batchAttachFile(@RequestParam List<String> fileIds) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		fileService.batchAttachFile(fileIds);
		jsonObject.put("code", "0");
		return jsonObject;
	}
	
	/**
	 * 获取文件的可预览状态
	 * 
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "preview/state", method = RequestMethod.POST)
	public @ResponseBody
	JSONObject obtainPreviewState(@RequestParam String fileId) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("code", "0");
		JSONObject data = new JSONObject();
		data.put("state", fileService.obtainFilePreviewStatus(fileId));
		jsonObject.put("data", data);
		return jsonObject;
	}
	
}
