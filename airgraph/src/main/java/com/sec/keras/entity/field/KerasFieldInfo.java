package com.sec.keras.entity.field;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Kerasネットワーク領域情報クラス
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
public class KerasFieldInfo implements Serializable {
	private static final long serialVersionUID = 7026122378983377285L;

	/**
	 * Kerasタブ情報
	 */
	private List<KerasTabInfo> kerasTabs;

}
