package com.neusoft.saca.snap.file.app.image.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.DefaultAvatarCacheService;
import com.neusoft.saca.snap.file.app.image.application.DiscussionGroupAvatarService;
import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.app.image.dto.DiscussionGroupAvatarCreateDto;
import com.neusoft.saca.snap.file.infrastructure.util.Http304Utils;
import com.neusoft.saca.snap.infrastructure.license.LicenseUtil;

/**
 * im讨论组头像服务
 * 
 * @author user
 *
 */
@RestController
@RequestMapping(value = "avatar/discussiongroup")
public class DiscussionGroupAvatarController {

	private static final Logger Logger = LoggerFactory
			.getLogger(DiscussionGroupAvatarController.class);

	@Autowired
	private DiscussionGroupAvatarService discussionGroupAvatarService;

	@Autowired
	private ImageService imageService;

	/**
	 * 生成讨论组头像
	 * 
	 * @param discussionGroupId
	 * @param userIdList
	 * @return
	 */
	@RequestMapping(value = "generate", method = RequestMethod.POST)
	public Map<String, Object> generateAvatar(
			@RequestBody DiscussionGroupAvatarCreateDto discussionGroupAvatarCreateDto) {

		Map<String, Object> map = new HashMap<String, Object>();
		boolean success = false;
		try {
			success = discussionGroupAvatarService.compositeUserAvatarPic(
					discussionGroupAvatarCreateDto.getDiscussionGroupId(),
					DefaultAvatarCacheService.obtainBackgroundAvatar(),
					discussionGroupAvatarCreateDto.getUserIdList());
		} catch (Exception e) {
			Logger.error("生成讨论组头像出错:" + discussionGroupAvatarCreateDto.getUserIdList(), e);
			e.printStackTrace();
		}

		if (success == true) {
			map.put("code", "0");
			map.put("msg", "生成讨论组头像成功");
		} else {
			map.put("code", "-1");
			map.put("msg", "生成讨论组头像失败");
		}

		return map;
	}

	/**
	 * 删除讨论组头像
	 * 
	 * @param discussionGroupId
	 * @param userIdList
	 * @return
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public Map<String, Object> deleteAvatar(
			@RequestBody DiscussionGroupAvatarCreateDto discussionGroupAvatarCreateDto) {
		Map<String, Object> map = new HashMap<String, Object>();
		discussionGroupAvatarService
				.deleteDiscussionGroupAvatar(discussionGroupAvatarCreateDto
						.getDiscussionGroupId());

		map.put("code", "0");
		map.put("msg", "删除讨论组头像成功");

		return map;
	}

	/**
	 * 获取讨论组头像
	 * 
	 * @param response
	 */
	@RequestMapping(value = "{discussionGroupId}")
	public void downloadImage(@PathVariable String discussionGroupId,
			HttpServletRequest request, HttpServletResponse response) {
		LicenseUtil.checkLicense();
		GridFSDBFile gfs = imageService
				.obtainAvatar(DiscussionGroupAvatarService
						.obtainDiscussionGroupAvatarId(discussionGroupId));
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
}
