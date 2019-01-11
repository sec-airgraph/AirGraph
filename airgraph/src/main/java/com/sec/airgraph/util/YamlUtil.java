package com.sec.airgraph.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * YAML関連Util
 * 
 * @author Tsuyoshi Hirose
 *
 */
public class YamlUtil {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YamlUtil.class);

	/**
	 * 指定されたリソースファイルを指定されたClassの形式で読み込む
	 * 
	 * @param clazz
	 * @param filePath
	 * @return
	 */
	public static <T> T loadYamlFromResource(Class<T> clazz, String filePath) {
		Yaml yaml = new Yaml();
		return (T) yaml.loadAs(ClassLoader.getSystemResourceAsStream(filePath), clazz);
	}

	/**
	 * 指定されたファイルを指定されたClassの形式で読み込む
	 * 
	 * @param clazz
	 * @param filePath
	 * @return
	 */
	public static <T> T loadYamlFromFile(Class<T> clazz, String filePath) {
		Yaml yaml = new Yaml();
		try {
			return (T) yaml.loadAs(new FileInputStream(filePath), clazz);
		} catch (FileNotFoundException e) {
			logger.error("exception handled. ex:", e);
		}
		return null;
	}

	/**
	 * 指定されたリソースファイルをオブジェクト形式で読み込む
	 * 
	 * @param filePath
	 * @return
	 */
	public static Object loadYamlFromResource(String filePath) {
		Yaml yaml = new Yaml();
		return yaml.load(YamlUtil.class.getClassLoader().getResourceAsStream(filePath));
	}

	/**
	 * 指定されたファイルをObject形式で読み込む
	 * 
	 * @param filePath
	 * @return
	 */
	public static Object loadYamlFromFile(String filePath) {
		Yaml yaml = new Yaml();
		try {
			return yaml.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			logger.error("exception handled. ex:", e);
		}
		return null;
	}

	/**
	 * 指定された文字列をObject形式のYamlに変換するで読み込む
	 * 
	 * @param str
	 * @return
	 */
	public static Object loadYamlFromString(String str) {
		Yaml yaml = new Yaml();
		return yaml.load(str);
	}
}
