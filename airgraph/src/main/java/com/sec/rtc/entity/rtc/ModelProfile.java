package com.sec.rtc.entity.rtc;

import lombok.Data;

/**
 * RTC描画モデル用定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class ModelProfile {

	/**
	 * モデル用ID
	 */
	private String modelId = "";
	
	/**
	 * モデル用名称
	 */
	private String modelName = "";
	
	/**
	 * Gitの名称（WasanbonでCloneするための名称）
	 */
	private String gitName = "";
	
	/**
	 * GitのURL
	 */
	private String remoteUrl;
	
	/**
	 * Cloneされているディレクトリ
	 */
	private String clonedDirectory;
}
