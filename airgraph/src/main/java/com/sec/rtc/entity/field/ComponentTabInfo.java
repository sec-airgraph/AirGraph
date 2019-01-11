package com.sec.rtc.entity.field;

import java.io.Serializable;
import java.util.List;

import com.sec.rtc.entity.rtc.Rtc;
import com.sec.rtc.entity.rts.Rts;

import lombok.Data;

/**
 * コンポーネント領域タブ情報クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class ComponentTabInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7322741181130923139L;

	/**
	 * タブ名称
	 */
	private String tabName;
	
	/**
	 * Webサーバ上のディレクトリパス
	 */
	private String srvPath;
	
	/**
	 * 子タブ
	 */
	private List<ComponentTabInfo> childTabs;
	
	/**
	 * タブ内に属するRTC
	 */
	private List<Rtc> rtcs;
	
	/**
	 * タブ内に即するRTS
	 */
	private List<Rts> rtss;
}
