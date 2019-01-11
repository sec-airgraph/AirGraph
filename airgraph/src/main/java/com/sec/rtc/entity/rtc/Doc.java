package com.sec.rtc.entity.rtc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.sec.airgraph.util.Const.RT_COMPONENT.DOCUMENT_TYPE;

import lombok.Data;

/**
 * ドキュメント定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Doc {

	/****************************************
	 * 共通
	 ****************************************/
	/**
	 * ドキュメント種別
	 */
	@XmlTransient
	private Integer docType = DOCUMENT_TYPE.DOCUMENT_VAL;
	
	/**
	 * ドキュメント　　　　　　：概要説明<br>
	 * アクション　　　　　　　：動作説明<br>
	 * データポート　　　　　　：概要説明<br>
	 * サービスポート　　　　　：概要説明<br>
	 * サービスインタフェース　：概要説明<br>
	 * コンフィギュレーション　：概要説明
	 */
	@XmlAttribute(name = "description", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String description;

	/**
	 * データポート ：単位<br>
	 * コンフィギュレーション：単位
	 */
	@XmlAttribute(name = "unit", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String unit;

	/****************************************
	 * ドキュメント生成
	 ****************************************/
	/**
	 * 入出力
	 */
	@XmlAttribute(name = "inout", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String inout;

	/**
	 * アルゴリズムなど
	 */
	@XmlAttribute(name = "algorithm", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String algorithm;

	/**
	 * 作成者・連絡先
	 */
	@XmlAttribute(name = "creator", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String creator;

	/**
	 * ライセンス，使用条件
	 */
	@XmlAttribute(name = "license", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String license;

	/**
	 * 参考文献
	 */
	@XmlAttribute(name = "reference", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String reference;

	/****************************************
	 * アクション設定
	 ****************************************/
	/**
	 * 事前条件
	 */
	@XmlAttribute(name = "preCondition", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String preCondition;

	/**
	 * 事後条件
	 */
	@XmlAttribute(name = "postCondition", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String postCondition;

	/****************************************
	 * データポート設定
	 ****************************************/
	/**
	 * データ型
	 */
	@XmlAttribute(name = "type", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String type;

	/**
	 * データ数
	 */
	@XmlAttribute(name = "number", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String number;

	/**
	 * 意味
	 */
	@XmlAttribute(name = "semantics", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String semantics;

	/**
	 * 発生頻度，周期
	 */
	@XmlAttribute(name = "occerrnce", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String occerrnce;

	/**
	 * 処理頻度，周期
	 */
	@XmlAttribute(name = "operation", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String operation;

	/****************************************
	 * サービスポート設定
	 ****************************************/
	/**
	 * I/F概要説明
	 */
	@XmlAttribute(name = "ifdescription", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String ifdescription;

	/****************************************
	 * サービスインタフェース設定
	 ****************************************/
	
	/**
	 * 引数
	 */
	@XmlAttribute(name = "docArgument", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String docArgument;
	
	/**
	 * 戻り値
	 */
	@XmlAttribute(name = "docReturn", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String docReturn;
	
	/**
	 * 例外
	 */
	@XmlAttribute(name = "docException", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String docException;
	
	/**
	 * 事前条件
	 */
	@XmlAttribute(name = "docPreCondition", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String docPreCondition;
	
	/**
	 * 事後条件
	 */
	@XmlAttribute(name = "docPostCondition", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String docPostCondition;

	/****************************************
	 * コンフィギュレーション設定
	 ****************************************/

	/**
	 * データ名
	 */
	@XmlAttribute(name = "dataname", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String dataname;

	/**
	 * デフォルト値
	 */
	@XmlAttribute(name = "dafaultValue", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String dafaultValue;

	/**
	 * データ範囲
	 */
	@XmlAttribute(name = "range", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String range;

	/**
	 * 制約条件
	 */
	@XmlAttribute(name = "constraint", namespace = "http://www.openrtp.org/namespaces/rtc_doc")
	private String constraint;

	/****************************************
	 * 初期化処理
	 ****************************************/
	/**
	 * コンストラクタ
	 */
	public Doc() {
		this(DOCUMENT_TYPE.DOCUMENT_VAL);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param docType
	 */
	public Doc(int docType) {
		// 初期化する
		this.docType = docType;
		
		switch (docType) {
		case DOCUMENT_TYPE.DOCUMENT_VAL:
			initDoc();
			break;
		case DOCUMENT_TYPE.ACTION_VAL:
			initDocAction();
			break;
		case DOCUMENT_TYPE.DATA_PORT_VAL:
			initDocDataPort();
			break;
		case DOCUMENT_TYPE.SERVICE_PORT_VAL:
			initDocServicePort();
			break;
		case DOCUMENT_TYPE.SERVICE_INTERFACE_VAL:
			initDocServiceInterface();
			break;
		case DOCUMENT_TYPE.CONFIGURATION_VAL:
			initDocConfiguration();
			break;
		default:
			// NOP
			break;
		}
	}

	/**
	 * ドキュメント生成用初期化
	 */
	private void initDoc() {
		this.description = "";
		this.inout = "";
		this.algorithm = "";
		this.creator = "";
		this.license = "";
		this.reference = "";
	}

	/**
	 * アクション設定用初期化
	 */
	private void initDocAction() {
		this.description = "";
		this.preCondition = "";
		this.postCondition = "";

	}

	/**
	 * データポート設定用初期化
	 */
	private void initDocDataPort() {
		this.description = "";
		this.unit = "";
		this.type = "";
		this.number = "";
		this.semantics = "";
		this.occerrnce = "";
		this.operation = "";
	}

	/**
	 * サービスポート設定用初期化
	 */
	private void initDocServicePort() {
		this.description = "";
		this.ifdescription = "";
	}

	/**
	 * サービスポート設定用初期化
	 */
	private void initDocServiceInterface() {
		this.description = "";
		this.docArgument = "";
		this.docReturn = "";
		this.docException = "";
		this.docPreCondition = "";
		this.docPostCondition = "";
	}

	/**
	 * コンフィギュレーション用初期化
	 */
	private void initDocConfiguration() {
		this.description = "";
		this.unit = "";
		this.dataname = "";
		this.dafaultValue = "";
		this.range = "";
		this.constraint = "";
	}
}
