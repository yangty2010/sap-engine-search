package com.neusoft.saca.snap.file.domain.directory.application;

import java.util.List;
import java.util.Map;

import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryPathVo;

public interface DirectoryService {

	/**
	 * 获取目录的路径
	 * @param paths 
	 * @param directoryId
	 * @return
	 */
	public List<DirectoryPathVo> obtainDirectoryPath(List<DirectoryPathVo> paths,String directoryId);
	/**
	 * 获取目录实体
	 * @param directoryId
	 * @return
	 */
	public DirectoryEntry obtain(String directoryId);
	
	/**
	 * 获取指定目录下的目录列表
	 * @param parent
	 * @return
	 */
	public List<DirectoryEntry> obtainDirectories(String parent);
	/**
	 * 获取指定目录下的目录列表
	 * @param parent
	 * @return
	 */
	public List<DirectoryEntry> obtainDirectories(String parent, String userId, String groupId);
	
	/**
	 * 获取指定目录下的目录Map
	 * @param parent
	 * @return
	 */
	public Map<String, DirectoryEntry> obtainDirectoriesMap(String parent);
	
	/**
	 * 获取指定目录下的目录Map
	 * @param parent
	 * @return
	 */
	public Map<String, DirectoryEntry> obtainDirectoriesMap(String parent, String userId, String groupId);
	
	/**
	 *  批量保存目录
	 * @param directories
	 */
	public void saveDirectories(List<DirectoryEntry> directories);
	
	/**
	 * 删除父目录下所有目录
	 * @param parent
	 */
	public void deleteDirectories(String parent);
	
	/**
	 * 删除父目录下指定子目录之外的子目录
	 * @param parent
	 */
	public void deleteDirectories(String parent,List<String> excludeDirectory);
	
}
