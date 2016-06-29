package com.neusoft.saca.snap.file.app.file.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class FileUtils {
	
	/**
	 *  默认排序条件：上传时间
	 */
	private static final String DEFAULT_SORT_CONDITION = "uploadTime";

	/**
	 * 文件排序条件
	 */
	@SuppressWarnings("serial")
	private static final List<String> registeredFileSortCondition = new ArrayList<String>() {

		{
			add("uploadTime");// 上传时间
			add("finalScore");// 评级 TODO 抛弃
			add("rating");// 评级
		}
	};

	/**
	 * 带有缓冲的下载，可有效解决内存
	 * 
	 * @param response
	 * @param inputStream
	 * @throws IOException
	 */
	public static void bufferedDownload(HttpServletResponse response, InputStream inputStream) throws IOException {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			response.addHeader("Access-Control-Allow-Origin", "*");// 跨域访问
			ServletOutputStream outputStream = response.getOutputStream();
			// 按缓存流的方式，一边读取一边下载
			bis = new BufferedInputStream(inputStream);
			bos = new BufferedOutputStream(outputStream);
			byte[] buff = new byte[10240]; // 每次读取10k
			int readedByteLength = 0;
			while ((readedByteLength = bis.read(buff)) > 0) {
				bos.write(buff, 0, readedByteLength);
			}
			outputStream.flush();
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}
	
	/**
	 * 
	 * @param sortCondition
	 * @param sortOrder
	 * @return
	 */
	public static Sort convert2Sort(String sortCondition, String sortOrder) {
		String condition = DEFAULT_SORT_CONDITION;
		if (StringUtils.isNotBlank(sortCondition) && registeredFileSortCondition.contains(sortCondition)) {
			condition = sortCondition;
		}
		Direction direction = Direction.DESC;
		if (StringUtils.isNotBlank(sortOrder) && "asc".equalsIgnoreCase(sortOrder)) {
			direction = Direction.ASC;
		}
		Sort sort = new Sort(direction, condition);
		return sort;
	}

}
