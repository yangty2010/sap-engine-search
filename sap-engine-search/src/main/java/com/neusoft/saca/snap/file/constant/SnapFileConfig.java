/**
 * 
 */
package com.neusoft.saca.snap.file.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author David Tian
 * 
 */
@Service
public class SnapFileConfig {
	@Value("#{config['image.scale.strategy']}")
	private String imageScaleStrategy;

	@Value("#{config['image.scale.thumbnails.size']}")
	private String imageScaleSize;

	@Value("#{config['avatar.scale.strategy']}")
	private String avatarScaleStrategy;

	@Value("#{config['avatar.scale.thumbnails.size']}")
	private String avatarScaleSize;
	
	@Value("#{config['site.url']}")
	private String siteUrl;
	
	@Value("#{config['solr.serverURL']}")
	private String solrServerUrl;
	
	@Value("#{config['image.scale.mobile.height']}")
	private String imageScaleMobileHeight;
	
	@Value("#{config['image.scale.mobile.width']}")
	private String imageScaleMobileWidth;

	/**
	 * 获取移动端图片缩略图的高度
	 * @return
	 */
	public String getImageScaleMobileHeight() {
		return imageScaleMobileHeight;
	}

	/**
	 * 获取移动端图片缩略图的宽度
	 * @return
	 */
	public String getImageScaleMobileWidth() {
		return imageScaleMobileWidth;
	}

	/**
	 * 获取图片缩放策略
	 * @return
	 */
	public String getImageScaleStrategy() {
		return imageScaleStrategy;
	}

	/**
	 * 获取图片缩放尺寸，字符类型
	 * @return
	 */
	public String getImageScaleSize() {
		return imageScaleSize;
	}

	/**
	 * 获取头像缩放策略
	 * @return
	 */
	public String getAvatarScaleStrategy() {
		return avatarScaleStrategy;
	}

	/**
	 * 获取头像缩放尺寸
	 * @return
	 */
	public String getAvatarScaleSize() {
		return avatarScaleSize;
	}

	/**
	 * 获取网站地址
	 * @return
	 */
	public String getSiteUrl() {
		return siteUrl;
	}
	
}
