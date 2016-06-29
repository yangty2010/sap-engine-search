package com.neusoft.saca.snap.file.app.image.controller.mobile;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.app.image.vo.ImageInfo;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping("mobile/image")
public class MobileImageController {

	@Autowired
	private ImageService imageService;
	
	/**
	 * 上传图片，并根据配置切割
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
		String scaleStrategy=SnapFileConfigUtils.obtainImageScaleStrategy();
		List<Double> thumbnails=SnapFileConfigUtils.obtainImageScaleSizes();
		
		Double mthumbnailHeight=SnapFileConfigUtils.obtainImageScaleMobileHeight();
		Double mthumbnailWidth=SnapFileConfigUtils.obtainImageScaleMobileWidth();
		ImageInfo imageInfo=null;
		if(thumbnails==null||thumbnails.size()==0){
			imageInfo=imageService.upload(imageId,imageFile);
		}else{
			imageInfo=imageService.upload(imageId,imageFile,scaleStrategy,thumbnails,mthumbnailWidth,mthumbnailHeight);
		}

		if (imageId != null) {
			jsonObject.put("code", "0");
			jsonObject.put("msg", "上传成功");
			jsonObject.put("result", imageInfo);
			
			ControllerHelperUtil.returnApplicationJson(response, jsonObject);
		}
	}
}
