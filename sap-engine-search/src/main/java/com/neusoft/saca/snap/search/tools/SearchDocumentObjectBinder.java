package com.neusoft.saca.snap.search.tools;

import java.util.HashMap;
import java.util.Map;

import com.neusoft.saca.snap.infrastructure.search.vo.SearchedResourceBean;
import com.neusoft.saca.snap.search.tools.constant.AppSearchConstant;
import com.neusoft.saca.snap.search.vo.SearchedFileBean;

/**
 * 检索结果转化为JavaBean的映射器
 * 
 * @author David Tian
 * 
 */
public class SearchDocumentObjectBinder {
	private static final Map<String, Class<? extends SearchedResourceBean>> binderMap = new HashMap<String, Class<? extends SearchedResourceBean>>();

	static {
		binderMap.put(AppSearchConstant.RESOURCE_TYPE_FILE, SearchedFileBean.class);
	}

	public static Map<String, Class<? extends SearchedResourceBean>> obtainBinderMap() {
		return binderMap;
	}
}
