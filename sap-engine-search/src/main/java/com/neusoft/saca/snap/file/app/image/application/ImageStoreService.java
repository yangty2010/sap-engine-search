package com.neusoft.saca.snap.file.app.image.application;

import java.io.InputStream;
import java.util.List;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * 非文件上传形式的图片存储服务
 * @author yan
 *
 */
public interface ImageStoreService {
	/**
	 * 使用传入的id保存
	 * 
	 * @param id
	 * @param inputstream
	 * @param metaData 元数据
	 */
	public void save(String id, InputStream inputstream,Object metaData);

	/**
	 * 保存文件，返回内部生成的id
	 * 
	 * @param inputstream
	 * @param metaData 元数据
	 * @return
	 */
	public String save(InputStream inputstream,Object metaData);

	/**
	 * 根据id获取附件的输入流
	 * 
	 * @param id
	 * @return
	 */
	public GridFSDBFile get(String id);
	
	/**
	 * 根据文件列表
	 * 
	 * @param id
	 * @return
	 */
	public List<GridFSDBFile> getList(List<String> idList);

	/**
	 * 删除文件，并删除相应的缩略图
	 * 
	 * @param id
	 */
	public void delete(String id);

	/**
	 * 批量删除文件，并删除相应的缩略图
	 * 
	 * @param fileIdList
	 */
	public void deleteByIdList(List<String> fileIdList);
	
	/**
	 * 设置图片为与业务绑定状态
	 * @param imageId
	 */
	public void attachImage(String imageId);
	
	/**
	 * 批量设置图片为与业务绑定状态
	 * @param imageIds
	 */
	public void batchAttachImage(List<String> imageIds);
}
