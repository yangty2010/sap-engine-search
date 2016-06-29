package com.neusoft.saca.snap.search.application;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.neusoft.saca.snap.engine.stub.file.api.FileStub;
import com.neusoft.saca.snap.search.tools.SearchUtils;

@Service
public class SearchIndexService {

	@Autowired
	private PdfExtractService pdfExtractService;

	@Autowired
	private FileStub fileStub;

	@Autowired
	@Qualifier("fileIndexExecutor")
	private ThreadPoolTaskExecutor fileIndexExecutor;
	
	@Autowired
	SearchUtils utils;
	
	/**
	 * 
	 * @param fileId
	 */
	public void indexFileContent(String fileId) {
		    //1 根据文件id,获取文件流
		InputStream inputStream = fileStub.obtainPdfFile(fileId); 
		
		if (inputStream!= null) {
			//2 根据文件流，抽取pdf流中的文本内容
			String content = pdfExtractService.extractContent(inputStream);
			//3 安置文本内容
			Map<String, Object> updateMap = new HashMap<String, Object>();
			updateMap.put("content", content);
			
			//4 
			utils.atomSet(fileId, updateMap);
		}
	}
	
	/**
	 * 
	 * @param fileIds
	 */
	public void indexMultiFileContent(List<String> fileIds) {
		if (fileIds!=null&&fileIds.size()>0) {
			for (String  fileId : fileIds) {
				final String finalFileId=fileId;
				fileIndexExecutor.execute(new Runnable() {
					@Override
					public void run() {
						indexFileContent(finalFileId);
					}
				});
				indexFileContent(fileId);
			}
		}
	}

}
