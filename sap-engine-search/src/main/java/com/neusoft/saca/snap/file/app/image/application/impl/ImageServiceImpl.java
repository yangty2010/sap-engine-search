package com.neusoft.saca.snap.file.app.image.application.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.coobird.thumbnailator.Thumbnails;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.app.image.application.ImageStoreService;
import com.neusoft.saca.snap.file.app.image.vo.ImageInfo;
import com.neusoft.saca.snap.file.app.image.vo.ImageUploadDateVO;
import com.neusoft.saca.snap.file.app.image.vo.TempLogoImageInfoVO;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;
import com.neusoft.saca.snap.file.infrastructure.util.ImageScaleUtil;

@Service
public class ImageServiceImpl implements ImageService {

	private static String SCALESTRATEGY_RECT = "rect";// 等比压缩
	private static String SCALESTRATEGY_WIDTH = "width";// 等宽压缩
	private static String SCALESTRATEGY_HEIGHT = "height";// 等高压缩

	private final static Logger logger = LoggerFactory
			.getLogger(ImageService.class);

	// JVM的系统临时文件夹,临时图片存放绝对路径
	private static String TMP_DIR = System.getProperties().getProperty(
			"java.io.tmpdir");
	static {
		TMP_DIR = TMP_DIR.replaceAll("\\\\", "/");
		if (TMP_DIR.lastIndexOf("/") != (TMP_DIR.length() - 1)) {
			TMP_DIR = TMP_DIR + "/";
		}
	}
	@Autowired
	private ImageStoreService imageStoreService;

