package com.neusoft.saca.snap.file.infrastructure.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class FileUtils {

	/**
	 * 判断文件的字符编码 首先，不同编码的文本，是根据文本的前两个字节来定义其编码格式的。定义如下：
	 * 
	 * ANSI：无格式定义 
	 * Unicode： 前两个字节为FFFE  Unicode文档以0xFFFE开头 
	 * Unicode big endian：前两字节为FEFF 
	 * UTF-8：前两字节为EFBB UTF-8以0xEFBBBF开头
	 * 
	 * @param inputFile
	 * @return charset "GBK,UTF-16LE,UTF-16BE,UTF-8"
	 */
	public static String getFileCharset(File inputFile) {
		// 默认编码GBK
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {

			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
//				return charset; // 文件编码为 ANSI
			} else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE"; // 文件编码为 Unicode
				// checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {

				charset = "UTF-16BE"; // 文件编码为 Unicode big endian
				// checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {

				charset = "UTF-8"; // 文件编码为 UTF-8
				// checked = true;
			}
			bis.reset();

			/*
			 * if (!checked) { int loc = 0;
			 * 
			 * while ((read = bis.read()) != -1) { loc++; if (read >= 0xF0)
			 * break; if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
			 * break; if (0xC0 <= read && read <= 0xDF) { read = bis.read(); if
			 * (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF) // (0x80 // -
			 * 0xBF),也可能在GB编码内 continue; else break; } else if (0xE0 <= read &&
			 * read <= 0xEF) {// 也有可能出错，但是几率较小 read = bis.read(); if (0x80 <=
			 * read && read <= 0xBF) { read = bis.read(); if (0x80 <= read &&
			 * read <= 0xBF) { charset = "UTF-8"; break; } else break; } else
			 * break; } } // System.out.println( loc + " " +
			 * Integer.toHexString( read ) // ); }
			 */
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return charset;
	}
}
