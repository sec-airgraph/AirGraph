package com.sec.rtc.entity.rts;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

/**
 * RTSystem定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RtsProfile", namespace = "http://www.openrtp.org/namespaces/rts")
public class RtsProfile {

	/**
	 * ID
	 */
	@XmlAttribute(name = "id", namespace = "http://www.openrtp.org/namespaces/rts")
	private String id = "";

	/**
	 * バージョン
	 */
	@XmlAttribute(name = "version", namespace = "http://www.openrtp.org/namespaces/rts")
	private String version = "";

	/**
	 * 概要？
	 */
	@XmlAttribute(name = "abstract", namespace = "http://www.openrtp.org/namespaces/rts")
	private String sAbstract = "";

	/**
	 * 作成日時
	 */
	@XmlAttribute(name = "creationDate", namespace = "http://www.openrtp.org/namespaces/rts")
	private Date creationDate = new Date();

	/**
	 * 更新日時
	 */
	@XmlAttribute(name = "updateDate", namespace = "http://www.openrtp.org/namespaces/rts")
	private Date updateDate = new Date();

	/**
	 * RTコンポーネント情報
	 */
	@XmlElement(name = "Components", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<Component> components = new ArrayList<Component>();

	/**
	 * RTコンポーネント情報Map
	 */
	@XmlTransient
	private Map<String, List<Component>> componentMap = new HashMap<>();

	/**
	 * データポート接続情報
	 */
	@XmlElement(name = "DataPortConnectors", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<DataPortConnector> dataPortConnectors = new ArrayList<DataPortConnector>();

	/**
	 * サービスポート接続情報
	 */
	@XmlElement(name = "ServicePortConnectors", namespace = "http://www.openrtp.org/namespaces/rts")
	private List<ServicePortConnector> servicePortConnectors = new ArrayList<ServicePortConnector>();

	/**
	 * オブジェクト比較処理<br>
	 * マップ以外で比較を行う
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RtsProfile other = (RtsProfile) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (dataPortConnectors == null) {
			if (other.dataPortConnectors != null)
				return false;
		} else if (!dataPortConnectors.equals(other.dataPortConnectors))
			return false;
		if (servicePortConnectors == null) {
			if (other.servicePortConnectors != null)
				return false;
		} else if (!servicePortConnectors.equals(other.servicePortConnectors))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (sAbstract == null) {
			if (other.sAbstract != null)
				return false;
		} else if (!sAbstract.equals(other.sAbstract))
			return false;
		if (updateDate == null) {
			if (other.updateDate != null)
				return false;
		} else if (!updateDate.equals(other.updateDate))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((components == null) ? 0 : components.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((dataPortConnectors == null) ? 0 : dataPortConnectors.hashCode());
		result = prime * result + ((servicePortConnectors == null) ? 0 : servicePortConnectors.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((sAbstract == null) ? 0 : sAbstract.hashCode());
		result = prime * result + ((updateDate == null) ? 0 : updateDate.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
}
