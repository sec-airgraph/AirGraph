package com.sec.rtc.entity.rts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * RTSystem設定-RTC設定-プロパティ設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

	/**
	 * name
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private String name = "";

	/**
	 * value
	 */
	@XmlAttribute(name = "value", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private String value = "";

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
	public Property(){
		
	}
}
