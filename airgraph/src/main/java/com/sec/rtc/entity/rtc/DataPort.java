package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_DATA_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_POSITION;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_TYPE;
import com.sec.airgraph.util.StringUtil;

import lombok.Data;

/**
 * データポート設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPort {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcExt:dataport_ext";

	/**
	 * ポート名称
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String name = "";

	/**
	 * 入出力区分
	 */
	@XmlAttribute(name = "portType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String portType = PORT_TYPE.IN;

	/**
	 * データ型
	 */
	@XmlAttribute(name = "type", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String dataType = "RTC::" + PORT_DATA_TYPE.RTC.TIMED_LONG;

	/**
	 * 変数名
	 */
	@XmlAttribute(name = "variableName", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String variableName = "";

	/**
	 * 表示位置
	 */
	@XmlAttribute(name = "position", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String position = PORT_POSITION.LEFT;

	/**
	 * 未実装
	 */
	@XmlAttribute(name = "subscriptionType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String subscriptionType = "";

	/**
	 * 未実装
	 */
	@XmlAttribute(name = "dataflowType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String dataflowType = "";

	/**
	 * 未実装
	 */
	@XmlAttribute(name = "interfaceType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String interfaceType = "";

	/**
	 * 未実装
	 */
	@XmlAttribute(name = "idlFile", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String idlFile = "";

	/**
	 * ドキュメント
	 */
	@XmlElement(name = "Doc", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private Doc doc = new Doc(DOCUMENT_TYPE.DATA_PORT_VAL);

	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	/**
	 * データポート宣言が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsDataportDeclare(DataPort target) {
		boolean result = true;
		result &= StringUtil.equals(this.name, target.getName());
		result &= StringUtil.equals(this.portType, target.getPortType());
		result &= StringUtil.equals(this.variableName, target.getVariableName());
		result &= StringUtil.equals(this.dataType, target.getDataType());
		return result;
	}
}
