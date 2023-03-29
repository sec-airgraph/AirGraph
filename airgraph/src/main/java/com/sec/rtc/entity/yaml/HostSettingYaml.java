package com.sec.rtc.entity.yaml;

import lombok.Data;

/**
 * Host用定義ファイル
 * 
 * @author Tatsuya Ide
 *
 */
@Data
public class HostSettingYaml {
	
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
	 * ネームサーバー　ポート番号 
	 */
	private String nsport;
	
	/**
	 * wasanbon webframework　ポート番号 
	 */
	private String wwport;
	
	/**
	 * パスワード
	 */
	private String password;
	
}
