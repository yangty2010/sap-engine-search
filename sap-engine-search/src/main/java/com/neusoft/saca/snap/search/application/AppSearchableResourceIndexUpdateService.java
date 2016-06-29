package com.neusoft.saca.snap.search.application;

import java.util.List;

import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;


/**
 * 该参考应用的可搜索资源索引更新业务，不包含利用关键词搜索的相关业务
 * 
 * @author yanzhx
 * 
 */
public interface AppSearchableResourceIndexUpdateService {
	
	/**
	 * 增加文件索引
	 * 
	 * @param feedEntry
	 * @return
	 */
	boolean addFileIndex(FileEntry fileEntry);
	
	/**
	 * 批量增加文件索引
	 * @param files
	 * @return
	 */
	boolean addFilesIndex(List<FileEntry> files);
	
	/**
	 * 重建文件索引
	 * @return
	 */
	boolean rebuildFilesIndex();
}
