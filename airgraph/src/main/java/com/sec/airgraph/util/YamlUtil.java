package com.sec.airgraph.util;

import com.sec.rtc.entity.yaml.HostSettingYaml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


/**
 * YAML関連Util.
 *
 * @author Tsuyoshi Hirose
 *
 */
public class YamlUtil {

	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(YamlUtil.class);
	
	/**
	 * 指定されたリソースファイルを指定されたClassの形式で読み込む.
	 *
	 * @param <T> ジェネリクス
	 * @param clazz 指定するクラスの型
	 * @param filePath ファイルパス
	 * @return 変換結果
	 */
	public static <T> T loadYamlFromResource(Class<T> clazz, String filePath) {
		Yaml yaml = new Yaml();
		return (T) yaml.loadAs(ClassLoader.getSystemResourceAsStream(filePath), clazz);
	}

	/**
	 * 指定されたリソースファイルをオブジェクト形式で読み込む.
	 *
	 * @param filePath ファイルパス
	 * @return Object型に変換した結果
	 */
	public static Object loadYamlFromResource(String filePath) {
		Yaml yaml = new Yaml();
		return yaml.load(YamlUtil.class.getClassLoader().getResourceAsStream(filePath));
	}

	/**
	 * 指定されたファイルを指定されたClassの形式で読み込む.
	 *
	 * @param <T> ジェネリクス
	 * @param clazz 指定するクラスの型
	 * @param filePath ファイルパス
	 * @return 変換結果
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
	 * 指定されたファイルをObject形式で読み込む.
	 *
	 * @param filePath ファイルパス
	 * @return Object型に変換した結果
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
	 * 指定された文字列をObject形式のYamlに変換して読み込む.
	 *
	 * @param str 文字列
	 * @return 変換結果
	 */
	public static Object loadYamlFromString(String str) {
		Yaml yaml = new Yaml();
		return yaml.load(str);
	}
	

	/**
	 * hostSettingYamlクラスのオブジェクトを指定されたファイルにダンプする.
	 *
	 * @param hosts ホストクラスのデータ
	 * @param filePath ファイルパス
	 * @return 正常終了したかどうか
	 */
	public static <T> boolean dampDataToYamlFile(List<T> hosts, String filePath) {
		DumperOptions dump = new DumperOptions();
		dump.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		dump.setIndent(4);
		dump.setIndicatorIndent(2);
		
		Yaml yaml = new Yaml(dump);

		String yamlData;
		yamlData = yaml.dump(hosts);
		
		
		try {
			FileUtil.writeAll(filePath, yamlData, true);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * hostIDから、該当するホストの情報を取得する.
	 *
	 * @param hostId ホストID
	 * @return そのIDのホストの情報
	 */
	public static HostSettingYaml getHostFromHostId(String hostId) {
			
		// Hostオブジェクトを作成する
		List<HostSettingYaml> hosts = new ArrayList<HostSettingYaml>();
		
		// ファイルパスを指定
		String filepath = PropUtil.getValue("hosts.setting.dir") + Const.COMMON.FILE_NAME.WASANBON_HOST_CONFIG;
		String basicFilePath = PropUtil.getValue("wasanbon.webframework.local.directory.path") + Const.COMMON.FILE_NAME.BASIC_AUTH;

		hosts = loadYamlFromFile(hosts.getClass(), filepath);
		
		// 同じホストIDのホストがあれば、それを返す
		for (int i = 0; i < hosts.size(); i++) {
			if (hosts.get(i).getId().equals(hostId)) {
				if (hostId.equals(Const.HOST_CONFIG.LOCALHOST_ID)) {
					// basic認証用のID、パスワードを取得する
					Map<String, String> idAndPass = new HashMap<String, String>();
					idAndPass = loadYamlFromFile(idAndPass.getClass(), basicFilePath);
					String id = idAndPass.keySet().toArray()[0].toString();
					hosts.get(i).setId(id);
					hosts.get(i).setPassword(idAndPass.get(id));
				}
				return hosts.get(i);
			}
		}
		return null;
	}
}
