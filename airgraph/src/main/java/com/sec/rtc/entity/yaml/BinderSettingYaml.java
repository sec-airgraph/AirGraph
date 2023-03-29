package com.sec.rtc.entity.yaml;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Binder用定義ファイル
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class BinderSettingYaml {
	
	/**
	 * 作者
	 */
	private String author;
	
	/**
	 * E-mailアドレス
	 */
	private String email;
	
	/**
	 * URL
	 */
	private String url;
	
	/**
	 * リポジトリ定義？
	 */
	private Map<String, String[]> repositories;
	
	/**
	 * 子Binder
	 */
	private List<String> child_binder;
}
