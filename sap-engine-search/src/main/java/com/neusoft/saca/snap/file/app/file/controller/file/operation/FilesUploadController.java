package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
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
import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "file")
public class FilesUploadController {

	@Autowired
	private FileService fileService;
	@Autowired
	private TicketManageFacade ticketManageFacade;

	/**
	 * 多文件上传，并按需进行pdf转存、或压缩处理
	 * 
	 * @param mfiles
	 * @param ticket
	 * @throws Exception
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public void upload(@RequestParam MultipartFile[] mfiles, @RequestParam(value = "creator") String creator,
			@RequestParam(value = "needCompress", required = false, defaultValue = "true") boolean needCompress,
			@RequestParam(value = "needPdfPreview", required = false, defaultValue = "true") boolean needPdfPreview,
			HttpServletResponse response) throws Exception {
		LicenseUtil.checkLicense();
		String previewStatus = FileConstant.FILE_PREVIEW_STATUS_UNKNOWN;
		if (!needPdfPreview) {
			previewStatus = FileConstant.FILE_PREVIEW_STATUS_UNPREVIEWABLE;
		}
		JSONObject jsonObject = new JSONObject();

		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < mfiles.length; i++) {
			// 如果上传的文件没有名字或者大小为0，那么不予保存
			MultipartFile mFile = mfiles[i];
			if (StringUtils.isEmpty(mFile.getOriginalFilename()) || mFile.getSize() == 0) {
				continue;
			}

			// 为每个文件构建元数据信息
			UploadReq uploadReq = new UploadReq();
			Date date = new Date();
			String id = UUID.randomUUID().toString(); //
			// + - + 当前毫秒数
			uploadReq.setFid(id);
			uploadReq.setOriginalFilename(mFile.getOriginalFilename());
			uploadReq.setCreator(creator);
			uploadReq.setSizeInByte(mFile.getSize());
			uploadReq.setNeedCompress(needCompress);
			uploadReq.setNeedPdfPreview(needPdfPreview);
			uploadReq.setUploadedTime(date);
			uploadReq.setAttachFlag(false);
			String fileType = FilenameUtils.getExtension(mFile.getOriginalFilename());
			uploadReq.setType(fileType);

			// 设置可预览状态
			uploadReq.setPreviewStatus(previewStatus);
			// 返回数据
			JSONObject dataJsonObject = new JSONObject();
			dataJsonObject.put("fileId", id);
			dataJsonObject.put("fileName", mFile.getOriginalFilename());
			dataJsonObject.put("size", mFile.getSize());
			jsonArray.add(dataJsonObject);
			fileService.upload(id, uploadReq, mFile);
		}

		jsonObject.put("code", "0");
		jsonObject.put("data", jsonArray);

		// return jsonObject;
		ControllerHelperUtil.returnJson(response, jsonObject);
	}


}