	@Override
	public TempLogoImageInfoVO uploadTempLogo(ByteArrayInputStream imgData,
			String imageId) throws IOException {
		TempLogoImageInfoVO imageInfo = null;
		String imgType = getImageType(imgData);
		if (imgType == null) {
			return null;
		}
		if (imgType.equalsIgnoreCase("jpeg") || imgType.equalsIgnoreCase("gif")
				|| imgType.equalsIgnoreCase("png")
				|| imgType.equalsIgnoreCase("jpg")) {
			imgData.reset();
			// 如果图片的宽、高大于540*440则需要压缩
			BufferedImage bufferedImg = ImageIO.read(imgData);
			int imgWidth = bufferedImg.getWidth();
			int imgHeight = bufferedImg.getHeight();

			BufferedImage bufferedImg1 = bufferedImg;
			if (imgWidth > 540 || imgHeight > 440) {
				// 对用户的图片生成缩略图，540 * 440，等比例缩放
				bufferedImg1 = Thumbnails.of(bufferedImg).outputQuality(1.0)
						.size(540, 440).asBufferedImage();
				imgWidth = bufferedImg1.getWidth();
				imgHeight = bufferedImg1.getHeight();
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			// File tempLogoFile = new File(TMP_DIR + imageId + "." + imgType);
			// ImageIO.write(bufferedImg1, imgType, tempLogoFile);
			ImageIO.write(bufferedImg1, imgType, byteArrayOutputStream);
			// 保存原始图片
			// File outFile=new File(TMP_DIR + imageId + "11." + imgType);
			// InputStream originalInputStream = new FileInputStream(outFile);
			InputStream originalInputStream = new ByteArrayInputStream(
					byteArrayOutputStream.toByteArray());
			imageStoreService.save(imageId, originalInputStream,
					buildUnattachedMetaData());

			// FileUtils.copyFile(tempLogoFile, outFile);
			originalInputStream.close();

			imageInfo = new TempLogoImageInfoVO();
			imageInfo.setImgUrl(SnapFileConfigUtils.obtainSiteUrl()
					+ "image/obtain/" + imageId);
			imageInfo.setWidth(imgWidth);
			imageInfo.setHeight(imgHeight);
			return imageInfo;
		} else {
			return null;
		}
	}

	@Override
	public ImageInfo upload(String imageId, MultipartFile imageFile) {
		String id = imageId;// 生成图片id
		String name = imageFile.getOriginalFilename();
		String imageType = name.substring(name.lastIndexOf(".") + 1,
				name.length());

		File originalImageFile = new File(TMP_DIR + id + "." + imageType);
		if (!originalImageFile.exists()) {
			originalImageFile.mkdirs();
		}
		ImageInfo imageInfo = new ImageInfo(imageId, 0, 0, 0, 0);
		String realImageType = null;
		// 保存图片到临时文件夹
		try {
			imageFile.transferTo(originalImageFile);
			realImageType = getImageType(originalImageFile);
			// 如果不是图片，直接返回
			if (realImageType == null) {
				originalImageFile.delete();
				throw new RuntimeException("不是图片类型");
			}
			BufferedImage bufferedImage = ImageIO.read(originalImageFile);
			double origianlWidth = bufferedImage.getWidth();
			double origianlHeight = bufferedImage.getHeight();
			imageInfo.setImageHeight(origianlHeight);
			imageInfo.setImageWidth(origianlWidth);
			// 保存原始图片
			InputStream originalInputStream = new FileInputStream(
					originalImageFile);
			imageStoreService.save(id, originalInputStream,
					buildUnattachedMetaData());
			originalInputStream.close();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		originalImageFile.delete();
		return imageInfo;
	}

	@Override
	public ImageInfo upload(String imageId, MultipartFile imageFile,
			String scaleStrategy, List<Double> thumbnails,
			Double thumbnailMobileWidth, Double thumbnailMobileHeight) {

		if (StringUtils.isBlank(imageId)) {
			throw new IllegalArgumentException("imageId不能为空");
		}

		if (thumbnails == null || thumbnails.size() == 0) {
			throw new IllegalArgumentException("thumbnails参数不正确");
		}

		String id = imageId;// 生成图片id
		String name = imageFile.getOriginalFilename();
		String imageType = name.substring(name.lastIndexOf(".") + 1,
				name.length());

		File originalImageFile = new File(TMP_DIR + id + "." + imageType);
		if (!originalImageFile.exists()) {
			originalImageFile.mkdirs();
		}
		String realImageType = null;
		ImageInfo imageInfo = new ImageInfo(imageId, 0, 0, 0, 0);
		try {

			// 保存图片到临时文件夹
			imageFile.transferTo(originalImageFile);
			realImageType = getImageType(originalImageFile);
			// 如果不是图片，直接返回
			if (realImageType == null) {
				originalImageFile.delete();
				throw new RuntimeException("不是图片类型");
			}
			// 保存原始图片
			InputStream originalInputStream = new FileInputStream(
					originalImageFile);
			JSONObject metaData = new JSONObject();
			metaData.put("attachFlag", false);
			metaData.put("originalFilename", name);
			imageStoreService.save(id, originalInputStream, metaData);
			originalInputStream.close();

			Map<Double, InputStream> rectPictureMap = new HashMap<Double, InputStream>();

			if (null == scaleStrategy
					|| SCALESTRATEGY_RECT.equalsIgnoreCase(scaleStrategy)) {// 默认为等比
				rectPictureMap = ImageScaleUtil.rectPicture(originalImageFile,
						thumbnails);
			} else if (SCALESTRATEGY_WIDTH.equalsIgnoreCase(scaleStrategy)) {// 等宽裁剪
				rectPictureMap = ImageScaleUtil.rectPictureByWidth(
						originalImageFile, thumbnails);
			} else if (SCALESTRATEGY_HEIGHT.equalsIgnoreCase(scaleStrategy)) {// 等高裁剪
				rectPictureMap = ImageScaleUtil.rectPictureByHeight(
						originalImageFile, thumbnails);
			}

			for (Double thumbnail : rectPictureMap.keySet()) {
				// 保存缩略图
				String thumbnailImageId = id + "_" + thumbnail.intValue();
				InputStream inputStream = rectPictureMap.get(thumbnail);
				imageStoreService.save(thumbnailImageId, inputStream, metaData);
				inputStream.close();
			}
			BufferedImage bufferedImage = ImageIO.read(originalImageFile);
			double origianlWidth = bufferedImage.getWidth();
			double origianlHeight = bufferedImage.getHeight();
			imageInfo.setImageHeight(origianlHeight);
			imageInfo.setImageWidth(origianlWidth);
			// 保存移动端缩略图 TODO 设置
			if (origianlHeight > thumbnailMobileHeight
					|| origianlWidth > thumbnailMobileWidth) {
				double height = 0;
				double width = 0;
				// 等比缩放
				if ((origianlHeight / origianlWidth) > (thumbnailMobileHeight / thumbnailMobileWidth)) {
					// 设置高度为要求缩放高度，宽度等比缩放
					height = thumbnailMobileHeight;
					width = origianlWidth * height / origianlHeight;
				} else {
					// 设置宽度为要求缩放宽度，高度等比缩放
					width = thumbnailMobileWidth;
					height = origianlHeight * width / origianlWidth;
				}
				// imageInfo.setMthumbnailHeight(Math.round(height));
				// imageInfo.setMthumbnailWidth(Math.round(width));
				String thumbnailImageId = id + "_mobile";
				InputStream inputStream = ImageScaleUtil.rectPic(
						originalImageFile, thumbnailMobileWidth,
						thumbnailMobileHeight);
				inputStream.mark(10 * 1024 * 1024);
				bufferedImage = ImageIO.read(inputStream);
				imageInfo.setMthumbnailHeight(bufferedImage.getHeight());
				imageInfo.setMthumbnailWidth(bufferedImage.getWidth());
				inputStream.reset();
				imageStoreService.save(thumbnailImageId, inputStream,
						buildUnattachedMetaData());
				inputStream.close();
			} else {
				// 保存原始图片
				InputStream inputStream = new FileInputStream(originalImageFile);
				String thumbnailImageId = id + "_mobile";
				imageStoreService.save(thumbnailImageId, inputStream,
						buildUnattachedMetaData());
			}

			originalImageFile.delete(); // 删除临时图片
		} catch (Exception e) {
			logger.error("图片上传失败", e);
			e.printStackTrace();
		}
		return imageInfo;
	}

	@Override
	public GridFSDBFile obtainWithDefault(String id, String defaultId) {
		GridFSDBFile gfs = imageStoreService.get(id);
		return gfs != null ? gfs : imageStoreService.get(defaultId);
	}

	@Override
	public GridFSDBFile obtainImage(String id) {
		return imageStoreService.get(id);
	}

	@Override
	public GridFSDBFile obtainAvatar(String id) {
		return imageStoreService.get(id);
	}

	/**
	 * 获取图片类型，用来判断是否是图片
	 * 
	 * @param imageFile
	 * @return
	 * @throws IOException
	 */
	@Override
	public String getImageType(Object imageFile) throws IOException {
		String imageType = null;

		ImageInputStream imageInputStream = ImageIO
				.createImageInputStream(imageFile);
		Iterator<ImageReader> iterator = ImageIO
				.getImageReaders(imageInputStream);
		if (iterator.hasNext()) {
			ImageReader imageReader = iterator.next();
			imageType = imageReader.getFormatName();
		}

		imageInputStream.close();
		return imageType;
	}

	private JSONObject buildUnattachedMetaData() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("attachFlag", false);
		return jsonObject;
	}

	private JSONObject buildAttachedMetaData() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("attachFlag", true);
		return jsonObject;
	}

