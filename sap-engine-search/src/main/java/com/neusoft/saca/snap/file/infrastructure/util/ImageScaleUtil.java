package com.neusoft.saca.snap.file.infrastructure.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.coobird.thumbnailator.Thumbnails;

public class ImageScaleUtil {

	/**
	 * 等比压缩图片为多张，宽和高同时小于thumbnail
	 * 
	 * @param originalImageFile
	 * @param thumbnails
	 *            可变参数
	 * @return
	 * @throws IOException
	 */
	public static Map<Double, InputStream> rectPicture(File originalImageFile, List<Double> thumbnails)
			throws IOException {

		Map<Double, InputStream> rectMap = new HashMap<Double, InputStream>();

		String realImageType = getImageType(originalImageFile);
		// 如果不是图片，直接返回
		if (realImageType == null || thumbnails.size() == 0) {
			return rectMap;
		}

		BufferedImage bufferedImage = ImageIO.read(originalImageFile);
		double origianlWidth = bufferedImage.getWidth();
		double origianlHeight = bufferedImage.getHeight();

		for (double thumbnail : thumbnails) {
			rectMap.put(thumbnail, rectPic(originalImageFile, thumbnail, origianlWidth, origianlHeight));
		}

		return rectMap;
	}

	/**
	 * 等宽压缩图片为多张，根据宽度等比压缩图片
	 * 
	 * @param originalImageFile
	 * @param thumbnails
	 *            可变参数
	 * @return
	 * @throws IOException
	 */
	public static Map<Double, InputStream> rectPictureByWidth(File originalImageFile, List<Double> thumbnails)
			throws IOException {

		Map<Double, InputStream> rectMap = new HashMap<Double, InputStream>();

		String realImageType = getImageType(originalImageFile);
		// 如果不是图片，直接返回
		if (realImageType == null || thumbnails.size() == 0) {
			return rectMap;
		}

		for (double thumbnail : thumbnails) {
			rectMap.put(thumbnail, compressPictureByWidth(originalImageFile, thumbnail));
		}

		return rectMap;
	}

	/**
	 * 等高压缩图片为多张，根据高度等比压缩图片
	 * 
	 * @param originalImageFile
	 * @param thumbnails
	 *            可变参数
	 * @return
	 * @throws IOException
	 */
	public static Map<Double, InputStream> rectPictureByHeight(File originalImageFile, List<Double> thumbnails)
			throws IOException {

		Map<Double, InputStream> rectMap = new HashMap<Double, InputStream>();

		String realImageType = getImageType(originalImageFile);
		// 如果不是图片，直接返回
		if (realImageType == null || thumbnails.size() == 0) {
			return rectMap;
		}

		for (double thumbnail : thumbnails) {
			rectMap.put(thumbnail, compressPictureByHeight(originalImageFile, thumbnail));
		}

		return rectMap;
	}

	/**
	 * 等宽压缩，按照宽度进行等比压缩
	 * 
	 * @return
	 * @throws IOException
	 */
	public static InputStream compressPictureByWidth(File originalImageFile, double routineWidth) throws IOException {
		String realImageType = getImageType(originalImageFile);
		// 如果不是图片，直接返回
		if (realImageType == null) {
			return null;
		}

		BufferedImage bufferedImage = ImageIO.read(originalImageFile);
		double origianlWidth = bufferedImage.getWidth();
		double origianlHeight = bufferedImage.getHeight();
		// 保存标准图和原始图到mongodb
		if (origianlWidth > routineWidth) {
			double width = 0;
			double height = 0;
			// 等比缩放
			width = routineWidth;
			height = origianlHeight / origianlWidth * routineWidth;

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Thumbnails.of(originalImageFile).outputQuality(1.0).size((int) width, (int) height)
					.toOutputStream(byteArrayOutputStream); // 生成单一博文页标准图

			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			byteArrayOutputStream.close();
			return inputStream;
		}

		return new FileInputStream(originalImageFile);
	}

	/**
	 * 等高压缩，按照高度进行等比压缩
	 * 
	 * @return
	 * @throws IOException
	 */
	public static InputStream compressPictureByHeight(File originalImageFile, double routineHeight) throws IOException {
		String realImageType = getImageType(originalImageFile);
		// 如果不是图片，直接返回
		if (realImageType == null) {
			return null;
		}

		BufferedImage bufferedImage = ImageIO.read(originalImageFile);
		double origianlWidth = bufferedImage.getWidth();
		double origianlHeight = bufferedImage.getHeight();
		// 保存标准图和原始图到mongodb
		if (origianlHeight > routineHeight) {
			double width = 0;
			double height = 0;
			// 等比缩放
			height = routineHeight;
			width = origianlWidth / origianlHeight * routineHeight;

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Thumbnails.of(originalImageFile).outputQuality(1.0).size((int) width, (int) height)
					.toOutputStream(byteArrayOutputStream); // 生成单一博文页标准图

			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			byteArrayOutputStream.close();
			return inputStream;
		}

		return new FileInputStream(originalImageFile);
	}

