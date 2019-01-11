package com.sec.rtc.entity.rts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * RTSystem設定-RTC設定-サービスポート設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicePort {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:serviceport_ext";

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

	/**
	 * コンストラクタ
	 * 
	 * @param name
	 * @param visible
	 */
	public ServicePort(String name, Boolean visible) {
		this.name = name;
		this.visible = visible;
	}
	
	/**
	 * コンストラクタ
	 */
	public ServicePort(){
		
	}
}
