package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * ライブラリ情報設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Library {

	/**
	 * Name
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String name = "";
	
	/**
	 * Version
	 */
	@XmlAttribute(name = "version", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String version = "";
	
	/**
	 * Info.
	 */
	@XmlAttribute(name = "other", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String other = "";
}