	/**
	 * 等比压缩
	 * 
	 * @param originalImageFile
	 * @param thumbnail
	 * @param origianlWidth
	 * @param origianlHeight
	 * @return
	 * @throws IOException
	 */
	private static InputStream rectPic(File originalImageFile, double thumbnail, double origianlWidth,
			double origianlHeight) throws IOException {
		double width = 0;
		double height = 0;

		if (origianlHeight > thumbnail || origianlWidth > thumbnail) {
			// 等比缩放
			if (origianlWidth > origianlHeight) {
				width = thumbnail;
				height = origianlHeight / origianlWidth * thumbnail;
			} else {
				height = thumbnail;
				width = origianlWidth / origianlHeight * thumbnail;
			}

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Thumbnails.of(originalImageFile).outputQuality(1.0).size((int) width, (int) height)
					.toOutputStream(byteArrayOutputStream); // 生成缩略图
			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			byteArrayOutputStream.close();
			return inputStream;
		}

		return new FileInputStream(originalImageFile);
	}

	/**
	 * 等比压缩
	 * 
	 * @param originalImageFile
	 * @param thumbnailHeight
	 * @param thumbnailWidth
	 * @param origianlWidth
	 * @param origianlHeight
	 * @return
	 * @throws IOException
	 */
	public static InputStream rectPic(File originalImageFile, double thumbnailHeight, double thumbnailWidth,
			double origianlWidth, double origianlHeight) throws IOException {
		double width = 0;
		double height = 0;

		if (origianlHeight > thumbnailHeight || origianlWidth > thumbnailWidth) {

			// 等比缩放
			if ((origianlHeight / origianlWidth) > (thumbnailHeight / thumbnailWidth)) {
				// 设置高度为要求缩放高度，宽度等比缩放
				height = thumbnailHeight;
				width = thumbnailWidth * height / thumbnailHeight;
			} else {
				// 设置宽度为要求缩放宽度，高度等比缩放
				width = thumbnailWidth;
				height = thumbnailHeight * width / thumbnailWidth;
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Thumbnails.of(originalImageFile).outputQuality(1.0).size((int) width, (int) height)
					.toOutputStream(byteArrayOutputStream); // 生成缩略图
			InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			byteArrayOutputStream.close();
			return inputStream;
		}

		return new FileInputStream(originalImageFile);
	}

	/**
	 * 等比压缩
	 * 
	 * @param originalImageFile
	 * @param thumbnailHeight
	 * @param thumbnailWidth
	 * @param origianlWidth
	 * @param origianlHeight
	 * @return
	 * @throws IOException
	 */
	public static InputStream rectPic(File originalImageFile, double width, double height) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Thumbnails.of(originalImageFile).outputQuality(1.0).size((int) width, (int) height)
				.toOutputStream(byteArrayOutputStream); // 生成缩略图
		InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		byteArrayOutputStream.close();
		return inputStream;
	}

	/**
	 * 获取图片类型，用例判断是否是图片
	 * 
	 * @param imageFile
	 * @return
	 * @throws IOException
	 */
	private static String getImageType(File imageFile) throws IOException {
		String imageType = null;

		ImageInputStream imageInputStream = ImageIO.createImageInputStream(imageFile);
		Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
		if (iterator.hasNext()) {
			ImageReader imageReader = iterator.next();
			imageType = imageReader.getFormatName();
		}

		imageInputStream.close();
		return imageType;
	}

	/**
	 * 生成缩略图
	 * 
	 * @param tempAvatarFile
	 *            需要生成缩略图的图片文件
	 * @param x
	 *            相对于左上角的横坐标点
	 * @param y
	 *            相对于左上角的纵坐标点
	 * @param width
	 *            选取区域的宽度
	 * @param height
	 *            选取区域的高度
	 * @param rltWidth
	 *            最终宽度
	 * @param rltHeight
	 *            最终高度
	 * @return
	 * @throws IOException
	 */
	public static InputStream thumbnail(byte[] imgDate, int x, int y, int width, int height, int rltWidth, int rltHeight)
			throws IOException {
		byte[] rtn = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(imgDate);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Thumbnails.of(bis).outputQuality(1.0).sourceRegion(x, y, width, height).size(rltWidth, rltHeight)
				.toOutputStream(bos);
		bis.close();
		rtn = bos.toByteArray();
		bos.close();
		return new ByteArrayInputStream(rtn);
	}

	/**
	 * 生成缩略图
	 * 
	 * @param tempAvatarFile
	 *            需要生成缩略图的图片文件
	 * @param rltWidth
	 *            最终宽度
	 * @param rltHeight
	 *            最终高度
	 * @return
	 * @throws IOException
	 */
	public static InputStream thumbnail(byte[] imgDate, int rltWidth, int rltHeight) throws IOException {
		byte[] rtn = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(imgDate);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Thumbnails.of(bis).outputQuality(1.0).size(rltWidth, rltHeight).toOutputStream(bos);
		bis.close();
		rtn = bos.toByteArray();
		bos.close();
		return new ByteArrayInputStream(rtn);
	}
}
