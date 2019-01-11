package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.INTERFACE_DIRECTION;

import lombok.Data;

/**
 * サービスインタフェース設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInterface {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcExt:serviceinterface_ext";

	/**
	 * インタフェース名称
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String name = "";

	/**
	 * 方向
	 */
	@XmlAttribute(name = "direction", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String direction = INTERFACE_DIRECTION.PROVIDED;
	
	/**
	 * インスタンス名
	 */
	@XmlAttribute(name = "instanceName", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String instanceName = "";
	
	/**
	 * 変数名
	 */
	@XmlAttribute(name = "variableName", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String variableName = "";
	
	/**
	 * IDLファイル
	 */
	@XmlAttribute(name = "idlFile", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String idlFile = "";

	/**
	 * インタフェース型
	 */
	@XmlAttribute(name = "type", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String interfaceType = "";

	/**
	 * IDLパス
	 */
	@XmlAttribute(name = "path", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String path = "";
	
	/**
	 * ドキュメント
	 */
	@XmlElement(name = "Doc", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private Doc doc = new Doc(DOCUMENT_TYPE.SERVICE_INTERFACE_VAL);

	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	/**
	 * サービスインタフェース宣言が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsServiceInterfaceDeclare(ServiceInterface target) {
		boolean result = true;
		result &= StringUtil.equals(this.name, target.getName());
		result &= StringUtil.equals(this.direction, target.getDirection());
		result &= StringUtil.equals(this.instanceName, target.getInstanceName());
		result &= StringUtil.equals(this.variableName, target.getVariableName());
		result &= StringUtil.equals(this.interfaceType, target.getInterfaceType());
		return result;
	}

}
