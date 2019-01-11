package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * プロパティ一致設定？
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyIsEqualTo {

	/**
	 * プロパティ一致フラグ？
	 */
	@XmlAttribute(name = "matchCase", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Boolean matchCase = false;
	
	/**
	 * 制約条件設定値
	 */
	@XmlElement(name = "Literal", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String literal = "";
}