	@Override
	@Transactional(readOnly = false)
	public void rectAndStoreAvatar(String imageId, int x, int y, int width,
			int height, List<Double> thumbnailSizeList, String type) {
		GridFSDBFile gfs = imageStoreService.get(imageId);
		InputStream inputStream = gfs == null ? null : gfs.getInputStream();
		if (inputStream == null) {
			throw new IllegalArgumentException("图片不存在,id: " + imageId);
		}
		if (thumbnailSizeList == null || thumbnailSizeList.size() == 0) {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
			throw new IllegalArgumentException("thumbnailSizeList 为空");
		}
		try {
			int count = 10240000;
			byte[] imgByte = new byte[count];
			int readCount = 0; // 已经成功读取的字节的个数
			while (readCount < count) {
				int currentReadCount = inputStream.read(imgByte, readCount,
						count - readCount);
				if (currentReadCount == -1) {
					break;
				}
				readCount += currentReadCount;
			}
			// inputStream.read(imgByte);
			inputStream.close();
			for (Double thumbnailSize : thumbnailSizeList) {
				String id = AvatarIdGenerator.genegratorUserAvatarId(imageId,
						thumbnailSize);
				InputStream thumbnailInputStream = ImageScaleUtil.thumbnail(
						imgByte, x, y, width, height, thumbnailSize.intValue(),
						thumbnailSize.intValue());
				imageStoreService.save(id, thumbnailInputStream,
						buildAttachedMetaData());
				thumbnailInputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void uploadAvatarForMobile(String imageId, MultipartFile imageFile,
			String type) throws IOException {
		InputStream inputStream = imageFile.getInputStream();
		if (inputStream == null) {
			throw new IllegalArgumentException("头像上传数据错误！");
		}
		List<Double> thumbnails = SnapFileConfigUtils.obtainAvatarScaleSizes();
		try {
			int count = 10240000;
			byte[] imgByte = new byte[count];
			int readCount = 0; // 已经成功读取的字节的个数
			while (readCount < count) {
				int currentReadCount = inputStream.read(imgByte, readCount,
						count - readCount);
				if (currentReadCount == -1) {
					break;
				}
				readCount += currentReadCount;
			}
			// inputStream.read(imgByte);
			inputStream.close();
			for (Double thumbnailSize : thumbnails) {
				String id = AvatarIdGenerator.genegratorUserAvatarId(imageId,
						thumbnailSize);
				InputStream thumbnailInputStream = ImageScaleUtil.thumbnail(
						imgByte, thumbnailSize.intValue(),
						thumbnailSize.intValue());
				imageStoreService.save(id, thumbnailInputStream,
						buildAttachedMetaData());
				thumbnailInputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<ImageUploadDateVO> obtainImageUploadDateList(
			List<String> imageIdList) {

		List<ImageUploadDateVO> list = new ArrayList<ImageUploadDateVO>();
		List<GridFSDBFile> gridFSDBFiles = imageStoreService
				.getList(imageIdList);

		for (GridFSDBFile gridFSDBFile : gridFSDBFiles) {
			ImageUploadDateVO imageUploadDateVO = new ImageUploadDateVO();
			if (gridFSDBFile != null) {
				imageUploadDateVO.setImageId(gridFSDBFile.getFilename());
				imageUploadDateVO.setUploadDate(gridFSDBFile.getUploadDate()
						.getTime());
				list.add(imageUploadDateVO);
			}
		}

		return list;
	}

}
