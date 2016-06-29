package com.neusoft.saca.snap.file.app.image.controller;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.saca.snap.file.app.image.application.ImageStoreService;


@Controller
@RequestMapping("imageinternal")
public class ImageInternalController {

	@Autowired
	private ImageStoreService imageStoreService;
	
	/**
	 * 删除一个图片以及相关的缩略图
	 * @param imageId
	 * @return
	 */
	@RequestMapping(value="delete",method=RequestMethod.POST)
	public @ResponseBody JSONObject deleteImage(@RequestParam String imageId){
		JSONObject jsonObject=new JSONObject();
		imageStoreService.delete(imageId);
		jsonObject.put("code", "0");
		return jsonObject;
	}
	
	/**
	 * 批量删除图片以及图片的相关缩略图
	 * @param imageIds
	 * @return
	 */
	@RequestMapping(value="batchdelete",method=RequestMethod.POST)
	public @ResponseBody JSONObject deleteImages(@RequestParam List<String> imageIds){
		JSONObject jsonObject=new JSONObject();
		imageStoreService.deleteByIdList(imageIds);
		jsonObject.put("code", "0");
		return jsonObject;
	}
	
	/**
	 * 将上传的图片状态置为“绑定”，即业务中存在对此图片的引用
	 * @param imageId
	 * @return
	 */
	@RequestMapping(value="attach",method=RequestMethod.POST)
	public @ResponseBody JSONObject attachImage(@RequestParam String imageId){
		JSONObject jsonObject=new JSONObject();
		imageStoreService.attachImage(imageId);
		jsonObject.put("code", "0");
		return jsonObject;
	}
	
	/**
	 * 批量将上传的图片状态置为“绑定”，即业务中存在对此图片的引用
	 * @param imageIds
	 * @return
	 */
	@RequestMapping(value="batchattach",method=RequestMethod.POST)
	public @ResponseBody JSONObject attachImages(@RequestParam List<String> imageIds){
		JSONObject jsonObject=new JSONObject();
		imageStoreService.batchAttachImage(imageIds);
		jsonObject.put("code", "0");
		return jsonObject;
	}
}
