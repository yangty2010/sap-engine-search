package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.utils.FileUtils;
import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;
import com.neusoft.saca.snap.file.domain.ticket.vo.TicketValue;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "file")
public class FileShowController {

	@Autowired
	private FileService fileService;
	
	@Autowired
	private TicketManageFacade ticketManageFacade;


	/**
	 * 预览pdf
	 * 
	 * @param response
	 * @param fileTicket
	 * @throws IOException
	 */
	@RequestMapping(value = "show", method = RequestMethod.GET)
	public void viewPdf(HttpServletResponse response, @RequestParam("fileTicket") String fileTicket) throws IOException {
		LicenseUtil.checkLicense();
		// 从key管理组件中获取文件id，并验证合法性
		TicketValue ticketValue = ticketManageFacade.obtainValue(fileTicket, "file");
		JSONObject jsonObject = new JSONObject();
		if (ticketValue == null) {
			jsonObject.put("msg", "下载请求不合法，fileTicket:" + fileTicket);
			jsonObject.put("code", "-1");
			ControllerHelperUtil.returnJson(response, jsonObject);
		}

		// 获取输出流
		InputStream pdfis = fileService.obtainPdf(ticketValue.getValue());

		FileUtils.bufferedDownload(response, pdfis);
	}
	
	/**
	 * 获取预览文件的页数
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "showfilepages", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject viewPdfPages( @RequestParam(required=true) String fileId) throws IOException {
		LicenseUtil.checkLicense();
		return fileService.obtainPdfPages(fileId);
	}


}
