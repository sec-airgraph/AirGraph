package com.sec.airgraph.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVCコンフィグクラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("main");
		registry.addViewController("/main").setViewName("main");
	}

}
