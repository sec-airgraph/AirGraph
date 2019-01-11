package com.sec.rtc.entity.rts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

/**
 * RTSystem設定-RTC設定-データポート設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPort {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:dataport_ext";

	/**
	 * name
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rts")
	private String name = "";

	/**
	 * visible
	 */
	@XmlAttribute(name = "visible", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Boolean visible = true;
	
	/****************************************
	 * IDE独自プロパティ
	 ****************************************/

	/**
	 * logging
	 */
	// @XmlAttribute(name = "logging", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	@XmlTransient
	private Boolean logging = false;

	/**
	 * logVisible
	 */
	//@XmlAttribute(name = "loggerVisible", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	@XmlTransient
	private Boolean loggerVisible = false;
	
	/**
	 * コンストラクタ
	 * 
	 * @param name
	 * @param visible
	 */
	public DataPort(String name, Boolean visible) {
		this.name = name;
		this.visible = visible;
	}

	/**
	 * コンストラクタ
	 */
	public DataPort() {

	}
}
