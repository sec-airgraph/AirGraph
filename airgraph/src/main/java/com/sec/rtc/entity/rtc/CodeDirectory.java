package com.sec.rtc.entity.rtc;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * ソースコードのディレクトリ管理用クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class CodeDirectory {

	/**
	 * ディレクトリ名称
	 */
	private String curDirName = "";
	
	/**
	 * ディレクトリ絶対パス
	 */
	private String dirPath = "";

	/**
	 * ディレクトリに存在するファイル名とファイルの絶対パスのMAP
	 */
	private Map<String, String> codePathMap = new HashMap<>();

	/**
	 * ディレクトリに存在するファイル名と最終更新日時の絶対パスのMAP
	 */
	private Map<String, String> lastModifiedMap = new HashMap<>();

	/**
	 * ディレクトリに存在する下位ディレクトリのMAP
	 */
	private Map<String, CodeDirectory> directoryMap = new HashMap<>();
}
