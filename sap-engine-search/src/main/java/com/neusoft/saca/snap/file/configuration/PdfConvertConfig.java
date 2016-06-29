package com.neusoft.saca.snap.file.configuration;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.neusoft.saca.snap.file.infrastructure.converter.PdfConvertPokeServiceImpl;
import com.neusoft.saca.snap.file.infrastructure.converter.PdfConvertService;
import com.neusoft.saca.snap.file.infrastructure.converter.PdfConvertServiceImpl;
import com.neusoft.saca.snap.infrastructure.config.ConfigUtil;

/**
 * 这个配置类纯粹为了技术上实现pdf文件转换的可配置，即是否可以通过配置不安装libeoffice
 * 
 * @author TS
 *
 */
@Configuration
public class PdfConvertConfig {

	@Bean
	public PdfConvertService pdfConvertService() {

		// 判断是否需要pdf转换，如果不需要返回空方法
		if (ConfigUtil.obtainConfigProperty("pdf.convert.need").equals("true")) {
			DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
			configuration.setOfficeHome(ConfigUtil
					.obtainConfigProperty("office.home"));
			configuration.setPortNumbers(Integer.valueOf(ConfigUtil
					.obtainConfigProperty("office.port")));
			configuration.setTaskExecutionTimeout(Long.valueOf(ConfigUtil
					.obtainConfigProperty("office.taskExecutionTimeout")));
			configuration.setTaskQueueTimeout(Long.valueOf(ConfigUtil
					.obtainConfigProperty("office.taskQueueTimeout")));
			return new PdfConvertServiceImpl(configuration);
		} else {
			return new PdfConvertPokeServiceImpl();
		}
	}
}
