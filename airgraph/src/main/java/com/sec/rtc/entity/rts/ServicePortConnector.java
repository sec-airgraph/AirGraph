package com.sec.rtc.entity.rts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_CONNECTOR_TYPE;

import lombok.Data;

/**
 * RTSystem上のサービスポート接続情報定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicePortConnector {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:serviceport_connector_ext";
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "connectorId", namespace = "http://www.openrtp.org/namespaces/rts")
	private String connectorId = "";
	
	/**
	 * 名称
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rts")
	private String name = "";
	
	/**
	 * visible
	 */
	@XmlAttribute(name = "visible", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Boolean visible = true;

	/**
	 * 接続元ポート
	 */
	@XmlElement(name = "sourceServicePort", namespace = "http://www.openrtp.org/namespaces/rts")
	private ConnectionServicePort sourceServicePort = new ConnectionServicePort();

	/**
	 * 接続先ポート
	 */
	@XmlElement(name = "targetServicePort", namespace = "http://www.openrtp.org/namespaces/rts")
	private ConnectionServicePort targetServicePort = new ConnectionServicePort();
	
	/**
	 * プロパティ設定
	 */
	@XmlElement(name = "Properties", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private List<Property> properties = new ArrayList<Property>();
	
	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	
	/**
	 * コンポーネント・ロガー種別
	 */
	// @XmlAttribute(name = "connectorType", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	@XmlTransient
	private String connectorType = COMPONENT_CONNECTOR_TYPE.CONNECTOR;
}
