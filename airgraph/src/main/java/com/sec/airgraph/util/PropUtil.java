package com.sec.airgraph.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定義ファイル関連Utility
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class PropUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PropUtil.class);

	private static Properties props;

	/**
	 * application.propertiesを読み込む
	 */
	public static synchronized void load() {
		InputStream is = PropUtil.class.getClassLoader().getResourceAsStream("application.properties");

		if (props == null) {
			props = new Properties();
		}
		try {
			props.load(is);

		} catch (Exception e) {
			logger.error("exception handled. ex:", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("exception handled. ex:", e);
			}
		}
	}

	/**
	 * Keyに対応したValueを取得する
	 * 
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		if (props == null) {
			load();
		}
		return props.getProperty(key);
	}
}
