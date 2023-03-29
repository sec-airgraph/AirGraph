package com.sec.rtc.entity.yaml;

import lombok.Data;

/**
 * AirGraph Host用定義ファイル
 * 
 * @author Tatsuya Ide
 *
 */
@Data
public class AirGraphHostYaml {
	
	/**
	 * ID
	 */
	private String id;
	
	/**
	 * ホスト名
	 */
	private String hostName;
	
	/**
	 * IPアドレス
	 */
	private String ip;
	
	/**
	 * AirGraphポート番号 
	 */
	private String port;
	
}
