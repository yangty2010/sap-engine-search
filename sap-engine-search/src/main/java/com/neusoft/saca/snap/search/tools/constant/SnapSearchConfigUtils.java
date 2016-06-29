/**
 * 
 */
package com.neusoft.saca.snap.search.tools.constant;

import com.neusoft.saca.snap.infrastructure.spring.SpringContextUtil;


/**
 * 应用工具类
 * 
 * @author yangty-tsd
 * 
 */
public class SnapSearchConfigUtils {
	private static SnapSearchConfig snapConfig;
	
	public static String obtainSolrServerUrl(){
		return obtainAppConfig().getSolrServerUrl();
	}
	
	private static SnapSearchConfig obtainAppConfig() {
		if (snapConfig == null) {
			snapConfig = (SnapSearchConfig) SpringContextUtil.getBean("snapSearchConfig");
		}
		return snapConfig;
	}
}
