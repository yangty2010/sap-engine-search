package com.neusoft.saca.snap.file.app.file.dto;

import java.io.InputStream;
import java.io.Serializable;

import com.neusoft.saca.snap.file.domain.UploadReq;

/**
 * 
 * @author TSD-yangty
 *
 */
public class FileUploadDto implements Serializable {

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public UploadReq getUploadReq() {
		return uploadReq;
	}
	public void setUploadReq(UploadReq uploadReq) {
		this.uploadReq = uploadReq;
	}
	public InputStream getData() {
		return data;
	}
	public void setData(InputStream data) {
		this.data = data;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 425774107447235371L;
	private String fileName;
	private String contentType;
	private UploadReq uploadReq;
	private InputStream data;
	
}
