package com.neusoft.saca.snap.file.domain;

import java.util.Date;

/**
 * 文件上传请求
 * 
 * @author David Tian
 * 
 */
public class UploadReq implements java.io.Serializable {
	private static final long serialVersionUID = 8721863799165084350L;

	// 文件id
	private String fid;

	// 文件原始名
	private String originalFilename;

	// 文件对应的ticket票号
	private String ticket;

	// 文件上传时间
	private Date uploadedTime;

	// 文件上传者
	private String creator;

	// 是否需要转换为pdf预览文件
	private boolean needPdfPreview;
	// 是否需要对原始文件进行压缩存储
	private boolean needCompress;
	// 文件大小
	private long sizeInByte;
	/**
	 * 文件的依附状态标志 true表示文件处于依附状态，与业务相绑定； false表示文件脱离依附状态，未与业务绑定，可进行垃圾文件清理
	 */
	private boolean attachFlag;
	
	//文件有几页
	private int pages;

	/**
	 * 文件预览状态： 不可预览； 未知状态，用于文档初始化并进行pdf转化的中间状态；可预览，文档pdf转化后更新此值。
	 */
	private String previewStatus;

	// 文件类型
	private String type;

	public String getPreviewStatus() {
		return previewStatus;
	}

	public void setPreviewStatus(String previewStatus) {
		this.previewStatus = previewStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getAttachFlag() {
		return attachFlag;
	}

	public void setAttachFlag(boolean attachFlag) {
		this.attachFlag = attachFlag;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Date getUploadedTime() {
		return uploadedTime;
	}

	public void setUploadedTime(Date uploadedTime) {
		this.uploadedTime = uploadedTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public boolean isNeedPdfPreview() {
		return needPdfPreview;
	}

	public void setNeedPdfPreview(boolean needPdfPreview) {
		this.needPdfPreview = needPdfPreview;
	}

	public boolean isNeedCompress() {
		return needCompress;
	}

	public void setNeedCompress(boolean needCompress) {
		this.needCompress = needCompress;
	}

	public long getSizeInByte() {
		return sizeInByte;
	}

	public void setSizeInByte(long sizeInByte) {
		this.sizeInByte = sizeInByte;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

}
