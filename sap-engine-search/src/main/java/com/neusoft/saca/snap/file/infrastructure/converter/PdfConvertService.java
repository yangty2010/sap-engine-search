package com.neusoft.saca.snap.file.infrastructure.converter;

import java.io.File;
import java.io.IOException;

/**
 * pdf转换服务 TODO 考虑并发问题，是否需要做队列
 * 
 * @author yan
 *
 */
public interface PdfConvertService {
	/**
	 * 根据MediaType判断是否支持此种格式的转换
	 * 
	 * @param mediaType
	 * @return
	 */
	public boolean isSupportByMediaType(String mediaType);

	/**
	 * 根据扩展名判断是否支持此种格式的转换
	 * 
	 * @param extension
	 * @return
	 */
	public boolean isSupportByExtension(String extension);

	/**
	 * 获取文件的真实类型
	 * 
	 * @param data
	 * @return
	 */
	public String obtainRealFileType(byte[] data);

	/**
	 * 普通文档转pdf
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @throws IOException
	 */
	public void doc2Pdf(File inputFile, File outputFile) throws IOException;
}
