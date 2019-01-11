package com.sec.rtc.entity.rts;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_CONNECTOR_TYPE;

import lombok.Data;

/**
 * RTSystem上のRTC定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Component {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtsExt:component_ext";
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "id", namespace = "http://www.openrtp.org/namespaces/rts")
	private String id = "";
	
	/**
	 * インスタンス名
	 */
	@XmlAttribute(name = "instanceName", namespace = "http://www.openrtp.org/namespaces/rts")
	private String instanceName = "";
	
	/**
	 * Path,Url
	 */
	@XmlAttribute(name = "pathUri", namespace = "http://www.openrtp.org/namespaces/rts")
	private String pathUri = "";
	
	/**
	 * compositeType
	 */
	@XmlAttribute(name = "compositeType", namespace = "http://www.openrtp.org/namespaces/rts")
	private String compositeType = "";

	/**
	 * isRequired
	 */
	@XmlAttribute(name = "isRequired", namespace = "http://www.openrtp.org/namespaces/rts")
	private Boolean isRequired = true;
	
	/**
	 * activeConfigurationSet
	 */
	@XmlAttribute(name = "activeConfigurationSet", namespace = "http://www.openrtp.org/namespaces/rts")
	private String activeConfigurationSet = null;
	
	/**
	 * visible
	 */
	@XmlAttribute(name = "visible", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Boolean visible = true;
	
	/**
	 * データポート
	 */
	@XmlElement(name = "DataPorts", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<DataPort> dataPorts = new ArrayList<DataPort>();
	
	/**
	 * サービスポート
	 */
	@XmlElement(name = "ServicePorts", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<ServicePort> servicePorts = new ArrayList<ServicePort>();
	
	/**
	 * コンフィギュレーション設定
	 */
	@XmlElement(name = "ConfigurationSets", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<ConfigurationSet> configurationSets = new ArrayList<ConfigurationSet>();

	/**
	 * コンフィギュレーション設定Map
	 */
	@XmlTransient
	private Map<String, List<ConfigurationSet>> configurationSetMap = new HashMap<>();
	
	/**
	 * 実行コンテキスト設定
	 */
	@XmlElement(name = "ExecutionContexts", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<ExecutionContext> ExecutionContexts = new ArrayList<ExecutionContext>();
	
	/**
	 * 位置設定
	 */
	@XmlElement(name = "Location", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	private Location location = new Location();
	
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
	// @XmlAttribute(name = "componentType", namespace = "http://www.openrtp.org/namespaces/rts_ext")
	@XmlTransient
	private String componentType = COMPONENT_CONNECTOR_TYPE.COMPONENT;
	
	/**
	 * 編集用activeConfigurationSet
	 */
	@XmlTransient
	private String editActiveConfigurationSet = null;

	/**
	 * 編集用コンフィギュレーション設定Map
	 */
	@XmlTransient
	private Map<String, List<ConfigurationSet>> editConfigurationSetMap = new HashMap<>();

	/**
	 * オブジェクト比較処理<br>
	 * 編集用以外で比較を行う
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Component other = (Component) obj;
		if (ExecutionContexts == null) {
			if (other.ExecutionContexts != null)
				return false;
		} else if (!ExecutionContexts.equals(other.ExecutionContexts))
			return false;
		if (activeConfigurationSet == null) {
			if (other.activeConfigurationSet != null)
				return false;
		} else if (!activeConfigurationSet.equals(other.activeConfigurationSet))
			return false;
		if (componentType == null) {
			if (other.componentType != null)
				return false;
		} else if (!componentType.equals(other.componentType))
			return false;
		if (compositeType == null) {
			if (other.compositeType != null)
				return false;
		} else if (!compositeType.equals(other.compositeType))
			return false;
		if (configurationSetMap == null) {
			if (other.configurationSetMap != null)
				return false;
		} else if (!configurationSetMap.equals(other.configurationSetMap))
			return false;
		if (configurationSets == null) {
			if (other.configurationSets != null)
				return false;
		} else if (!configurationSets.equals(other.configurationSets))
			return false;
		if (dataPorts == null) {
			if (other.dataPorts != null)
				return false;
		} else if (!dataPorts.equals(other.dataPorts))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (instanceName == null) {
			if (other.instanceName != null)
				return false;
		} else if (!instanceName.equals(other.instanceName))
			return false;
		if (isRequired == null) {
			if (other.isRequired != null)
				return false;
		} else if (!isRequired.equals(other.isRequired))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (pathUri == null) {
			if (other.pathUri != null)
				return false;
		} else if (!pathUri.equals(other.pathUri))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (servicePorts == null) {
			if (other.servicePorts != null)
				return false;
		} else if (!servicePorts.equals(other.servicePorts))
			return false;
		if (visible == null) {
			if (other.visible != null)
				return false;
		} else if (!visible.equals(other.visible))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ExecutionContexts == null) ? 0 : ExecutionContexts.hashCode());
		result = prime * result + ((activeConfigurationSet == null) ? 0 : activeConfigurationSet.hashCode());
		result = prime * result + ((componentType == null) ? 0 : componentType.hashCode());
		result = prime * result + ((compositeType == null) ? 0 : compositeType.hashCode());
		result = prime * result + ((configurationSetMap == null) ? 0 : configurationSetMap.hashCode());
		result = prime * result + ((configurationSets == null) ? 0 : configurationSets.hashCode());
		result = prime * result + ((dataPorts == null) ? 0 : dataPorts.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
		result = prime * result + ((isRequired == null) ? 0 : isRequired.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((pathUri == null) ? 0 : pathUri.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((servicePorts == null) ? 0 : servicePorts.hashCode());
		result = prime * result + ((visible == null) ? 0 : visible.hashCode());
		return result;
	}
	
	
}
