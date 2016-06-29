/**
 * 
 */
package com.neusoft.saca.snap.search.tools.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author yangty-tsd
 * 
 */
@Service
public class SnapSearchConfig {
	
	@Value("#{config['solr.serverURL']}")
	private String solrServerUrl;

	/**
	 * 获取搜索服务器地址
	 * @return
	 */
	public String getSolrServerUrl() {
		return solrServerUrl;
	}
	
}
