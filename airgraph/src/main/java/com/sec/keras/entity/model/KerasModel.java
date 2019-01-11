package com.sec.keras.entity.model;

import lombok.Data;

/**
 * Model定義
 */
@Data
public class KerasModel {
	/*
	 * JSON文字列
	 */
	private String jsonString;
	/*
	 * モデル名（ファイル名）
	 */
	private String modelName;
	/**
	 * data_maker.py
	 */
	private String dataMakerStr;
	
	/**
	 * データセット名
	 */
	private String dataset;
}
