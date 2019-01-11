package com.sec.rtc.entity.rts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * コンフィギュレーション設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationSet {

	/**
	 * ID
	 */
	@XmlAttribute(name = "id", namespace = "http://www.openrtp.org/namespaces/rts")
	private String id = "";

	/**
	 * コンフィギュレーションデータ設定
	 */
	@XmlElement(name = "ConfigurationData", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<ConfigurationData> configurationDatas = new ArrayList<ConfigurationData>();
}
