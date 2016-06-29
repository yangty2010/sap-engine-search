package com.neusoft.saca.snap.search.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.saca.snap.engine.stub.file.api.TicketStub;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;
import com.neusoft.saca.snap.infrastructure.search.application.SearchConstant;
import com.neusoft.saca.snap.infrastructure.search.application.SearchableResourceIndexUpdateService;
import com.neusoft.saca.snap.infrastructure.search.vo.SearchResult;
import com.neusoft.saca.snap.infrastructure.search.vo.SearchedResourceBean;
import com.neusoft.saca.snap.infrastructure.search.vo.TagHighlight;
import com.neusoft.saca.snap.search.application.SearchIndexService;
import com.neusoft.saca.snap.search.tools.assembles.SearchAssembles;
import com.neusoft.saca.snap.search.tools.constant.SnapSearchConfigUtils;
import com.neusoft.saca.snap.search.vo.SearchableFileBean;
import com.neusoft.saca.snap.search.vo.SearchedFileBean;

/**
 * @author YANGTY-TSD
 */
@Service
public class SearchUtils {

	@Autowired
	private HttpClient httpClient;
	
	@Autowired
	private SearchAssembles assembles;
	
	@Autowired
	private TicketStub ticketStub;

	@Autowired
	private SearchIndexService searchIndexService;

	@Autowired
	private SearchableResourceIndexUpdateService searchableResourceIndexUpdateService;

	/**
	 * 
	 */
	public void atomSet(String id, Map<String, Object> value) {
		JSONArray content = new JSONArray();
		JSONObject json = new JSONObject();

		try {
			json.put("id", id);
			for (String field : value.keySet()) {
				JSONObject set = new JSONObject();
				set.put("set", value.get(field));
				json.put(field, set);
			}
			content.add(json);
		} catch (final JSONException e) {
			
		}
		
		sendHttpMessage(SnapSearchConfigUtils.obtainSolrServerUrl() + "/update?commit=true", content);
	}
	
	
	@SuppressWarnings("deprecation")
	private String sendHttpMessage(String url, JSONArray content) {
		//1 拼装post协议
		PostMethod post = new PostMethod(url);
		post.setRequestHeader("Accept", "*/*");
		post.setRequestHeader("Accept-Language", "zh-cn");
		post.setRequestHeader("Accept-Encoding", "gzip, deflate");
		post.setRequestHeader("Cache-Control", "no-cache");
		post.setRequestHeader("Accept-Charset", "UTF-8");
		post.setRequestHeader("Content-type", "application/json");
		String response = "";
		try {
			//2 获取输入流
			InputStream inputStream = new ByteArrayInputStream(content.toString().getBytes("UTF-8"));
			post.setRequestBody(inputStream);
			httpClient.executeMethod(post);
			response = new String(post.getResponseBodyAsString().getBytes("8859_1"));
			post.releaseConnection();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public static SearchResult extractHighlightTags(SearchResult searchResult) {
		
		for (SearchedResourceBean item : searchResult.getHitResourceBeans()) {
			
			List<TagHighlight> tagHighlights = new ArrayList<TagHighlight>();
			SearchedFileBean file = (SearchedFileBean) item;
			file.setTagHighlights(tagHighlights);
			
			Map<String, List<String>> highlightSnippetsMap = item.getHighlightSnippetsMap();
			// 设置高亮标签
			List<String> highlightTags = highlightSnippetsMap.get("tags");
			String[] tags = new String[0];
			tags = file.getTags();
			
			if(tags == null || tags.length ==0){
				continue;
			}
			
			for (int j = 0; j < tags.length; j++) {
				TagHighlight tagHighlight = new TagHighlight();
				tagHighlight.setTag(tags[j]);
				tagHighlight.setHighlightTag(tags[j]);

				if (highlightTags == null) {
					continue;
				}
				
				for (String highlightTag : highlightTags) {
					if (tags[j].equals(extractTag(highlightTag))) {
						tagHighlight.setHighlightTag(highlightTag);
						break;
					}
				}
				tagHighlights.add(tagHighlight);
			}
		}

		return searchResult;
	}

	/**
	 * 将高亮的标签抽取成原始标签
	 * 
	 * @param highlightTag
	 * @return
	 */
	public static String extractTag(String highlightTag) {
		if (StringUtils.isEmpty(highlightTag)) {
			throw new IllegalArgumentException("传入的高亮标签为空");
		}
		String newString = "";
		newString = highlightTag.replace(SearchConstant.Highlight_Simple_Pre, "");
		newString = newString.replace(SearchConstant.Highlight_Simple_Post, "");

		return newString;
	}


	
	/**
	 * 
	 */
	public String[] getOrgids(List<String> orgIdList){
		String[] orgIds = new String[0];
		
		if (orgIdList != null) {
			orgIds = new String[orgIdList.size()];
			for (int i = 0; i < orgIdList.size(); i++) {
				orgIds[i] = orgIdList.get(i);
			}
		}
		return orgIds;
	}
	
	/**
	 * 判断redis中是否有fileKey，如果存在说明pdf转化已成功，直接发布创建内容索引请求，如果没有，
	 * 说明pdf未转化成功, 向redis中放置ticket 
	 * @param file
	 * 
	 * 调用rest接口
	 */
	public void publishIndexContent(FileEntry file){
		if (ticketStub.obtainTicketValue(file.getId())){
			searchIndexService.indexFileContent(file.getId());
		}else{
			ticketStub.createTicket(file.getId());
		}
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "null", "static-access" })
	public boolean addFilesIndexForRebuild(List<FileEntry> list) {
		try {
			//判断文件对象是否为空
			if (list == null && list.size() == 0) {
				return false;
			}
			//定义声明变量
			List<SearchableFileBean> fileBeans = new ArrayList<SearchableFileBean>();
			List<String> fileIds = new ArrayList<>();
			
			//装配filebeans
			for (FileEntry file : list) {
				SearchableFileBean fileBean = assembles.assembleFileBeanForPrivacy(file);
				fileBeans.add(fileBean);
				fileIds.add(file.getId());
			}
			
			//建立索引
			searchableResourceIndexUpdateService.addResourceBeans(fileBeans);

		    //根据文件id建立文件索引关联
			searchIndexService.indexMultiFileContent(fileIds);
			Thread.currentThread().sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
}
