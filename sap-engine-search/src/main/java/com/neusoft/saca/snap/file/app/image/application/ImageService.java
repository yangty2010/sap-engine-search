package com.neusoft.saca.snap.file.app.image.application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.vo.ImageInfo;
import com.neusoft.saca.snap.file.app.image.vo.ImageUploadDateVO;
import com.neusoft.saca.snap.file.app.image.vo.TempLogoImageInfoVO;

/**
 * 图片存储服务
 * 
 * @author yan
 * 
 */

@Service
public interface ImageService {

	/**
	 * 上传图片
	 * 
	 * @param imageId
	 * @param imageFile
	 * @param scaleStrategy
	 *            缩放策略
	 * @param thumbnails
	 *            缩放尺寸
	 * @return
	 */
	public ImageInfo upload(String imageId, MultipartFile imageFile, String scaleStrategy, List<Double> thumbnails,Double thumbnailMobileWidth,Double thumbnailMobileHeight);

	/**
	 * 上传图片
	 * 
	 * @param imageId
	 * @param imageFile
	 * @return
	 */
	public ImageInfo upload(String imageId, MultipartFile imageFile);

	/**
	 * 临时logo图片上传，进行固定比例压缩并保存
	 * @param imgData
	 * @param imageId
	 * @return TempLogoImageInfoVO
	 */
	public TempLogoImageInfoVO uploadTempLogo(ByteArrayInputStream imgData, String imageId)throws IOException;
	
	/**
	 * 切割、生成多张缩略图，并保存缩略图
	 * 
	 * @param imageId
	 *            原图id
	 * @param x
	 *            相对于左上角的横坐标点
	 * @param y
	 *            相对于左上角的纵坐标点
	 * @param width
	 *            选取区域的宽度
	 * @param height
	 *            选取区域的高度
	 * @param thumbnailSizeList 缩略图的大小
	 * @param type 
	 */
	public void rectAndStoreAvatar(String imageId, int x, int y, int width, int height,
			List<Double> thumbnailSizeList,String type);
	
	/**
	 * 手机端上传头像
	 * @param id
	 * @param imageFile
	 * @param type
	 */
	public void uploadAvatarForMobile(String id, MultipartFile imageFile,String type) throws IOException;

	/**
	 * 获取用户头像，如果没有则获取默认头像
	 * @param id
	 * @param defaultId
	 * @return
	 */
	public GridFSDBFile obtainWithDefault(String id, String defaultId);
	
	/**
	 * 获取图片
	 * 
	 * @param id
	 * @return
	 */
	public GridFSDBFile obtainImage(String id);
	
	/**
	 * 获取用户头像
	 * 
	 * @param id
	 * @return
	 */
	public GridFSDBFile obtainAvatar(String id);
	
	public String getImageType(Object imageFile) throws IOException;
	
	/**
	 * 获取图片上传时间
	 * 
	 * @param imageIdList
	 * @return
	 */
	public List<ImageUploadDateVO> obtainImageUploadDateList(List<String> imageIdList);
}
