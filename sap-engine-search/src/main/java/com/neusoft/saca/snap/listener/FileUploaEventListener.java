package com.neusoft.saca.snap.listener;

import org.rribbit.Listener;

import com.neusoft.saca.snap.file.app.file.event.FileUploadEvent;

/**
 * 
 * @author TSD-yangty
 *
 */
public interface FileUploaEventListener {

	@Listener
	public void handleEvent(FileUploadEvent event);

}
