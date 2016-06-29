package com.neusoft.saca.snap.search.application.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.neusoft.saca.snap.engine.stub.file.api.FileRestConstant;
import com.neusoft.saca.snap.engine.stub.file.api.FileStub;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;
import com.neusoft.saca.snap.infrastructure.search.application.SearchableResourceIndexUpdateService;
import com.neusoft.saca.snap.search.application.AppSearchableResourceIndexUpdateService;
import com.neusoft.saca.snap.search.tools.SearchUtils;
import com.neusoft.saca.snap.search.tools.assembles.SearchAssembles;
import com.neusoft.saca.snap.search.tools.constant.AppSearchConstant;
import com.neusoft.saca.snap.search.vo.SearchableFileBean;

@Service
public class AppSearchableResourceIndexUpdateServiceImpl implements AppSearchableResourceIndexUpdateService {
	private final static Logger logger = LoggerFactory.getLogger(AppSearchableResourceIndexUpdateServiceImpl.class);
	// 查询返回的最多条目数
	protected static final int MAX_RESULT_COUNT = 50;
	
	@Autowired
	private FileStub fileStub;

	@Autowired
	private SearchableResourceIndexUpdateService searchableResourceIndexUpdateService;
	
	@Autowired
	private SearchAssembles assembles;
	
	@Autowired
	private SearchUtils utils;

	@Override
	public boolean addFileIndex(FileEntry fileEntry) {
		//装配filebean
		SearchableFileBean searchableFileBean = assembles.assembleFileBean(fileEntry);
		
		//新增文件索引内容
		searchableResourceIndexUpdateService.addResourceBean(searchableFileBean);
		
		//创建内容索引请求
		utils.publishIndexContent(fileEntry);
		
		return true;
	}

	@SuppressWarnings("null")
	@Override
	public boolean addFilesIndex(List<FileEntry> files) {
		//判断验证文件对象
		if (files == null && files.size() == 0) {
			return false;
		}
		
		//装配filebean
		List<SearchableFileBean> fileBeans = new ArrayList<SearchableFileBean>();
		for (FileEntry file : files) {
			SearchableFileBean fileBean = assembles.assembleFileBeanForPrivacy(file);
			fileBeans.add(fileBean);
		}
		
		//新增文件索引内容
		searchableResourceIndexUpdateService.addResourceBeans(fileBeans);
		
		//创建内容索引请求
		for (FileEntry file : files) {
			utils.publishIndexContent(file);
		}
		return true;
	}


	@Override
	public boolean rebuildFilesIndex() {
		// 清空Solr的file索引库
		UpdateResponse rlt = searchableResourceIndexUpdateService.deleteResourceBeansByQuery("resourceType:"
				+ AppSearchConstant.RESOURCE_TYPE_FILE);
		if (rlt == null || rlt.getStatus() != 0) {
			logger.error("重建文件搜索索引时发生了错误。");
			return false;
		}

		// 循环重建每100篇文件的搜索索引
		Page<FileEntry> fileAll = null;
		int pageNum = 1;
		while (true) {
			//调用rest接口
			JSONObject result = fileStub.obtainFilesAll(pageNum);
			if(result.getBoolean(FileRestConstant.SUCCESS_FLAG)){
				JSONArray files = result.getJSONArray("files");
				List<FileEntry> fileVos=new ArrayList<FileEntry>();
				for (Object fileJson : files) {
					JSONObject fileJsonObject=(JSONObject)fileJson;
					FileEntry file=(FileEntry)fileJsonObject.toBean(fileJsonObject, FileEntry.class);
					fileVos.add(file);
				}
				Pageable pageable = new  PageRequest(pageNum-1, 50);
				fileAll = new PageImpl<FileEntry>(fileVos, pageable, result.getLong("amount"));
			}else{
				break;
			}
			if (fileAll.hasContent()) {
				utils.addFilesIndexForRebuild(fileAll.getContent());
				pageNum++;
			}
			if (!fileAll.hasNextPage()) {
				break;
			}
		}

		return true;
	}

}
