package com.neusoft.saca.snap.file.app.file.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.domain.UploadReq;

import net.sf.json.JSONObject;

public interface FileService {
	/**
	 * 下载原始文件
	 * 
	 * @param id
	 * @return
	 */
	public GridFSDBFile obtainOriginalFile(String id);
	
	/**
	 * 把文件转换为pdf格式
	 * 
	 * @param metadata
	 * @param mFile
	 * @throws IOException
	 */
	public void convertToPdfAndRestore(UploadReq uploadedReq, MultipartFile mFile,String fileType) throws IOException;

	/**
	 * 上传文件
	 * @param metadata
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public boolean upload(String id, UploadReq uploadedReq, MultipartFile file) throws IOException;
	
	/**
	 * 上传音频文件，并将amr转成mp3
	 * @param id
	 * @param file
	 * @return
	 */
	public boolean uploadVoice(String id, UploadReq uploadedReq, MultipartFile file)throws IOException;

	/**
	 * 获取pdf文件，如果pdf文件不存在，则使用默认的那个"暂未生成预览"的pdf文件
	 * 
	 * @param fileId
	 * @return
	 */
	public InputStream obtainPdf(String fileId);
	
	/**
	 * 获取pdf文件，如果pdf文件不存在，返回null
	 * 
	 * @param fileId
	 * @return
	 */
	public InputStream obtainPdfWithoutDefault(String fileId);
	
	/**
	 * 删除文件
	 * @param fileId
	 */
	public void delete(String fileId);
	
	/**
	 * 批量删除文件
	 * @param fileIds
	 */
	public void delete(List<String> fileIds);
	
	/**
	 * 设置文件为与业务依附状态
	 * @param fileId
	 */
	public void attachFile(String fileId);
	
	/**
	 * 批量设置文件为与业务依附状态
	 * @param fileIds
	 */
	public void batchAttachFile(List<String> fileIds);
	
	/**
	 * 获取文件的可预览状态
	 * @param fileId
	 * @return
	 */
	public String obtainFilePreviewStatus(String fileId);

	/**
	 * 获取文件的页数
	 * @param value
	 * @return
	 */
	public JSONObject obtainPdfPages(String fileId);
	
}
