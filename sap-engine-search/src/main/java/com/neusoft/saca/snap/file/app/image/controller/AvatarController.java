package com.neusoft.saca.snap.file.app.image.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.DefaultAvatarGeneratorService;
import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.app.image.dto.UserAvatarBatchGenerateDto;
import com.neusoft.saca.snap.file.app.image.vo.TempLogoImageInfoVO;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;
import com.neusoft.saca.snap.file.infrastructure.util.ControllerHelperUtil;
import com.neusoft.saca.snap.file.infrastructure.util.Http304Utils;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

@Controller
@RequestMapping(value = "avatar")
public class AvatarController {
	/**
	 * 默认群组头像
	 */
	private final String DEFAULT_AVATAR_GROUP = "group_profile180.png";
	/**
	 * 默认用户大头像
	 */
	private final String DEFAULT_AVATAR_USER_LARGE = "no_photo180.png";
	/**
	 * 默认用户小头像
	 */
	private final String DEFAULT_AVATAR_USER_SMALL = "no_photo50.png";

	@Autowired
	private ImageService imageService;

	@Autowired
	private DefaultAvatarGeneratorService defaultAvatarGeneratorService;

	/**
	 * 生成默认头像，用于注册时生成
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "default/generate", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> generateDefaultAvatar(
			@RequestParam String userId, @RequestParam String userName,
			HttpServletResponse response) {

		LicenseUtil.checkLicense();

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			defaultAvatarGeneratorService.avatarGenerate(userId,
					URLDecoder.decode(userName, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("code", "0");
		map.put("msg", "生成头像成功");

		return map;
	}

	/**
	 * 批量生成默认头像，用于管理端调用
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "batch/default/generate", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> batchGenerateAvatar(
			@RequestBody UserAvatarBatchGenerateDto userAvatarBatchGenerateDto) {

		LicenseUtil.checkLicense();

		Map<String, Object> map = new HashMap<String, Object>();
		JSONArray jsonArray = JSONArray.fromObject(userAvatarBatchGenerateDto
				.getUserArray());
		int i = 0;
		for (Object jsonOject : jsonArray) {
			JSONObject jsonObject = JSONObject.fromObject(jsonOject);
			defaultAvatarGeneratorService.avatarGenerate(
					jsonObject.getString("userId"),
					jsonObject.getString("userName"));
			System.out.println("用户：" + jsonObject.getString("userId") + " "
					+ jsonObject.getString("userName") + "头像初始化完成");
			// 每100个数据休息1秒
			if (i > 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i = 0;
			}
			i++;
		}

		map.put("code", "0");
		map.put("msg", "生成头像成功");

		return map;
	}

	/**
	 * 上传头像原始图片，并保存
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "temp/upload", method = RequestMethod.POST)
	public void uploadAvatar(
			@RequestParam(value = "Filedata") MultipartFile mpFile,
			HttpServletResponse response, @RequestParam String id) {
		LicenseUtil.checkLicense();
		JSONObject rtn = new JSONObject();
		try {
			TempLogoImageInfoVO imageInfo = imageService.uploadTempLogo(
					new ByteArrayInputStream(mpFile.getBytes()), id);
			if (imageInfo == null) {
				rtn.put("code", "-1");
				rtn.put("msg", "只能上传图片类型的文件哦～");
			} else {
				rtn.put("code", "0");
				rtn.put("msg", imageInfo.getImgUrl());
				Map<String, String> data = new HashMap<String, String>();
				data.put("rltWidth", imageInfo.getWidth() + "");
				data.put("rltHeight", imageInfo.getHeight() + "");
				data.put("id", id);
				rtn.put("data", data);
			}
		} catch (IOException e) {
			rtn.put("code", "-1");
			rtn.put("msg", "文件上传失败了哦～");
		}

		ControllerHelperUtil.returnJson(response, rtn);
	}

	/**
	 * 切割头像，并保存配置尺寸的缩略图
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public void rectAvatar(HttpServletRequest request,
			HttpServletResponse response, @RequestParam String id,
			@RequestParam int x, @RequestParam int y, @RequestParam int width,
			@RequestParam int height, @RequestParam String avatarType) {
		if (id != null) {
			LicenseUtil.checkLicense();
			List<Double> thumbnails = SnapFileConfigUtils
					.obtainAvatarScaleSizes();
			imageService.rectAndStoreAvatar(id, x, y, width, height,
					thumbnails, avatarType);

			Map<String, String> avatarUrls = new HashMap<String, String>();
			avatarUrls.put("small",
					SnapFileConfigUtils.obtainSmallAvatarUrl(id, avatarType));
			avatarUrls.put("middle",
					SnapFileConfigUtils.obtainMiddleAvatarUrl(id, avatarType));
			avatarUrls.put("large",
					SnapFileConfigUtils.obtainLargeAvatarUrl(id, avatarType));

			JSONObject rtn = new JSONObject();

			rtn.put("code", "0");
			rtn.put("msg", "头像上传成功！");
			rtn.put("data", avatarUrls);
			// response.addHeader("Access-Control-Allow-Origin", "*");// 跨域访问
			ControllerHelperUtil.returnJson(response, rtn);
		}
	}

	/**
	 * 切割头像，并保存配置尺寸的缩略图
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	@RequestMapping(value = "upload", method = RequestMethod.GET)
	@ResponseBody
	public String rectAvatarForGET(@RequestParam String jsonpcallback,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam String id, @RequestParam int x, @RequestParam int y,
			@RequestParam int width, @RequestParam int height,
			@RequestParam String avatarType) {
		if (id != null) {
			LicenseUtil.checkLicense();
			List<Double> thumbnails = SnapFileConfigUtils
					.obtainAvatarScaleSizes();
			imageService.rectAndStoreAvatar(id, x, y, width, height,
					thumbnails, avatarType);

			Map<String, String> avatarUrls = new HashMap<String, String>();
			avatarUrls.put("small",
					SnapFileConfigUtils.obtainSmallAvatarUrl(id, avatarType));
			avatarUrls.put("middle",
					SnapFileConfigUtils.obtainMiddleAvatarUrl(id, avatarType));
			avatarUrls.put("large",
					SnapFileConfigUtils.obtainLargeAvatarUrl(id, avatarType));

			JSONObject rtn = new JSONObject();

			rtn.put("code", "0");
			rtn.put("msg", "头像上传成功！");
			rtn.put("data", avatarUrls);
			return jsonpcallback + "(" + rtn.toString() + ");";
		}
		return jsonpcallback;
	}

	/**
	 * 浏览头像图片
	 * 
	 * @param imageId
	 * @param response
	 * @deprecated {@link downloadAvatar}
	 */
	@RequestMapping(value = "{type}/{avatarId}/{size}")
	public void downloadImage(@PathVariable String avatarId,
			@PathVariable String type, @PathVariable String size,
			HttpServletRequest request, HttpServletResponse response) {
		LicenseUtil.checkLicense();
		GridFSDBFile gfs = imageService.obtainWithDefault(avatarId + "_" + type
				+ "_" + size, getDefaultAvatar(type, size));
		if (Http304Utils.hasTheSameEtag(gfs.getUploadDate().getTime(), request,
				response)) {
			return;
		}
		try {
			FileCopyUtils
					.copy(gfs.getInputStream(), response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("与服务器连接断开", e);
		}
	}
	
	/**
	 * 浏览头像图片
	 * 
	 * @param avatarId	example:discussionGroupId + "_discussionGroup"、userId+"_user_large"
	 * @param response
	 */
	@RequestMapping(value = "obtain")
	public void downloadAvatar(@RequestParam String avatarId,
			HttpServletRequest request, HttpServletResponse response) {
		LicenseUtil.checkLicense();
		GridFSDBFile gfs = imageService.obtainAvatar(avatarId);
		if(gfs == null) {
			return;
		}
		if (Http304Utils.hasTheSameEtag(gfs.getUploadDate().getTime(), request,
				response)) {
			return;
		}
		try {
			FileCopyUtils
					.copy(gfs.getInputStream(), response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("与服务器连接断开", e);
		}
	}

	/**
	 * 获取头像更新时间
	 * 
	 * @param avatarIdList
	 * @return
	 */
	@RequestMapping(value = "obtain/uploaddates", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> obtainUploadDateList(@RequestParam List<String> avatarIdList) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(CollectionUtils.isNotEmpty(avatarIdList)){
			result.put("data", imageService.obtainImageUploadDateList(avatarIdList));
			result.put("code", "0");
		} else {
			result.put("msg", "头像ID列表不能为空");
			result.put("code", "-1");
		}
		
		return result;
	}
	
	private String getDefaultAvatar(String type, String size) {
		if ("user".equals(type)) {
			if ("small".equals(size)) {
				return DEFAULT_AVATAR_USER_SMALL;
			} else {
				return DEFAULT_AVATAR_USER_LARGE;
			}
		} else {
			return DEFAULT_AVATAR_GROUP;
		}
	}
}
