package com.sec.rtc.entity.rts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * コンフィギュレーションデータ設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationData {

	
	/**
	 * data
	 */
	@XmlAttribute(name = "data", namespace = "http://www.openrtp.org/namespaces/rts")
	private String data = "";
	
	/**
	 * name
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rts")
	private String name = "";
}
