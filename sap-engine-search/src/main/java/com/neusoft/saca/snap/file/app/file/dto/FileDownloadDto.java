package com.neusoft.saca.snap.file.app.file.dto;

import java.io.BufferedOutputStream;
import java.io.Serializable;

/**
 * 
 * @author TSD-yangty
 *
 */
public class FileDownloadDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5245267129035412073L;
	
	
	public BufferedOutputStream getBos() {
		return bos;
	}


	public void setBos(BufferedOutputStream bos) {
		this.bos = bos;
	}


	private BufferedOutputStream bos;
	
}
