/**
 * 
 */
package com.neusoft.saca.snap.file.constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.neusoft.saca.snap.infrastructure.spring.SpringContextUtil;

/**
 * 应用工具类
 * 
 * @author David Tian
 * 
 */
public class SnapFileConfigUtils {
	private static SnapFileConfig snapConfig;

	public static final String AVATAR_SIZE_LARGE = "large";
	public static final String AVATAR_SIZE_MIDDLE = "middle";
	public static final String AVATAR_SIZE_SMALL = "small";

	public static final String AVATAR_SIZE_MOBILE_LARGE = "mobile_large";
	public static final String AVATAR_SIZE_MOBILE_SMALL = "mobile_small";

	// 讨论组组装头像间距
	public static final int DISCUSSION_GROUP_SPACE_SIEZ = 5;

	private static List<Double> sizeList = new ArrayList<Double>();

	/**
	 * 获取移动端大头像
	 * 
	 * @param id
	 * @param avatarType
	 * @return
	 */
	public static String obtainLargeMobileAvatarUrl(String id, String avatarType) {
		return obtainSiteUrl() + "mobile/avatar/" + avatarType + "/" + id + "/"
				+ AVATAR_SIZE_MOBILE_LARGE + "_" + new Date().getTime();
	}

	/**
	 * 获取移动端小头像
	 * 
	 * @param id
	 * @param avatarType
	 * @return
	 */
	public static String obtainSmallMobileAvatarUrl(String id, String avatarType) {
		return obtainSiteUrl() + "mobile/avatar/" + avatarType + "/" + id + "/"
				+ AVATAR_SIZE_MOBILE_SMALL + "_" + new Date().getTime();
	}

	/**
	 * 获取图片缩放策略
	 * 
	 * @return
	 */
	public static String obtainImageScaleStrategy() {
		return obtainAppConfig().getImageScaleStrategy();
	}

	/**
	 * 获取头像缩放策略
	 * 
	 * @return
	 */
	public static String obtainAvatarScaleStrategy() {
		return obtainAppConfig().getAvatarScaleStrategy();
	}

	/**
	 * 获取图片缩放尺寸
	 * 
	 * @return
	 */
	public static List<Double> obtainImageScaleSizes() {
		String scaleSize = obtainAppConfig().getImageScaleSize();
		if (StringUtils.isEmpty(scaleSize)) {
			return new ArrayList<Double>();
		}
		String[] sizes = scaleSize.split(",");

		List<Double> sizeList = new ArrayList<Double>();

		for (String size : sizes) {
			sizeList.add(Double.valueOf(size));
		}

		return sizeList;
	}

	/**
	 * 获取移动端图片缩略图的高度
	 * 
	 * @return
	 */
	public static Double obtainImageScaleMobileHeight() {
		String height = obtainAppConfig().getImageScaleMobileHeight();
		if (StringUtils.isNotBlank(height)) {
			return Double.valueOf(height);
		}
		return null;
	}

	/**
	 * 获取移动端图片缩略图的宽度
	 * 
	 * @return
	 */
	public static Double obtainImageScaleMobileWidth() {
		String width = obtainAppConfig().getImageScaleMobileWidth();
		if (StringUtils.isNotBlank(width)) {
			return Double.valueOf(width);
		}
		return null;
	}

	/**
	 * 获取头像缩放尺寸
	 * 
	 * @return
	 */
	public static List<Double> obtainAvatarScaleSizes() {
		if (!sizeList.isEmpty()) {
			return sizeList;
		}
		String scaleSize = obtainAppConfig().getAvatarScaleSize();
		if (StringUtils.isEmpty(scaleSize)) {
			return null;
		}
		String[] sizes = scaleSize.split(",");

		for (String size : sizes) {
			sizeList.add(Double.valueOf(size));
		}
		
		// 添加讨论组组装头像大小，两个，sizeList.get(6)和sizeList.get(7)是两个讨论组组装头像大小
		sizeList.add((sizeList.get(4) - 3 * DISCUSSION_GROUP_SPACE_SIEZ) / 2);
		sizeList.add((sizeList.get(4) - 4 * DISCUSSION_GROUP_SPACE_SIEZ) / 3);
		return sizeList;
	}

	private static String obtainAvatarUrl(String avatarId, String type,
			String size) {
		return obtainSiteUrl() + "avatar/" + type + "/" + avatarId + "/" + size;
	}

	/**
	 * 获取大头像url
	 * 
	 * @param avatarId
	 * @param type
	 * @return
	 */
	public static String obtainLargeAvatarUrl(String avatarId, String type) {
		return obtainAvatarUrl(avatarId, type, AVATAR_SIZE_LARGE);
	}

	/**
	 * 获取中头像url
	 * 
	 * @param avatarId
	 * @return
	 */
	public static String obtainMiddleAvatarUrl(String avatarId, String type) {
		return obtainAvatarUrl(avatarId, type, AVATAR_SIZE_MIDDLE);
	}

	/**
	 * 获取小头像url
	 * 
	 * @param avatarId
	 * @return
	 */
	public static String obtainSmallAvatarUrl(String avatarId, String type) {
		return obtainAvatarUrl(avatarId, type, AVATAR_SIZE_SMALL);
	}

	/**
	 * 获取网站地址
	 * 
	 * @return
	 */
	public static String obtainSiteUrl() {
		return obtainAppConfig().getSiteUrl();
	}

	private static SnapFileConfig obtainAppConfig() {
		if (snapConfig == null) {
			snapConfig = (SnapFileConfig) SpringContextUtil
					.getBean("snapFileConfig");
		}
		return snapConfig;
	}
}
