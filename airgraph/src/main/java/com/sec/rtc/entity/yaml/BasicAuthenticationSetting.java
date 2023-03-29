package com.sec.rtc.entity.yaml;

import lombok.Data;

/**
 * Basic認証用ホスト定義ファイル
 * 
 * @author Tatsuya Ide
 *
 */
@Data
public class BasicAuthenticationSetting {
	
	/**
	 * ユーザー名
	 */
	private String username;
	
	/**
	 * パスワード
	 */
	private String password;

}
