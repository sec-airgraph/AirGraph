package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * コンフィギュレーション設定-制約条件設定-パラメタ設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

	/**
	 * value
	 */
	@XmlAttribute(name = "value", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String value = "";

	/**
	 * name
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String name = "";

	/**
	 * コンストラクタ
	 * 
	 * @param value
	 * @param name
	 */
	public Property(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	/**
	 * コンストラクタ
	 */
	public Property() {
		
	}
}
