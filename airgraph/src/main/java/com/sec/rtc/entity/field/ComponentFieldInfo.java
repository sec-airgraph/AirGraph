package com.sec.rtc.entity.field;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * コンポーネント領域情報クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class ComponentFieldInfo implements Serializable {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1915325963557023880L;

	/**
	 * コンポーネントタブ情報
	 */
	private List<ComponentTabInfo> componentTabs;
}
