package com.neusoft.saca.snap.file.app.file.controller.file.operation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.util.Streams;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.neusoft.saca.snap.file.app.file.dto.FileDownloadDto;

public class FileTest {
	
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void dddmain_bak(String[] args) throws IOException {
			long start = System.currentTimeMillis();
//		 	String url = "http://localhost:8880/snap-engine-file/file/restupload?mfile_is={mfile_is}&filename={filename}&filesize={filesize}&mfilecontentType={mfilecontentType}&creator={creator}";
			String url = "http://localhost:8111/snap-engine-file/file/restupload";
//			String filePath = "F:\\迅雷下载\\[阳光电影www.ygdy8.com].地球回音.BD.720p.中英双字幕.rmvb";
			String filePath = "E:\\test.zip";

//		    RestTemplate rest = new RestTemplate();
		    RestTemplate restTemplate = new RestTemplate(); 
		    //MultipartFile mfile = new MultipartFile();
		    File file = new File(filePath);
	        FileInputStream fis = new FileInputStream(file);
	        String mfilecontentType = "application/octet-stream";
	        String creator = "wangdg";
	        //指定文件位置读取的文件流
	        //InputStream is = new BufferedInputStream(fis); //得到指定位置的流
		    FileSystemResource resource = new FileSystemResource(new File(filePath));
		    InputStream is = resource.getInputStream();
		    MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
//		    Map<String, Object> param = new HashMap<String, Object>();
		    param.add("mfile", resource);
//		    param.add("filename", "[阳光电影www.ygdy8.com].地球回音.BD.720p.中英双字幕.rmvb");
//		    param.add("filesize", filesize);
//		    param.add("mfilecontentType", mfilecontentType);
		    param.add("creator", creator);
		    for(int i=1;i<2;i++){
//		    	 Thread thread1 = new Thread();  
//			     thread1.start();  
				 JSONObject result = restTemplate.postForObject(url, param, JSONObject.class);
		    }
	        long end = System.currentTimeMillis();
	        System.out.println("耗时"+(end-start)/1000);
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
			RestTemplate restTemplate = new RestTemplate(); 
			long start = System.currentTimeMillis();
			String url = "http://localhost/snap-engine-file/file/restupload";
			String filePath = "D:\\unieap-dataexchange-4.6-win64.zip";
	        String creator = "wangdg";
		    FileSystemResource resource = new FileSystemResource(new File(filePath));
		    MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		    param.add("mfile", resource);
		    param.add("creator", creator);
//		    JSONObject result = restTemplate.postForObject(url, param, JSONObject.class);
		    
		    for(int i=1;i<4;i++){
		    	 Thread thread1 = new Thread();  
			     thread1.start(); 
		    	JSONObject result = restTemplate.postForObject(url, param, JSONObject.class);
		    }
//		    String fileId = "00590b62-30ea-4058-b6ff-5b7dd0c65107";
//		    String url = "http://localhost:8880/snap-engine-file/file/restdownload1?fileId={fileId}";
//		    Map<String, Object> requestMap = new HashMap<String, Object>();
//		    requestMap.put("fileId", fileId);
//		    byte[] result = restTemplate.getForObject(url, byte[].class, requestMap);
//		    
////		    ResponseEntity<FileDownloadDto> result = restTemplate.getForEntity(url, FileDownloadDto.class, requestMap);
//		    //1.获取指定地址的输入流.URL url = new URL("url");
//		    InputStream in = new ByteArrayInputStream(result);
//		    //2.在指定文件夹下创建文件。
//		    File dir = new File("E:\\");
//		    File file = new File(dir,"ddd.zip");
//		    //3.将下载保存到文件。
//		    FileOutputStream out = new FileOutputStream(file);
//		    Streams.copy(in, out, true);
	        long end = System.currentTimeMillis();
	        System.out.println("耗时"+(end-start)/1000);
		
	}

}
