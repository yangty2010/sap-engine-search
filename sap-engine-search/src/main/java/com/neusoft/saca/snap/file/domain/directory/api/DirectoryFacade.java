package com.neusoft.saca.snap.file.domain.directory.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryEntry;
import com.neusoft.saca.snap.file.domain.directory.vo.DirectoryPathVo;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 文件目录facade
 * @author yan
 *
 */
public interface DirectoryFacade {
	

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
	 * 获取指定根目录下的目录数
	 * @param rootDirectory
	 * @return
	 */
	public List<DirectoryEntry> obtainDirectoryTree(String rootDirectory);
	
	/**
	 * 根据目录id获取目录
	 * @param directoryId
	 * @return
	 */
	public DirectoryEntry obtainDirectory(String directoryId);
	
	/**
	 * 分页获取指定目录下的文件列表
	 * @param directory
	 * @param page
	 * @param size
	 * @param sort
	 * @return
	 */
	public Page<FileEntry> obtainFiles(String directory,int page,int size,Sort sort);
	
	/**
	 * 检查目录是否可删除
	 * @param directoryId
	 * @return
	 */
	public boolean checkDirectoryRemovable(String directoryId);
	
	/**
	 * 调整目录结构
	 * @param parent
	 * @param directories
	 */
	public void adjustDirectory(String parent,List<DirectoryEntry> directories);
	
	/**
	 * 获取当前目录的路径
	 * @param directoryId
	 * @return List<DirectoryPathVo> 从系统根路径至当前路径中的所有目录节点
	 */
	public List<DirectoryPathVo> obtainPathList(String directoryId);
	
	/**
	 * 增加目录中的文件计数
	 * @param directoryId
	 * @param fileAmount
	 */
	public void addFileAmount(String directoryId,int fileAmount);
	
	/**
	 * 减少目录中的文件计数
	 * @param directoryId
	 * @param fileAmount
	 */
	public void decreaseFileAmount(String directoryId,int fileAmount);
	
	/**
	 * 创建一个目录
	 * @param directoryEntry
	 */
	public void create(DirectoryEntry directoryEntry);
	
	/**
	 * 批量创建目录
	 * @param directoryEntries
	 */
	public void batchCreate(List<DirectoryEntry> directoryEntries);

	/**
	 * 删除一个目录
	 * 
	 * @param directoryId
	 * @return
	 */
	public boolean deleteDirectory(String directoryId);

	/**
	 * 修改目录
	 * 
	 * @param directoryEntry
	 */
	public void update(DirectoryEntry directoryEntry);
	
}
