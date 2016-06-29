package com.neusoft.saca.snap.file.infrastructure.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class ControllerHelperUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(ControllerHelperUtil.class);

	private static final String formContentType = "text/html; charset=gbk";
	
	private static final String jsonType = "application/json;charset=UTF-8";
	
	/**
	 * 将JSONObject转换为字符串,普通ajax提交
	 * 
	 * @param response
	 * @param jsonObject
	 */
	public static void returnJson(HttpServletResponse response, JSONObject jsonObject) {
		returnJson(response, jsonObject, null);
	}
	
	/**
	 * 将JSONObject转换为字符串,普通ajax提交
	 * 
	 * @param response
	 * @param jsonObject
	 */
	public static void returnApplicationJson(HttpServletResponse response, JSONObject jsonObject) {
		returnJson(response, jsonObject, jsonType);
	}
	
	/**
	 * 将JSONObject转换为字符串,针对Form的提交
	 * 
	 * @param response
	 * @param jsonObject
	 */
	public static void returnFormJson(HttpServletResponse response, JSONObject jsonObject) {
		returnJson(response, jsonObject, formContentType);
	}
	
	/**
	 * 将JSONObject转换为字符串，用于form提交，应对IE下bug
	 * 
	 * @param response
	 * @param jsonObject
	 * @param contentType
	 */
	public static void returnJson(HttpServletResponse response, JSONObject jsonObject, String contentType) {
		try {
			if (contentType != null) {
				response.setContentType(contentType);
			}
			response.addHeader("Access-Control-Allow-Origin", "*");//跨域访问
			
			response.getWriter().write(jsonObject.toString());
			response.getWriter().flush();
		} catch (IOException e) {
			logger.error("写回JSON数据错误", e);
		}
	}
	
	/**
	 * 计算字符串长度，全角2，半角1
	 * @param str
	 * @return
	 */
	public static int newLength(String str) {
		try {
			str = new String(str.getBytes("gb2312"), "iso-8859-1");
		} catch (Exception e) {
			return 0;
		}
		return str.length();
	}
}
