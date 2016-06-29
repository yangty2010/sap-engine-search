package com.neusoft.saca.snap.file.app.image.application;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class DefaultAvatarCacheService implements
		ApplicationListener<ContextRefreshedEvent> {

	private final static Logger logger = LoggerFactory
			.getLogger(DefaultAvatarCacheService.class);

	private static Map<String, BufferedImage> list = null;

	public static int DEFAULT_AVATAR_NUMBER = 8;

	public static Map<String, BufferedImage> obtainCacheAvatar() {

		if (list == null) {
			cacheAvatar();
		}

		return list;
	}

	/**
	 * 获取默认头像
	 * 
	 * @param number
	 * @return
	 */
	public static BufferedImage obtainCacheRandomAvatar(int number) {

		return getNewCopy(DefaultAvatarCacheService.obtainCacheAvatar().get(
				number + ".jpg"));
	}

	/**
	 * 获取BufferedImage的copy
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage getNewCopy(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		return newImage;
	}

	/**
	 * 获取讨论组组装头像背景图
	 * 
	 * @return
	 */
	public static BufferedImage obtainBackgroundAvatar() {
		return getNewCopy(DefaultAvatarCacheService.obtainCacheAvatar().get(
				"background_84.jpg"));
	}

	/**
	 * 缓存本地头像
	 * 
	 * @return
	 * @throws Exception
	 */
	private static void cacheAvatar() {
		list = new HashMap<String, BufferedImage>();
		File dir = new File(getWebRootPath() + "/data/avatar/");
		File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (fileName.endsWith(".jpg")) { // 判断文件是否是图片
					// 将图片放入内存
					try {
						list.put(fileName, ImageIO.read(files[i]));
					} catch (IOException e) {
						logger.error("缓存本地头像出错", e);
						e.printStackTrace();
					}

				} else {
					continue;
				}
			}

		}
	}

	// 获取WebRoot目录
	public static String getWebRootPath() {
		URL urlpath = DefaultAvatarCacheService.class.getResource("");
		String path = urlpath.toString();
		if (path.startsWith("file")) {
			path = path.substring(5);
		}
		if (path.indexOf("WEB-INF") > 0) {
			path = path.substring(0, path.indexOf("WEB-INF") - 1);
		}
		path.replace("/", File.separator);
		return path;
	}

	/**
	 * 系统初始时将图片缓存
	 * 
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			cacheAvatar();
		}
	}
}
