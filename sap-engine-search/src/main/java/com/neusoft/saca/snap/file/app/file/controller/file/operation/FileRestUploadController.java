package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.dto.FileUploadDto;
import com.neusoft.saca.snap.file.app.file.event.FileUploadEvent;
import com.neusoft.saca.snap.file.domain.UploadReq;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.event.EventBus;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "file")
public class FileRestUploadController {

	@Autowired
	private FileService fileService;
	
	@Autowired
	@Qualifier("sourceGridfs")
	private GridFsOperations srcGridfsOperations;


	/**
	 * 单文件上传，并按需进行pdf转存、或压缩处理
	 * 
	 * @param mfiles
	 * @param ticket
	 * @throws Exception
	 */
	@RequestMapping(value = "restupload", method = RequestMethod.POST)
	public JSONObject restupload(@RequestParam MultipartFile mfile,
//			@RequestParam(value = "mfile_is") InputStream mfile_is, 
//			@RequestParam(value = "filename") String filename,
//			@RequestParam(value = "filesize") long filesize,
//			@RequestParam(value = "mfilecontentType") String mfilecontentType,
			@RequestParam(value = "creator") String creator,
			@RequestParam(value = "needCompress", required = false, defaultValue = "false") boolean needCompress,
			HttpServletResponse response) throws Exception {
//		long start = System.currentTimeMillis();
		//验证license
		LicenseUtil.checkLicense();
		
		//定义声明变量
		JSONObject jsonObject = new JSONObject();
		JSONObject dataJsonObject = new JSONObject();
		
		String filename = mfile.getOriginalFilename();
		long filesize = mfile.getSize();
		String mfilecontentType = mfile.getContentType();
		// 获取原始流文件
		InputStream mfile_is = mfile.getInputStream();
		
		
		// 验证判断：如果上传的文件没有名字或者大小为0，那么不予保存
		if (StringUtils.isEmpty(filename) || filesize == 0) {
			jsonObject.put("code", "-1");
			ControllerHelperUtil.returnJson(response, jsonObject);
			// return jsonObject;
		}

		// 构建文件上传对象
		UploadReq uploadReq = new UploadReq();
		Date date = new Date();
		String id = UUID.randomUUID().toString();
		uploadReq.setFid(id);
		uploadReq.setOriginalFilename(filename);
		uploadReq.setCreator(creator);
		uploadReq.setSizeInByte(filesize);
		uploadReq.setNeedCompress(needCompress);
//		uploadReq.setNeedPdfPreview(needPdfPreview);
		uploadReq.setUploadedTime(date);
		uploadReq.setAttachFlag(false);
		String fileType = FilenameUtils.getExtension(filename);
		uploadReq.setType(fileType);

		// 设置文件可预览状态
//		if (needPdfPreview) {
//			uploadReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_UNKNOWN);
//		} else {
//			uploadReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_UNPREVIEWABLE);
//		}

		//上传文件
		//fileService.upload(id, uploadReq, mfile);
		// 文件的原始名称
//		final String originalFilename = uploadReq.getOriginalFilename();
		
		// 文件类型
		//String fileType = FilenameUtils.getExtension(originalFilename);
		// 存储文件名
		String storedFilename = id + "." + fileType;

		String contentType = mfilecontentType;
		// 指定文件id
		// uploadReq.setFid(id);

		// 获取原始流文件
		// 这里可按需要进行压缩处理后再存储
//		if (uploadReq.isNeedCompress()) {
//			byte[] bytes = mfile.getBytes();
//			byte[] zippedByteArray = ZipUtils.zipByteArray(bytes, originalFilename);
//			is = new ByteArrayInputStream(zippedByteArray);
//			storedFilename = id + ".zip";
//		}
		
		// 在指定的GridFS Bucket中存储文件
		restoreFile(srcGridfsOperations, storedFilename, contentType, uploadReq, mfile_is);
		

		//装配页面返回对象
		jsonObject.put("code", "0");
		dataJsonObject.put("fileId", id);
		dataJsonObject.put("fileName", filename);
		dataJsonObject.put("size", filename);
		jsonObject.put("data", dataJsonObject);

		// return jsonObject;
		//返回页面
		//ControllerHelperUtil.returnApplicationJson(response, jsonObject);
//		long end = System.currentTimeMillis();
//        System.out.println("内部逻辑耗时"+(end-start)/1000);
		return jsonObject;
	}
	
	/**
	 * 在指定的GridFS Bucket中存储文件
	 * 
	 * @param gridFsOperations
	 * @param id
	 * @param fileName
	 * @param contentType
	 * @param metadata
	 * @param data
	 * @throws IOException
	 */
	private void restoreFile(GridFsOperations gridFsOperations, String fileName, String contentType,
			UploadReq uploadReq, InputStream data) throws IOException {

//		gridFsOperations.store(data, fileName, contentType, uploadReq);
		FileUploadDto fileuploadDto = new FileUploadDto();
		fileuploadDto.setData(data);
		fileuploadDto.setContentType(contentType);
		fileuploadDto.setFileName(fileName);
		fileuploadDto.setUploadReq(uploadReq);
		// 4.7 发布创建动态审批事件
		EventBus.send(new FileUploadEvent(fileuploadDto));
	}


}
