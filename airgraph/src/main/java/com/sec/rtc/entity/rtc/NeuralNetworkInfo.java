package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * DNN関連クラス
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class NeuralNetworkInfo {
	/**
	 * モデル名
	 */
	@XmlAttribute(name = "ModelName", namespace = "http://www.sec.co.jp/namespaces/ide_ext")
	private String modelName = "";
	
	/**
	 * データセット名
	 */
	@XmlAttribute(name = "DatasetName", namespace = "http://www.sec.co.jp/namespaces/ide_ext")
	private String datasetName = ""; 
}
