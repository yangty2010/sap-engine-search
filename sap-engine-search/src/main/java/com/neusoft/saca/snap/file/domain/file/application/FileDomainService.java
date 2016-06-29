package com.neusoft.saca.snap.file.domain.file.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.neusoft.saca.snap.file.app.file.dto.FileCreateDto;
import com.neusoft.saca.snap.file.app.file.dto.FileUpdateDto;
import com.neusoft.saca.snap.file.app.file.dto.FilesCreateDto;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;

/**
 * 文件通用内部服务
 * 
 * @author yan
 * 
 */
public interface FileDomainService {
	
	/**
	 * 分页获取文件列表
	 * @param page
	 * @param size
	 * @param inFeed
	 * @return
	 */
    public Page<FileEntry> obtainFiles(int page, int size, boolean inFeed);
    
    /**
     * 分页获取文件列表
     * @param page
     * @param size
     * @return
     */
    public Page<FileEntry> obtainFiles(int page, int size);
	
	/**
	 * 创建文件
	 * @param fileCreateDto
	 * @return
	 */
	public FileEntry create(FileCreateDto fileCreateDto);
	
	/**
	 * 更新
	 * @param fileUpdateDto
	 * @return
	 */
	public FileEntry update(FileUpdateDto fileUpdateDto);
	
	/**
	 * 创建多文件
	 * @param filesCreateDto
	 * @return
	 */
	public List<FileEntry> create(FilesCreateDto filesCreateDto);
	/**
	 * 删除文件信息
	 * @param id
	 */
	public FileEntry delete(String id);
	/**
	 * 判断文件是否存在
	 * @param id
	 * @return
	 */
	public boolean isFileExist(String id);
	/**
	 * 根据文件id列表查询文件信息，按照id列表顺序查询
	 * @param ids
	 * @return
	 */
	public List<FileEntry> obtainFilesByIds(List<String> ids);
	
	/**
	 * 根据文件id获取文件信息
	 * @param id
	 * @return
	 */
	public FileEntry obtain(String id);
	
	/**
	 * 根据文件id更新文件预览状态
	 * @param id
	 * @return
	 */
	public FileEntry updatePdfPreview(String id);
	
	/**
	 * 分页获取用户发布的文件
	 * @param publisher
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<FileEntry> obtainFilesByPublisher(String publisher,Sort sort,int page,int size);
	
	/**
	 * 获取用户文件列表，带有隐私
	 * @param currentUserId
	 * @param publisher
	 * @param orgIds
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<FileEntry> obtainUserFilesWithPrivacy(String currentUserId, String publisher, List<String> orgIds, Sort sort, int page,
			int size);
	
	/**
	 * 分页获取指定区域内发布的文件
	 * @param scopeId
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<FileEntry> obtainFilesByScopeId(String scopeId,Sort sort,int page,int size);
	
	/**
	 * 分页获取指定区域内发布的文件
	 * @param scopeId
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<FileEntry> obtainFilesByScopeIdWithPrivacy(String currentUserId,String scopeId,List<String> orgIds,Sort sort,int page,int size);
	
	/**
	 * 获取指定区域内发布的所有文件
	 * @param scopeId
	 * @param sort
	 * @return
	 */
	public List<FileEntry> obtainFilesByScopeId(String scopeId,Sort sort);

	/**
	 * 更新文件发布区域
	 * @param fileId
	 * @param scopeId
	 * @param scopeName
	 */
	public void updateFileScope(String fileId,String scopeId,String scopeName);
	
	/**
	 * 获取知识库中的范围内的文件列表，带隐私
	 * @param currentUserId
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<FileEntry> obtainScopeFilesWithPrivacy(String currentUserId,String scopeId, Sort sort, int page, int size); 
}
