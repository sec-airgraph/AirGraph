package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;

import lombok.Data;

/**
 * アクティビティ設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionStatusDoc {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcDoc:action_status_doc";
	
	/**
	 * 使用有無
	 */
	@XmlAttribute(name = "implemented", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Boolean implemented = false;
	
	/**
	 * ドキュメント
	 */
	@XmlElement(name = "Doc", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private Doc doc = new Doc(DOCUMENT_TYPE.ACTION_VAL);
}
