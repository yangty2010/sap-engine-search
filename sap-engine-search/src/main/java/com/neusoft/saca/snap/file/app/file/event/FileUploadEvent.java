package com.neusoft.saca.snap.file.app.file.event;


import com.neusoft.saca.snap.domain.event.AppAuthedSnapEvent;
import com.neusoft.saca.snap.file.app.file.dto.FileUploadDto;
import com.neusoft.saca.snap.infrastructure.event.SnapEvent;

/**
 * 
 * @author TSD-yangty
 */
public class FileUploadEvent extends SnapEvent{

	private static final long serialVersionUID = -6590303613392655559L;
	
	private FileUploadDto fileuploaddto;

	/**
	 * 重写构造方法，初始化系统变量参数，申请数据赋值
	 * @param approvedto
	 */
	public FileUploadEvent(FileUploadDto fileuploaddto) {
		
		super();
		this.fileuploaddto = fileuploaddto;
	}

	public FileUploadDto getFileUploadDto() {
		return fileuploaddto;
	}
}
