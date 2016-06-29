package com.neusoft.saca.snap.file.app.image.application;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.impl.AvatarIdGenerator;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;

/**
 * 默认头像生成，从8张背景图中随机选取一张加入姓名水印
 * 
 * @author user
 *
 */
@Service
public class DefaultAvatarGeneratorService {

	private static final Logger Logger = LoggerFactory
			.getLogger(DefaultAvatarGeneratorService.class);

	@Autowired
	private ImageStoreService imageStoreService;

	@Autowired
	private ImageService imageService;

	private static int font_size = 70;

	/**
	 * 判断是否需要生成默认头像， 用于判断组装的小头像是否存在，如果存在直接返回
	 * 
	 * @param userId
	 * @return
	 */
	public boolean needGenerateDefaultAvatar(String userId) {
		GridFSDBFile tinyAvatar = imageStoreService.get(AvatarIdGenerator
				.obtainBigTinyAvatarId(userId));
		if (tinyAvatar != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取不存在的头像尺寸
	 * 
	 * @param userId
	 * @return
	 */
	private List<Double> obtainThumbnailSizeList(String userId) {
		List<Double> list = new ArrayList<Double>();
		GridFSDBFile intinalAvatarFile = null;
		for (Double size : SnapFileConfigUtils.obtainAvatarScaleSizes()) {
			intinalAvatarFile = imageStoreService.get(AvatarIdGenerator
					.genegratorUserAvatarId(userId, size));
			if (intinalAvatarFile == null) {
				list.add(size);
			}
		}
		return list;
	}

	/**
	 * 生成随机默认头像，如果存在原图，则查询各个尺寸头像，对不存在的头像重新用原图生成
	 * 
	 * @param userId
	 * @param userName
	 * @return
	 */
	public void avatarGenerate(String userId, String userName) {

		// 获取缩略名字
		userName = DefaultAvatarGeneratorService.obatainName(userName);

		// 判断是否有初始头像，如果有用初始头像切割小头像，然后组装
		// 如果没有则随机选择一个初始头像，添加姓名水印后切割小头像，其实是一种容错机制
		GridFSDBFile intinalAvatarFile = imageStoreService
				.get(AvatarIdGenerator.obtainMobileLargeAvatarId(userId));

		// 要裁剪的尺寸
		List<Double> thumbnailSizeList = SnapFileConfigUtils
				.obtainAvatarScaleSizes();
		int number = 0;
		BufferedImage bimage = null;
		if (intinalAvatarFile == null) {
			
			Random random = new Random();
			number = random
					.nextInt(DefaultAvatarCacheService.DEFAULT_AVATAR_NUMBER);
			number++;
			bimage = DefaultAvatarCacheService.obtainCacheRandomAvatar(number);

			// 如果姓名不为空，加水印随机图片
			if (!StringUtils.isEmpty(userName)) {
				drawName(bimage, userName);
			}
		} else {
			try {
				bimage = ImageIO.read(intinalAvatarFile.getInputStream());
				thumbnailSizeList = this.obtainThumbnailSizeList(userId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int width = bimage.getWidth();
		int height = bimage.getHeight();

		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut;
		try {
			imOut = ImageIO.createImageOutputStream(bs);

			ImageIO.write(bimage, "png", imOut);

			// 存储原图到数据库
			imageService.uploadTempLogo(
					new ByteArrayInputStream(bs.toByteArray()), userId);

		} catch (IOException e) {
			Logger.error("上传随机头像出错", e);
			e.printStackTrace();
		}

		if (!thumbnailSizeList.isEmpty()) {
			// 生成切割小图片，存入数据库
			imageService.rectAndStoreAvatar(userId, 0, 0, width, height,
					thumbnailSizeList, AvatarIdGenerator.USER_TYPE);
		}
	}

	/**
	 * 加姓名水印
	 * 
	 * @param bimage
	 * @param userName
	 */
	private static void drawName(BufferedImage bimage, String userName) {
		int width = bimage.getWidth();
		int height = bimage.getHeight();
		// 2d 画笔
		Graphics2D g = bimage.createGraphics();

		// --------对要显示的文字进行处理--------------
		AttributedString ats = new AttributedString(userName);
		Font f = new Font("微软雅黑", Font.PLAIN, font_size);
		ats.addAttribute(TextAttribute.FONT, f, 0, userName.length());
		AttributedCharacterIterator iter = ats.getIterator();
		// ----------------------
		if(userName.length() == 1) {
			int widthRec = (width- font_size)/ 2;
			if (isChinese(userName.toCharArray()[0])) {
				g.drawString(iter, widthRec + 2,
						(int) (height / 2 + font_size / 4 + 5 ));
			} else {
				 if(userName.toUpperCase().contains("W")){
					g.drawString(iter, widthRec,
							(int) (height / 2 + font_size / 3 + 2));
				} else {
					g.drawString(iter, widthRec + 10,
							(int) (height / 2 + font_size / 3 + 2));
				}
				
			}
		} else {
			if (isChinese(userName.toCharArray()[0])) {
				g.drawString(iter, (int) (width / 2 - font_size +1),
						(int) (height / 2 + font_size / 4 + 5 ));
			} else {
				if(userName.toUpperCase().equals("WW")){
					g.drawString(iter, (int) (width / 2 - font_size),
							(int) (height / 2 + font_size / 3 + 1));
				} else if(userName.toUpperCase().contains("W")){
					g.drawString(iter, (int) (width / 2 - font_size/1.3 - 2),
							(int) (height / 2 + font_size / 3 + 2));
				} else {
					g.drawString(iter, (int) (width / 2 - font_size/1.8 - 5),
							(int) (height / 2 + font_size / 3 + 2));
				}
				
			}
		}

		// 添加水印的文字和设置水印文字出现的内容 ----位置
		g.dispose();// 画笔结束
	}

	/**
	 * 获取缩略名字
	 * 
	 * @param fullName
	 * @return
	 */
	public static String obatainName(String fullName) {
		String name = "";
		if (StringUtils.isEmpty(fullName)) {
			return null;
		}
		String[] nameStrings = fullName.split(" ");
		for (String str : nameStrings) {
			char[] ch = str.toCharArray();
			for (char c : ch) {
				if (isChinese(c)) {
					name += c;
				} else {
					c = String.valueOf(c).toUpperCase().toCharArray()[0];
					name += c;
					break;
				}
			}
		}

		// 只取后两个字符
		if (name.length() > 2) {
			name = name.substring(name.length() - 2, name.length());
		}
		return name;
	}

	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}
}
