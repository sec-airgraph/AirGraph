package com.sec.rtc.entity.rts;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

/**
 * 実行コンテキスト
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionContext {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:execution_context_ext";
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "id", namespace = "http://www.openrtp.org/namespaces/rts")
	private String id = "";
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "kind", namespace = "http://www.openrtp.org/namespaces/rts")
	private String kind = "PERIODIC";
	
	/**
	 * 周期
	 */
	@XmlAttribute(name = "rate", namespace = "http://www.openrtp.org/namespaces/rts")
	private Double rate = 1000.0;
}
