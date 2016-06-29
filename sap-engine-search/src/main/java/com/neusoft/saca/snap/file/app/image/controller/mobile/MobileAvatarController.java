package com.neusoft.saca.snap.file.app.image.controller.mobile;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping("mobile/avatar")
public class MobileAvatarController {

	/**
	 * 默认群组头像
	 */
	private final String DEFAULT_AVATAR_GROUP = "group_profile180.png";
	/**
	 * 默认用户大头像
	 */
	private final String DEFAULT_AVATAR_USER_LARGE= "no_photo180.png";
	/**
	 * 默认用户小头像
	 */
	private final String DEFAULT_AVATAR_USER_SMALL= "no_photo50.png";
	
	@Autowired
	private ImageService imageService;

	/**
	 * 手机端上传头像
	 * @param request
	 * @param response
	 * @param id
	 * @param avatarType
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
		public JSONObject uploadAvatar(HttpServletRequest request, HttpServletResponse response, @RequestParam String id,
				@RequestParam String avatarType) {
		LicenseUtil.checkLicense();

		JSONObject jsonObject = new JSONObject();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile imageFile = multipartRequest.getFile("image");
		try {
			imageService.uploadAvatarForMobile(id, imageFile, avatarType);
			jsonObject.put("code","0");
			JSONObject jsonObject2=new JSONObject();
			jsonObject2.put("smallAvatarUrl", SnapFileConfigUtils.obtainSmallMobileAvatarUrl(id, avatarType));
			jsonObject2.put("largeAvatarUrl", SnapFileConfigUtils.obtainLargeMobileAvatarUrl(id, avatarType));
			jsonObject.put("result", jsonObject2);
			jsonObject.put("msg", "上传成功");
			return jsonObject;
		} catch (IOException e) {
			e.printStackTrace();
			jsonObject.put("code","2");
			jsonObject.put("msg", "数据处理异常！");
			return jsonObject;
		}
	}
	
//	/**
//	 * 浏览头像图片
//	 * 
//	 * @param imageId
//	 * @param response
//	 */
//	@RequestMapping(value = "{type}/{avatarId}/{size}")
//	public void downloadImage(@PathVariable String avatarId, @PathVariable String type, @PathVariable String size, 
//			HttpServletRequest request, HttpServletResponse response) {
//		LicenseUtil.checkLicense();
//		String realSize=size.substring(0, size.lastIndexOf("_"));
//		GridFSDBFile gfs = imageService.obtainWithDefault(avatarId+"_"+type+"_"+realSize, getDefaultAvatar(type,size));
////		if(Http304Utils.hasTheSameEtag(gfs.getUploadDate().getTime(), request, response)){
////			return;
////		}
//		try {
//			FileCopyUtils.copy(gfs.getInputStream(), response.getOutputStream());
//		} catch (IOException e) {
//			throw new RuntimeException("与服务器连接断开", e);
//		}
//	}
//	
//	private String getDefaultAvatar(String type,String size){
//		if ("user".equals(type)) {
//			if ("small".equals(size)) {
//				return DEFAULT_AVATAR_USER_SMALL;
//			}else{
//				return DEFAULT_AVATAR_USER_LARGE;
//			}
//		}else {
//			return DEFAULT_AVATAR_GROUP;
//		}
//	}
}
