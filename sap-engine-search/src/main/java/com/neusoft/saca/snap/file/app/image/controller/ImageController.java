package com.neusoft.saca.snap.file.app.image.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.app.image.vo.ImageInfo;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "image")
public class ImageController {

	/**
	 * 默认图片
	 */
	// private final String
	// DEFAULT_IMAGE="da21da5d-2184-4ddb-bcbf-41ca3c413400";
	private final String DEFAULT_IMAGE = "file-nopreview.png";
	@Autowired
	private ImageService imageService;

	/**
	 * 上传图片，并根据配置切割
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public void uploadImageWithoutFilekey(HttpServletRequest request, HttpServletResponse response) {
		LicenseUtil.checkLicense();

		JSONObject jsonObject = new JSONObject();

		String imageId = UUID.randomUUID().toString();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile imageFile = multipartRequest.getFile("image");
		String scaleStrategy = SnapFileConfigUtils.obtainImageScaleStrategy();
		List<Double> thumbnails = SnapFileConfigUtils.obtainImageScaleSizes();

		Double mthumbnailHeight = SnapFileConfigUtils.obtainImageScaleMobileHeight();
		Double mthumbnailWidth = SnapFileConfigUtils.obtainImageScaleMobileWidth();
		ImageInfo imageInfo = null;
		if (thumbnails == null || thumbnails.size() == 0) {
			imageInfo = imageService.upload(imageId, imageFile);
		} else {
			imageInfo = imageService.upload(imageId, imageFile, scaleStrategy, thumbnails, mthumbnailWidth,
					mthumbnailHeight);
		}

		if (imageId != null) {
			jsonObject.put("code", "0");
			jsonObject.put("error", 0);

			JSONObject dataJsonObject = new JSONObject();
			dataJsonObject.put("imageId", imageId);
			dataJsonObject.put("url", SnapFileConfigUtils.obtainSiteUrl() + "image/obtain/" + imageId);

			dataJsonObject.put("imageHeight", imageInfo.getImageHeight());
			dataJsonObject.put("imageWidth", imageInfo.getImageWidth());
			dataJsonObject.put("mthumbnailWidth", imageInfo.getMthumbnailWidth());
			dataJsonObject.put("mthumbnailHeight", imageInfo.getMthumbnailHeight());
			jsonObject.put("data", dataJsonObject);

			/**
			 * 将所有需要的数据平铺返回，后期需要配置返回TODO
			 */
			jsonObject.put("state", "SUCCESS");
			jsonObject.put("title", imageFile.getOriginalFilename());
			jsonObject.put("url", SnapFileConfigUtils.obtainSiteUrl() + "image/obtain/" + imageId);
			jsonObject.put("original", imageFile.getOriginalFilename());
			ControllerHelperUtil.returnApplicationJson(response, jsonObject);
		}
	}

	/**
	 * 上传图片，供html5测试请求用
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "upload", method = RequestMethod.OPTIONS)
	public void uploadImageWithoutFilekey1(HttpServletRequest request, HttpServletResponse response) {
		LicenseUtil.checkLicense();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", "0");
		jsonObject.put("error", 0);
		ControllerHelperUtil.returnApplicationJson(response, jsonObject);
	}

	/**
	 * 下载图片或者浏览图片
	 * 
	 * @param imageId
	 * @param response
	 */
	@RequestMapping(value = "obtain/{imageId}")
	public void downloadImage(@PathVariable String imageId, HttpServletRequest request, HttpServletResponse response) {
		LicenseUtil.checkLicense();
		GridFSDBFile gfs = imageService.obtainWithDefault(imageId, DEFAULT_IMAGE);
		response.setHeader("Cache-Control", "max-age=31536000"); // 设置一万年永不过期
		response.setHeader("Content-Disposition", "inline;filename=" + imageId);
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		try {
			FileCopyUtils.copy(gfs.getInputStream(), response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("与服务器连接断开", e);
		}
	}

	/**
	 * 下载图片
	 * 
	 * @param imageId
	 * @param response
	 */
	@RequestMapping(value = "download/{imageId}",method=RequestMethod.GET)
	public void downloadImageDirect(@PathVariable String imageId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		LicenseUtil.checkLicense();
		GridFSDBFile gfs = imageService.obtainWithDefault(imageId, DEFAULT_IMAGE);
		String fileName =gfs.getFilename();
		if (gfs.getMetaData().containsField("originalFilename")) {
			fileName=(String) gfs.getMetaData().get("originalFilename");
		}
		String finalName=URLEncoder.encode(fileName,"UTF-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + finalName+";filename*=utf-8''"+finalName);
		response.addHeader("Content-Length", "" + gfs.getLength());
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		try {
			FileCopyUtils.copy(gfs.getInputStream(), response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("与服务器连接断开", e);
		}
	}
	
}
