/**
 * 
 */
package com.neusoft.saca.snap.file.infrastructure.util;

import org.apache.commons.codec.binary.Base64;

/**
 * 加密/解密服务工具类
 * 
 * @author David Tian
 *
 */
public class EncryptUtils {
	/**
	 * 使用Base64进行简单的可逆加密
	 * 
	 * @param plainText
	 * @return
	 */
	public static String encodeWithBase64(String plainText) {
		byte[] b = plainText.getBytes();
		Base64 base64 = new Base64();
		b = base64.encode(b);
		String s = new String(b);
		return s;
	}
	
	/**
	 * 使用Base64进行简单的可逆解密
	 * 
	 * @param plainText
	 * @return
	 */
	public static String decodeWithBase64(String encodeStr) {
		byte[] b = encodeStr.getBytes();
		Base64 base64 = new Base64();
		b = base64.decode(b);
		String s = new String(b);
		return s;
	}
}
