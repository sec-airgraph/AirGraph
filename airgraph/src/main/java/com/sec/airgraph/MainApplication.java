package com.sec.airgraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * メイン
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class MainApplication {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

	public static void main(String[] args) throws Throwable {

		logger.info("RTComponent - IDE start!!!!");

		SpringApplication.run(MainApplication.class, args);

	}
}
