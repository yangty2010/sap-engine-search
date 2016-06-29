package com.neusoft.saca.snap.search.application;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.stereotype.Service;

/**
 * pdf内容抽取服务
 * @author yan
 *
 */
@Service
public class PdfExtractService {

	/**
	 * 抽取pdf流中的文本内容
	 * @param inputStream
	 * @return
	 */
	public String extractContent(InputStream inputStream){
		
		String contenttxt="";
		try {
			//1 创建PDF解析器
			PDFParser parser = new PDFParser(inputStream); 
			//2 执行PDF解析过程
			parser.parse(); 
			//3 获取解析器的PDF文档对象
			PDDocument pdfdocument = parser.getPDDocument(); 
			//4 生成PDF文档内容剥离器
			PDFTextStripper pdfstripper = new PDFTextStripper(); 
			//5 利用剥离器获取文档
			contenttxt = pdfstripper.getText(pdfdocument); 
			parser.clearResources();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contenttxt;
	}
}
