package com.neusoft.saca.snap.listener.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neusoft.saca.snap.file.app.file.dto.FileUploadDto;
import com.neusoft.saca.snap.file.app.file.event.FileUploadEvent;
import com.neusoft.saca.snap.listener.FileUploaEventListener;


/**
 * 
 * @author TSD-yangty
 *
 */
@Service
public class FileUploadEventListenerImpl implements FileUploaEventListener{
	
	@Autowired
	@Qualifier("sourceGridfs")
	private GridFsOperations srcGridfsOperations;
	
	@Override
	@Transactional(readOnly = false)
	public void handleEvent(FileUploadEvent event) {
		FileUploadDto fileupoladDto = event.getFileUploadDto();
		srcGridfsOperations.store(fileupoladDto.getData(), fileupoladDto.getFileName(), fileupoladDto.getContentType(), fileupoladDto.getUploadReq());
		
	}


}
