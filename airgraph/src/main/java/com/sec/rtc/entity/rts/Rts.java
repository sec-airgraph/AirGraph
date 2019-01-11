package com.sec.rtc.entity.rts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sec.rtc.entity.rtc.ModelProfile;
import com.sec.rtc.entity.rtc.Rtc;

import lombok.Data;

/**
 * RTSystem定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class Rts {

	/**
	 * DefaultSystem.xml定義
	 */
	private RtsProfile rtsProfile = new RtsProfile();
	
	/**
	 * 描画用定義
	 */
	private ModelProfile modelProfile = new ModelProfile();

	/**
	 * rtc.conf定義
	 */
	private List<Rtc> rtcs = new ArrayList<Rtc>();

	/**
	 * rtc_cpp.conf定義
	 */
	private String rtcCppConf = "";

	/**
	 * rtc_java.conf定義
	 */
	private String rtcJavaConf = "";

	/**
	 * rtc_py.conf定義
	 */
	private String rtcPythonConf = "";
	
	/**
	 * 編集したソースコード
	 */
	private Map<String, String> editSourceCode = new HashMap<>();
	
	/**
	 * 削除するファイルパス
	 */
	private List<String> deleteFileList = new ArrayList<>();
}
