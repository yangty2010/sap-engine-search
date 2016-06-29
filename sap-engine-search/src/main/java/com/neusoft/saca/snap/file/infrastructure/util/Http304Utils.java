/**
 * 
 */
package com.neusoft.saca.snap.file.infrastructure.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于自动控制Http客户端缓存的304状态吗
 * 
 * @author David Tian
 * 
 */
public class Http304Utils {
	
	/**
	 * 检查并设置Http浏览器的缓存控制
	 * 
	 * @param lastModifiedMillisecs 资源在服务端的最后修改时间，单位是毫秒
	 * @param request
	 * @param response
	 * @return true则需要重新传送给客户端；false则不需要重新传输，直接返回304即可。
	 */
	public static boolean hasTheSameEtag(long lastModifiedMillisecs, 
			HttpServletRequest request, HttpServletResponse response) {

		// 检查ETag
		String previousToken = request.getHeader("If-None-Match");
		if (previousToken != null && previousToken.equals(Long.toString(lastModifiedMillisecs))) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return true;
		}
		
		// ETag有更新
		response.setHeader("ETag", Long.toString(lastModifiedMillisecs));

		// 设置相对过期时间, max-age指明以秒为单位的缓存时间
		// String maxAgeDirective = "max-age=" + 0;
		// response.setHeader("Cache-Control", maxAgeDirective);
		
		// 当用户首次请求该文件的时候，Last-Modified字段将该文件的最后修改日期发送到客户端
		// response.addDateHeader("Last-Modified", lastModifiedMillisecs);
		
		// 表示存在时间，允许客户端在这个时间之前不去检查（发请求），等同max-age的效果。但是如果同时存在，则被Cache-Control的max-age覆盖。
		// 指定相对的过期时间,以分钟为单位,表示从当前时间起过多少分钟过期。
		// response.addDateHeader("Expires", lastModifiedMillisecs + cacheMillisecs);
		
		response.setStatus(HttpServletResponse.SC_OK);
		return false;
	}
}
