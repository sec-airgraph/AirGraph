package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * コンフィギュレーション設定-パラメタ設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {

	/**
	 * デフォルト値
	 */
	@XmlAttribute(name = "defaultValue", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String defaultValue = "";

	/**
	 * confguration
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String name = "";

	/**
	 * コンストラクタ
	 * 
	 * @param defaultValue
	 * @param name
	 */
	public Parameter(String defaultValue, String name) {
		this.defaultValue = defaultValue;
		this.name = name;
	}
}
