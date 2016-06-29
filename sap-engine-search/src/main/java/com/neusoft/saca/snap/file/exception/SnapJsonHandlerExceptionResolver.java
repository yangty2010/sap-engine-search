/**
 * 
 */
package com.neusoft.saca.snap.file.exception;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用于处理异常并返回给客户端
 * 
 * @author David Tian
 * 
 */
public class SnapJsonHandlerExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		try {
			PrintWriter writer = response.getWriter();
			String msg = ex.getMessage();
			
			JSONObject json = new JSONObject();
			json.put("code", "-1");
			json.put("msg", msg);
			
			writer.write(json.toString());
			writer.flush();
		} catch (IOException e) {

		}
		return null;
	}

}
