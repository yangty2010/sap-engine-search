package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.utils.FileUtils;
import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "file")
public class FilePlayController {

	@Autowired
	private FileService fileService;
	@Autowired
	private TicketManageFacade ticketManageFacade;


	/**
	 * 直接读取文件流，用于音视频文件播放
	 * 
	 * @param request
	 * @param response
	 * @param fileId
	 * @throws IOException
	 */
	@RequestMapping(value = "play", method = RequestMethod.GET)
	public void play(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileId)
			throws IOException {
		LicenseUtil.checkLicense();
		GridFSDBFile gridFSFile = fileService.obtainOriginalFile(fileId);
		// 设置response的Header
		// response.addHeader("Content-Disposition", "attachment; filename="+
		// finalName+";filename*=utf-8''"+finalName);
		String type = (String) gridFSFile.getMetaData().get("type");
		response.addHeader("Content-Length", "" + gridFSFile.getLength());
		String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		if(StringUtils.isNotBlank(type)){
			type = type.toLowerCase();
			switch (type) {
			case "mp3":
				contentType = "audio/mpeg";
				break;
			case "mp4":
				contentType = "audio/mp4";
				break;
			case "ogg":
				contentType = "audio/mp4";
				break;
			}
		}
		
		response.setContentType(contentType);

		InputStream inputStream = gridFSFile.getInputStream();

		FileUtils.bufferedDownload(response, inputStream);

	}


}
