package com.neusoft.saca.snap.file.infrastructure.converter;

import java.io.File;
import java.io.IOException;

public class PdfConvertPokeServiceImpl implements PdfConvertService{

	@Override
	public boolean isSupportByMediaType(String mediaType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupportByExtension(String extension) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String obtainRealFileType(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doc2Pdf(File inputFile, File outputFile) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
