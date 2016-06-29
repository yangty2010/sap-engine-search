package com.neusoft.saca.snap.file.app.image.vo;

import java.io.Serializable;

public class ImageInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String imageId;
	private double imageHeight;
	private double imageWidth;
	private double mthumbnailWidth;
	private double mthumbnailHeight;

	public ImageInfo(String imageId, double imageHeight, double imageWidth, double mthumbnailWidth,
			double mthumbnailHeight) {
		this.imageId = imageId;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.mthumbnailWidth = mthumbnailWidth;
		this.mthumbnailHeight = mthumbnailHeight;
	}

	public ImageInfo() {
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public double getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(double imageHeight) {
		this.imageHeight = imageHeight;
	}

	public double getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(double imageWidth) {
		this.imageWidth = imageWidth;
	}

	public double getMthumbnailWidth() {
		return mthumbnailWidth;
	}

	public void setMthumbnailWidth(double mthumbnailWidth) {
		this.mthumbnailWidth = mthumbnailWidth;
	}

	public double getMthumbnailHeight() {
		return mthumbnailHeight;
	}

	public void setMthumbnailHeight(double mthumbnailHeight) {
		this.mthumbnailHeight = mthumbnailHeight;
	}

}
