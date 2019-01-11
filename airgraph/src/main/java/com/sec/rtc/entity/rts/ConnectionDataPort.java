package com.sec.rtc.entity.rts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * RTSystem上のデータポート接続情報のポート定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionDataPort {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:target_port_ext";
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "componentId", namespace = "http://www.openrtp.org/namespaces/rts")
	private String componentId = "";
	
	/**
	 * 名称
	 */
	@XmlAttribute(name = "portName", namespace = "http://www.openrtp.org/namespaces/rts")
	private String portName = "";
	
	/**
	 * インスタンス名
	 */
	@XmlAttribute(name = "instanceName", namespace = "http://www.openrtp.org/namespaces/rts")
	private String instanceName = "";
	
	/**
	 * プロパティ設定
	 */
	@XmlElement(name = "Properties", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private List<Property> properties = new ArrayList<Property>();

}
