package com.sec.rtc.entity.rtc;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * RTComponent定義
 */
@Data
public class Rtc {

	/**
	 * RTC.xml定義
	 */
	private RtcProfile rtcProfile = new RtcProfile();
	
	/**
	 * 描画用定義
	 */
	private ModelProfile modelProfile = new ModelProfile();

	/**
	 * ソースコードディレクトリ管理
	 */
	private CodeDirectory codeDirectory = new CodeDirectory();
	
	/*:
	 * ソースコード絶対パス内容MAP
	 */
	private Map<String, String> pathContentMap = new HashMap<>();
	
	/**
	 * 新規作成時の作業領域名
	 */
	private String workspaceName = "";
}
