package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * コンフィギュレーション設定-制約条件設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConstraintUnitType {

	/**
	 * コンフィギュレーション設定-制約条件設定
	 */
	@XmlElement(name = "propertyIsEqualTo", namespace = "http://www.openrtp.org/namespaces/rtc")
	private PropertyIsEqualTo propertyIsEqualTo = new PropertyIsEqualTo();
}
