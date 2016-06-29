package com.neusoft.saca.snap.file.domain.file.api;

/**
 * 文件相关的常量类
 * @author yan
 *
 */
public class FileDomainConstant {

	/**
	 * 文件发布区域类型：微博
	 */
	public static final String FILE_SCOPE_TYPE_MICROBLOG="microblog";
	
	/**
	 * 文件发布区域类型：网盘
	 */
	public static final String FILE_SCOPE_TYPE_PUBRES="pubres";
	/**
	 * 文件发布区域类型：群组
	 */
	public static final String FILE_SCOPE_TYPE_GROUP="group";
	/**
	 * 文件发布区域类型：部门
	 */
	public static final String FILE_SCOPE_TYPE_DEPT="dept";
	/**
	 * 文件发布区域类型：IM
	 */
	public static final String FILE_SCOPE_TYPE_IM="im";
	
	/**
	 * 文件发布区域ID：IM
	 */
	public static final String FILE_SCOPE_ID_IM="im";
	
	/**
	 * 文件发布区域ID：微博
	 */
	public static final String FILE_SCOPE_ID_MICROBLOG="open";

	/**
	 * 文件发布区域名称：微博
	 */
	public static final String FILE_SCOPE_NAME_MICROBLOG="scope_name_microblog";
	
	/**
	 * 文件计数器key前缀
	 */
	public static final String FILE_COUNTER_KEY_PREFIX="file:";
	/**
	 * 文件计数器field：read
	 */
	public static final String FILE_COUNTER_FIELD_READ="read";
	/**
	 * 文件计数器field：download
	 */
	public static final String FILE_COUNTER_FIELD_DOWNLOAD="download";
	
	/**
	 * 文件夹中的显示文件的数量
	 */
	public static final int PAGE_SIZE_IN_DIRECTORY=20;
	
	
	public static final String FILE_PREVIEW_NO="0";//不能预览
	public static final String FILE_PREVIEW_PROBABLE="1";//可能有预览，但是需要再确定
	public static final String FILE_PREVIEW_YES="2";//能预览
	
}
