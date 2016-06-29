package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

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
import com.neusoft.saca.snap.file.domain.ticket.vo.TicketValue;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "file")
public class FileDownLoadController {

	@Autowired
	private FileService fileService;
	@Autowired
	private TicketManageFacade ticketManageFacade;


	/**
	 * 下载原始文件
	 * 
	 * @param fileTicket
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "download", method = RequestMethod.GET)
	public void download(@RequestParam("fileTicket") String fileTicket, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		LicenseUtil.checkLicense();
		// 从key管理组件中获取文件id，并验证合法性
		TicketValue ticketValue = ticketManageFacade.obtainValue(fileTicket, "file");
		JSONObject jsonObject = new JSONObject();
		if (ticketValue == null) {
			jsonObject.put("msg", "下载请求不合法，fileTicket:" + fileTicket);
			jsonObject.put("code", "-1");
			ControllerHelperUtil.returnJson(response, jsonObject);
		}

		GridFSDBFile gridFSFile = fileService.obtainOriginalFile(ticketValue.getValue());
		String fileName = (String) gridFSFile.getMetaData().get("originalFilename");
		boolean needCompress = (Boolean) gridFSFile.getMetaData().get("needCompress");
		if (needCompress) {// 如果是压缩文件的话修改文件名后缀
			fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".zip";
		}
		// 设置response的Header
		// 支持断点下载
		response.setHeader("Accept-Ranges", "bytes");
		String finalName = URLEncoder.encode(fileName, "UTF-8");
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment; filename=" + finalName + ";filename*=utf-8''"
				+ finalName);
		long fSize = gridFSFile.getLength();
		response.addHeader("Content-Length", "" + fSize);
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		InputStream inputStream = gridFSFile.getInputStream();

		long pos = 0;
		if (null != request.getHeader("Range")) {
			// 断点续传
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			try {
				pos = Long.parseLong(request.getHeader("Range").replaceAll("bytes=", "").replaceAll("-", ""));
			} catch (NumberFormatException e) {
				System.out.println(request.getHeader("Range") + " is not Number!");
				pos = 0;
			}
		}

		String contentRange = new StringBuffer("bytes ").append(new Long(pos).toString()).append("-")
				.append(new Long(fSize - 1).toString()).append("/").append(new Long(fSize).toString()).toString();
		response.setHeader("Content-Range", contentRange);

		inputStream.skip(pos);

		FileUtils.bufferedDownload(response, inputStream);

	}

	/**
	 * 下载原始文件，不带fileTicket
	 * 
	 * @param request
	 * @param response
	 * @param fileId
	 * @throws IOException
	 */
	@RequestMapping(value = "download1", method = RequestMethod.GET)
	public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileId)
			throws IOException {
		LicenseUtil.checkLicense();
		GridFSDBFile gridFSFile = fileService.obtainOriginalFile(fileId);
		String fileName = (String) gridFSFile.getMetaData().get("originalFilename");
		boolean needCompress = (Boolean) gridFSFile.getMetaData().get("needCompress");
		if (needCompress) {// 如果是压缩文件的话修改文件名后缀
			fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".zip";
		}
		String finalName = URLEncoder.encode(fileName, "UTF-8");
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment; filename=" + finalName + ";filename*=utf-8''"
				+ finalName);
		response.addHeader("Content-Length", "" + gridFSFile.getLength());
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

		InputStream inputStream = gridFSFile.getInputStream();

		FileUtils.bufferedDownload(response, inputStream);

	}


}
