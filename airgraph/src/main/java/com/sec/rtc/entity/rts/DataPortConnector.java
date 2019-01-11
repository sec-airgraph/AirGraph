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
 * RTSystem上のデータポート接続情報定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPortConnector {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:dataport_connector_ext";
	
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
	 * dataType
	 */
	@XmlAttribute(name = "dataType", namespace = "http://www.openrtp.org/namespaces/rts")
	private String dataType = "";
	
	/**
	 * dataflowType
	 */
	@XmlAttribute(name = "dataflowType", namespace = "http://www.openrtp.org/namespaces/rts")
	private String dataflowType = "";
	
	/**
	 * interfaceType
	 */
	@XmlAttribute(name = "interfaceType", namespace = "http://www.openrtp.org/namespaces/rts")
	private String interfaceType = "";
	
	/**
	 * interfaceType
	 */
	@XmlAttribute(name = "pushInterval", namespace = "http://www.openrtp.org/namespaces/rts")
	private Double pushInterval = 0.0;
	
	/**
	 * interfaceType
	 */
	@XmlAttribute(name = "subscriptionType", namespace = "http://www.openrtp.org/namespaces/rts")
	private String subscriptionType = "";
	
	/**
	 * visible
	 */
	@XmlAttribute(name = "visible", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Boolean visible = true;

	/**
	 * 接続元ポート
	 */
	@XmlElement(name = "sourceDataPort", namespace = "http://www.openrtp.org/namespaces/rts")
	private ConnectionDataPort sourceDataPort = new ConnectionDataPort();

	/**
	 * 接続先ポート
	 */
	@XmlElement(name = "targetDataPort", namespace = "http://www.openrtp.org/namespaces/rts")
	private ConnectionDataPort targetDataPort = new ConnectionDataPort();
	
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
