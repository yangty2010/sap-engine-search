package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.constant.FileConstant;
import com.neusoft.saca.snap.file.domain.UploadReq;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "file")
public class FileSimpleUploadController {

	@Autowired
	private FileService fileService;


	/**
	 * 单文件上传，并按需进行pdf转存、或压缩处理
	 * 
	 * @param mfiles
	 * @param ticket
	 * @throws Exception
	 */
	@RequestMapping(value = "simpleupload", method = RequestMethod.POST)
	public void simpleupload(@RequestParam MultipartFile mfile, @RequestParam(value = "creator") String creator,
			@RequestParam(value = "needCompress", required = false, defaultValue = "false") boolean needCompress,
			@RequestParam(value = "needPdfPreview", required = false, defaultValue = "true") boolean needPdfPreview,
			HttpServletResponse response) throws Exception {
		//验证license
		LicenseUtil.checkLicense();
		
		//定义声明变量
		JSONObject jsonObject = new JSONObject();
		JSONObject dataJsonObject = new JSONObject();
		
		// 验证判断：如果上传的文件没有名字或者大小为0，那么不予保存
		if (StringUtils.isEmpty(mfile.getOriginalFilename()) || mfile.getSize() == 0) {
			jsonObject.put("code", "-1");
			ControllerHelperUtil.returnJson(response, jsonObject);
			// return jsonObject;
		}

		// 构建文件上传对象
		UploadReq uploadReq = new UploadReq();
		Date date = new Date();
		String id = UUID.randomUUID().toString();
		uploadReq.setFid(id);
		uploadReq.setOriginalFilename(mfile.getOriginalFilename());
		uploadReq.setCreator(creator);
		uploadReq.setSizeInByte(mfile.getSize());
		uploadReq.setNeedCompress(needCompress);
		uploadReq.setNeedPdfPreview(needPdfPreview);
		uploadReq.setUploadedTime(date);
		uploadReq.setAttachFlag(false);
		String fileType = FilenameUtils.getExtension(mfile.getOriginalFilename());
		uploadReq.setType(fileType);

		// 设置文件可预览状态
		if (needPdfPreview) {
			uploadReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_UNKNOWN);
		} else {
			uploadReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_UNPREVIEWABLE);
		}

		//上传文件
		fileService.upload(id, uploadReq, mfile);

		//装配页面返回对象
		jsonObject.put("code", "0");
		dataJsonObject.put("fileId", id);
		dataJsonObject.put("fileName", mfile.getOriginalFilename());
		dataJsonObject.put("size", mfile.getSize());
		jsonObject.put("data", dataJsonObject);

		// return jsonObject;
		//返回页面
		ControllerHelperUtil.returnApplicationJson(response, jsonObject);
	}

	/**
	 * 单文件上传，为HTML5跨域提供OPTIONS方法
	 * 
	 * @param mfiles
	 * @param ticket
	 * @throws Exception
	 */
	@RequestMapping(value = "simpleupload", method = RequestMethod.OPTIONS)
	public void simpleupload1(HttpServletResponse response) throws Exception {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", "0");

		ControllerHelperUtil.returnApplicationJson(response, jsonObject);
	}


}
