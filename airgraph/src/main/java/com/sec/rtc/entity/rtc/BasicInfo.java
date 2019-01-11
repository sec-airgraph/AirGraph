package com.sec.rtc.entity.rtc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.sec.airgraph.util.Const.RT_COMPONENT.ACTIVITY_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_KIND;
import com.sec.airgraph.util.Const.RT_COMPONENT.COMPONENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;
import com.sec.airgraph.util.Const.RT_COMPONENT.EXECUTION_TYPE;
import com.sec.airgraph.util.NumberUtil;
import com.sec.airgraph.util.StringUtil;

import lombok.Data;

/**
 * RTC基本設定
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BasicInfo {

	/**
	 * 種別
	 */
	@XmlAttribute(name = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
	private final String type = "rtcExt:basic_info_ext";

	/**
	 * モジュール名
	 */
	@XmlAttribute(name = "name", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String moduleName = "";

	/**
	 * モジュール概要
	 */
	@XmlAttribute(name = "description", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String moduleDescription = "";

	/**
	 * バージョン
	 */
	@XmlAttribute(name = "version", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String version = "";

	/**
	 * ベンダ名
	 */
	@XmlAttribute(name = "vendor", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String vendor = "";

	/**
	 * モジュールカテゴリ
	 */
	@XmlAttribute(name = "category", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String moduleCategory = "";

	/**
	 * コンポーネント型
	 */
	@XmlAttribute(name = "componentType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String componentType = COMPONENT_TYPE.STATIC;

	/**
	 * アクティビティ型
	 */
	@XmlAttribute(name = "activityType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String activityType = ACTIVITY_TYPE.PERIODIC;

	/**
	 * コンポーネント種類
	 */
	@XmlAttribute(name = "componentKind", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String componentKind = COMPONENT_KIND.DATA_FROW;

	/**
	 * 最大インスタンス数
	 */
	@XmlAttribute(name = "maxInstances", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Integer maxInstances = 1;

	/**
	 * 実行型
	 */
	@XmlAttribute(name = "executionType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String executionType = EXECUTION_TYPE.PERIODIC_EXECUTION_CONTEXT;

	/**
	 * 実行周期
	 */
	@XmlAttribute(name = "executionRate", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Double executionRate = 1000.0;

	/**
	 * 概要
	 */
	@XmlAttribute(name = "abstract", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String sabstract = "";

	/**
	 * RTCType
	 */
	@XmlAttribute(name = "rtcType", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String rtcType = "";

	/**
	 * 作成日時
	 */
	@XmlAttribute(name = "creationDate", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Date creationDate = new Date();

	/**
	 * 更新日時
	 */
	@XmlAttribute(name = "updateDate", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Date updateDate = new Date();

	/**
	 * プロジェクト名
	 */
	@XmlAttribute(name = "saveProject", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private String saveProject = "";

	/**
	 * ドキュメント生成
	 */
	@XmlElement(name = "Doc", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private Doc doc = new Doc(DOCUMENT_TYPE.DOCUMENT_VAL);

	/**
	 * VersionUp Log
	 */
	@XmlElement(name = "VersionUpLogs", namespace = "http://www.openrtp.org/namespaces/rtc_ext")
	private List<String> versionUpLogs = new ArrayList<String>();

	/**
	 * ModuleSpecが一致するかどうか
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalModuleSpec(BasicInfo target) {
		boolean result = true;
		result &= StringUtil.equals(this.moduleName, target.getModuleName());
		result &= StringUtil.equals(this.moduleDescription, target.getModuleDescription());
		result &= StringUtil.equals(this.version, target.getVersion());
		result &= StringUtil.equals(this.vendor, target.getVendor());
		result &= StringUtil.equals(this.moduleCategory, target.getModuleCategory());
		result &= StringUtil.equals(this.componentType, target.getComponentType());
		result &= StringUtil.equals(this.activityType, target.getActivityType());
		result &= StringUtil.equals(this.componentKind, target.getComponentKind());
		result &= NumberUtil.equals(this.maxInstances, target.getMaxInstances());

		return result;
	}
}
