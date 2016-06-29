package com.neusoft.saca.snap.file.app.image.application.impl;

import java.util.List;

import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;

/**
 * 头像id生成器
 * 
 * @author user
 *
 */
public class AvatarIdGenerator {

	public static String USER_TYPE = "user";

	/**
	 * 根据用户名和尺寸大小获取用户头像对应id
	 * 
	 * @param userId
	 * @return
	 */
	public static String genegratorUserAvatarId(String userId, Double length) {
		String id = userId + "_" + USER_TYPE;
		List<Double> thumbnails = SnapFileConfigUtils.obtainAvatarScaleSizes();
		
		if (length == thumbnails.get(0)) {

			return id += "_large";
		}

		if (length == thumbnails.get(1)) {

			return id += "_middle";
		}

		if (length == thumbnails.get(2)) {

			return id += "_small";
		}
		if (length == thumbnails.get(3)) {

			return id += "_mobile_large";
		}
		if (length == thumbnails.get(4)) {

			return id += "_mobile_middle";
		}
		if (length == thumbnails.get(5)) {

			return id += "_mobile_small";
		}

		return id += "_" + length.intValue();

	}
	
	/**
	 * 获取大头像id
	 * 
	 * @param userId
	 * @return
	 */
	public static String obtainMobileLargeAvatarId(String userId) {
		return userId +  "_" + USER_TYPE + "_large";
	}
	
	/**
	 * 获取讨论组合成所需大头像id
	 * 
	 * @param userId
	 * @return
	 */
	public static String obtainBigTinyAvatarId(String userId) {
		return userId+"_" + USER_TYPE + "_"+ SnapFileConfigUtils.obtainAvatarScaleSizes().get(6).intValue();
	}
	
	/**
	 * 获取讨论组合成所需小头像id
	 * 
	 * @param userId
	 * @return
	 */
	public static String obtainSmallTinyAvatarId(String userId) {
		return userId+"_" +  USER_TYPE + "_" + SnapFileConfigUtils.obtainAvatarScaleSizes().get(7).intValue();
	}
}
