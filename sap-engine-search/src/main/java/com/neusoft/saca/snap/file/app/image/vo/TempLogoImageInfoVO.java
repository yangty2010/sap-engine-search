package com.neusoft.saca.snap.file.app.image.vo;

/**
 * 临时logo图片类，保存临时图片类型、临时图片的获取路径、高度、宽度
 * 
 * @author TSD-wjn
 * 
 */
public class TempLogoImageInfoVO {
	/**
	 * 临时logo图片的绝对路径
	 */
	private String imgUrl;
	/**
	 * 临时logo图片的像素宽度
	 */
	private int width;
	/**
	 * 临时logo图片的像素高度
	 */
	private int height;

	public String getImgUrl() {
		return imgUrl;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
