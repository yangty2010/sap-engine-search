package com.neusoft.saca.snap.file.infrastructure.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.SimpleDocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.artofsolving.jodconverter.util.PlatformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.neusoft.saca.snap.file.infrastructure.util.FileTypeUtil;

public class PdfConvertServiceImpl implements PdfConvertService,
		InitializingBean {

	// JVM的系统临时文件夹
	private static String TMP_DIR = System.getProperties().getProperty(
			"java.io.tmpdir");
	static {
		TMP_DIR = TMP_DIR.replaceAll("\\\\", "/");
		if (TMP_DIR.lastIndexOf("/") != (TMP_DIR.length() - 1)) {
			TMP_DIR = TMP_DIR + "/";
		}
	}

	private static Charset charsetUTF8 = Charset.forName("UTF-8");

	private final static Logger logger = LoggerFactory
			.getLogger(PdfConvertServiceImpl.class);

	private DefaultOfficeManagerConfiguration configuration;

	private OfficeManager officeManager;
	private SimpleDocumentFormatRegistry customDocumentFormatRegistry;
	private OfficeDocumentConverter converter;

	public PdfConvertServiceImpl(
			DefaultOfficeManagerConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("******************");
		// 启动libreoffice进程 Windows平台需要启动，linux不需要启动
		if (PlatformUtils.isWindows()) {
			String command = "cmd /c soffice --headless --accept=\"socket,host=127.0.0.1,port=8100;urp\" --nofirststartwizard";
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		}

		officeManager = configuration.buildOfficeManager();
		converter = new OfficeDocumentConverter(officeManager);
		customDocumentFormatRegistry = (SimpleDocumentFormatRegistry) converter
				.getFormatRegistry();

		/*
		 * // TODO：这里可以增加OpenOffice支持的文件类型 异常json
		 * customDocumentFormatRegistry.addFormat(new
		 * DocumentFormat("OpenOffice.org 1.0 Template", "sxd",
		 * "application/vnd.sun.xml.draw"));
		 * customDocumentFormatRegistry.addFormat(new
		 * DocumentFormat("OpenOffice.org 1.0 Template", "odf",
		 * "application/vnd.oasis.opendocument.formula"));
		 */
		DocumentFormat txt = new DocumentFormat("Java", "java", "text/plain");
		txt.setInputFamily(DocumentFamily.TEXT);
		Map<String, Object> txtLoadAndStoreProperties = new LinkedHashMap<String, Object>();
		txtLoadAndStoreProperties.put("FilterName", "Text (encoded)");
		txtLoadAndStoreProperties.put("FilterOptions", "utf8");
		txt.setLoadProperties(txtLoadAndStoreProperties);
		txt.setStoreProperties(DocumentFamily.TEXT, txtLoadAndStoreProperties);
		customDocumentFormatRegistry.addFormat(txt);

		// 添加wps格式
		customDocumentFormatRegistry.addFormat(new DocumentFormat("wps文字",
				"wps", "application/msword"));// wps文字
		customDocumentFormatRegistry.addFormat(new DocumentFormat("wps表格",
				"et", "application/msword"));// wps表格
		customDocumentFormatRegistry.addFormat(new DocumentFormat("wps演示",
				"dps", "application/msword"));// wps演示
		// 启动officeManager，保持在Bean的生命周期中一直在启动状态
		officeManager.start();
	}

	@PreDestroy
	private void preDestroy() throws Exception {
		officeManager.stop();
		logger.info("stop office manager....");
	}

	@Override
	public boolean isSupportByExtension(String extension) {
		return customDocumentFormatRegistry.getFormatByExtension(extension) != null;
	}

	@Override
	public boolean isSupportByMediaType(String mediaType) {
		return customDocumentFormatRegistry.getFormatByMediaType(mediaType) != null;
	}

	/**
	 * 其他格式文档转pdf服务
	 * 
	 * @param inputFile
	 *            输入的文件（原始文档）
	 * @param outputFile
	 *            输出的文件（转化后的pdf文件）
	 */
	public void doc2Pdf(File inputFile, File outputFile) throws IOException {
		String canonicalPath = inputFile.getCanonicalPath();
		// 文件类型
		String fileType = FilenameUtils.getExtension(canonicalPath);

		// 判断是不是文本类型
		boolean isTxt = FileTypeUtil.isTextPlain(fileType);
		boolean isHtml = FileTypeUtil.isHtml(fileType);
		// 如果是html，进行编码转换(转成utf-8无BOM格式)，然后采用jodconverter转化
		if (isHtml) {
			Charset charset = FileTypeUtil.detectFileEncoding(inputFile);
			if (!charset.equals(charsetUTF8)) {
				// 需要转码到临时文件
				File tmpFile = new File(TMP_DIR + "tmp" + inputFile.getName());
				tmpFile.createNewFile();
				FileTypeUtil.convert2Utf8(inputFile, tmpFile, charset.name());
				commonOfficeDoc2Pdf(tmpFile, outputFile);
			} else {
				commonOfficeDoc2Pdf(inputFile, outputFile);
			}
		} else if (isTxt) {// 类型为txt
			// 将txt转化为pdf，pdf直接打印
			Charset charset = FileTypeUtil.detectFileEncoding(inputFile);
			txt2Pdf(inputFile, outputFile, charset);
			// commonOfficeDoc2Pdf(inputFile, outputFile);
		} else if (isSupportByExtension(fileType)) {
			// 执行office转化为pdf
			commonOfficeDoc2Pdf(inputFile, outputFile);
		}
	}

	private boolean commonOfficeDoc2Pdf(File inputFile, File outputFile) {
		try {
			DocumentFormat opf = converter.getFormatRegistry()
					.getFormatByExtension("pdf");
			logger.info("start converting...");
			converter.convert(inputFile, outputFile, opf);
			return true;
		} catch (Exception cex) {
			cex.printStackTrace();
			return false;
		} finally {
			// close the connection
			if (officeManager != null) {
				logger.info("converting processing has done: "
						+ inputFile.getName());
				// officeManager.stop();
			}
		}
	}

	private boolean txt2Pdf(File inputFile, File outputFile,
			Charset inputFileCharset) {
		// // 先将txt转成odt
		// String fileName = inputFile.getAbsolutePath();
		// if (fileName.endsWith(".txt")) {
		BufferedReader bufferedReader = null;
		try {
			// 判断原始txt文件的编码格式，获取响应的文件读入
			bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inputFile), inputFileCharset));
			// 将txt内容直接生成pdf
			Document document = new Document();
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			Font font_normal = new Font(bfChinese, 10, Font.NORMAL);// 设置字体大小
			document.setPageSize(PageSize.A4);// 设置页面大小
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			try {
				PdfWriter.getInstance(document,
						new FileOutputStream(outputFile));
				document.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String content = null;
			while ((content = bufferedReader.readLine()) != null) {
				document.add(new Paragraph(content, font_normal));
			}
			document.close();
			bufferedReader.close();
			return true;
		} catch (ConnectException cex) {
			cex.printStackTrace();
			// System.out.println("转换失败！");
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		} finally {
			// close the connection
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// }
	}

	@Override
	public String obtainRealFileType(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

}
