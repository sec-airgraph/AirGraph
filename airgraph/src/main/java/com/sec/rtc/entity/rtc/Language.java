package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.sec.airgraph.util.Const.RT_COMPONENT.LANGUAGE_KIND;

import lombok.Data;

/**
 * 言語・環境設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Language {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcExt:language_ext";

	/**
	 * 言語種別
	 */
	@XmlAttribute(name = "kind", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String kind = LANGUAGE_KIND.CPP;
}
