package com.neusoft.saca.snap.file.app.file.controller.mobile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.constant.FileConstant;
import com.neusoft.saca.snap.file.domain.UploadReq;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping("mobile/file")
public class MobileFileController {

	@Autowired
	private FileService fileService;
	
	/**
	 * 直接读取文件流，用于音视频文件播放
	 * 
	 * @param request
	 * @param response
	 * @param fileId
	 * @throws IOException
	 */
	@RequestMapping(value = "voice/download", method = RequestMethod.GET)
	public void play(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileId)
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

		bufferedDownload(response, inputStream);

	}

	/**
	 * 带有缓冲的下载，可有效解决内存
	 * 
	 * @param response
	 * @param inputStream
	 * @throws IOException
	 */
	private void bufferedDownload(HttpServletResponse response, InputStream inputStream) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			response.addHeader("Access-Control-Allow-Origin", "*");// 跨域访问
			ServletOutputStream outputStream = response.getOutputStream();
			// 按缓存流的方式，一边读取一边下载
			bis = new BufferedInputStream(inputStream);
			bos = new BufferedOutputStream(outputStream);
			byte[] buff = new byte[10240]; // 每次读取10k
			int readedByteLength = 0;
			while ((readedByteLength = bis.read(buff)) > 0) {
				bos.write(buff, 0, readedByteLength);
			}
			outputStream.flush();
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}
	
	/**
	 * 手机音频上传amr格式
	 * @param mfile
	 * @param creator
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "voice/upload", method = RequestMethod.POST)
	public void simpleupload(@RequestParam MultipartFile mfile, @RequestParam(value = "creator") String creator,
			HttpServletResponse response) throws Exception {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();

		JSONObject dataJsonObject = new JSONObject();
		// 如果上传的文件没有名字或者大小为0，那么不予保存
		if (StringUtils.isEmpty(mfile.getOriginalFilename()) || mfile.getSize() == 0) {
			jsonObject.put("code", "-1");
			ControllerHelperUtil.returnJson(response, jsonObject);
			// return jsonObject;
		}

		// 为每个文件构建上传对象
		UploadReq uploadReq = new UploadReq();
		Date date = new Date();
		String id = UUID.randomUUID().toString();
		uploadReq.setFid(id);
		uploadReq.setOriginalFilename(mfile.getOriginalFilename());
		uploadReq.setCreator(creator);
		uploadReq.setSizeInByte(mfile.getSize());
		uploadReq.setNeedCompress(false);
		uploadReq.setNeedPdfPreview(false);
		uploadReq.setUploadedTime(date);
		uploadReq.setAttachFlag(true);
		String fileType = FilenameUtils.getExtension(mfile.getOriginalFilename());
		uploadReq.setType(fileType);

		// 设置文件可预览状态
		uploadReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_UNPREVIEWABLE);

		fileService.uploadVoice(id, uploadReq, mfile);

		jsonObject.put("code", "0");
		dataJsonObject.put("fileId", id);
		dataJsonObject.put("fileName", mfile.getOriginalFilename());
		dataJsonObject.put("size", mfile.getSize());
		jsonObject.put("data", dataJsonObject);

		// return jsonObject;
		ControllerHelperUtil.returnApplicationJson(response, jsonObject);
	}
}
