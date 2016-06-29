package com.neusoft.saca.snap.file.infrastructure.util;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

public class FileTypeUtil {

	private static final String DEFAULT_TYPE = "octet-stream";// 任意二进制文件
	/**
	 * 应用支持的可转换成pdf的文件类型
	 */
	private static Map<String, String[]> supportedType = new HashMap<String, String[]>();

	/**
	 * 初始化系统可根据Magic Number支持的文件类型 TODO 随着需要逐渐添加
	 */
	static {
		supportedType.put("D0CF11E0", new String[] { "doc", "ppt", "xls" });// MS
																			// OFFICE
																			// 2003
		supportedType.put("504B0304140006000800000021", new String[] { "docx", "pptx", "xlsx" });// MS
																									// OFFICE
																									// 2007
		supportedType.put("504B0304140000080000", new String[] { "odt", "ods", "odp", "odg", "sxw", "sxc", "sxi" });// OPEN
																													// OFFICE
		supportedType.put("FF575043", new String[] { "wpd" });// WORDPERFECT
		supportedType.put("7B5C727466", new String[] { "rtf" });// Rich Text
																// Format
		supportedType.put("25504446", new String[] { "pdf" });
	}

	/**
	 * BOM(Byte Order Mark)标识文件的编码类型
	 */
	private static Map<String, String> boms = new HashMap<String, String>();

	/**
	 * Initialise the supported encodings to be those supported by the JVM. This
	 * will NOT be updated should you later add encodings dynamically to your
	 * running code.
	 * 
	 * You can also remove some of these later if you know they will not be
	 * used. The more you remove the more performant the it will be.
	 */
	static {
		// We have this switched off by default. If you want to initialise with
		// all encodings
		// supported by your JVM the just un-comment the following line
		// EncodingGuesser.supportedEncodings =
		// getCanonicalEncodingNamesSupportedByJVM();

		// Initialise some known BOM (s) keyed by their canonical encoding name.

		boms.put("0000FEFF", "UTF-32BE");
		boms.put("FFFE0000", "UTF-32LE");
		boms.put("FEFF", "UTF-16BE");
		boms.put("FFFE", "UTF-16LE");
		boms.put("EFBBBF", "UTF-8");
		boms.put("2B2F76", "UTF-7");// We may need to cater for the next char as
									// well which can be one of [38 | 39 | 2B |
									// 2F]
		boms.put("F7644C", "UTF-1");
		boms.put("DD736673", "UTF-EBCDIC");
		boms.put("0EFEFF", "SCSU");
		boms.put("FBEE28", "BOCU-1");// optionally followed by 0xFF
	}

	/**
	 * 文本类型包括txt、html（htm、xhtml）、xml，后期再逐渐加TODO
	 */
	private static List<String> txtTypes = new ArrayList<String>();

	static {
		txtTypes.add("txt");
		txtTypes.add("html");
		txtTypes.add("htm");
		txtTypes.add("xhtml");
		txtTypes.add("xml");
		txtTypes.add("java");
	}

	/**
	 * 网页类型包括html（htm、xhtml）
	 */
	private static List<String> htmlTypes = new ArrayList<String>();

	static {
		htmlTypes.add("html");
		htmlTypes.add("htm");
		htmlTypes.add("xhtml");
	}

	/**
	 * 获取文件的真实类型
	 * 
	 * @param header
	 *            文件的头信息
	 * @param typeHint
	 *            文件可能的类型，例如扩展名类型
	 * @return 文件的真实类型,如果判断不出来,即返回typeHint
	 */
	public static String getFileType(byte[] header, String typeHint) {
		if (StringUtils.isBlank(typeHint)) {
			typeHint = DEFAULT_TYPE;
		}
		String headerStr = hex2String(header);
		// 先判断是不是在支持的类型中
		for (Entry<String, String[]> supportedTypeEntry : supportedType.entrySet()) {
			String magicNumber = supportedTypeEntry.getKey();
			if (headerStr.toUpperCase().startsWith(magicNumber)) {
				String[] types = supportedTypeEntry.getValue();
				for (String type : types) {
					if (typeHint.equalsIgnoreCase(type)) {
						return typeHint;
					}
				}
				return types[0];
			}
		}

		// 如果无法判断类别，返回typeHint
		return typeHint;
	}

