package com.sec.rtc.entity.rtc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sec.airgraph.util.CollectionUtil;
import com.sec.airgraph.util.NumberUtil;
import com.sec.airgraph.util.StringUtil;
import com.sec.airgraph.util.Const.RT_COMPONENT.PORT_TYPE;

import lombok.Data;

/**
 * RTC.xml定義
 * 
 * @author Tsuyoshi Hirose
 *
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RtcProfile", namespace = "http://www.openrtp.org/namespaces/rtc")
public class RtcProfile {
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "id", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String id = "";

	/**
	 * バージョン
	 */
	@XmlAttribute(name = "version", namespace = "http://www.openrtp.org/namespaces/rtc")
	private String version = "0.2";

	/**
	 * 基本設定,ドキュメント設定
	 */
	@XmlElement(name = "BasicInfo", namespace = "http://www.openrtp.org/namespaces/rtc")
	private BasicInfo basicInfo = new BasicInfo();

	/**
	 * アクション設定
	 */
	@XmlElement(name = "Actions", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Actions actions = new Actions();

	/**
	 * データポート設定
	 */
	@XmlElement(name = "DataPorts", namespace = "http://www.openrtp.org/namespaces/rtc")
	private List<DataPort> dataPorts = new ArrayList<DataPort>();
	
	/**
	 * サービスポート設定
	 */
	@XmlElement(name = "ServicePorts", namespace = "http://www.openrtp.org/namespaces/rtc")
	private List<ServicePort> servicePorts = new ArrayList<ServicePort>();

	/**
	 * コンフィギュレーション設定
	 */
	@XmlElement(name = "ConfigurationSet", namespace = "http://www.openrtp.org/namespaces/rtc")
	private ConfigurationSet configurationSet = new ConfigurationSet();
	
	/**
	 * コンフィギュレーション設定-パラメタ設定
	 */
	@XmlElement(name = "Parameters", namespace = "http://www.openrtp.org/namespaces/rtc")
	private List<Parameter> parameters = new ArrayList<Parameter>(); 
	
	/**
	 * 言語・環境設定
	 */
	@XmlElement(name = "Language", namespace = "http://www.openrtp.org/namespaces/rtc")
	private Language language = new Language();

	/****************************************
	 * IDE独自プロパティ
	 * DNN関連
	 ****************************************/
	/**
	 * NN設定
	 */
	@XmlElement(name = "NeuralNetworkInfo", namespace = "http://www.sec.co.jp/namespaces/ide_ext")
	private NeuralNetworkInfo neuralNetworkInfo = new NeuralNetworkInfo();
	
	/**
	 * NN設定が一致するかを判定する
	 * 
	 * @param target
	 * @return
	 */
	public boolean equalsNeuralNetworkInfo(NeuralNetworkInfo target) {
		boolean result = true;
		if (neuralNetworkInfo != null && target != null) {
			result &= StringUtil.equals(neuralNetworkInfo.getModelName(), target.getModelName());
			result &= StringUtil.equals(neuralNetworkInfo.getDatasetName(), target.getDatasetName());
		} else if (neuralNetworkInfo == null && target == null) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}
	
	/****************************************
	 * IDE独自プロパティ
	 * データポート関連
	 ****************************************/
	/**
	 * データインポート設定
	 */
	public List<DataPort> getDataInPorts() {
		return CollectionUtil.filter(this.dataPorts, data -> PORT_TYPE.IN.equals(data.getPortType()));
	}

	/**
	 * データアウトポート設定
	 */
	public List<DataPort> getDataOutPorts() {
		return CollectionUtil.filter(this.dataPorts, data -> PORT_TYPE.OUT.equals(data.getPortType()));
	}
	
	/**
	 * データインポート宣言が全て一致するかを判定する
	 * 
	 * @param targetList
	 * @return
	 */
	public boolean equalsInportDeclares(RtcProfile target) {
		List<DataPort> orgList = this.getDataInPorts();
		List<DataPort> newList = target.getDataInPorts();
		return equalsDataportDeclares(orgList, newList);
	}
	
	/**
	 * データアウトポート宣言が全て一致するかを判定する
	 * 
	 * @param targetList
	 * @return
	 */
	public boolean equalsOutportDeclares(RtcProfile target) {
		List<DataPort> orgList = this.getDataOutPorts();
		List<DataPort> newList = target.getDataOutPorts();
		return equalsDataportDeclares(orgList, newList);
	}
	
	/**
	 * データポート宣言が全て一致するかを判定する
	 * @param orgList
	 * @param newList
	 * @return
	 */
	private boolean equalsDataportDeclares(List<DataPort> orgList, List<DataPort> newList) {
		boolean result = true;
		if (CollectionUtil.isNotEmpty(orgList) && CollectionUtil.isNotEmpty(newList)) {
			// 両方存在する
			if (NumberUtil.equals(orgList.size(), newList.size())) {
				// 件数が等しい
				for (int i = 0; i < orgList.size(); i++) {
					DataPort orgPort = orgList.get(i);
					DataPort newPort = newList.get(i);
					result &= orgPort.equalsDataportDeclare(newPort);
				}
			} else {
				// 件数が異なる
				result = false;
			}
		} else if (CollectionUtil.isEmpty(orgList) && CollectionUtil.isEmpty(newList)) {
			// 両方存在しない
			result = true;
		} else {
			// どちらかが存在しない
			result = false;
		}
		return result;
	}

	/****************************************
	 * IDE独自プロパティ
	 * サービスポート関連
	 ****************************************/
	/**
	 * サービスポート宣言が全て一致するかを判定する
	 * @param orgList
	 * @param newList
	 * @return
	 */
	public boolean equalsServiceportDeclares(RtcProfile target) {
		List<ServicePort> orgList = this.getServicePorts();
		List<ServicePort> newList = target.getServicePorts();
		boolean result = true;
		if (CollectionUtil.isNotEmpty(orgList) && CollectionUtil.isNotEmpty(newList)) {
			// 両方存在する
			if (NumberUtil.equals(orgList.size(), newList.size())) {
				// 件数が等しい
				for (int i = 0; i < orgList.size(); i++) {
					ServicePort orgPort = orgList.get(i);
					ServicePort newPort = newList.get(i);
					result &= orgPort.equalsServiceportDeclare(newPort);
				}
			} else {
				// 件数が異なる
				result = false;
			}
		} else if (CollectionUtil.isEmpty(orgList) && CollectionUtil.isEmpty(newList)) {
			// 両方存在しない
			result = true;
		} else {
			// どちらかが存在しない
			result = false;
		}
		return result;
	}
	
	/**
	 * Providedのサービスインタフェース宣言が全て一致するかを判定する
	 * @param orgList
	 * @param newList
	 * @return
	 */
	public boolean equalsProvidedServiceInterfaceDeclares(RtcProfile target) {
		List<ServicePort> orgList = this.getServicePorts();
		List<ServicePort> newList = target.getServicePorts();
		boolean result = equalsServiceportDeclares(target);
		if (result && CollectionUtil.isNotEmpty(orgList) && CollectionUtil.isNotEmpty(newList)) {
			for (int i = 0; i < orgList.size(); i++) {
				ServicePort orgPort = orgList.get(i);
				ServicePort newPort = newList.get(i);
				result &= orgPort.equalsProvidedIntefaceDeclare(newPort);
			}
		} else if (!result) {
			// サービスポートが一致していない場合はインタフェースも一致していないとみなす
			result = false;
		}
		return result;
	}
	
	/**
	 * Requiredのサービスインタフェース宣言が全て一致するかを判定する
	 * @param orgList
	 * @param newList
	 * @return
	 */
	public boolean equalsRequiredServiceInterfaceDeclares(RtcProfile target) {
		List<ServicePort> orgList = this.getServicePorts();
		List<ServicePort> newList = target.getServicePorts();
		boolean result = equalsServiceportDeclares(target);
		if (result && CollectionUtil.isNotEmpty(orgList) && CollectionUtil.isNotEmpty(newList)) {
			for (int i = 0; i < orgList.size(); i++) {
				ServicePort orgPort = orgList.get(i);
				ServicePort newPort = newList.get(i);
				result &= orgPort.equalsRequiredIntefaceDeclare(newPort);
			}
		} else if (!result) {
			// サービスポートが一致していない場合はインタフェースも一致していないとみなす
			result = false;
		}
		return result;
	}
}
