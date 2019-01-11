package com.sec.keras.entity.field;

import java.io.Serializable;
import java.util.List;

import com.sec.keras.entity.model.KerasModel;

import lombok.Data;

/**
 * Kerasネットワーク領域タブ情報クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class KerasTabInfo implements Serializable {
	private static final long serialVersionUID = -1319719666437061234L;

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
	private List<KerasTabInfo> childTabs;

	/**
	 * タブ内に属するlayer
	 */
	private List<String> layers;

	/**
	 * タブ内に即するNetwork
	 */
	private List<KerasModel> models;

}
