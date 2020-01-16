package com.sec.airgraph.initializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import com.sec.airgraph.MainApplication;
import com.sec.airgraph.config.WebMvcConfig;

/**
 * 起動方法設定クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Import({ WebMvcConfig.class })
public class WebApplicationInitializer extends SpringBootServletInitializer {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebApplicationInitializer.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MainApplication.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);

		if (logger.isDebugEnabled()) {
			logger.debug("初期起動処理開始");
		}
	}
}
