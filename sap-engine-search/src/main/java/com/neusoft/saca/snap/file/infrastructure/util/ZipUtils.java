package com.neusoft.saca.snap.file.infrastructure.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩工具类
 * 
 * @author David Tian
 */
public class ZipUtils {

	/**
	 * 内存压缩字节数组
	 * 
	 * @param data
	 * @param zipEntryName
	 * @return
	 * @throws IOException
	 */
	public static byte[] zipByteArray(byte[] data, String zipEntryName) throws IOException {
		ZipEntry ze = new ZipEntry(zipEntryName);
		ze.setMethod(ZipEntry.DEFLATED);// 指定压缩存储
		ze.setTime(System.currentTimeMillis());

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bao);
		zos.putNextEntry(ze);
		zos.write(data);

		zos.close();
		return bao.toByteArray();
	}

}