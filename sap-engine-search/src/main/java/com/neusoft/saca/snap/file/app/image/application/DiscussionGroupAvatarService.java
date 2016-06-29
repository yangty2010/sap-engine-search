package com.neusoft.saca.snap.file.app.image.application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.impl.AvatarIdGenerator;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;

/**
 * 讨论组头像组装
 * 
 * @author user
 *
 */
@Service
public class DiscussionGroupAvatarService {

	private final static Logger logger = LoggerFactory
			.getLogger(DiscussionGroupAvatarService.class);

	public static final String DISCUSSION_TYPE = "discussionGroup";

	@Autowired
	private ImageStoreService imageStoreService;

	public static String obtainDiscussionGroupAvatarId(String discussionGroupId) {
		return discussionGroupId + "_" + DISCUSSION_TYPE;
	}

	public void deleteDiscussionGroupAvatar(String discussionGroupId) {
		imageStoreService
				.delete(obtainDiscussionGroupAvatarId(discussionGroupId));
	}

	/**
	 * 组装用户头像，比如讨论组头像
	 * 
	 * @param backgroundPic
	 *            背景图
	 * @param userIdList
	 *            要组装的用户ID
	 */
	public boolean compositeUserAvatarPic(String discussionGroupId,
			BufferedImage backgroundBufferedImage, List<String> userIdList)
			throws Exception {

		if (userIdList.isEmpty() || userIdList.size() > 9) {
			logger.info("待组装图片不能为空或超过9个");
			return false;
		}

		int length = backgroundBufferedImage.getWidth();

		// 假设间隔5个像素
		int picSize = userIdList.size();

		// 偏移量，记录两个记录，其中后2个元素是excepted偏移量，前2个是正常可for循环的偏移量
		int for_x = 0, for_y = 0, exp_x = 0, exp_y = 0;

		// 可for循环的大小，比如当3个图合并时，这个值是2，当5个图合并时，这个值是3
		int forSize = (int) Math.ceil(Math.sqrt(picSize));

		String userAvatarId = obtainTinyAvatarId(userIdList.get(0),
				userIdList.size());

		GridFSDBFile gfs = imageStoreService.get(userAvatarId);

		// 如果头像不存在则直接返回，不进行容错，不组装头像
		if (gfs == null) {
			return false;
		}

		BufferedImage bufferedImage = ImageIO.read(gfs.getInputStream());

		// 待组装的缩略小图大小
		int thumbnaliSize = bufferedImage.getWidth();

		// 当不是偶数时，出现的例外个数，比如3个时候，上面有1个，5个时候，上面有2两个
		int exceptedSize = picSize - picSize / forSize * forSize;

		// 列数
		int lieSize = (int) Math.ceil((float) picSize / forSize);

		int picPoint = 0;
		// 边框与最底部图片距离，同时也是最上面图片y值
		int for_y_space = (length - thumbnaliSize * lieSize - SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ
				* (lieSize - 1)) / 2;
		if (exceptedSize > 0) {
			// 设置excepted偏移量
			// 设置x偏移量
			exp_x = (length - thumbnaliSize * exceptedSize - SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ
					* (exceptedSize - 1)) / 2;
			// 设置y偏移量
			exp_y = for_y_space;

			// 组装excepted
			for (int i = 0; i < exceptedSize; i++) {
				String avatarId = obtainTinyAvatarId(
						userIdList.get(picPoint++), userIdList.size());
				gfs = imageStoreService.get(avatarId);
				// 如果头像不存在则直接返回，不进行容错，不组装头像
				if (gfs == null) {
					return false;
				}
				InputStream inputStream = gfs.getInputStream();
				BufferedImage ImageOne = ImageIO.read(inputStream);
				inputStream.close();
				int width = ImageOne.getWidth();// 图片宽度
				int height = ImageOne.getHeight();// 图片高度

				// 从图片中读取RGB
				int[] ImageArrayOne = new int[width * height];
				ImageOne.getRGB(0, 0, width, height, ImageArrayOne, 0, width);
				backgroundBufferedImage
						.setRGB(exp_x
								+ i
								* (thumbnaliSize + SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ),
								exp_y, width, height, ImageArrayOne, 0,
								thumbnaliSize);
			}

			// for循环列数减1
			lieSize--;
		}

		// 设置可for循环偏移量
		// 设置x偏移量
		if (userIdList.size() == 1) {
			for_x = (length - thumbnaliSize) / 2;
		} else {
			for_x = SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ;
		}
		// 设置y偏移量
		for_y = (length - thumbnaliSize * lieSize - SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ
				* (lieSize - 1))
				- for_y_space;
		// 组装可for循环
		for (int i = 0; i < lieSize; i++) {
			for (int j = 0; j < forSize; j++) {
				String avatarId = obtainTinyAvatarId(
						userIdList.get(picPoint++), userIdList.size());
				gfs = imageStoreService.get(avatarId);
				// 如果头像不存在则直接返回，不进行容错，不组装头像
				if (gfs == null) {
					return false;
				}

				InputStream inputStream = gfs.getInputStream();
				BufferedImage ImageOne = ImageIO.read(inputStream);
				inputStream.close();
				int width = ImageOne.getWidth();// 图片宽度
				int height = ImageOne.getHeight();// 图片高度

				// 从图片中读取RGB
				int[] ImageArrayOne = new int[width * height];
				ImageOne.getRGB(0, 0, width, height, ImageArrayOne, 0, width);
				backgroundBufferedImage
						.setRGB(for_x
								+ j
								* (thumbnaliSize + SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ),
								for_y
										+ i
										* (thumbnaliSize + SnapFileConfigUtils.DISCUSSION_GROUP_SPACE_SIEZ),
								width, height, ImageArrayOne, 0, thumbnaliSize);
			}
		}

		// 将组装完的图片存入数据库
		imageStoreService.save(
				obtainDiscussionGroupAvatarId(discussionGroupId),
				bufferedImageToInputStream(backgroundBufferedImage),
				buildUnattachedMetaData());
		return true;
	}

	private JSONObject buildUnattachedMetaData() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("attachFlag", false);
		return jsonObject;
	}

	private InputStream bufferedImageToInputStream(BufferedImage bufferedImage) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		} catch (IOException e) {
			logger.error("bufferedImageToInputStream出错", e);
			e.printStackTrace();
		}
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}

	/**
	 * 获取小头像ID
	 * 
	 * @param userId
	 * @param size
	 * @return
	 */
	private static String obtainTinyAvatarId(String userId, int size) {
		if (size > 4) {
			return AvatarIdGenerator.obtainSmallTinyAvatarId(userId);
		} else {
			return AvatarIdGenerator.obtainBigTinyAvatarId(userId);
		}
	}

	public static int[] obtainBytes(InputStream inputStream) throws Exception {
		BufferedImage ImageOne = ImageIO.read(inputStream);
		inputStream.close();
		int width = ImageOne.getWidth();// 图片宽度
		int height = ImageOne.getHeight();// 图片高度

		// 从图片中读取RGB
		int[] ImageArrayOne = new int[width * height];
		ImageOne.getRGB(0, 0, width, height, ImageArrayOne, 0, width);
		return ImageArrayOne;

	}
}
