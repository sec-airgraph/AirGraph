package com.sec.rtc.entity.yaml;

import lombok.Data;

/**
 * GitHub用定義ファイル
 * 
 * @author Tatsuya Ide
 *
 */

@Data
public class GitHubSetting {

	/**
	 * GitHubユーザー名
	 */
    private String username;
    
	/**
	 * GitHubトークン
	 */
    private String token;
    
}