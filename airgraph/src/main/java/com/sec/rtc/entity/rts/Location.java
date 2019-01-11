package com.sec.rtc.entity.rts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * RTSystem設定-RTC設定-位置設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {

	/**
	 * 向き
	 */
	@XmlAttribute(name = "direction", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private String direction = "DOWN";
	
	/**
	 * 幅
	 */
	@XmlAttribute(name = "width", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Integer width = 0;
	
	/**
	 * 高さ
	 */
	@XmlAttribute(name = "height", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Integer height = 0;
	
	/**
	 * X座標
	 */
	@XmlAttribute(name = "x", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Integer x = 0;
	
	/**
	 * Y座標
	 */
	@XmlAttribute(name = "y", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Integer y = 0;
}	