	private static CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
	static {

		/*--------------------------------------------------------------------------  
		   JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码  
		   测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以  
		   再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。  
		  ---------------------------------------------------------------------------*/
		detector.add(JChardetFacade.getInstance());
		// ASCIIDetector用于ASCII编码测定
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector用于Unicode家族编码的测定
		detector.add(UnicodeDetector.getInstance());
		/*-------------------------------------------------------------------------  
		ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于  
		指示是否显示探测过程的详细信息，为false不显示。  
		---------------------------------------------------------------------------*/
		detector.add(new ParsingDetector(false));// 如果不希望判断xml的encoding，而是要判断该xml文件的编码，则可以注释掉
	}

	public static void main(String[] args) throws IOException {
		String inputFilePath = "E:/filetype/111.html";
		File inputFile = new File(inputFilePath);

		String outputFilePath = "E:/filetype/utf-withoutbom.html";
		File outputFile = new File(outputFilePath);
		outputFile.createNewFile();

		BufferedReader bufferedReader = null;
		// 判断原始txt文件的编码格式，获取响应的文件读入
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),
				detectFileEncoding(inputFile)));

		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),
				"UTF-8"));

		String content = null;
		while ((content = bufferedReader.readLine()) != null) {
			bufferedWriter.write(content);
			bufferedWriter.write("\r\n");
		}

		bufferedWriter.close();
		bufferedReader.close();
		// InputStream inputStream=new BufferedInputStream(new
		// FileInputStream(file));
		// Charset charset=detectFileEncoding(inputStream,50);
		// Charset charset = detectFileEncoding(file);
	}

	/**
	 * 将原始文本文件转为utf8编码
	 * 
	 * @param inputFile
	 *            原文件
	 * @param outputFile
	 *            转换后的文件
	 * @param inputFileCharset 要转换文件的编码
	 * 				
	 * @throws IOException
	 */
	public static void convert2Utf8(File inputFile, File outputFile, String inputFileCharset) throws IOException {
		BufferedReader bufferedReader = null;
		// 判断原始txt文件的编码格式，获取响应的文件读入
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputFileCharset));

		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),
				"UTF-8"));
		String content = null;
		while ((content = bufferedReader.readLine()) != null) {
			bufferedWriter.write(content);
			bufferedWriter.write("\r\n");
		}

		bufferedWriter.close();
		bufferedReader.close();

	}

	/**
	 * 探测一个（文本）流的编码
	 * 
	 * @param inputStream
	 *            必须支持mark
	 * @param length
	 * @return
	 */
	public static Charset detectFileEncoding(InputStream inputStream, int length) {
		Charset charset = null;
		try {
			charset = detector.detectCodepage(inputStream, length);
//			System.out.println("编码为：" + charset.name());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return charset;
	}

	/**
	 * 探测一个文件的编码
	 * 
	 * @param inputStream
	 *            必须支持mark
	 * @param length
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Charset detectFileEncoding(File file) {
		Charset charset = null;
		try {
			charset = detector.detectCodepage(file.toURL());
//			System.out.println("编码为：" + charset.name());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return charset;
	}

	/**
	 * 16进制转字符串
	 * 
	 * @param data
	 * @return
	 */
	private static String hex2String(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		if (data == null || data.length <= 0) {
			return null;
		}
		for (int i = 0; i < data.length; i++) {
			int v = data[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 是否是文本类型
	 * 
	 * @param fileType
	 * @return
	 */
	public static boolean isTextPlain(String fileType) {
		if (StringUtils.isBlank(fileType)) {
			throw new IllegalArgumentException("fileType不能为空");
		}
		if (txtTypes.contains(fileType.toLowerCase())) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是html类型
	 * 
	 * @param fileType
	 * @return
	 */
	public static boolean isHtml(String fileType) {
		if (StringUtils.isBlank(fileType)) {
			throw new IllegalArgumentException("fileType不能为空");
		}
		if (htmlTypes.contains(fileType.toLowerCase())) {
			return true;
		}
		return false;
	}
}
