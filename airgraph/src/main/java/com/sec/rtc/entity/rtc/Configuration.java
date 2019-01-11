package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.WIDGET_TYPE;

import lombok.Data;

/**
 * コンフィギュレーション設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcExt:configuration_ext";
	
	/**
	 * パラメータ名
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String name = "";
	
	/**
	 * デフォルト値
	 */
	@XmlAttribute(name = "defaultValue", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String defaultValue = "";
	
	/**
	 * データ型
	 */
	@XmlAttribute(name = "type", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String dataType = "";
	
	/**
	 * 変数名
	 */
	@XmlAttribute(name = "variableName", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String variableName = "";
	
	/**
	 * 単位
	 */
	@XmlAttribute(name = "unit", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String unit = "";
	
	/**
	 * 制約条件
	 */
	@XmlElement(name = "Constraint", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Constraint constraint = new Constraint();
	
	/**
	 * Widget,Step
	 */
	@XmlElement(name = "Properties", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private Property properties = new Property(WIDGET_TYPE.TEXT, "__widget__");
	
	/**
	 * ドキュメント
	 */
	@XmlElement(name = "Doc", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private Doc doc = new Doc(DOCUMENT_TYPE.CONFIGURATION_VAL);
	
	/****************************************
	 * IDE独自プロパティ
	 ****************************************/
	/**
	 * コンフィギュレーション設定が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsConfiguration(Configuration target) {
		boolean result = true;
		result &= StringUtil.equals(this.name, target.getName());
		result &= StringUtil.equals(this.defaultValue, target.getDefaultValue());
		return result;
	}
}
